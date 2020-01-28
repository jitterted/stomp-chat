package com.jitterted.stompchat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  @MessageMapping
  public String echoMessage(String message) {
    return message;
  }
}
