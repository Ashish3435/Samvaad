import api from "../api/axios";

export const getMessages = async (roomCode) => {

    const response = await api.get(`/messages/${roomCode}`);

    return response.data;
};