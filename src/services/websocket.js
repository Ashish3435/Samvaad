import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const SOCKET_URL = "http://localhost:8083/chat";

let stompClient = null;

export const connectWebSocket = (
    roomCode,
    onMessage,
    onTyping
) => {

    const token = localStorage.getItem("token");

    const socket = new SockJS(SOCKET_URL);

    stompClient = new Client({

        webSocketFactory: () => socket,

        reconnectDelay: 5000,

        connectHeaders: {
            Authorization: `Bearer ${token}`
        },

        onConnect: () => {

            console.log("WebSocket Connected");

            stompClient.subscribe(
                `/topic/${roomCode}`,
                (message) => {

                    onMessage(
                        JSON.parse(message.body)
                    );

                }
            );

            stompClient.subscribe(
                `/topic/${roomCode}/typing`,
                (message) => {

                    onTyping(
                        JSON.parse(message.body)
                    );

                }
            );

        },

        onStompError: (frame) => {

            console.error(
                "STOMP ERROR:",
                frame
            );

        },

        onWebSocketError: (error) => {

            console.error(
                "WEBSOCKET ERROR:",
                error
            );

        }

    });

    stompClient.activate();

};

export const sendMessage = (message) => {

    if (
        !stompClient ||
        !stompClient.connected
    ) {

        return;

    }

    stompClient.publish({

        destination: "/app/chat.send",

        body: JSON.stringify(message)

    });

};

export const sendTyping = (data) => {

    if (
        !stompClient ||
        !stompClient.connected
    ) {

        return;

    }

    stompClient.publish({

        destination: "/app/chat.typing",

        body: JSON.stringify(data)

    });

};

export const disconnectWebSocket = () => {

    if (stompClient) {

        stompClient.deactivate();

        stompClient = null;

    }

};