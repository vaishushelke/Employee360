import { Link } from "react-router-dom";

export default function ResetPassword() {
  return <section className="auth-card">
    <div className="auth-kicker">SECURE YOUR ACCOUNT</div>
    <h1>Set a new<br /><span>password.</span></h1>
    <p className="text-secondary">Password reset is not enabled by the current backend.</p>
    <div className="alert alert-warning py-2" role="status">The Spring Boot API does not currently provide a password-reset endpoint or reset-token workflow. This page will not submit a request that the backend cannot handle.</div>
    <p className="text-secondary small mt-4 mb-0"><Link to="/login" className="subtle-link">Back to sign in</Link></p>
  </section>;
}
