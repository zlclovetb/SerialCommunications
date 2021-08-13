package com.example.serialcommunications.exception;

public class ReadDataFromSerialPortFailure extends Exception {
  public ReadDataFromSerialPortFailure() {
    super("从串口读取数据时出错");
  }
}
