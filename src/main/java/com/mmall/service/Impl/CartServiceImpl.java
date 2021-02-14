package com.mmall.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/** @ClassName: CartServiceImpl @Description: 购物车模块实现类 @Author @Date 2021/2/14 @Version 1.0 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {
  @Autowired private CartMapper cartMapper;
  @Autowired private ProductMapper productMapper;

  public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
    if (productId == null || count == 0) {
      return ServerResponse.createByErrorCodeMessage(
          ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }
    Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
    if (cart == null) {
      // 需要新增产品记录
      Cart cartItem = new Cart();
      cartItem.setQuantity(count);
      cartItem.setChecked(Const.Cart.CHECKED);
      cartItem.setProductId(productId);
      cartItem.setUserId(userId);
      cartMapper.insert(cartItem);
    } else {
      // 产品存在 数量相加
      count += cart.getQuantity();
      cart.setQuantity(count);
      cartMapper.updateByPrimaryKeySelective(cart);
    }
    return list(userId);
  }

  public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
    if (productId == null || count == 0) {
      return ServerResponse.createByErrorCodeMessage(
          ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }
    Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
    if (cart != null) {
      cart.setQuantity(count);
    }
    cartMapper.updateByPrimaryKeySelective(cart);
    return list(userId);
  }
  
  public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
    List<String> productList = Splitter.on(",").splitToList(productIds);
    if (CollectionUtils.isEmpty(productList)) {
      return ServerResponse.createByErrorCodeMessage(
          ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }
    cartMapper.deleteByUserIdProductIds(userId, productList);
    return list(userId);
  }
  
  public ServerResponse<CartVo> list(Integer userId) {
    CartVo cartVo = getCartVoLimit(userId);
    return ServerResponse.createBySuccess(cartVo);
  }
  
  public ServerResponse<CartVo> selectOrUnselect(Integer userId, Integer productId, Integer checked) {
    cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
    return list(userId);
  }
  private CartVo getCartVoLimit(Integer userId) {
    CartVo cartVo = new CartVo();
    List<Cart> cartList = cartMapper.selectCartByUserId(userId);
    List<CartProductVo> cartProductVoList = Lists.newArrayList();
    BigDecimal cartTotalPrice = new BigDecimal("0");
    if (!CollectionUtils.isEmpty(cartList)) {
      for (Cart cartItem : cartList) {
        CartProductVo cartProductVo = new CartProductVo();
        cartProductVo.setId(cartItem.getId());
        cartProductVo.setUserId(userId);
        cartProductVo.setProductId(cartItem.getProductId());

        Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
        if (product != null) {
          cartProductVo.setProductMainImage(product.getMainImage());
          cartProductVo.setProductName(product.getName());
          cartProductVo.setProductSubtitle(product.getSubtitle());
          cartProductVo.setProductStatus(product.getStatus());
          cartProductVo.setProductPrice(product.getPrice());
          cartProductVo.setProductStock(product.getStock());

          int buyLimitCount = 0;
          if (product.getStock() >= cartItem.getQuantity()) {
            buyLimitCount = cartItem.getQuantity();
            cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
          } else {
            buyLimitCount = product.getStock();
            cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
            Cart cartForQuantity = new Cart();
            cartForQuantity.setId(cartItem.getId());
            cartForQuantity.setQuantity(buyLimitCount);
            cartMapper.updateByPrimaryKeySelective(cartForQuantity);
          }
          cartProductVo.setQuantity(buyLimitCount);
          // 计算总价
          cartProductVo.setProductTotalPrice(
              BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
          cartProductVo.setProductChecked(cartItem.getChecked());
        }
        if (cartItem.getChecked() == Const.Cart.CHECKED) {
          cartTotalPrice =
              BigDecimalUtil.add(
                  cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
        }
        cartProductVoList.add(cartProductVo);
      }
    }
    cartVo.setCartTotalPrice(cartTotalPrice);
    cartVo.setCartProductVoList(cartProductVoList);
    cartVo.setAllChecked(isAllCheckedStatus(userId));
    cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
    return cartVo;
  }

  private boolean isAllCheckedStatus(Integer userId) {
    if (userId == null) {
      return false;
    }
    return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
  }
  
  public ServerResponse<Integer> getCartProductCount(Integer userId) {
    if (userId == null) {
      return ServerResponse.createBySuccess(0);
    }
    return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
  }
}
