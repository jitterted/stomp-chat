package com.jitterted.stompchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@SpringBootApplication
public class StompChatApplication {

  private static final Logger logger = LoggerFactory.getLogger(StompChatApplication.class);

  // look at this test: https://github.com/spring-projects/spring-integration/blob/master/spring-integration-stomp/src/test/java/org/springframework/integration/stomp/outbound/StompMessageHandlerWebSocketIntegrationTests.java
  public static void main(String[] args) {
    new SpringApplicationBuilder(StompChatApplication.class)
        .listeners((ApplicationListener<SessionConnectedEvent>) event -> {
                     logger.info("Session Connected Event: {}", StompHeaderAccessor.wrap(event.getMessage()));
                   },
                   (ApplicationListener<SessionSubscribeEvent>) event -> {
                     logger.info("Session Subscribe Event: {}", StompHeaderAccessor.wrap(event.getMessage()));
                   })
        .run(args);
//    SpringApplication.run(StompChatApplication.class, args)
  }

}
