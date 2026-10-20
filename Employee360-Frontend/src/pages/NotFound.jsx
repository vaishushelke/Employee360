import { Link } from "react-router-dom";
export default function NotFound() {
  return (
    <div className="not-found">
      <div className="not-found-code">404</div>
      <h1>
        Page not found<span>.</span>
      </h1>
      <p className="text-secondary">
        This address does not match an Employee360 page.
      </p>
      <Link className="btn btn-gold mt-3" to="/dashboard">
        Return to dashboard
      </Link>
    </div>
  );
}
