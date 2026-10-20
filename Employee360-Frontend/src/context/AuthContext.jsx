import { useCallback, useEffect, useMemo, useState } from "react";
import api from "../api/api";
import AuthContext from "./AuthContextValue";

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("token"));
  const [username, setUsername] = useState(() => localStorage.getItem("username") || "");
  const [role, setRoleState] = useState(() => localStorage.getItem("role") || "");

  const setRole = useCallback((nextRole) => {
    const normalized = nextRole || "";
    if (normalized) {
      localStorage.setItem("role", normalized);
    } else {
      localStorage.removeItem("role");
    }
    setRoleState(normalized);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    setToken(null);
    setUsername("");
    setRoleState("");
  }, []);

  const login = useCallback(async (credentials) => {
    const response = await api.post("/auth/login", credentials);
    const data = response.data || {};
    if (!data.token) throw new Error("Login response did not contain a token.");
    localStorage.setItem("token", data.token);
    localStorage.setItem("username", data.username || credentials.username || "");
    localStorage.setItem("role", data.role || "");
    setToken(data.token);
    setUsername(data.username || credentials.username || "");
    setRoleState(data.role || "");
    return data;
  }, []);

  useEffect(() => {
    const onUnauthorized = () => logout();
    window.addEventListener("employee360:unauthorized", onUnauthorized);
    return () => window.removeEventListener("employee360:unauthorized", onUnauthorized);
  }, [logout]);

  const value = useMemo(() => ({
    token, username, role, isAuthenticated: Boolean(token), login, logout, setRole
  }), [token, username, role, login, logout, setRole]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
