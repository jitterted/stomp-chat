# WebSockets
## State of websockets in 2020
* All major and actively used browsers support WebSockets natively, which means there's no need to use libraries like SockJS, unless you need compatibility with older browsers. This tutorial is going to assume that's not needed.
    * Mesnil's site has a good intro to this: http://jmesnil.net/stomp-websocket/doc/
## Message Format
* STOMP vs. Socket.io
    * Since WebSocket is a protocol (https://tools.ietf.org/html/rfc6455), like HTTP, the contents of the "body" (or "payload") aren't defined (is it HTML? JSON? XML? etc.), so that's up to another layer to implement.
    * For working with Spring (and therefore Spring Boot), STOMP is the easiest to use since it's supported natively by Spring's messaging and websocket projects (and is included by specifying the websocket starter). Since these support integration between systems, it falls under the Spring Integration set of projects: https://docs.spring.io/spring-integration/reference/html/index.html
    * STOMP stands for Simple (or Streaming) Text-Orientated Messaging Protocol, which was created as a standard for messaging applications as a simpler subset compared to protocols like AMPQ. Reference to 1.2 spec is https://stomp.github.io/stomp-specification-1.2.html

Frame-based, consisting of:

* Command: the purpose of this frame, e.g., CONNECT, SUBSCRIBE, SEND, etc.
* Headers (optional): similar to HTTP headers, they are name-value pairs that can be things like login & password (for authentication)
* Body (optional): this is text, which should be unsurprising given the "T" in STOMP is Text. 😀
* Client and Server
A specific Server has a set of named "destinations" that are unique strings. The server's purpose is to deliver received messages to subscribers. We'll see that destination names are prefixed by "/app" or "/topic" or "/queue", followed by the name of the destination, e.g., "/app/score", or "/topic/chatmessage". The format of this string is completely up to the server. For Spring, we'll see that "/topic" and "/queue" are treated the same, but "/app" is 

* Clients can "consume" messages by "subscribing" to a destination, and then receiving "messages" from the server when a message is "produced".
* Clients can "produce" messages by SENDing them to a destination on a server, which will then deliver them to any and all subscribers to that destination.

## Browser Client
Three libraries
"Old" Stomp.js - Stomp Over WebSocket http://jmesnil.net/stomp-websocket/doc/ from Jeff Mesnil, repo is https://github.com/jmesnil/stomp-websocket. This project is no longer maintained.
"New" Stomp.js - (version 5 and later) - written in TypeScript, has a different API than the "Old" one, but is actively maintained. https://github.com/stomp-js/stompjs
webstomp-client - fork of the "old" Stomp.js client: https://github.com/JSteunou/webstomp-client
Note that this also changes the API, but in a non-obvious way.
It isn't actively maintained, and should not be used.
Of the three, recommend using the "new" Stomp.js. Unfortunately, the one most commonly found, as well as the one used in many Spring examples, uses the old one. Of note, the WebJars stomp-websocket wrapper also uses the old Stomp.js that is no longer maintained.
 
# Code

* Minimal code for a chat client + server
    * Create parent project with two module: frontend and backend
        * Multi-module maven with front-end plugin
        * set up proxying to avoid CORS
    * Back-end: Spring Boot
        * The only dependency needed is starter-websocket (it includes the starter-web as a dependency)
    * Front-end: Vue + TypeScript
        * Vue, TypeScript, with StompJS module
            - basic project creation with TypeScript and Router and Unit Tests (Jest)
            - yarn add @stomp/stompjs
    * Single topic (queue/destination)
        * /topic/chat
    * No logging in, no user names, no rooms/channels, just connect and echo back
    * Server reflects/echoes back chat messages

1. Create Spring Boot and Vue projects
2. Create Spring ChatController to echo message
    1. Configuration of websocket and stomp -- don't need SockJS
    1. Explain destination vs. topic
    1. Write integration test for websocket: should echo what is sent
    1. Create controller with message mapping
3. Front-end
    1. Create front-end form with input for chat message and button to send the message
    1. Display messages received on websocket/stomp


-- Part 2 --
* ?? How to Write test for front- and back-end ??
* Add logging in for username (no AuthN)
* Add server modifies chat messages by prepending username and timestamp to messages
* Change to use structured chat messages with username, timestamp, etc., in an object
-- Part 3 --
* Separate rooms/channels using topics
-- Part 4 security stuff below --
* Secure login for username (AuthN)
* Add Private messages to direct to specific users
    * Need to be careful from security standpoint: restrict subscribing to a user's private topic
    
