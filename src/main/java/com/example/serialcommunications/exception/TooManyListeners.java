package com.example.serialcommunications.exception;

public class TooManyListeners extends Exception{
  public TooManyListeners(){
    super("监听类对象过多");
  }
}
