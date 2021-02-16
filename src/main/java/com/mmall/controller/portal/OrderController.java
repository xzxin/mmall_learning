package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/** @ClassName: OrderController @Description: @Author @Date 2021/2/15 @Version 1.0 */
@Controller
@RequestMapping("/order/")
public class OrderController {
  private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

  @Autowired private IOrderService iOrderService;

  @RequestMapping("pay.do")
  @ResponseBody
  public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
    User user = (User) session.getAttribute(Const.CURRENT_USER);
    if (user == null) {
      return ServerResponse.createByErrorCodeMessage(
          ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
    }
    String path = request.getSession().getServletContext().getRealPath("upload");
    return iOrderService.pay(orderNo, user.getId(), path);
  }

  @RequestMapping("alipay_callback.do")
  @ResponseBody
  public Object alipayCallback(HttpServletRequest request) {
    Map<String, String> params = Maps.newHashMap();
    Map requestParams = request.getParameterMap();
    for (Iterator iter = requestParams.keySet().iterator(); ((Iterator) iter).hasNext(); ) {
      String name = (String) iter.next();
      String[] values = (String[]) requestParams.get(name);
      String valueStr = "";
      for (int i = 0; i < values.length; i++) {
        // 拼接 "," 分割
        valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
      }
      params.put(name, valueStr);
    }
    logger.info(
        "支付宝回调, sign:{}, trade_status:{}, 参数:{}",
        params.get("sign"),
        params.get("trade_status"),
        params.toString());
    // 重要： 验证回调的准确性，并且避免重复通知
    params.remove("sign_type");
    try {
      boolean alipayRSACheckedV2 =
          AlipaySignature.rsaCheckV2(
              params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
      if (!alipayRSACheckedV2) {
        return ServerResponse.createByErrorMessage("非法请求，验证不通过");
      }
    } catch (AlipayApiException e) {
      logger.error("支付宝验证回调异常", e);
    }
    // todo 验证各种数据
    ServerResponse serverResponse = iOrderService.aliCallback(params);
    if (serverResponse.isSuccess()) {
      return Const.AlipayCallback.RESPONSE_SUCCESS;
    }
    return Const.AlipayCallback.RESPONSE_FAILED;
  }
  
  @RequestMapping("query_order_pay_status.do")
  @ResponseBody
  public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
    User user = (User) session.getAttribute(Const.CURRENT_USER);
    if (user == null) {
      return ServerResponse.createByErrorCodeMessage(
          ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
    }
    if (iOrderService.queryOrderPayStatus(user.getId(), orderNo).isSuccess()) {
      return ServerResponse.createBySuccess(true);
    }
    return ServerResponse.createBySuccess(false);
  }
}
