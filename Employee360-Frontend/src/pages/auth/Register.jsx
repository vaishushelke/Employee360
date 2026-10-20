import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../../api/api";

const roleOptions = [
  { value: "EMPLOYEE", label: "Employee" },
  { value: "HR", label: "HR" },
  { value: "MANAGER", label: "Manager" },
  { value: "ADMIN", label: "Admin" }
];

function getError(error) {
  const data = error.response?.data;
  return typeof data === "string" ? data : data?.message || error.message || "Registration failed.";
}

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    email: "",
    role: "EMPLOYEE",
    password: "",
    confirmPassword: ""
  });
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState("");

  const update = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }));

  const requestOtp = async (email) => {
    setError("");
    setLoading(true);
    try {
      await api.post("/auth/send-otp", { email });
      navigate(`/verify-otp?email=${encodeURIComponent(email)}`);
    } catch (requestError) {
      setError(`Your account was created, but the verification code could not be sent. ${getError(requestError)}`);
    } finally {
      setLoading(false);
    }
  };

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setMessage("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      await api.post("/auth/register", {
        username: form.username.trim(),
        email: form.email.trim(),
        role: form.role,
        password: form.password
      });

      const email = form.email.trim();
      setRegisteredEmail(email);
      setMessage("Account created. Verify your email to finish registration.");
      await requestOtp(email);
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setLoading(false);
    }
  };

  return <section className="auth-card">
    <div className="auth-kicker">GET STARTED</div>
    <h1>Create your<br /><span>account.</span></h1>
    <p className="text-secondary">Choose your real role, create your account, and verify your email.</p>
    {error && <div className="alert alert-danger py-2" role="alert">{error}</div>}
    {message && <div className="alert alert-success py-2" role="status">{message}</div>}
    {registeredEmail ? (
      <button className="btn btn-gold w-100 mt-3" type="button" disabled={loading} onClick={() => requestOtp(registeredEmail)}>
        {loading ? "Sending code..." : "Send verification code again"}
      </button>
    ) : (
      <form onSubmit={submit} className="mt-4">
        <label className="form-label" htmlFor="username">Username</label>
        <input className="form-control form-control-dark mb-3" id="username" name="username" value={form.username} onChange={update} autoComplete="username" minLength="3" required />

        <label className="form-label" htmlFor="email">Work email</label>
        <input className="form-control form-control-dark mb-3" id="email" name="email" type="email" value={form.email} onChange={update} autoComplete="email" required />

        <label className="form-label" htmlFor="role">Role</label>
        <select className="form-select form-control-dark mb-3" id="role" name="role" value={form.role} onChange={update}>
          {roleOptions.map((role) => (
            <option key={role.value} value={role.value}>{role.label}</option>
          ))}
        </select>

        <label className="form-label" htmlFor="password">Password</label>
        <input className="form-control form-control-dark mb-3" id="password" name="password" type="password" minLength="6" value={form.password} onChange={update} autoComplete="new-password" required />

        <label className="form-label" htmlFor="confirmPassword">Confirm password</label>
        <input className="form-control form-control-dark" id="confirmPassword" name="confirmPassword" type="password" minLength="6" value={form.confirmPassword} onChange={update} autoComplete="new-password" required />

        <button className="btn btn-gold w-100 mt-4" disabled={loading}>{loading ? "Creating account..." : "Create account"}</button>
      </form>
    )}
    <p className="text-secondary small mt-4 mb-0">Already registered? <Link to="/login" className="subtle-link">Sign in</Link></p>
  </section>;
}
