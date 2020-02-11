<template>
  <div class="chat">

    <p v-for="(message, index) in chatMessages" :key="index">
      {{ message }}
    </p>

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

  </div>
</template>

<script lang="ts">
  import {Component, Vue} from "vue-property-decorator";
  import {Client, StompSubscription} from "@stomp/stompjs";

  @Component
  export default class ChatClient extends Vue {
    private readonly stompDestination = '/topic/chat';

    private client!: Client;
    private subscription!: StompSubscription;
    private chatMessages: string[] = [];

    private messageToSend = "";

    sendMessage() {
      this.client.publish({
        destination: this.stompDestination,
        body: this.messageToSend
      });
      this.messageToSend = "";
    }

    created() {
      this.client = new Client({
        brokerURL: 'ws://localhost:8080/ws',
        debug: function (str) {
          console.log(str);
        }
      });

      this.client.onConnect = (() => {
        console.log('Connected, now subscribing...');
        this.subscription = this.client.subscribe(this.stompDestination, message => {
          this.chatMessages.push(message.body);
        });
        console.log('Subscribed: ', this.subscription);
      });

      this.client.activate(); // connect
    }
  }
</script>

<style>
  .chat {
    font-size: 1.25rem;
    font-weight: bold;
  }

  input {
    font-size: 1.125rem;
    width: 80%;
    margin-top: 1rem;
  }

  button {
    font-size: 1.125rem;
    font-weight: bold;
    color: white;
    background: teal;
  }

  .chat p {
    margin: 0 0 .5rem;
  }
</style>