import { useEffect, useState } from "react";
import api from "../api/api";
import LoadingSpinner from "../components/LoadingSpinner";
import useAuth from "../hooks/useAuth";

export default function Profile() {
  const { username, role } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    api.get("/api/v2/employees/me")
      .then(({ data }) => {
        if (active) setProfile(data);
      })
      .catch((requestError) => {
        const response = requestError.response?.data;
        if (active) setError(typeof response === "string" ? response : response?.message || "Your employee profile could not be loaded.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => { active = false; };
  }, []);

  const displayName = [profile?.firstName, profile?.lastName].filter(Boolean).join(" ") || username || "User";

  return (
    <div className="page-enter">
      <div className="page-heading">
        <div>
          <div className="eyebrow">ACCOUNT</div>
          <h1>
            My profile<span>.</span>
          </h1>
          <p>Your Employee360 account details.</p>
        </div>
      </div>
      <section className="panel-card profile-card">
        <div className="profile-avatar">
          {displayName.slice(0, 1).toUpperCase()}
        </div>
        <div>
          <h2>{displayName}</h2>
          <p className="text-secondary mb-2">{profile?.employeeCode || username || "Employee profile"}</p>
          <span className="role-badge">{role || "Employee"}</span>
        </div>
        <div className="profile-note">
          {loading && <LoadingSpinner label="Loading your profile..." />}
          {error && <div className="alert alert-warning mb-0" role="status">{error}</div>}
          {!loading && !error && profile && <dl className="employee-profile-grid mb-0">
            <div><dt>Email</dt><dd>{profile.email || "—"}</dd></div>
            <div><dt>Department</dt><dd>{profile.departmentName || "—"}</dd></div>
            <div><dt>Designation</dt><dd>{profile.designation || "—"}</dd></div>
            <div><dt>Joining date</dt><dd>{profile.joiningDate ? new Date(`${profile.joiningDate}T00:00:00`).toLocaleDateString() : "—"}</dd></div>
            <div><dt>Status</dt><dd>{profile.status || "—"}</dd></div>
          </dl>}
          {!loading && !error && !profile && <p className="mb-0">No employee profile is linked to this account.</p>}
        </div>
      </section>
    </div>
  );
}
