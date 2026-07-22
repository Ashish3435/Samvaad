import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";

import { logout } from "../services/authService";
import { getMessages } from "../services/messageService";
import { getOnlineUsers } from "../services/userService";
import {
    getRooms,
    createPrivateRoom
} from "../services/roomService";

import {
    connectWebSocket,
    disconnectWebSocket,
    sendMessage
} from "../services/websocket";

import Sidebar from "../components/Sidebar";
import ChatWindow from "../components/ChatWindow";
import ChatInput from "../components/ChatInput";
import NewChatModal from "../components/NewChatModal";

export default function Chat() {

    const navigate = useNavigate();

    const bottomRef = useRef(null);

    const [messages, setMessages] = useState([]);

    const [onlineUsers, setOnlineUsers] = useState([]);

    const [rooms, setRooms] = useState([]);

    const [selectedRoom, setSelectedRoom] = useState("");

    const [showNewChat, setShowNewChat] = useState(false);

    const [typingUser, setTypingUser] = useState("");

    useEffect(() => {

        loadRooms();

        loadOnlineUsers();

        const interval = setInterval(() => {

            loadOnlineUsers();

        }, 5000);

        return () => {

            clearInterval(interval);

            disconnectWebSocket();

        };

    }, []);

    useEffect(() => {

        if (!selectedRoom) {

            return;

        }

        disconnectWebSocket();

        setMessages([]);

        setTypingUser("");

        loadMessages(selectedRoom);

        connect(selectedRoom);

    }, [selectedRoom]);

    useEffect(() => {

        bottomRef.current?.scrollIntoView({

            behavior: "smooth"

        });

    }, [messages]);

    const connect = (roomCode) => {

        connectWebSocket(

            roomCode,

            (message) => {

                setMessages((previousMessages) => [

                    ...previousMessages,

                    message

                ]);

            },

            (typingData) => {

                setTypingUser(

                    typingData.typing
                        ? typingData.email
                        : ""

                );

            }

        );

    };

    const loadRooms = async () => {

        try {

            const data = await getRooms();

            setRooms(data);

            if (
                data.length > 0 &&
                !selectedRoom
            ) {

                setSelectedRoom(
                    data[0].roomCode
                );

            }

        } catch (error) {

            console.error(
                "ROOM ERROR :",
                error
            );

        }

    };

    const loadMessages = async (roomCode) => {

        try {

            const data =
                await getMessages(roomCode);

            setMessages(data);

        } catch (error) {

            console.error(
                "MESSAGE ERROR :",
                error
            );

        }

    };

    const loadOnlineUsers = async () => {

        try {

            const data =
                await getOnlineUsers();

            setOnlineUsers(data);

        } catch (error) {

            console.error(
                "ONLINE USERS ERROR :",
                error
            );

        }

    };

    const handleSend = (text) => {

        if (!text.trim()) {

            return;

        }

        sendMessage({

            roomCode: selectedRoom,

            content: text

        });

    };

    const handleRoomCreated = async (newRoom) => {

        try {

            const updatedRooms =
                await getRooms();

            setRooms(updatedRooms);

            setSelectedRoom(
                newRoom.roomCode
            );

        } catch (error) {

            console.error(
                "ROOM REFRESH ERROR :",
                error
            );

        }

    };

    const handleUserSelected = async (user) => {

        try {

            setShowNewChat(false);

            const privateRoom =
                await createPrivateRoom(
                    user.email
                );

            const updatedRooms =
                await getRooms();

            setRooms(updatedRooms);

            setSelectedRoom(
                privateRoom.roomCode
            );

        } catch (error) {

            console.error(
                "PRIVATE CHAT ERROR :",
                error
            );

        }

    };

    const handleLogout = () => {

        disconnectWebSocket();

        logout();

        navigate("/login");

    };

    const selectedRoomName =
        rooms.find(
            (room) =>
                room.roomCode === selectedRoom
        )?.roomName || selectedRoom;

    return (

        <div className="h-screen flex bg-slate-100">

            <Sidebar

                rooms={rooms}

                selectedRoom={selectedRoom}

                onSelectRoom={
                    setSelectedRoom
                }

                onlineUsers={onlineUsers}

                onLogout={
                    handleLogout
                }

                onRoomCreated={
                    handleRoomCreated
                }

                onNewChat={() => {

                    if (!showNewChat) {

                        setShowNewChat(true);

                    }

                }}

            />

            <div className="flex-1 flex flex-col">

                <div className="bg-white border-b px-5 py-3">

                    <h2 className="text-xl font-bold">

                        {selectedRoomName}

                    </h2>

                    {typingUser && (

                        <p className="text-sm text-green-600">

                            {typingUser} is typing...

                        </p>

                    )}

                </div>

                <ChatWindow

                    messages={messages}

                    bottomRef={bottomRef}

                />

                <ChatInput

                    onSend={handleSend}

                    roomCode={selectedRoom}

                />

            </div>

            {showNewChat && (

                <NewChatModal

                    onClose={() => {

                        setShowNewChat(false);

                    }}

                    onUserSelected={
                        handleUserSelected
                    }

                />

            )}

        </div>

    );

}