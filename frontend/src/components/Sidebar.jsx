import RoomList from "./RoomList";
import OnlineUsers from "./OnlineUsers";


export default function Sidebar({

                                    rooms,

                                    selectedRoom,

                                    onSelectRoom,

                                    onlineUsers,

                                    onLogout,

                                    onRoomCreated,

                                    onNewChat,

                                }) {


    const chats = rooms.filter(

        (room) => room.roomType === "CHAT"

    );


    const groups = rooms.filter(

        (room) => room.roomType === "GROUP"

    );


    const channels = rooms.filter(

        (room) => room.roomType === "CHANNEL"

    );


    return (

        <div className="w-80 bg-white border-r flex flex-col">


            {/* Header */}

            <div className="p-5 border-b flex justify-between items-center">


                <h2 className="text-2xl font-bold text-blue-600">

                    Samvaad

                </h2>


                <button

                    onClick={onLogout}

                    className="text-red-500 font-semibold hover:text-red-700"

                >

                    Logout

                </button>


            </div>


            {/* Sidebar content */}

            <div className="flex-1 overflow-y-auto p-4">


                {/* Chats */}

                <RoomList

                    title="💬 Chats"

                    rooms={chats}

                    selectedRoom={selectedRoom}

                    onSelectRoom={onSelectRoom}

                    onNewChat={onNewChat}

                    onRoomCreated={onRoomCreated}

                />


                <div className="my-5 border-t"></div>


                {/* Groups */}

                <RoomList

                    title="👥 Groups"

                    rooms={groups}

                    selectedRoom={selectedRoom}

                    onSelectRoom={onSelectRoom}

                    onRoomCreated={onRoomCreated}

                />


                <div className="my-5 border-t"></div>


                {/* Channels */}

                <RoomList

                    title="📢 Channels"

                    rooms={channels}

                    selectedRoom={selectedRoom}

                    onSelectRoom={onSelectRoom}

                    onRoomCreated={onRoomCreated}

                />


                <div className="my-5 border-t"></div>


                {/* Online users */}

                <OnlineUsers

                    users={onlineUsers}

                />


            </div>


        </div>

    );

}