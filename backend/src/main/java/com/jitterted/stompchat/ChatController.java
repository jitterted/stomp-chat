package com.jitterted.stompchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

  @MessageMapping("/chat") // destination /app/chat Spring sends the message here
  public String echoMessage(String message) {
    logger.info("Received chat message: {}", message);
    return message; // message is sent out to: /topic/chat
  }

  @SubscribeMapping("/chat")
  public String handleSubscribe(StompHeaderAccessor stompHeaderAccessor) {
    logger.info("Message ID: {}, Receipt: {}, Receipt ID: {}, Login: {}",
                stompHeaderAccessor.getMessageId(),
                stompHeaderAccessor.getReceipt(),
                stompHeaderAccessor.getReceiptId(),
                stompHeaderAccessor.getLogin());
    return "You're subscribed!";
  }
}
