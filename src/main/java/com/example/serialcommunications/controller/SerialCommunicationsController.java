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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger log = LoggerFactory.getLogger(SerialCommunicationsController.class);

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
  public @ResponseBody Map<String, ? extends Object> start(String serialArray){
    Map<String, Object> result = new HashMap<String, Object>();

    if(serialArray != null && serialArray.trim().length() > 0) {
      serialMap.clear();
      String[] messageArr = serialArray.split(";");
      for (String s : messageArr) {
        serialMap.put(s.split(",")[0], s.split(",")[1]);
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
    try {
      for (Entry<String, String> entry : serialMap.entrySet()) {
        String type = entry.getKey();
        String serialName = entry.getValue();

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
                message = type + "," + message;
                System.out.println("接收到数据为：" + message);
                WebSocketServer.sendMessage(message);
              } catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
                log.error(e.getMessage(), e);
              }
            }
          }
        });
        openedSerialPortMap.put(serialName, serialPort);
        result.put("status", "Success");
      }
    } catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e) {
      result.put("status", "Error");
      result.put("message", e.getMessage());
      log.error(e.getMessage(), e);
    }
    return result;
  }

  @GetMapping("stop")
  public @ResponseBody Map<String, ? extends Object> stop(){
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
