package com.example.serialcommunications.exception;

public class SerialPortOutputStreamCloseFailure extends Exception {
  public SerialPortOutputStreamCloseFailure(){
    super("关闭串口对象的输出流出错");
  }
}
