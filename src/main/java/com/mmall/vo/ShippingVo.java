package com.mmall.vo;

import java.util.Date;

/**
 * @ClassName: ShippingVo
 * @Description:
 * @Author
 * @Date 2021/2/17
 * @Version 1.0
 */
public class ShippingVo {
    private String receiverName;
  
  private String receiverPhone;
  
  private String receiverMobile;
  
  private String receiverProvince;
  
  private String receiverCity;
  
  public String getReceiverName() {
    return receiverName;
  }
  
  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }
  
  public String getReceiverPhone() {
    return receiverPhone;
  }
  
  public void setReceiverPhone(String receiverPhone) {
    this.receiverPhone = receiverPhone;
  }
  
  public String getReceiverMobile() {
    return receiverMobile;
  }
  
  public void setReceiverMobile(String receiverMobile) {
    this.receiverMobile = receiverMobile;
  }
  
  public String getReceiverProvince() {
    return receiverProvince;
  }
  
  public void setReceiverProvince(String receiverProvince) {
    this.receiverProvince = receiverProvince;
  }
  
  public String getReceiverCity() {
    return receiverCity;
  }
  
  public void setReceiverCity(String receiverCity) {
    this.receiverCity = receiverCity;
  }
  
  public String getReceiverDistrict() {
    return receiverDistrict;
  }
  
  public void setReceiverDistrict(String receiverDistrict) {
    this.receiverDistrict = receiverDistrict;
  }
  
  public String getReceiverAddress() {
    return receiverAddress;
  }
  
  public void setReceiverAddress(String receiverAddress) {
    this.receiverAddress = receiverAddress;
  }
  
  public String getReceiverZip() {
    return receiverZip;
  }
  
  public void setReceiverZip(String receiverZip) {
    this.receiverZip = receiverZip;
  }
  
  private String receiverDistrict;
  
  private String receiverAddress;
  
  private String receiverZip;
}
