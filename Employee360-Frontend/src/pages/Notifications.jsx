import { useState } from "react";
import { Link } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";
import useNotifications from "../hooks/useNotifications";

function showDate(value) {
  if (!value) return "Date unavailable";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? String(value) : date.toLocaleString();
}

export default function Notifications() {
  const { notifications, unreadCount, loading, error, markRead, markAllRead, refresh } = useNotifications();
  const [actionError, setActionError] = useState("");
  const [updating, setUpdating] = useState(false);

  const run = async (action) => {
    setActionError("");
    setUpdating(true);
    try {
      await action();
    } catch (requestError) {
      const data = requestError.response?.data;
      setActionError(typeof data === "string" ? data : data?.message || "The notification action could not be completed.");
    } finally {
      setUpdating(false);
    }
  };

  return <div className="page-enter">
    <div className="page-heading">
      <div><div className="eyebrow">WORKSPACE / UPDATES</div><h1>Notifications<span>.</span></h1><p>Stay informed about changes and requests.</p></div>
      <button className="btn btn-outline-secondary" type="button" onClick={() => run(markAllRead)} disabled={updating || unreadCount === 0}>Mark all read</button>
    </div>
    <section className="panel-card">
      <div className="panel-heading"><div><h2>All notifications</h2><p>{unreadCount} unread</p></div><button className="btn btn-sm btn-outline-secondary" type="button" onClick={() => run(refresh)} disabled={updating}>Refresh</button></div>
      {(error || actionError) && <div className="alert alert-danger py-2" role="alert">{actionError || error}</div>}
      {loading && <LoadingSpinner label="Loading notifications..." />}
      {!loading && !error && notifications.length === 0 && <div className="empty-state"><span className="empty-state-icon"><i className="bi bi-bell" /></span><strong>You’re all caught up</strong><p>No notifications were returned for your account.</p><Link to="/dashboard" className="btn btn-gold mt-2">Back to dashboard</Link></div>}
      {!loading && notifications.map((notification) => <article className={`notification-row ${notification.read ? "" : "notification-unread"}`} key={notification.id}>
        <span className="notification-row-icon"><i className={`bi ${notification.read ? "bi-check2-circle" : "bi-bell"}`} aria-hidden="true" /></span>
        <div className="flex-grow-1">
          <div className="d-flex align-items-center gap-2"><strong>{notification.title || "Notification"}</strong>{!notification.read && <span className="badge text-bg-warning">Unread</span>}</div>
          <p>{notification.message || "No details provided."}</p>
          <small>{showDate(notification.createdAt)}</small>
        </div>
        {!notification.read && <button className="btn btn-sm btn-outline-secondary" type="button" disabled={updating} onClick={() => run(() => markRead(notification.id))}>Mark read</button>}
      </article>)}
    </section>
  </div>;
}
