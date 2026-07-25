import {
    useCallback,
    useEffect,
    useRef,
    useState
} from "react";

import { useNavigate } from "react-router-dom";

import { logout } from "../services/authService";

import {
    getMessages,
    markMessagesAsSeen
} from "../services/messageService";

import {
    getOnlineUsers
} from "../services/userService";

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


    const [messages, setMessages] =
        useState([]);

    const [onlineUsers, setOnlineUsers] =
        useState([]);

    const [rooms, setRooms] =
        useState([]);

    const [selectedRoom, setSelectedRoom] =
        useState("");

    const [showNewChat, setShowNewChat] =
        useState(false);

    const [typingUser, setTypingUser] =
        useState("");


    const currentUserEmail =
        localStorage.getItem("email");


    const loadRooms =
        useCallback(async () => {

            try {

                const data =
                    await getRooms();

                setRooms(data);


                setSelectedRoom(
                    (currentSelectedRoom) => {

                        if (
                            currentSelectedRoom &&
                            data.some(
                                (room) =>
                                    room.roomCode ===
                                    currentSelectedRoom
                            )
                        ) {

                            return currentSelectedRoom;
                        }


                        return data.length > 0
                            ? data[0].roomCode
                            : "";

                    }
                );

            } catch (error) {

                console.error(
                    "ROOM ERROR :",
                    error
                );

            }

        }, []);


    const loadMessages =
        useCallback(async (roomCode) => {

            try {

                const data =
                    await getMessages(
                        roomCode
                    );

                setMessages(data);

            } catch (error) {

                console.error(
                    "MESSAGE ERROR :",
                    error
                );

            }

        }, []);


    const loadOnlineUsers =
        useCallback(async () => {

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

        }, []);


    useEffect(() => {

        loadRooms();

        loadOnlineUsers();


        const interval =
            setInterval(
                loadOnlineUsers,
                5000
            );


        return () => {

            clearInterval(interval);

            disconnectWebSocket();

        };

    }, [
        loadRooms,
        loadOnlineUsers
    ]);


    useEffect(() => {

        if (!selectedRoom) {

            return;

        }


        disconnectWebSocket();


        setMessages([]);

        setTypingUser("");


        loadMessages(
            selectedRoom
        );


        markMessagesAsSeen(
            selectedRoom
        ).catch((error) => {

            console.error(
                "MARK SEEN ERROR :",
                error
            );

        });


        connectWebSocket(

            selectedRoom,


            (message) => {

                setMessages(
                    (previousMessages) => [

                        ...previousMessages,

                        message

                    ]
                );

            },


            (typingData) => {

                setTypingUser(

                    typingData.typing
                        ? typingData.email
                        : ""

                );

            }

        );


        return () => {

            disconnectWebSocket();

        };

    }, [
        selectedRoom,
        loadMessages
    ]);


    useEffect(() => {

        bottomRef.current?.scrollIntoView({

            behavior: "smooth"

        });

    }, [
        messages
    ]);


    const handleSend =
        (text) => {

            if (
                !text ||
                !text.trim() ||
                !selectedRoom
            ) {

                return;

            }


            sendMessage({

                roomCode:
                selectedRoom,

                content:
                text

            });

        };


    const handleRoomCreated =
        async (newRoom) => {

            try {

                const updatedRooms =
                    await getRooms();

                setRooms(
                    updatedRooms
                );


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


    const handleUserSelected =
        async (user) => {

            try {

                setShowNewChat(
                    false
                );


                const privateRoom =
                    await createPrivateRoom(
                        user.email
                    );


                const updatedRooms =
                    await getRooms();


                setRooms(
                    updatedRooms
                );


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


    const handleLogout =
        () => {

            disconnectWebSocket();

            logout();

            navigate(
                "/login"
            );

        };


    const selectedRoomData =
        rooms.find(

            (room) =>

                room.roomCode ===
                selectedRoom

        );


    const selectedRoomName =
        selectedRoomData?.roomName ||
        selectedRoom;


    const selectedRoomType =
        selectedRoomData?.roomType;


    const opponent =
        selectedRoomType === "CHAT"

            ? selectedRoomData?.members?.find(

                (member) =>

                    member.email !==
                    currentUserEmail

            )

            : null;


    const displayRoomName =
        opponent?.fullName ||
        selectedRoomName;


    const isOpponentOnline =
        opponent
            ? onlineUsers.some(
                (user) =>
                    user.email ===
                    opponent.email
            )
            : false;


    return (

        <div className="h-screen flex bg-slate-100">


            <Sidebar

                rooms={rooms}

                selectedRoom={selectedRoom}

                onSelectRoom={
                    setSelectedRoom
                }

                onlineUsers={
                    onlineUsers
                }

                onLogout={
                    handleLogout
                }

                onRoomCreated={
                    handleRoomCreated
                }

                onNewChat={() => {

                    setShowNewChat(
                        true
                    );

                }}

            />


            <div className="flex-1 flex flex-col min-w-0">


                <div className="bg-white border-b px-5 py-3">


                    <h2 className="text-xl font-bold">

                        {displayRoomName}

                    </h2>


                    {selectedRoomType === "CHAT" &&
                        opponent && (

                            <p
                                className={`text-sm ${
                                    isOpponentOnline
                                        ? "text-green-600"
                                        : "text-gray-400"
                                }`}
                            >

                                <span className="mr-1">

                                    ●

                                </span>


                                {isOpponentOnline
                                    ? "Online"
                                    : "Offline"}

                            </p>

                        )}


                    {typingUser && (

                        <p className="text-sm text-green-600">

                            {typingUser}

                            {" "}

                            is typing...

                        </p>

                    )}

                </div>


                <ChatWindow

                    messages={
                        messages
                    }

                    bottomRef={
                        bottomRef
                    }

                />


                <ChatInput

                    onSend={
                        handleSend
                    }

                    roomCode={
                        selectedRoom
                    }

                />

            </div>


            {showNewChat && (

                <NewChatModal

                    onClose={() => {

                        setShowNewChat(
                            false
                        );

                    }}

                    onUserSelected={
                        handleUserSelected
                    }

                />

            )}

        </div>

    );

}