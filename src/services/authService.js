import axios from "axios";

const API_URL = import.meta.env.PROD
    ? "https://samvaad-0c2e.onrender.com"
    : "http://localhost:8083";

const API = `${API_URL}/api/auth`;

export const register = async (data) => {

    const response = await axios.post(
        `${API}/register`,
        data
    );

    return response.data;
};

export const login = async (data) => {

    const response = await axios.post(
        `${API}/login`,
        data
    );

    localStorage.setItem(
        "token",
        response.data.token
    );

    return response.data;
};

export const logout = () => {

    localStorage.removeItem("token");
};

export const getToken = () => {

    return localStorage.getItem("token");
};

export const getCurrentUser = () => {

    const token = localStorage.getItem("token");

    if (!token) {
        return null;
    }

    try {

        const payload = JSON.parse(
            atob(token.split(".")[1])
        );

        return payload.sub;

    } catch {

        return null;
    }
};