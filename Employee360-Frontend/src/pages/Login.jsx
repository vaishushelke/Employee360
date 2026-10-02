import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/auth.css";

function Login() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();


    // ==========================================
    // LOGIN
    // ==========================================

    const handleLogin = async (e) => {

        e.preventDefault();

        if (!username || !password) {

            alert("Please enter username and password.");

            return;
        }


        try {

            const response = await api.post(
                "/auth/login",
                {
                    username: username,
                    password: password
                }
            );


            // ==========================================
            // RESPONSE DATA
            // ==========================================

            const data = response.data;

            const token = data.token;
            const loggedInUsername = data.username;
            const role = data.role;


            if (!token) {

                alert(
                    "Login failed. Token not received."
                );

                return;
            }


            // ==========================================
            // SAVE LOGIN INFORMATION
            // ==========================================

            localStorage.setItem(
                "token",
                token
            );

            localStorage.setItem(
                "username",
                loggedInUsername
            );

            localStorage.setItem(
                "role",
                role
            );


            console.log(
                "Login successful"
            );

            console.log(
                "Username:",
                loggedInUsername
            );

            console.log(
                "Role:",
                role
            );


            alert("Login successful!");


            // ==========================================
            // GO TO DASHBOARD
            // ==========================================

            navigate("/dashboard");


        } catch (error) {

            console.error(
                "Login Error:",
                error
            );


            if (error.response) {

                if (
                    error.response.status === 401
                ) {

                    alert(
                        "Invalid username or password."
                    );

                }

                else if (
                    error.response.status === 403
                ) {

                    alert(
                        "Access denied. Please check your login details."
                    );

                }

                else {

                    alert(
                        "Login failed: " +
                        (
                            error.response.data ||
                            "Something went wrong."
                        )
                    );
                }

            }

            else {

                alert(
                    "Cannot connect to backend. " +
                    "Please make sure Spring Boot is running."
                );
            }
        }
    };


    return (

        <div className="auth-page">

            <div className="auth-container">


                {/* ==========================================
                    LEFT SIDE
                ========================================== */}

                <div className="auth-left">

                    <div className="left-content">


                        {/* Logo */}

                        <div className="brand">

                            <div className="brand-icon">

                                <i className="bi bi-people-fill"></i>

                            </div>


                            <div>

                                <h2>
                                    Employee<span>360</span>
                                </h2>

                                <p>
                                    Smart HR. Better Tomorrow.
                                </p>

                            </div>

                        </div>


                        {/* Heading */}

                        <div className="hero-content">

                            <p className="small-heading">
                                EMPLOYEE MANAGEMENT SYSTEM
                            </p>


                            <h1>

                                Manage Your
                                <br />

                                Workforce
                                <br />

                                <span>
                                    with Ease
                                </span>

                            </h1>


                            <p className="hero-description">

                                Simplify HR processes and build
                                a better workplace with Employee360.

                            </p>

                        </div>


                        {/* Features */}

                        <div className="feature-list">


                            {/* Employees */}

                            <div className="feature-item">

                                <div className="feature-icon">

                                    <i className="bi bi-people"></i>

                                </div>


                                <div>

                                    <h4>
                                        Employees
                                    </h4>

                                    <p>
                                        Manage your team
                                    </p>

                                </div>

                            </div>


                            {/* Attendance */}

                            <div className="feature-item">

                                <div className="feature-icon">

                                    <i className="bi bi-calendar-check"></i>

                                </div>


                                <div>

                                    <h4>
                                        Attendance
                                    </h4>

                                    <p>
                                        Track daily attendance
                                    </p>

                                </div>

                            </div>


                            {/* Leave */}

                            <div className="feature-item">

                                <div className="feature-icon">

                                    <i className="bi bi-calendar-event"></i>

                                </div>


                                <div>

                                    <h4>
                                        Leave Management
                                    </h4>

                                    <p>
                                        Handle leave requests
                                    </p>

                                </div>

                            </div>


                            {/* Payroll */}

                            <div className="feature-item">

                                <div className="feature-icon">

                                    <i className="bi bi-wallet2"></i>

                                </div>


                                <div>

                                    <h4>
                                        Payroll
                                    </h4>

                                    <p>
                                        Simplify payroll process
                                    </p>

                                </div>

                            </div>


                            {/* Reports */}

                            <div className="feature-item">

                                <div className="feature-icon">

                                    <i className="bi bi-bar-chart"></i>

                                </div>


                                <div>

                                    <h4>
                                        Reports
                                    </h4>

                                    <p>
                                        Get detailed reports
                                    </p>

                                </div>

                            </div>

                        </div>

                    </div>


                    {/* Decorative circles */}

                    <div className="left-circle circle-one"></div>

                    <div className="left-circle circle-two"></div>

                </div>


                {/* ==========================================
                    RIGHT SIDE
                ========================================== */}

                <div className="auth-right">


                    {/* Top Register */}

                    <div className="register-top">

                        <span>
                            New Employee?
                        </span>


                        <button
                            onClick={() =>
                                navigate("/register")
                            }
                            className="create-account-small"
                        >

                            Create Account

                            <i className="bi bi-arrow-right"></i>

                        </button>

                    </div>


                    {/* Login Content */}

                    <div className="login-content">


                        {/* Welcome */}

                        <div className="welcome-text">

                            <p className="welcome-small">
                                EMPLOYEE360 PORTAL
                            </p>


                            <h1>
                                Welcome Back!
                            </h1>


                            <p>
                                Sign in to your Employee360
                                account to continue.
                            </p>

                        </div>


                        {/* Login Form */}

                        <form onSubmit={handleLogin}>


                            {/* Username */}

                            <div className="input-group-custom">

                                <i className="bi bi-person"></i>


                                <input
                                    type="text"
                                    placeholder="Email or Username"
                                    value={username}
                                    onChange={(e) =>
                                        setUsername(
                                            e.target.value
                                        )
                                    }
                                />

                            </div>


                            {/* Password */}

                            <div className="input-group-custom">

                                <i className="bi bi-lock"></i>


                                <input
                                    type="password"
                                    placeholder="Password"
                                    value={password}
                                    onChange={(e) =>
                                        setPassword(
                                            e.target.value
                                        )
                                    }
                                />


                                <i className="bi bi-eye password-eye"></i>

                            </div>


                            {/* Remember */}

                            <div className="login-options">

                                <label>

                                    <input
                                        type="checkbox"
                                    />

                                    <span>
                                        Remember me
                                    </span>

                                </label>


                                <button
                                    type="button"
                                    className="forgot-btn"
                                >

                                    Forgot Password?

                                </button>

                            </div>


                            {/* Login Button */}

                            <button
                                type="submit"
                                className="login-button"
                            >

                                <span>

                                    <i className="bi bi-arrow-right"></i>

                                </span>

                                Login

                            </button>

                        </form>


                        {/* OR */}

                        <div className="or-divider">

                            <span></span>

                            <p>
                                OR
                            </p>

                            <span></span>

                        </div>


                        {/* Create Account */}

                        <button
                            className="create-account-button"
                            onClick={() =>
                                navigate("/register")
                            }
                        >

                            <i className="bi bi-person-plus"></i>

                            Create New Account

                        </button>


                        {/* Register */}

                        <p className="register-text">

                            New to Employee360?


                            <button
                                onClick={() =>
                                    navigate("/register")
                                }
                            >

                                Register here

                                <i className="bi bi-arrow-right"></i>

                            </button>

                        </p>


                        {/* Security */}

                        <div className="security-text">

                            <i className="bi bi-shield-check"></i>

                            <span>
                                Secure
                            </span>

                            <b>
                                •
                            </b>

                            <span>
                                Fast
                            </span>

                            <b>
                                •
                            </b>

                            <span>
                                Reliable
                            </span>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Login;