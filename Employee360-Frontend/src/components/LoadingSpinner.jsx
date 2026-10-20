export default function LoadingSpinner({ label = "Loading...", fullPage = false }) {
  return (
    <div className={`d-flex align-items-center justify-content-center gap-2 ${fullPage ? "min-vh-100" : "py-4"}`} role="status">
      <span className="spinner-border spinner-border-sm text-gold" aria-hidden="true" />
      <span className="text-secondary">{label}</span>
    </div>
  );
}
