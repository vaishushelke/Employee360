import { Link } from "react-router-dom";
import useNotifications from "../hooks/useNotifications";

export default function NotificationBell() {
  const { unreadCount } = useNotifications();
  return (
    <Link to="/notifications" className="notification-link" aria-label={`${unreadCount} unread notifications`}>
      <i className="bi bi-bell" />
      {unreadCount > 0 && <span className="notification-count">{unreadCount > 99 ? "99+" : unreadCount}</span>}
    </Link>
  );
}
