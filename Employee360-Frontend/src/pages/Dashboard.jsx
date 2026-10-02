
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../services/api";
import "../styles/dashboard.css";

function Dashboard() {

    const navigate = useNavigate();

    const [employeeCount, setEmployeeCount] = useState(0);
    const [departmentCount, setDepartmentCount] = useState(0);
    const [leaveCount, setLeaveCount] = useState(0);
    const [payrollCount, setPayrollCount] = useState(0);

    // ================= GREETING =================

    const [greeting, setGreeting] = useState("Good Morning");

    useEffect(() => {

        const updateGreeting = () => {

            const hour = new Date().getHours();

            if (hour < 12) {

                setGreeting("Good Morning");

            } else if (hour < 17) {

                setGreeting("Good Afternoon");

            } else {

                setGreeting("Good Evening");

            }
        };

        updateGreeting();

        // Check every minute
        const greetingTimer = setInterval(
            updateGreeting,
            60000
        );

        return () => {
            clearInterval(greetingTimer);
        };

    }, []);


    // ================= LOAD DASHBOARD DATA =================
const loadDashboardData = async () => {
    console.log("========== DASHBOARD DATA ==========");

    try {
        const response = await api.get("/Employee/getAll");
        console.log("EMPLOYEES STATUS:", response.status);
        console.log("EMPLOYEES DATA:", response.data);

        if (Array.isArray(response.data)) {
            setEmployeeCount(response.data.length);
        }
    } catch (error) {
        console.error("EMPLOYEE ERROR:", error.response?.status);
        console.error("EMPLOYEE RESPONSE:", error.response?.data);
    }

    try {
        const response = await api.get("/Department/getalld");
        console.log("DEPARTMENT STATUS:", response.status);
        console.log("DEPARTMENT DATA:", response.data);

        if (Array.isArray(response.data)) {
            setDepartmentCount(response.data.length);
        }
    } catch (error) {
        console.error("DEPARTMENT ERROR:", error.response?.status);
        console.error("DEPARTMENT RESPONSE:", error.response?.data);
    }

    try {
        const response = await api.get("/Leave/getAll");
        console.log("LEAVE STATUS:", response.status);
        console.log("LEAVE DATA:", response.data);

        if (Array.isArray(response.data)) {
            setLeaveCount(response.data.length);
        }
    } catch (error) {
        console.error("LEAVE ERROR:", error.response?.status);
        console.error("LEAVE RESPONSE:", error.response?.data);
    }

    try {
        const response = await api.get("/Payroll/getAll");
        console.log("PAYROLL STATUS:", response.status);
        console.log("PAYROLL DATA:", response.data);

        if (Array.isArray(response.data)) {
            setPayrollCount(response.data.length);
        }
    } catch (error) {
        console.error("PAYROLL ERROR:", error.response?.status);
        console.error("PAYROLL RESPONSE:", error.response?.data);
    }
};


    useEffect(() => {
        loadDashboardData();
    }, []);

    // ================= LOGOUT =================

    const logout = () => {

        localStorage.removeItem("token");
        localStorage.removeItem("username");
        localStorage.removeItem("role");

        navigate("/login");

    };


    return (

        <div className="dashboard-page">

            {/* ================= SIDEBAR ================= */}

            <aside className="dashboard-sidebar">

                <div className="sidebar-brand">

                    <div className="sidebar-logo">

                        <i className="bi bi-people-fill"></i>

                    </div>

                    <div>

                        <h3>
                            Employee<span>360</span>
                        </h3>

                        <small>
                            HR Management
                        </small>

                    </div>

                </div>


                <div className="sidebar-menu">

                    <p className="menu-title">
                        MAIN MENU
                    </p>


                    <button
                        className="sidebar-link active"
                        onClick={() =>
                            navigate("/dashboard")
                        }
                    >

                        <i className="bi bi-grid-1x2-fill"></i>

                        Dashboard

                    </button>


                    <button
                        className="sidebar-link"
                        onClick={() =>
                            navigate("/employees")
                        }
                    >

                        <i className="bi bi-people"></i>

                        Employees

                    </button>


                    <button
                        className="sidebar-link"
                        onClick={() =>
                            navigate("/departments")
                        }
                    >

                        <i className="bi bi-building"></i>

                        Departments

                    </button>


                    <button
                        className="sidebar-link"
                        onClick={() =>
                            navigate("/leaves")
                        }
                    >

                        <i className="bi bi-calendar-event"></i>

                        Leaves

                    </button>


                    <button
                        className="sidebar-link"
                        onClick={() =>
                            navigate("/attendance")
                        }
                    >

                        <i className="bi bi-calendar-check"></i>

                        Attendance

                    </button>


                    <button
                        className="sidebar-link"
                        onClick={() =>
                            navigate("/payroll")
                        }
                    >

                        <i className="bi bi-wallet2"></i>

                        Payroll

                    </button>

                </div>


                <div className="sidebar-bottom">

                    <button
                        className="logout-button"
                        onClick={logout}
                    >

                        <i className="bi bi-box-arrow-left"></i>

                        Logout

                    </button>

                </div>

            </aside>


            {/* ================= MAIN CONTENT ================= */}

            <main className="dashboard-main">


                {/* ================= TOP NAVBAR ================= */}

                <header className="dashboard-header">

                    <div>

                        <h2>
                            Dashboard
                        </h2>

                        <p>
                            Employee360 Management Portal
                        </p>

                    </div>


                    <div className="header-right">

                        <button className="notification-button">

                            <i className="bi bi-bell"></i>

                            <span></span>

                        </button>


                        <div className="user-profile">

                            <div className="user-avatar">

                                <i className="bi bi-person"></i>

                            </div>

                            <div>

                                <strong>
                                    {localStorage.getItem("username") || "User"}
                                </strong>

                                <small>
                                    {
                                        localStorage.getItem("role") === "HR"
                                            ? "HR / Manager"
                                            : localStorage.getItem("role") || "Employee"
                                    }
                                </small>

                            </div>

                        </div>

                    </div>

                </header>


                {/* ================= CONTENT ================= */}

                <section className="dashboard-content">


                    {/* ================= WELCOME ================= */}

                    <div className="dashboard-welcome">

                        <div>

                            <p>
                                EMPLOYEE360 PORTAL
                            </p>

                            <h1>
                                {greeting} 👋
                            </h1>

                            <span>
                                Here's what's happening
                                in your organization today.
                            </span>

                        </div>


                        <div className="welcome-icon">

                            <i className="bi bi-bar-chart-line"></i>

                        </div>

                    </div>


                    {/* ================= STAT CARDS ================= */}

                    <div className="stats-grid">


                        {/* Employees */}

                        <div
                            className="stat-card"
                            onClick={() =>
                                navigate("/employees")
                            }
                        >

                            <div className="stat-icon">

                                <i className="bi bi-people"></i>

                            </div>

                            <div>

                                <p>
                                    Total Employees
                                </p>

                                <h2>
                                    {employeeCount}
                                </h2>

                                <small>

                                    View employees

                                    <i className="bi bi-arrow-right"></i>

                                </small>

                            </div>

                        </div>


                        {/* Departments */}

                        <div
                            className="stat-card"
                            onClick={() =>
                                navigate("/departments")
                            }
                        >

                            <div className="stat-icon">

                                <i className="bi bi-building"></i>

                            </div>

                            <div>

                                <p>
                                    Departments
                                </p>

                                <h2>
                                    {departmentCount}
                                </h2>

                                <small>

                                    View departments

                                    <i className="bi bi-arrow-right"></i>

                                </small>

                            </div>

                        </div>


                        {/* Leaves */}

                        <div
                            className="stat-card"
                            onClick={() =>
                                navigate("/leaves")
                            }
                        >

                            <div className="stat-icon">

                                <i className="bi bi-calendar-event"></i>

                            </div>

                            <div>

                                <p>
                                    Leave Requests
                                </p>

                                <h2>
                                    {leaveCount}
                                </h2>

                                <small>

                                    Manage leaves

                                    <i className="bi bi-arrow-right"></i>

                                </small>

                            </div>

                        </div>


                        {/* Payroll */}

                        <div
                            className="stat-card"
                            onClick={() =>
                                navigate("/payroll")
                            }
                        >

                            <div className="stat-icon">

                                <i className="bi bi-wallet2"></i>

                            </div>

                            <div>

                                <p>
                                    Payroll Records
                                </p>

                                <h2>
                                    {payrollCount}
                                </h2>

                                <small>

                                    View payroll

                                    <i className="bi bi-arrow-right"></i>

                                </small>

                            </div>

                        </div>

                    </div>


                    {/* ================= QUICK ACTIONS ================= */}

                    <div className="section-heading">

                        <div>

                            <p>
                                QUICK ACCESS
                            </p>

                            <h2>
                                Manage Your Workforce
                            </h2>

                        </div>

                    </div>


                    <div className="quick-grid">


                        <button
                            onClick={() =>
                                navigate("/employees")
                            }
                            className="quick-card"
                        >

                            <i className="bi bi-person-plus"></i>

                            <div>

                                <h3>
                                    Employees
                                </h3>

                                <p>
                                    Add and manage employees
                                </p>

                            </div>

                            <i className="bi bi-arrow-right"></i>

                        </button>


                        <button
                            onClick={() =>
                                navigate("/attendance")
                            }
                            className="quick-card"
                        >

                            <i className="bi bi-calendar-check"></i>

                            <div>

                                <h3>
                                    Attendance
                                </h3>

                                <p>
                                    Track employee attendance
                                </p>

                            </div>

                            <i className="bi bi-arrow-right"></i>

                        </button>


                        <button
                            onClick={() =>
                                navigate("/leaves")
                            }
                            className="quick-card"
                        >

                            <i className="bi bi-calendar2-check"></i>

                            <div>

                                <h3>
                                    Leave Management
                                </h3>

                                <p>
                                    Review employee leaves
                                </p>

                            </div>

                            <i className="bi bi-arrow-right"></i>

                        </button>


                        <button
                            onClick={() =>
                                navigate("/payroll")
                            }
                            className="quick-card"
                        >

                            <i className="bi bi-cash-stack"></i>

                            <div>

                                <h3>
                                    Payroll
                                </h3>

                                <p>
                                    Manage salary records
                                </p>

                            </div>

                            <i className="bi bi-arrow-right"></i>

                        </button>

                    </div>


                </section>

            </main>

        </div>

    );

}

export default Dashboard;
