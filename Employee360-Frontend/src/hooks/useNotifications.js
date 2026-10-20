import { useCallback, useEffect, useState } from "react";
import api from "../api/api";
import useAuth from "./useAuth";

export default function useNotifications() {
  const { isAuthenticated } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const refresh = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const [listResponse, countResponse] = await Promise.all([
        api.get("/api/notifications"),
        api.get("/api/notifications/unread/count")
      ]);
      const list = Array.isArray(listResponse.data) ? listResponse.data : [];
      setNotifications(list);
      const countData = countResponse.data;
      setUnreadCount(typeof countData === "number" ? countData : Number(countData?.count ?? 0));
    } catch (requestError) {
      const data = requestError.response?.data;
      setError(typeof data === "string" ? data : data?.message || "Notifications could not be loaded.");
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    const timer = window.setTimeout(refresh, 0);
    return () => window.clearTimeout(timer);
  }, [refresh]);

  const markRead = async (id) => {
    await api.patch(`/api/notifications/${id}/read`);
    await refresh();
  };

  const markAllRead = async () => {
    await api.patch("/api/notifications/read-all");
    await refresh();
  };

  return { notifications, unreadCount, loading, error, refresh, markRead, markAllRead };
}
