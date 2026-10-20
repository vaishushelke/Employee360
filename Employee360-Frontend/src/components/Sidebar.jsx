import { NavLink } from "react-router-dom";
import useAuth from "../hooks/useAuth";

const staffRoles = ["ADMIN", "CEO", "HR", "MANAGER"];
const employeeItems = [
  { to: "/dashboard", label: "Dashboard", icon: "bi-grid-1x2" },
  { to: "/attendance", label: "My attendance", icon: "bi-clock-history" },
  { to: "/leaves", label: "My leaves", icon: "bi-calendar2-check" },
  { to: "/payments", label: "My payroll", icon: "bi-credit-card" },
  { to: "/reimbursements", label: "My reimbursements", icon: "bi-receipt" },
  { to: "/learning-catalog", label: "Learning", icon: "bi-mortarboard" },
  { to: "/quizzes", label: "Quizzes", icon: "bi-patch-question" },
  { to: "/notifications", label: "Notifications", icon: "bi-bell" }
];
const staffItems = [
  { to: "/dashboard", label: "Dashboard", icon: "bi-grid-1x2" },
  { to: "/employees", label: "Employees", icon: "bi-people" },
  { to: "/departments", label: "Departments", icon: "bi-diagram-3" },
  { to: "/leaves", label: "Leave management", icon: "bi-calendar2-check" },
  { to: "/attendance", label: "Attendance", icon: "bi-clock-history" },
  { to: "/payroll", label: "Payroll", icon: "bi-wallet2" },
  { to: "/payments", label: "Payments", icon: "bi-credit-card" },
  { to: "/documents", label: "Documents", icon: "bi-folder2" },
  { to: "/reimbursements", label: "Reimbursements", icon: "bi-receipt" },
  { to: "/quizzes", label: "Quizzes", icon: "bi-patch-question" },
  { to: "/learning-catalog", label: "Learning catalog", icon: "bi-mortarboard" },
  { to: "/reports", label: "Reports", icon: "bi-bar-chart" },
  { to: "/notifications", label: "Notifications", icon: "bi-bell" },
];

export default function Sidebar({ onNavigate }) {
  const { role } = useAuth();
  const normalizedRole = String(role || "").replace(/^ROLE_/, "").toUpperCase();
  const items = staffRoles.includes(normalizedRole) ? staffItems : employeeItems;
  return (
    <aside className="sidebar" aria-label="Main navigation">
      <NavLink to="/dashboard" className="brand">
        <span className="brand-mark">E<span>360</span></span>
        <span className="brand-copy"><strong>Employee360</strong><small>People operations</small></span>
      </NavLink>
      <div className="sidebar-label">WORKSPACE</div>
      <nav className="sidebar-nav">
        {items.map((item) => (
          <NavLink key={item.to} to={item.to} onClick={onNavigate} className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}>
            <i className={`bi ${item.icon}`} /><span>{item.label}</span>
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-bottom">
        <div className="sidebar-label">ACCOUNT</div>
        <NavLink to="/profile" onClick={onNavigate} className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}><i className="bi bi-person-circle" /><span>My profile</span></NavLink>
        <NavLink to="/settings" onClick={onNavigate} className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}><i className="bi bi-gear" /><span>Settings</span></NavLink>
        <div className="sidebar-role"><i className="bi bi-shield-check" /><span>Signed in as <strong>{role || "Employee"}</strong></span></div>
      </div>
    </aside>
  );
}
