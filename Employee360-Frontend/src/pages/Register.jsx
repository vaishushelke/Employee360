import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/auth.css";

function Register() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [role, setRole] = useState("EMPLOYEE");

    const navigate = useNavigate();

    const handleRegister = async (e) => {

        e.preventDefault();

        if (!username || !password || !confirmPassword) {
            alert("Please fill all required fields.");
            return;
        }

        if (password !== confirmPassword) {
            alert("Password and Confirm Password do not match.");
            return;
        }

        try {

            const response = await api.post("/auth/register", {
                username: username,
                password: password,
                role: role
            });

            console.log("Register Response:", response.data);

            alert("Registration successful! Please login.");

            navigate("/login");

        } catch (error) {

            console.error("Registration Error:", error);

            if (error.response) {

                if (error.response.status === 400) {
                    alert(
                        error.response.data ||
                        "Username already exists."
                    );
                } else {
                    alert(
                        "Registration failed: " +
                        (error.response.data ||
                            "Something went wrong.")
                    );
                }

            } else {

                alert(
                    "Cannot connect to backend. " +
                    "Please make sure Spring Boot is running."
                );
            }
        }
    };

    return (

        <div className="auth-page">

            <div className="auth-container register-container">

                {/* ================= LEFT SIDE ================= */}

                <div className="auth-left register-left">

                    <div className="left-content">

                        {/* Brand */}

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


                        {/* Register Hero */}

                        <div className="hero-content">

                            <p className="small-heading">
                                JOIN EMPLOYEE360
                            </p>

                            <h1>
                                Build Your
                                <br />
                                <span>HR Journey</span>
                            </h1>

                            <p className="hero-description">
                                Create your Employee360 account
                                and access a smarter employee
                                management experience.
                            </p>

                        </div>


                        {/* Benefits */}

                        <div className="feature-list">

                            <div className="feature-item">

                                <div className="feature-icon">
                                    <i className="bi bi-shield-check"></i>
                                </div>

                                <div>
                                    <h4>Secure Account</h4>
                                    <p>
                                        Your account is protected
                                    </p>
                                </div>

                            </div>


                            <div className="feature-item">

                                <div className="feature-icon">
                                    <i className="bi bi-lightning-charge"></i>
                                </div>

                                <div>
                                    <h4>Fast Access</h4>
                                    <p>
                                        Simple and quick login
                                    </p>
                                </div>

                            </div>


                            <div className="feature-item">

                                <div className="feature-icon">
                                    <i className="bi bi-person-check"></i>
                                </div>

                                <div>
                                    <h4>Employee Portal</h4>
                                    <p>
                                        Manage your HR activities
                                    </p>
                                </div>

                            </div>


                            <div className="feature-item">

                                <div className="feature-icon">
                                    <i className="bi bi-graph-up"></i>
                                </div>

                                <div>
                                    <h4>Better Management</h4>
                                    <p>
                                        Everything in one place
                                    </p>
                                </div>

                            </div>

                        </div>

                    </div>

                    <div className="left-circle circle-one"></div>
                    <div className="left-circle circle-two"></div>

                </div>


                {/* ================= RIGHT SIDE ================= */}

                <div className="auth-right">

                    {/* Top Login */}

                    <div className="register-top">

                        <span>
                            Already have an account?
                        </span>

                        <button
                            className="create-account-small"
                            onClick={() => navigate("/login")}
                        >
                            Login
                            <i className="bi bi-arrow-right"></i>
                        </button>

                    </div>


                    {/* Register Form */}

                    <div className="login-content">

                        <div className="welcome-text">

                            <p className="welcome-small">
                                EMPLOYEE360 PORTAL
                            </p>

                            <h1>
                                Create Account
                            </h1>

                            <p>
                                Create your account to get
                                started with Employee360.
                            </p>

                        </div>


                        <form onSubmit={handleRegister}>

                            {/* Username */}

                            <div className="input-group-custom">

                                <i className="bi bi-person"></i>

                                <input
                                    type="text"
                                    placeholder="Enter Username"
                                    value={username}
                                    onChange={(e) =>
                                        setUsername(e.target.value)
                                    }
                                />

                            </div>


                            {/* Password */}

                            <div className="input-group-custom">

                                <i className="bi bi-lock"></i>

                                <input
                                    type="password"
                                    placeholder="Create Password"
                                    value={password}
                                    onChange={(e) =>
                                        setPassword(e.target.value)
                                    }
                                />

                            </div>


                            {/* Confirm Password */}

                            <div className="input-group-custom">

                                <i className="bi bi-shield-lock"></i>

                                <input
                                    type="password"
                                    placeholder="Confirm Password"
                                    value={confirmPassword}
                                    onChange={(e) =>
                                        setConfirmPassword(e.target.value)
                                    }
                                />

                            </div>


                            {/* Role */}

                            <div className="input-group-custom">

                                <i className="bi bi-person-badge"></i>

                                <select
                                    value={role}
                                    onChange={(e) =>
                                        setRole(e.target.value)
                                    }
                                >

                                    <option value="EMPLOYEE">
                                        Employee / User
                                    </option>

                                    <option value="HR">
                                        HR / Manager
                                    </option>

                                    <option value="ADMIN">
                                        Admin
                                    </option>

                                </select>

                            </div>


                            {/* Terms */}

                            <div className="terms-row">

                                <input
                                    type="checkbox"
                                    required
                                />

                                <span>
                                    I agree to the Employee360
                                    terms and conditions.
                                </span>

                            </div>


                            {/* Register */}

                            <button
                                type="submit"
                                className="login-button"
                            >

                                <span>
                                    <i className="bi bi-person-plus"></i>
                                </span>

                                Create Account

                            </button>

                        </form>


                        {/* Login link */}

                        <p className="register-text">

                            Already registered?

                            <button
                                onClick={() => navigate("/login")}
                            >
                                Login here
                                <i className="bi bi-arrow-right"></i>
                            </button>

                        </p>


                        {/* Security */}

                        <div className="security-text">

                            <i className="bi bi-shield-check"></i>

                            <span>Secure</span>

                            <b>•</b>

                            <span>Fast</span>

                            <b>•</b>

                            <span>Reliable</span>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Register;