import { Navigate, Outlet } from "react-router-dom";
import useAuth from "../hooks/useAuth";

const normalizeRole = (role) => String(role || "").replace(/^ROLE_/, "").toUpperCase();

export default function RoleRoute({ allowedRoles = [], children }) {
  const { role } = useAuth();
  if (!allowedRoles.length) return <Outlet />;
  return allowedRoles.map(normalizeRole).includes(normalizeRole(role))
    ? children || <Outlet />
    : <Navigate to="/dashboard" replace />;
}
