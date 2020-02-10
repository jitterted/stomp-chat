
## Back-end with Spring Boot and 

1. Created Spring Boot app from the Spring Initalizr
    1. Split this pom.xml into two parts: 
        * Parent of the project: requires the Spring parent setup and modules section for the two modules,
         "backend" and "frontend"
         
            ```
            <modules>
              <module>frontend</module>
              <module>backend</module>
            </modules>
            ```
          
        * backend module: gets all the dependencies and plugins for Spring Boot, points to top-level pom.xml for its parent
            ```
          	<parent>
          		<artifactId>stomp-chat</artifactId>
          		<groupId>com.jitterted</groupId>
          		<version>0.0.1-SNAPSHOT</version>
          	</parent>
            ```
          but does not need anything other than the <artifactId>, as its <groupId> is inherited from the parent

        * frontend module: a new pom.xml with just the basics, otherwise it's the Vue-created project
    
2. Configure Spring Boot to work with WebSockets and STOMP

    Add configuration file, like this:
    
    ```
    import org.springframework.context.annotation.Configuration;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
    
    @Configuration
    @EnableWebSocketMessageBroker // does what it says: configures the WS broker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            // websocket/STOMP endpoint is /ws, so websocket connects to http://stompchat.com/ws 
            // allow CORS from anywhere
            registry.addEndpoint("/ws").setAllowedOrigins("*");
        }
        
        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            // incoming STOMP broker destination for our server /app/<topic name>
            // to be handled by @MessageMapping mapped methods in a @Controller 
            config.setApplicationDestinationPrefixes("/app");
            // this turns on the simple broker to handle any messages sent to /topic/*
            config.enableSimpleBroker("/topic");
        }
    
    }
    ```

3. Create a @Controller with a method to handle incoming STOMP messages

   ```
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.stereotype.Controller;
    
    @Controller // have Spring manage this component
    public class ChatController {
    
      @MessageMapping("/chat") // message destination must be /app/chat to receive it here
      public String echoMessage(String message) {
        return message; // outgoing message will go to /topic/chat, replacing "/app" with "/topic"
      }
    }
   ```

4. That's it for the backend, now on to the front end...

## Front-end with Vue.js and Stomp.js

