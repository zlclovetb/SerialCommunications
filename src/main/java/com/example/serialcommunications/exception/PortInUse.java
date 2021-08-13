package com.example.serialcommunications.exception;

public class PortInUse extends Exception {
  public PortInUse () {
    super("串口被占用");
  }
}
