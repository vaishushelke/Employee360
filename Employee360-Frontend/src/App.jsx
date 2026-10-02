import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Employees from "./pages/Employees";
import Departments from "./pages/Departments";
import Leaves from "./pages/Leaves";
import Attendance from "./pages/Attendance";
import Payroll from "./pages/Payroll";

function App() {

    return (
        <BrowserRouter>

            <Routes>

                {/* Login Page */}
                <Route
                    path="/login"
                    element={<Login />}
                />

                {/* Register Page */}
                <Route
                    path="/register"
                    element={<Register />}
                />

                {/* Dashboard */}
                <Route
                    path="/dashboard"
                    element={<Dashboard />}
                />

                {/* Employees */}
                <Route
                    path="/employees"
                    element={<Employees />}
                />

                {/* Departments */}
                <Route
                    path="/departments"
                    element={<Departments />}
                />

                {/* Leaves */}
                <Route
                    path="/leaves"
                    element={<Leaves />}
                />

                {/* Attendance */}
                <Route
                    path="/attendance"
                    element={<Attendance />}
                />

                {/* Payroll */}
                <Route
                    path="/payroll"
                    element={<Payroll />}
                />

                {/* Default Page */}
                <Route
                    path="/"
                    element={<Navigate to="/login" replace />}
                />

                {/* Unknown URL */}
                <Route
                    path="*"
                    element={<Navigate to="/login" replace />}
                />

            </Routes>

        </BrowserRouter>
    );
}

export default App;