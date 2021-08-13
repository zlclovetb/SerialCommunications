package com.example.serialcommunications.exception;

public class NoSuchPort extends Exception {
  public NoSuchPort () {
    super("串口不存在");
  }
}
