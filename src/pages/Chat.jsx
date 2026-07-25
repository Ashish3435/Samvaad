import {
    useCallback,
    useEffect,
    useRef,
    useState
} from "react";

import { useNavigate } from "react-router-dom";

import {
    logout,
    getCurrentUser
} from "../services/authService";

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


    const [messages, setMessages] = useState([]);

    const [onlineUsers, setOnlineUsers] = useState([]);

    const [rooms, setRooms] = useState([]);

    const [selectedRoom, setSelectedRoom] = useState("");

    const [showNewChat, setShowNewChat] = useState(false);

    const [typingUser, setTypingUser] = useState("");


    /*
    =====================================================
    CURRENT LOGGED-IN USER
    =====================================================
    */

    const currentUserEmail =
        getCurrentUser()?.trim().toLowerCase();


    /*
    =====================================================
    LOAD ROOMS
    =====================================================
    */

    const loadRooms = useCallback(async () => {

        try {

            const data = await getRooms();

            console.log("ROOMS FROM BACKEND:", data);

            setRooms(data);

            setSelectedRoom((currentSelectedRoom) => {

                if (
                    currentSelectedRoom &&
                    data.some(
                        (room) =>
                            room.roomCode === currentSelectedRoom
                    )
                ) {
                    return currentSelectedRoom;
                }

                return data.length > 0
                    ? data[0].roomCode
                    : "";

            });

        } catch (error) {

            console.error(
                "ROOM ERROR:",
                error
            );

        }

    }, []);


    /*
    =====================================================
    LOAD MESSAGES
    =====================================================
    */

    const loadMessages = useCallback(async (roomCode) => {

        try {

            const data =
                await getMessages(roomCode);

            setMessages(data);

        } catch (error) {

            console.error(
                "MESSAGE ERROR:",
                error
            );

        }

    }, []);


    /*
    =====================================================
    LOAD ONLINE USERS
    =====================================================
    */

    const loadOnlineUsers = useCallback(async () => {

        try {

            const data =
                await getOnlineUsers();

            setOnlineUsers(data);

        } catch (error) {

            console.error(
                "ONLINE USERS ERROR:",
                error
            );

        }

    }, []);


    /*
    =====================================================
    INITIAL LOAD
    =====================================================
    */

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


    /*
    =====================================================
    ROOM CHANGE
    =====================================================
    */

    useEffect(() => {

        if (!selectedRoom) {

            return;

        }


        disconnectWebSocket();


        setMessages([]);

        setTypingUser("");


        /*
        LOAD OLD MESSAGES
        */

        loadMessages(selectedRoom);


        /*
        MARK OTHER USER MESSAGES AS SEEN
        */

        markMessagesAsSeen(selectedRoom)
            .catch((error) => {

                console.error(
                    "MARK SEEN ERROR:",
                    error
                );

            });


        /*
        WEBSOCKET
        */

        connectWebSocket(

            selectedRoom,


            /*
            NEW MESSAGE
            */

            (message) => {

                setMessages((previousMessages) => {

                    return [
                        ...previousMessages,
                        message
                    ];

                });

            },


            /*
            TYPING
            */

            (typingData) => {

                setTypingUser(

                    typingData.typing
                        ? typingData.email
                        : ""

                );

            },


            /*
            MESSAGE SEEN
            */

            (seenData) => {

                setMessages((previousMessages) => {

                    return previousMessages.map(
                        (message) => {

                            if (

                                message.senderEmail
                                    ?.trim()
                                    .toLowerCase() ===
                                currentUserEmail &&

                                message.roomCode ===
                                seenData.roomCode

                            ) {

                                return {

                                    ...message,

                                    seen: true

                                };

                            }

                            return message;

                        }

                    );

                });

            }

        );


        return () => {

            disconnectWebSocket();

        };


    }, [
        selectedRoom,
        loadMessages,
        currentUserEmail
    ]);


    /*
    =====================================================
    AUTO SCROLL
    =====================================================
    */

    useEffect(() => {

        bottomRef.current?.scrollIntoView({

            behavior: "smooth"

        });

    }, [
        messages
    ]);


    /*
    =====================================================
    SEND MESSAGE
    =====================================================
    */

    const handleSend = (text) => {

        if (

            !text ||
            !text.trim() ||
            !selectedRoom

        ) {

            return;

        }


        sendMessage({

            roomCode: selectedRoom,

            content: text

        });

    };


    /*
    =====================================================
    ROOM CREATED
    =====================================================
    */

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
                "ROOM REFRESH ERROR:",
                error
            );

        }

    };


    /*
    =====================================================
    USER SELECTED FOR PRIVATE CHAT
    =====================================================
    */

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
                "PRIVATE CHAT ERROR:",
                error
            );

        }

    };


    /*
    =====================================================
    LOGOUT
    =====================================================
    */

    const handleLogout = () => {

        disconnectWebSocket();

        logout();

        navigate("/login");

    };


    /*
    =====================================================
    SELECTED ROOM
    =====================================================
    */

    const selectedRoomData =
        rooms.find(
            (room) =>
                room.roomCode === selectedRoom
        );


    console.log(
        "SELECTED ROOM:",
        selectedRoomData
    );


    /*
    =====================================================
    ROOM TYPE
    =====================================================
    */

    const selectedRoomType =
        selectedRoomData?.roomType;


    /*
    =====================================================
    EXACT OPPONENT
    =====================================================
    */

    const opponent =

        selectedRoomType === "CHAT"

            ? selectedRoomData?.members?.find(
                (member) =>
                    member.email
                        ?.trim()
                        .toLowerCase() !==
                    currentUserEmail
            )

            : null;


    console.log(
        "CURRENT USER:",
        currentUserEmail
    );


    console.log(
        "OPPONENT:",
        opponent
    );


    /*
    =====================================================
    HEADER NAME
    =====================================================
    */

    const displayRoomName =

        selectedRoomType === "CHAT" &&
        opponent?.fullName

            ? opponent.fullName

            : selectedRoomData?.roomName ||
            selectedRoom;


    /*
    =====================================================
    ONLINE STATUS
    =====================================================
    */

    const isOpponentOnline =

        opponent
            ? onlineUsers.some(
                (user) =>
                    user.email
                        ?.trim()
                        .toLowerCase() ===
                    opponent.email
                        ?.trim()
                        .toLowerCase()
            )

            : false;


    /*
    =====================================================
    UI
    =====================================================
    */

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

                    setShowNewChat(true);

                }}

            />


            <div className="flex-1 flex flex-col min-w-0">


                {/* HEADER */}

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


                {/* MESSAGES */}

                <ChatWindow

                    messages={messages}

                    bottomRef={bottomRef}

                />


                {/* INPUT */}

                <ChatInput

                    onSend={handleSend}

                    roomCode={selectedRoom}

                />

            </div>


            {/* NEW CHAT MODAL */}

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