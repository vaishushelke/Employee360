export default function Reports() {
  return <div className="page-enter">
    <div className="page-heading"><div><div className="eyebrow">WORKSPACE / REPORTS</div><h1>Reports<span>.</span></h1><p>Explore organization and workforce reports.</p></div><div className="page-heading-symbol"><i className="bi bi-bar-chart" aria-hidden="true" /></div></div>
    <section className="panel-card access-notice">
      <i className="bi bi-bar-chart" aria-hidden="true" />
      <h2>Reports API is not available</h2>
      <p>The current Spring Boot backend does not define a reports endpoint. This page avoids requesting a nonexistent route or presenting fabricated metrics. Add the required reporting endpoints to the backend to enable reports and exports.</p>
    </section>
  </div>;
}
