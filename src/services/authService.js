import api from "../api/axios";

export const register = async (userData) => {
    const response = await api.post(
        "/auth/register",
        userData
    );

    return response.data;
};

export const login = async (credentials) => {
    const response = await api.post(
        "/auth/login",
        credentials
    );

    const data = response.data;

    localStorage.setItem(
        "token",
        data.token
    );

    return data;
};

export const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
};

export const getCurrentUser = () => {
    const token =
        localStorage.getItem("token");

    if (!token) {
        return null;
    }

    try {
        const payload =
            JSON.parse(
                atob(
                    token.split(".")[1]
                )
            );

        return payload.sub;

    } catch (error) {

        console.error(
            "JWT PARSE ERROR:",
            error
        );

        return null;
    }
};

export const isAuthenticated = () => {
    return Boolean(
        localStorage.getItem("token")
    );
};