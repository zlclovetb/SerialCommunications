package com.example.serialcommunications.exception;

public class NotASerialPort extends Exception {
  public NotASerialPort () {
    super("这不是一个串口");
  }
}
