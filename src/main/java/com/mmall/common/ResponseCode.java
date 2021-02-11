package com.mmall.common;

/**
 * @ClassName: ResponseCode
 * @Description: 状态码
 * @Author
 * @Date 2021/2/11
 * @Version 1.0
 */
public enum ResponseCode {
  SUCCESS(0, "SUCCESS"),
  ERROR(1, "ERROR"),
  NEED_LOGIN(10, "NEED_LOGIN"),
  ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");
  
  private final int code;
  private final String desc;
  
  public int getCode() {
    return code;
  }
  
  public String getDesc() {
    return desc;
  }
  
  ResponseCode(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }}
