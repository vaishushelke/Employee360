import { useState } from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "../components/Sidebar";
import Navbar from "../components/Navbar";

export default function MainLayout() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  return (
    <div className={`app-shell ${mobileMenuOpen ? "mobile-menu-open" : ""}`}>
      {mobileMenuOpen && <button className="sidebar-backdrop" type="button" aria-label="Close navigation menu" onClick={() => setMobileMenuOpen(false)} />}
      <Sidebar onNavigate={() => setMobileMenuOpen(false)} />
      <div className="main-column">
        <Navbar onMenuClick={() => setMobileMenuOpen((open) => !open)} />
        <main className="page-content"><Outlet /></main>
        <footer className="app-footer">© {new Date().getFullYear()} Employee360 <span>•</span> Built for better people operations</footer>
      </div>
    </div>
  );
}
