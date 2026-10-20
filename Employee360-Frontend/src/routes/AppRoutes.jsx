import { Navigate, Route, Routes } from "react-router-dom";
import ProtectedRoute from "../components/ProtectedRoute";
import RoleRoute from "./RoleRoute";
import AuthLayout from "../layouts/AuthLayout";
import MainLayout from "../layouts/MainLayout";
import Login from "../pages/auth/Login";
import Register from "../pages/auth/Register";
import VerifyOTP from "../pages/auth/VerifyOTP";
import ForgotPassword from "../pages/auth/ForgotPassword";
import ResetPassword from "../pages/auth/ResetPassword";
import Dashboard from "../pages/Dashboard";
import Employees from "../pages/Employees";
import Departments from "../pages/Departments";
import Leaves from "../pages/Leaves";
import Attendance from "../pages/Attendance";
import Payroll from "../pages/Payroll";
import Payments from "../pages/Payments";
import Notifications from "../pages/Notifications";
import Documents from "../pages/Documents";
import Reimbursements from "../pages/Reimbursements";
import Quizzes from "../pages/Quizzes";
import LearningCatalog from "../pages/LearningCatalog";
import Reports from "../pages/Reports";
import Profile from "../pages/Profile";
import Settings from "../pages/Settings";
import NotFound from "../pages/NotFound";

const staffRoles = ["ADMIN", "CEO", "HR", "MANAGER"];

export default function AppRoutes() {
  return <Routes>
    <Route element={<AuthLayout />}>
      <Route path="/login" element={<Login />} /><Route path="/register" element={<Register />} />
      <Route path="/verify-otp" element={<VerifyOTP />} />
      <Route path="/forgot-password" element={<ForgotPassword />} /><Route path="/reset-password" element={<ResetPassword />} />
    </Route>
    <Route element={<ProtectedRoute />}>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="employees" element={<RoleRoute allowedRoles={staffRoles}><Employees /></RoleRoute>} />
        <Route path="departments" element={<RoleRoute allowedRoles={staffRoles}><Departments /></RoleRoute>} />
        <Route path="leaves" element={<Leaves />} />
        <Route path="attendance" element={<RoleRoute allowedRoles={[...staffRoles, "EMPLOYEE"]}><Attendance /></RoleRoute>} />
        <Route path="payroll" element={<Payroll />} />
        <Route path="payments" element={<Payments />} /><Route path="notifications" element={<Notifications />} />
        <Route path="documents" element={<RoleRoute allowedRoles={staffRoles}><Documents /></RoleRoute>} />
        <Route path="reimbursements" element={<Reimbursements />} />
        <Route path="quizzes" element={<Quizzes />} />
        <Route path="learning-catalog" element={<LearningCatalog />} />
        <Route path="reports" element={<RoleRoute allowedRoles={staffRoles}><Reports /></RoleRoute>} />
        <Route path="profile" element={<Profile />} /><Route path="settings" element={<Settings />} />
      </Route>
    </Route>
    <Route path="*" element={<NotFound />} />
  </Routes>;
}
