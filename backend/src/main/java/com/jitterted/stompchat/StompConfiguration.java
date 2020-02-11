package com.jitterted.stompchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker // does what it says: configures the WS broker
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

  private static final Logger logger = LoggerFactory.getLogger(StompConfiguration.class);


  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // websocket/STOMP endpoint is /ws, so websocket connects to ws://localhost:8080/ws
    // when running locally
    registry.addEndpoint("/ws")
            .setAllowedOrigins("*");  // allow CORS from anywhere
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // incoming STOMP broker destination for our server /app/<topic name>
    // to be handled by @MessageMapping mapped methods in a @Controller
    config.setApplicationDestinationPrefixes("/app");
    // this turns on the simple broker to handle any messages sent to /topic/*
    config.enableSimpleBroker("/topic");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ExecutorChannelInterceptor() {
      @Override
      public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, Exception ex) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        if (accessor.getMessageType() == SimpMessageType.SUBSCRIBE && handler instanceof AbstractBrokerMessageHandler) {
          logger.info("Subscribe Message Handled by Broker. Message: {}, Channel: {}, Handler: {}",
                      message, channel, handler);
        }
      }
    });
  }
}
