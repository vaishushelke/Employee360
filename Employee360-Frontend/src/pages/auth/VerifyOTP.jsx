import { useEffect, useRef, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import api from "../../api/api";

const OTP_LENGTH = 6;

function getError(error) {
  const data = error.response?.data;
  return typeof data === "string" ? data : data?.message || error.message || "Email verification failed.";
}

export default function VerifyOTP() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const email = params.get("email") || "";
  const [digits, setDigits] = useState(Array(OTP_LENGTH).fill(""));
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [countdown, setCountdown] = useState(0);
  const inputs = useRef([]);

  useEffect(() => {
    if (countdown <= 0) return undefined;
    const timer = window.setTimeout(() => setCountdown((current) => current - 1), 1000);
    return () => window.clearTimeout(timer);
  }, [countdown]);

  const fillCode = (value, startIndex = 0) => {
    const chars = value.replace(/\D/g, "").slice(0, OTP_LENGTH);
    setDigits((current) => {
      const next = [...current];
      for (let index = 0; index < chars.length && startIndex + index < OTP_LENGTH; index += 1) {
        next[startIndex + index] = chars[index];
      }
      return next;
    });
    inputs.current[Math.min(startIndex + chars.length, OTP_LENGTH - 1)]?.focus();
  };

  const verify = async (event) => {
    event.preventDefault();
    setError("");
    setMessage("");
    const otp = digits.join("");
    if (!email) {
      setError("The verification email is missing. Return to registration and request a new code.");
      return;
    }
    if (otp.length !== OTP_LENGTH) {
      setError("Enter all six digits from the verification email.");
      return;
    }
    setLoading(true);
    try {
      await api.post("/auth/verify-otp", { email, otp });
      setMessage("Email verified. Redirecting to sign in...");
      window.setTimeout(() => navigate("/login", { replace: true }), 900);
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setLoading(false);
    }
  };

  const resend = async () => {
    setError("");
    setMessage("");
    setLoading(true);
    try {
      await api.post("/auth/send-otp", { email });
      setCountdown(30);
      setMessage("A new verification code was sent.");
    } catch (requestError) {
      setError(getError(requestError));
    } finally {
      setLoading(false);
    }
  };

  return <section className="auth-card">
    <div className="auth-kicker">EMAIL VERIFICATION</div>
    <h1>Verify your<br /><span>email.</span></h1>
    <p className="text-secondary">Enter the six-digit code sent to {email || "your email address"}.</p>
    {error && <div className="alert alert-danger py-2" role="alert">{error}</div>}
    {message && <div className="alert alert-success py-2" role="status">{message}</div>}
    <form onSubmit={verify} className="mt-4">
      <label className="form-label" htmlFor="otp-0">Verification code</label>
      <div className="otp-inputs">
        {digits.map((digit, index) => <input
          key={index}
          ref={(element) => { inputs.current[index] = element; }}
          id={`otp-${index}`}
          className="form-control form-control-dark otp-input"
          type="text"
          inputMode="numeric"
          autoComplete={index === 0 ? "one-time-code" : "off"}
          aria-label={`Verification code digit ${index + 1}`}
          maxLength="1"
          value={digit}
          onChange={(event) => fillCode(event.target.value, index)}
          onKeyDown={(event) => {
            if (event.key === "Backspace" && !digits[index] && index > 0) inputs.current[index - 1]?.focus();
            if (event.key === "ArrowLeft" && index > 0) inputs.current[index - 1]?.focus();
            if (event.key === "ArrowRight" && index < OTP_LENGTH - 1) inputs.current[index + 1]?.focus();
          }}
          onPaste={(event) => {
            event.preventDefault();
            fillCode(event.clipboardData.getData("text"), index);
          }}
          required
        />)}
      </div>
      <button className="btn btn-gold w-100 mt-4" disabled={loading || !email}>{loading ? "Verifying..." : "Verify email"}</button>
    </form>
    <button className="btn btn-link subtle-link px-0 mt-3" type="button" disabled={loading || countdown > 0 || !email} onClick={resend}>
      {countdown > 0 ? `Resend code in ${countdown}s` : "Resend verification code"}
    </button>
    <p className="text-secondary small mt-2 mb-0"><Link to="/register" className="subtle-link">Back to registration</Link></p>
  </section>;
}
