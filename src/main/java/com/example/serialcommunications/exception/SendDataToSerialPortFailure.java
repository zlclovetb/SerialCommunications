package com.example.serialcommunications.exception;

public class SendDataToSerialPortFailure extends Exception {
  public SendDataToSerialPortFailure () {
    super("向串口发送数据失败");
  }
}
