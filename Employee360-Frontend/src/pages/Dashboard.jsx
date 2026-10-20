import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, Cell, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import api from "../api/api";
import LoadingSpinner from "../components/LoadingSpinner";
import useAuth from "../hooks/useAuth";
import EmployeeDashboard from "./EmployeeDashboard";
import Calendar from "../components/Calendar";

const numberFormat = new Intl.NumberFormat();

const getGreeting = () => {
  const hour = new Date().getHours();
  if (hour < 12) return "Good morning";
  if (hour < 18) return "Good afternoon";
  return "Good evening";
};

const holidayCalendar = [
  { date: "2026-10-02", title: "Gandhi Jayanti" },
  { date: "2026-10-15", title: "Dussehra" },
  { date: "2026-11-14", title: "Diwali" },
  { date: "2026-12-25", title: "Christmas" }
];

export default function Dashboard() {
  const { role, username } = useAuth();
  const normalizedRole = String(role || "").replace(/^ROLE_/, "").toUpperCase();
  const [summary, setSummary] = useState(null);
  const [learningCatalog, setLearningCatalog] = useState(null);
  const [learningCatalogLoading, setLearningCatalogLoading] = useState(true);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (normalizedRole === "EMPLOYEE") return undefined;

    let active = true;
    api.get("/api/dashboard/summary")
      .then(({ data }) => {
        if (active) setSummary(data);
      })
      .catch((requestError) => {
        const data = requestError.response?.data;
        setError(typeof data === "string" ? data : data?.message || "Dashboard data could not be loaded. Check your access and the backend service.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => { active = false; };
  }, [normalizedRole]);

  useEffect(() => {
    if (normalizedRole === "EMPLOYEE") {
      return undefined;
    }

    let active = true;
    api.get("/api/learning/catalog")
      .then(({ data }) => {
        if (!active) return;
        setLearningCatalog(Array.isArray(data?.departments) ? data.departments : []);
      })
      .catch(() => {
        if (active) setLearningCatalog(null);
      })
      .finally(() => {
        if (active) setLearningCatalogLoading(false);
      });

    return () => { active = false; };
  }, [normalizedRole]);

  const metrics = summary ? [
    { label: "Total employees", value: summary.totalEmployees, icon: "bi-people", note: "Organization directory" },
    { label: "Departments", value: summary.totalDepartments, icon: "bi-diagram-3", note: "Organization structure" },
    { label: "Present today", value: summary.presentToday, icon: "bi-person-check", note: `${numberFormat.format(summary.totalAttendanceToday ?? 0)} attendance records today` },
    { label: "Pending leave requests", value: summary.pendingLeaveRequests, icon: "bi-calendar2-check", note: `${numberFormat.format(summary.totalLeaveRequests ?? 0)} total requests` }
  ] : [];

  const chartData = summary ? [
    { name: "Employees", value: summary.totalEmployees },
    { name: "Departments", value: summary.totalDepartments },
    { name: "Present today", value: summary.presentToday },
    { name: "Pending leaves", value: summary.pendingLeaveRequests }
  ].map((item) => ({ ...item, value: Number(item.value ?? 0) })) : [];

  const upcomingHolidays = useMemo(() => holidayCalendar.slice(0, 4), []);
  const attendanceSplit = summary ? [
    { name: "Present", value: Math.max(Number(summary.presentToday ?? 0), 0), color: "#e4c56a" },
    { name: "Not present", value: Math.max(Number(summary.totalEmployees ?? 0) - Number(summary.presentToday ?? 0), 0), color: "#172536" }
  ] : [];
  const attendanceTotal = attendanceSplit.reduce((total, item) => total + item.value, 0);
  const learningDepartments = learningCatalog || [];
  const learningTopicCount = learningDepartments.reduce((total, department) => total + (department.topics || []).length, 0);
  const learningQuestionCount = learningDepartments.reduce((total, department) => total + (department.topics || []).reduce((topicTotal, topic) => topicTotal + (topic.slots || []).reduce((slotTotal, slot) => slotTotal + (slot.levels || []).reduce((levelTotal, level) => levelTotal + Number(level.plannedQuestionCount || 0), 0), 0), 0), 0);

  if (normalizedRole === "EMPLOYEE") {
    return <EmployeeDashboard />;
  }

  return <div className="page-enter admin-dashboard">
    <header className="admin-overview-banner">
      <div className="admin-overview-copy">
        <div className="admin-overview-kicker"><i className="bi bi-grid-1x2-fill" aria-hidden="true" /> PEOPLE OPERATIONS / ADMIN</div>
        <h1>Organization command center</h1>
        <p>{getGreeting()}, {username || "there"}. Here’s your workforce overview.</p>
      </div>
      <div className="admin-overview-context">
        <span className="admin-scope"><i className="bi bi-building-gear" aria-hidden="true" /> Organization-wide</span>
        <span className="admin-overview-date"><i className="bi bi-calendar3" aria-hidden="true" />{new Date().toLocaleDateString(undefined, { weekday: "short", day: "numeric", month: "short", year: "numeric" })}</span>
      </div>
    </header>

    {loading && <LoadingSpinner label="Loading dashboard summary..." />}
    {error && <div className="alert alert-danger" role="alert">{error}</div>}
    {!loading && !error && !summary && <div className="empty-state panel-card"><strong>No dashboard data is available.</strong><p>The dashboard endpoint returned no summary.</p></div>}

    {summary && <>
      <div className="row g-3 mb-4">
        {metrics.map((metric) => <div className="col-12 col-sm-6 col-xl-3" key={metric.label}>
          <div className="stat-card admin-stat-card"><div className="stat-icon"><i className={`bi ${metric.icon}`} aria-hidden="true" /></div><div className="stat-label">{metric.label}</div><div className="stat-value">{numberFormat.format(Number(metric.value ?? 0))}</div><div className="stat-note">{metric.note}</div></div>
        </div>)}
      </div>

      <section className="admin-priority-panel" aria-labelledby="admin-priority-title">
        <div className="admin-priority-symbol"><i className="bi bi-inbox" aria-hidden="true" /></div>
        <div className="admin-priority-copy">
          <span className="admin-priority-label">REVIEW QUEUE</span>
          <h2 id="admin-priority-title">{Number(summary.pendingLeaveRequests ?? 0) > 0 ? "Leave requests need your review" : "No leave requests waiting"}</h2>
          <p>{Number(summary.pendingLeaveRequests ?? 0) > 0 ? `${numberFormat.format(Number(summary.pendingLeaveRequests))} requests are awaiting an administrator decision.` : "The leave approval queue is clear right now."}</p>
        </div>
        <Link className="admin-priority-action" to="/leaves">
          Review requests <i className="bi bi-arrow-up-right" aria-hidden="true" />
        </Link>
      </section>

      <section className="panel-card admin-it-learning mb-3" aria-labelledby="admin-it-learning-title">
        <div className="panel-heading">
          <div><h2 id="admin-it-learning-title">Learning catalog</h2><p>Organization-wide department topics and planned question coverage</p></div>
          <div className="admin-it-actions">
            <Link className="panel-heading-link" to="/learning-catalog">Open catalog <i className="bi bi-arrow-right" aria-hidden="true" /></Link>
            <Link className="admin-it-quiz-link" to="/quizzes">View quizzes <i className="bi bi-arrow-up-right" aria-hidden="true" /></Link>
          </div>
        </div>
        {learningCatalogLoading ? <p className="text-secondary mb-0">Loading learning catalog...</p> : learningCatalog ? <>
          <div className="admin-it-metrics">
            <div><strong>{numberFormat.format(learningDepartments.length)}</strong><span>departments</span></div>
            <div><strong>{numberFormat.format(learningTopicCount)}</strong><span>topics</span></div>
            <div><strong>{numberFormat.format(learningQuestionCount)}</strong><span>planned questions</span></div>
          </div>
          <div className="admin-it-topic-list">
            {learningDepartments.map((department) => <div className="admin-it-topic" key={department.id}>
              <i className="bi bi-diagram-3" aria-hidden="true" />
              <span>{department.name}</span>
              <small>{department.topics?.length || 0} topics</small>
            </div>)}
          </div>
        </> : <p className="text-secondary mb-0">The learning catalog is not available.</p>}
      </section>

      <div className="row g-3">
        <div className="col-12 col-xl-7"><section className="panel-card h-100">
          <div className="panel-heading"><div><h2>Workforce snapshot</h2><p>Organization totals at a glance</p></div><i className="bi bi-graph-up-arrow panel-heading-icon" aria-hidden="true" /></div>
          <div className="chart-area">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData} margin={{ top: 8, right: 12, bottom: 8, left: 0 }}>
                <CartesianGrid stroke="#e8eaf0" vertical={false} />
                <XAxis dataKey="name" stroke="#727987" tickLine={false} axisLine={false} />
                <YAxis stroke="#727987" tickLine={false} axisLine={false} allowDecimals={false} />
                <Tooltip contentStyle={{ background: "#fff", border: "1px solid #e5e7eb", borderRadius: 10 }} />
                <Bar dataKey="value" name="Count" fill="#e4c56a" radius={[5, 5, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </section></div>

        <div className="col-12 col-xl-5"><section className="panel-card h-100">
          <div className="panel-heading"><div><h2>Attendance distribution</h2><p>Today's team attendance</p></div><i className="bi bi-pie-chart panel-heading-icon" aria-hidden="true" /></div>
          <div className="attendance-chart-wrap">
            {attendanceTotal > 0 ? (
              <>
                <div className="attendance-donut">
                  <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                      <Pie data={attendanceSplit} dataKey="value" nameKey="name" innerRadius="67%" outerRadius="91%" paddingAngle={3} stroke="none">
                        {attendanceSplit.map((entry) => <Cell key={entry.name} fill={entry.color} />)}
                      </Pie>
                      <Tooltip contentStyle={{ background: "#fff", border: "1px solid #e5e7eb", borderRadius: 10 }} />
                    </PieChart>
                  </ResponsiveContainer>
                  <div className="attendance-donut-total">
                    <strong>{numberFormat.format(Number(summary.presentToday ?? 0))}</strong>
                    <small>present</small>
                  </div>
                </div>
                <div className="attendance-legend">
                  {attendanceSplit.map((entry) => (
                    <div className="attendance-legend-item" key={entry.name}>
                      <span style={{ backgroundColor: entry.color }} />
                      <div><strong>{entry.name}</strong><small>{numberFormat.format(entry.value)} people</small></div>
                    </div>
                  ))}
                </div>
              </>
            ) : <div className="attendance-empty">No attendance summary is available yet.</div>}
          </div>
        </section></div>
      </div>

      <div className="quick-access-strip">
        {[
          ["Employees", "/employees", "bi-people"],
          ["Departments", "/departments", "bi-diagram-3"],
          ["Leave management", "/leaves", "bi-calendar2-check"],
          ["Attendance", "/attendance", "bi-clock-history"]
        ].map(([label, to, icon]) => (
          <Link to={to} key={to} className="quick-access-link">
            <i className={`bi ${icon}`} aria-hidden="true" />
            <span>{label}</span>
            <i className="bi bi-arrow-up-right" aria-hidden="true" />
          </Link>
        ))}
      </div>

      <div className="row g-3 mt-1">
        <div className="col-12 col-xl-8">
          <section className="panel-card h-100">
            <div className="panel-heading">
              <div><h2>Upcoming events</h2><p>Team activities and company reminders</p></div>
              <i className="bi bi-calendar-event panel-heading-icon" aria-hidden="true" />
            </div>
            <div className="event-list">
              {[
                { title: "Performance review cycle", meta: "Today • 3:30 PM", color: "gold" },
                { title: "Employee onboarding", meta: "Tomorrow • 11:00 AM", color: "purple" },
                { title: "Payroll sync", meta: "Thu • 9:30 AM", color: "green" }
              ].map((event) => (
                <div className="event-item" key={event.title}>
                  <span className={`event-dot ${event.color}`} />
                  <div>
                    <strong>{event.title}</strong>
                    <small>{event.meta}</small>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>

        <div className="col-12 col-xl-4">
          <section className="panel-card h-100">
            <div className="panel-heading">
              <div><h2>Holiday calendar</h2><p>Corporate leaves</p></div>
            </div>
            <div className="holiday-list">
              {upcomingHolidays.map((holiday) => (
                <div className="holiday-item" key={holiday.date}>
                  <div className="holiday-date">
                    <strong>{new Date(`${holiday.date}T00:00:00`).getDate()}</strong>
                    <span>{new Date(`${holiday.date}T00:00:00`).toLocaleDateString(undefined, { month: "short" })}</span>
                  </div>
                  <div className="holiday-copy">
                    <strong>{holiday.title}</strong>
                    <small>Company holiday</small>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </>}
  </div>;
}
