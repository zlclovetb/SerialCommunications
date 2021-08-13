package com.example.serialcommunications.exception;

public class SerialPortParameterFailure extends Exception {
  public SerialPortParameterFailure() {
    super("串口参数错误");
  }
}
