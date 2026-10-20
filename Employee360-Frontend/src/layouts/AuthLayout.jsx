import { Outlet } from "react-router-dom";

export default function AuthLayout() {
  return (
    <main className="auth-layout">
      <div className="auth-frame">
        <section className="auth-panel">
          <div className="auth-brand">
            <span className="auth-brand-icon"><i className="bi bi-people-fill" aria-hidden="true" /></span>
            <span>Employee<span>360</span></span>
          </div>
          <Outlet />
          <p className="auth-footer">Manage <span>•</span> Track <span>•</span> Grow</p>
        </section>
        <aside className="auth-art-panel" aria-label="Employee360 workspace preview">
          <div className="auth-art-copy">
            <span className="auth-art-eyebrow">SMARTER HR. HAPPIER TEAMS.</span>
            <h2>People-first work<br />starts right here.</h2>
            <p>One connected workspace to help your team do its best work.</p>
          </div>
          <div className="auth-art-scene" aria-hidden="true">
            <div className="auth-art-window">
              <div className="auth-art-window-top"><i /><i /><i /></div>
              <div className="auth-art-window-body">
                <div className="auth-art-side"><span /><span /><span /><span /></div>
                <div className="auth-art-content">
                  <span className="auth-art-heading" />
                  <div className="auth-art-stat-row"><i /><i /><i /></div>
                  <div className="auth-art-chart"><span /><span /><span /><span /><span /><span /></div>
                </div>
              </div>
            </div>
            <div className="auth-art-person"><i className="bi bi-person-fill" /></div>
            <div className="auth-art-plant"><i className="bi bi-flower1" /></div>
            <span className="auth-art-sparkle sparkle-one"><i className="bi bi-stars" /></span>
            <span className="auth-art-sparkle sparkle-two"><i className="bi bi-circle-fill" /></span>
          </div>
          <div className="auth-art-footer">
            <span><i className="bi bi-shield-check" /> Secure and simple</span>
            <span>Built for better workplaces</span>
          </div>
        </aside>
      </div>
    </main>
  );
}
