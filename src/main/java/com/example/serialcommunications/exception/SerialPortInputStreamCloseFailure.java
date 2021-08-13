package com.example.serialcommunications.exception;

public class SerialPortInputStreamCloseFailure extends Exception {
  public SerialPortInputStreamCloseFailure(){
    super("关闭串口对象输入流出错");
  }
}
