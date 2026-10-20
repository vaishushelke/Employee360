import { useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import NotificationBell from "./NotificationBell";

export default function Navbar({ onMenuClick }) {
  const { username, role, logout, setRole } = useAuth();
  const navigate = useNavigate();
  const signOut = () => { logout(); navigate("/login", { replace: true }); };
  const isAdmin = String(role || "").toUpperCase() === "ADMIN";

  return (
    <header className="topbar">
      <div>
        <button className="mobile-menu-button" type="button" onClick={onMenuClick} aria-label="Toggle navigation menu">
          <i className="bi bi-list" aria-hidden="true" />
        </button>
        <div className="topbar-eyebrow">EMPLOYEE MANAGEMENT</div>
        <div className="topbar-title">Workspace</div>
      </div>
      <div className="d-flex align-items-center gap-3">
        <NotificationBell />
        {isAdmin && (
          <select className="role-switcher" aria-label="Switch dashboard role" value={String(role || "ADMIN").toUpperCase()} onChange={(event) => setRole(event.target.value)}>
            <option value="ADMIN">Admin</option>
            <option value="HR">HR</option>
            <option value="MANAGER">Manager</option>
            <option value="EMPLOYEE">Employee</option>
          </select>
        )}
        <div className="user-chip d-none d-sm-flex">
          <span className="user-avatar">{(username || "U").slice(0, 1).toUpperCase()}</span>
          <span><strong>{username || "User"}</strong><small>{role || "Employee"}</small></span>
        </div>
        <button className="btn btn-sm btn-outline-light" onClick={signOut}><i className="bi bi-box-arrow-right me-1" />Logout</button>
      </div>
    </header>
  );
}
