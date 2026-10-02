
import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },

});

// ==========================================
// REQUEST INTERCEPTOR
// ==========================================
api.interceptors.request.use(
    (config) => {

        const isAuthRequest =
            config.url === "/auth/login" ||
            config.url === "/auth/register";

        if (!isAuthRequest) {

            const token = localStorage.getItem("token");

            if (token) {
                config.headers.Authorization =
                    `Bearer ${token}`;
            }
        }

        return config;
    },
    (error) => Promise.reject(error)
);


export default api;

