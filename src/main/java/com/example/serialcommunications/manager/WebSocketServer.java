package com.example.serialcommunications.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ServerEndpoint("/webSocket/serial")
@Component
public class WebSocketServer {

  private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
  //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
  private static final AtomicInteger onlineNumber = new AtomicInteger();

  //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象
  private static final ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

  /**
   * 连接建立成功调用的方法
   */
  @OnOpen
  public void onOpen(Session session) {
    onlineNumber.incrementAndGet(); // 在线数加1
    log.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), onlineNumber.get());
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClose(Session session) {
    onlineNumber.decrementAndGet(); // 在线数减1
    log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), onlineNumber.get());
  }

  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息
   */
  @OnMessage
  public void onMessage(String message, Session session) {
    log.info("服务端收到客户端[{}]的消息:{}", session.getId(), message);
    sendMessage("Hello, " + message, session);
  }

  @OnError
  public void onError(Session session, Throwable error) {
    log.error("发生错误");
    error.printStackTrace();
  }

  /**
   * 服务端发送消息给客户端
   */
  private static void sendMessage(String message, Session toSession) {
    try {
      log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
      toSession.getBasicRemote().sendText(message);
    } catch (Exception e) {
      log.error("服务端发送消息给客户端失败：{}", e);
    }
  }

  /**
   * 群发消息
   *
   * @param message 要发送的消息
   */
  public static void sendMessage(String message) {
    sessionPools.forEach((sessionId, session) -> {
      if (session != null && session.isOpen()) {
        sendMessage(message, session);
      }
    });
  }

  /**
   * 指定Session发送消息
   *
   * @param message 要发送的消息
   * @param sessionId 对应客户端的session id
   */
  public static void sendMessage(String message, String sessionId) {
    Session session = sessionPools.get(sessionId);
    if (session != null && session.isOpen()) {
      sendMessage(message, session);
    } else {
      log.warn("没有找到你指定ID的会话：{}", sessionId);
    }
  }
}
