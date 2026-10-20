import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import useAuth from "../../hooks/useAuth";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value });
  const submit = async (event) => {
    event.preventDefault(); setError(""); setLoading(true);
    try {
      await login(form);
      navigate(location.state?.from?.pathname || "/dashboard", { replace: true });
    } catch (err) {
      setError(err.response?.status === 401 || err.response?.status === 403
        ? "Invalid username or password."
        : "Unable to sign in right now. Please try again.");
    } finally { setLoading(false); }
  };

  return (
    <section className="auth-card">
      <div className="auth-kicker">WELCOME BACK</div>
      <h1>Sign in to your<br /><span>workspace.</span></h1>
      <p className="text-secondary">Access your people operations dashboard.</p>
      {error && <div className="alert alert-danger py-2">{String(error)}</div>}
      <form onSubmit={submit} className="mt-4">
        <label className="form-label" htmlFor="username">Username or email</label>
        <input className="form-control form-control-dark mb-3" id="username" name="username" value={form.username} onChange={update} autoComplete="username" required />
        <label className="form-label" htmlFor="password">Password</label>
        <div className="input-group">
          <input className="form-control form-control-dark" id="password" name="password" type={showPassword ? "text" : "password"} value={form.password} onChange={update} autoComplete="current-password" required />
          <button className="btn btn-outline-light" type="button" aria-label={showPassword ? "Hide password" : "Show password"} aria-pressed={showPassword} onClick={() => setShowPassword((visible) => !visible)}>
            <i className={`bi ${showPassword ? "bi-eye-slash" : "bi-eye"}`} aria-hidden="true" />
          </button>
        </div>
        <div className="text-end mt-2"><Link className="subtle-link" to="/forgot-password">Forgot password?</Link></div>
        <button className="btn btn-gold w-100 mt-4" disabled={loading}>{loading ? "Signing in..." : "Sign in"} <i className="bi bi-arrow-up-right ms-1" /></button>
      </form>
      <p className="text-secondary small mt-4 mb-0">New to Employee360? <Link to="/register" className="subtle-link">Create an account</Link></p>
    </section>
  );
}
