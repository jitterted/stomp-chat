## Pre-requisites

* vue-devtools extension

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

1. Add stomp/stompjs (v5.4.3 as of this writing) to the project: yarn add @stomp/stompjs or npm i @stomp/stompjs
    * Not to be confused with just "stompjs" that is 6 years old
    * Usage docs are here: https://stomp-js.github.io/guide/stompjs/using-stompjs-v5.html

2. Import the `Client` class into the ChatClient.vue class from Stomp.js:

    ```
    import {Client} from "@stomp/stompjs";
    ```

3. Create a STOMP client instance in the `created()` function, with configuration as follows:

    ```
    created() {
      this.client = new Client({
        brokerURL: 'ws://localhost:8080/ws',
        debug: function (str) {
          console.log(str);
        }
      });
    }
    ```
   
   The `brokerURL` points to the Spring Boot back-end endpoint of `/ws` as that's what we defined in
   the `WebSocketConfig` Java class in its `registerStompEndpoints` method.
   
   Here the `debug` function is used to display runtime debugging info about connections,
   subscriptions, and messages sent and received.

4. Let's validate that we can connect to the back-end websocket broker, by connecting after we've
   created the client object. StompJS calls the connection process "activate".
   
   ```
   this.client.activate();
   ```

5. Open up the browser console

    (image of websocket failing to connect here)
    
6. Run the back-end, where you'll see the following in the log output

    ```
    2020-02-10 13:38:18.455  INFO 2105 --- [  restartedMain] o.s.m.s.b.SimpleBrokerMessageHandler     : Starting...
    2020-02-10 13:38:18.455  INFO 2105 --- [  restartedMain] o.s.m.s.b.SimpleBrokerMessageHandler     : BrokerAvailabilityEvent[available=true, SimpleBrokerMessageHandler [DefaultSubscriptionRegistry[cache[0 destination(s)], registry[0 sessions]]]]
    2020-02-10 13:38:18.456  INFO 2105 --- [  restartedMain] o.s.m.s.b.SimpleBrokerMessageHandler     : Started.
    ```
   
   This tells us that the STOMP broker is running.

7. Refresh the browser page and you'll see a 404

    (insert image of white label error page from Spring)
    
8. To fix this, we have two choices:

    A. Run the Vue front-end on port 8081, and let Spring Boot run on 8080
    
    B. Run Spring Boot on 8081, and configure proxying so that `8080/ws` gets forwarded to `8081/ws`
    
   We'll choose **A** here. See part II for setting up proxying. (or as a separate post?)

9. In the browser, navigate to `localhost:8081/` with the console open.
   You should see console messages showing that the websocket was connected,
   and then that STOMP CONNECT messages were sent with a CONNECTED message received

    (insert image of connect and connected messages)

10. Now that we have verified that the front-end connected to the back-end,
    we need to subscribe to a specific channel, often referred to as a "topic".
    
    In order to subscribe, we need to have already been connected, and to do this
    with the StompJS client, we do the subscription in a callback that gets invoked
    when the connection is successful. For example:
    
    ```
    this.client.onConnect = (() => {
      console.log('Connected, now subscribing...');
      this.subscription = this.client.subscribe('/topic/chat', message => {
        console.log('Message received on /topic/chat:', message);
      });
      console.log('Subscribed: ', this.subscription);
    });
    ```
    
    The above code requires two private fields in the class:
    
    ```
    private client!: Client;
    private subscription!: StompSubscription;
    ```

11. To do a Tiny Validation, we can send (publish) a message from the console
    
    (SIDEBAR: how to access Vue instances from the console, see image for extension):

    ```
    $vm2.client.publish({destination: '/topic/chat', body: 'Hello chat!'})
    ```

    You should then see:
    
    (image of message sent with the message display in the console)

12. Add a chat "area" to the front-end to display incoming chat messages.
    Define a `chatMessages` field as a string array to hold the incoming chat messages.
    
    ```
    private chatMessages: string[] = [];
    ```
    
    Then update the subscription callback function to add the incoming STOMP
    message to the array, and we need to pull out the text body of the message
    via `message.body`:
    
    ```
    this.subscription = this.client.subscribe('/topic/chat', message => {
      this.chatMessages.push(message.body);
    });
    ```

    Then display the messages using `v-for` to display each message in a `<p>` element:

    ```
    <p v-for="(message, index) in chatMessages" :key="index">
      {{ message }}
    </p>
    ```

13. Now let's make it easier to send a message.
    Let's create a text input with a button that calls the sendMessage function.
    
    ```
    <form v-on:submit.prevent>
      <input
          v-model="messageToSend"
      >
      <button
          type="submit"
          @click="sendMessage"
      >Send
      </button>
    </form>
    ```
    
14. Inside the `sendMessage` function, we'll call the `publish` function on the StompJS client.

    ```
    private messageToSend = ""; // bound to the input text field

    sendMessage() {
      this.client.publish({
        destination: this.stompDestination,
        body: this.messageToSend
      });
      this.messageToSend = ""; // clear the text field when sent
    }
    ```


