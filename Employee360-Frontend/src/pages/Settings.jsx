import { useEffect, useState } from "react";
export default function Settings() {
  const [compact, setCompact] = useState(() => localStorage.getItem("employee360:compact") === "true");

  useEffect(() => {
    document.documentElement.dataset.compact = String(compact);
    localStorage.setItem("employee360:compact", String(compact));
  }, [compact]);

  return (
    <div className="page-enter">
      <div className="page-heading">
        <div>
          <div className="eyebrow">ACCOUNT</div>
          <h1>
            Settings<span>.</span>
          </h1>
          <p>Personalize your workspace experience.</p>
        </div>
      </div>
      <section className="panel-card settings-list">
        <label className="setting-row">
          <span>
            <strong>Compact layout</strong>
            <small>Reduce spacing between interface elements.</small>
          </span>
          <input
            className="form-check-input"
            type="checkbox"
            checked={compact}
            onChange={(e) => setCompact(e.target.checked)}
          />
        </label>
        <p className="small text-secondary mt-3">
          This preference is saved in this browser. Notification and language preferences are not available in the backend yet.
        </p>
      </section>
    </div>
  );
}
