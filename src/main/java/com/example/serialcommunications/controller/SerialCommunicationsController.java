package com.example.serialcommunications.controller;

import com.example.serialcommunications.exception.NoSuchPort;
import com.example.serialcommunications.exception.NotASerialPort;
import com.example.serialcommunications.exception.PortInUse;
import com.example.serialcommunications.exception.ReadDataFromSerialPortFailure;
import com.example.serialcommunications.exception.SerialPortInputStreamCloseFailure;
import com.example.serialcommunications.exception.SerialPortParameterFailure;
import com.example.serialcommunications.exception.TooManyListeners;
import com.example.serialcommunications.manager.SerialPortManager;
import com.example.serialcommunications.manager.WebSocketServer;
import com.example.serialcommunications.tools.ToolUtility;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("serial")
public class SerialCommunicationsController {
  @Autowired
  private WebSocketServer webSocketServer;

  private static final Map<String, SerialPort> openedSerialPortMap = new ConcurrentHashMap<>();
  private static final Map<String, String> serialMap = new ConcurrentHashMap<>();

  @GetMapping("view")
  public String view(Model model){
    model.addAttribute("title", "欢迎来到串口界面");
    return "serialView";
  }

  @GetMapping("list")
  public @ResponseBody Map<String, ? extends Object> list(){
    Map<String, Object> result = new HashMap<String, Object>();
    List<String> portList = SerialPortManager.findPort();
    result.put("protList", portList);
    result.put("status", "Success");
    return result;
  }

  @GetMapping("start")
  public @ResponseBody Map<String, ? extends Object> start(@RequestBody String serialArray){
    Map<String, Object> result = new HashMap<String, Object>();

    if(serialArray != null && serialArray.trim().length() > 0) {
      serialMap.clear();
      String[] messageArr = serialArray.split(";");
      for (String s : messageArr) {
        serialMap.put(s.split(",")[1], s.split(",")[0]);
      }
    }

    //启动所有端口前，先关闭
    if(!openedSerialPortMap.isEmpty()){
      openedSerialPortMap.forEach((serialName, serialPort) -> {
        if(serialPort != null){
          serialPort.close();
          serialPort = null;
        }
      });
    }
    serialMap.forEach((serialName, type) -> {
      try {
        SerialPort serialPort = SerialPortManager.openPort(serialName, 9600);
        SerialPortManager.addListener(serialPort, new SerialPortEventListener() {
          @Override
          public void serialEvent(SerialPortEvent serialPortEvent) {
            if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
              //读取串口数据
              byte[] bytes = new byte[0];
              try {
                bytes = SerialPortManager.readFromPort(serialPort);
                String message = ToolUtility.byte2HexStr(bytes);
                System.out.println("接收到数据为：" + message);
                WebSocketServer.sendMessage(message);
              } catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
                e.printStackTrace();
              }
            }
          }
        });
        openedSerialPortMap.put(serialName, serialPort);
      } catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e) {
        e.printStackTrace();
      }
    });
    result.put("status", "Success");
    return result;
  }

  @GetMapping("stop")
  public @ResponseBody Map<String, ? extends Object> stop(@RequestBody Map<String, String> serialMap){
    Map<String, Object> result = new HashMap<String, Object>();
    openedSerialPortMap.forEach((serialName, serialPort) -> {
      if(serialPort != null){
        serialPort.close();
        serialPort = null;
      }
    });
    result.put("status", "Success");
    return result;
  }
}
