import { Link } from "react-router-dom";

export default function ForgotPassword() {
  return <section className="auth-card"><div className="auth-kicker">ACCOUNT RECOVERY</div><h1>Forgot your<br /><span>password?</span></h1><p className="text-secondary">Password recovery is not enabled by the current backend.</p>
    <div className="alert alert-warning py-2" role="status">The Spring Boot API does not currently provide a forgot-password or password-reset endpoint. Ask the backend maintainer to add a secure recovery flow before using this feature.</div>
    <p className="text-secondary small mt-4 mb-0"><Link to="/login" className="subtle-link">Back to sign in</Link></p>
  </section>;
}
