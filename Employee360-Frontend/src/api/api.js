import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const isAuthRequest = [
    "/auth/login", "/auth/register", "/auth/forgot-password",
    "/auth/reset-password", "/auth/send-otp", "/auth/verify-otp"
  ].some((path) => config.url?.startsWith(path));

  if (!isAuthRequest) {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const url = error.config?.url || "";
    const isAuthRequest = ["/auth/login", "/auth/register", "/auth/forgot-password",
      "/auth/reset-password", "/auth/send-otp", "/auth/verify-otp"
    ].some((path) => url.startsWith(path));

    if (error.response?.status === 401 && !isAuthRequest) {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      localStorage.removeItem("role");
      window.dispatchEvent(new Event("employee360:unauthorized"));
    }
    return Promise.reject(error);
  }
);

export default api;
