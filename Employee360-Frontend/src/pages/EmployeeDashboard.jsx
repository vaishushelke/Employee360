import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import LoadingSpinner from "../components/LoadingSpinner";
import useAuth from "../hooks/useAuth";

const formatNumber = new Intl.NumberFormat();
const moneyFormat = new Intl.NumberFormat(undefined, {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0
});

function requestMessage(error) {
  const data = error.response?.data;
  return typeof data === "string" ? data : data?.message || error.message || "Request failed.";
}

function getGreeting() {
  const hour = new Date().getHours();
  if (hour < 12) return "Good morning";
  if (hour < 18) return "Good afternoon";
  return "Good evening";
}

function isFutureOrToday(date) {
  return date && new Date(`${date}T23:59:59`) >= new Date();
}

function normalizeDepartmentName(name) {
  const normalized = String(name || "").toLowerCase().replace(/[^a-z0-9]/g, "");
  if (["hr", "humanresources"].includes(normalized)) return "hr";
  if (["it", "informationtechnology"].includes(normalized)) return "it";
  return normalized;
}

function getQuizDepartment(quiz) {
  return quiz.departmentName
    || quiz.department?.name
    || quiz.topic?.departmentName
    || quiz.topic?.department?.name
    || (typeof quiz.department === "string" ? quiz.department : "");
}

function MetricCard({ label, value, icon, note, to }) {
  const content = <>
    <div className="stat-icon"><i className={`bi ${icon}`} aria-hidden="true" /></div>
    <div className="stat-label">{label}</div>
    <div className="stat-value">{value}</div>
    <div className="stat-note">{note}</div>
  </>;
  return to
    ? <Link className="employee-metric-link" to={to}><div className="stat-card">{content}</div></Link>
    : <div className="stat-card">{content}</div>;
}

export default function EmployeeDashboard() {
  const { username } = useAuth();
  const [dashboard, setDashboard] = useState({
    profile: null,
    attendance: [],
    leaves: [],
    payments: [],
    reimbursements: [],
    quizResults: [],
    quizzes: []
  });
  const [loading, setLoading] = useState(true);
  const [errors, setErrors] = useState([]);

  useEffect(() => {
    let active = true;
    const requests = [
      ["profile", "Employee profile", "/api/v2/employees/me"],
      ["attendance", "Attendance history", "/Attendance/my"],
      ["leaves", "Leave requests", "/Leave/my"],
      ["payments", "Salary history", "/api/salary-payments/my"],
      ["reimbursements", "Reimbursements", "/api/reimbursements/my"],
      ["quizResults", "Quiz results", "/api/quizzes/my-results"],
      ["quizzes", "Available quizzes", "/api/quizzes"]
    ];

    Promise.allSettled(requests.map(([, , endpoint]) => api.get(endpoint)))
      .then((results) => {
        if (!active) return;
        const nextDashboard = { profile: null, attendance: [], leaves: [], payments: [], reimbursements: [], quizResults: [], quizzes: [] };
        const nextErrors = [];
        results.forEach((result, index) => {
          const [key, label] = requests[index];
          if (result.status === "fulfilled" && key === "profile" && result.value.data && typeof result.value.data === "object") {
            nextDashboard[key] = result.value.data;
          } else if (result.status === "fulfilled" && Array.isArray(result.value.data)) {
            nextDashboard[key] = result.value.data;
          } else {
            const message = result.status === "rejected"
              ? requestMessage(result.reason)
              : "The server returned data in an unexpected format.";
            nextErrors.push(`${label}: ${message}`);
          }
        });
        setDashboard(nextDashboard);
        setErrors(nextErrors);
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => { active = false; };
  }, []);

  const upcomingLeaves = useMemo(() => dashboard.leaves
    .filter((leave) => isFutureOrToday(leave.endDate))
    .sort((left, right) => String(left.startDate).localeCompare(String(right.startDate)))
    .slice(0, 3), [dashboard.leaves]);
  const latestPayment = useMemo(() => [...dashboard.payments]
    .sort((left, right) => String(right.paymentDate || right.salaryMonth || "").localeCompare(String(left.paymentDate || left.salaryMonth || "")))[0],
  [dashboard.payments]);
  const latestReimbursements = useMemo(() => [...dashboard.reimbursements]
    .sort((left, right) => String(right.createdAt || "").localeCompare(String(left.createdAt || "")))
    .slice(0, 3), [dashboard.reimbursements]);
  const employeeDepartment = normalizeDepartmentName(dashboard.profile?.departmentName);
  const availableQuizzes = dashboard.quizzes.filter((quiz) => {
    const quizDepartment = normalizeDepartmentName(getQuizDepartment(quiz));
    return quiz.active !== false && !quiz.archived
      && (!employeeDepartment || !quizDepartment || employeeDepartment === quizDepartment);
  }).slice(0, 3);
  const completedQuizzes = dashboard.quizResults.filter((result) => String(result.status).toUpperCase() !== "IN_PROGRESS");
  const pendingLeaves = dashboard.leaves.filter((leave) => String(leave.status).toUpperCase() === "PENDING").length;
  const pendingReimbursements = dashboard.reimbursements.filter((item) =>
    String(item.approvalStatus).toUpperCase() === "PENDING").length;
  const displayName = [dashboard.profile?.firstName, dashboard.profile?.lastName].filter(Boolean).join(" ") || username || "there";
  const presentDays = dashboard.attendance.filter((record) => ["PRESENT", "HALF_DAY"].includes(String(record.status).toUpperCase())).length;
  const absentDays = dashboard.attendance.filter((record) => String(record.status).toUpperCase() === "ABSENT").length;
  const workingDays = dashboard.attendance.filter((record) => String(record.status).toUpperCase() !== "LEAVE").length;
  const attendancePercentage = workingDays ? Math.round((presentDays / workingDays) * 100) : 0;
  const recentAttendance = [...dashboard.attendance]
    .sort((left, right) => String(right.attendanceDate || "").localeCompare(String(left.attendanceDate || "")))
    .slice(0, 5);

  return <div className="page-enter">
    <div className="employee-welcome-banner">
      <div>
        <div className="eyebrow">YOUR WORKSPACE / OVERVIEW</div>
        <h1>{getGreeting()}, {displayName}<span>.</span></h1>
        <p>{dashboard.profile?.employeeCode ? `Employee ID ${dashboard.profile.employeeCode} · ` : ""}Here’s your personal work and learning snapshot.</p>
      </div>
      <div className="employee-welcome-date">
        <i className="bi bi-calendar3" aria-hidden="true" />
        <span>{new Date().toLocaleDateString(undefined, { weekday: "long", day: "numeric", month: "long" })}</span>
      </div>
    </div>

    {loading && <LoadingSpinner label="Loading your dashboard..." />}
    {!loading && errors.length > 0 && (
      <div className="alert alert-warning" role="status">
        <strong>Some personal dashboard information could not be loaded.</strong>
        <ul className="mb-0 mt-2">{errors.map((message) => <li key={message}>{message}</li>)}</ul>
      </div>
    )}

    <div className="row g-3 mb-4">
      <div className="col-12 col-sm-6 col-xl-3">
        <MetricCard label="Leave requests" value={formatNumber.format(dashboard.leaves.length)} icon="bi-calendar2-check" note={`${pendingLeaves} awaiting review`} to="/leaves" />
      </div>
      <div className="col-12 col-sm-6 col-xl-3">
        <MetricCard label="Salary payments" value={formatNumber.format(dashboard.payments.length)} icon="bi-wallet2" note={latestPayment ? `${latestPayment.paymentStatus || "Status unavailable"} · ${latestPayment.currency || "INR"} ${latestPayment.netSalary ?? "—"}` : "Your payment history"} to="/payments" />
      </div>
      <div className="col-12 col-sm-6 col-xl-3">
        <MetricCard label="Reimbursements" value={formatNumber.format(dashboard.reimbursements.length)} icon="bi-receipt" note={`${pendingReimbursements} awaiting review`} to="/reimbursements" />
      </div>
      <div className="col-12 col-sm-6 col-xl-3">
        <MetricCard label="Quizzes completed" value={formatNumber.format(completedQuizzes.length)} icon="bi-mortarboard" note="Keep building your skills" to="/quizzes" />
      </div>
    </div>

    <div className="row g-3 mb-4">
      <div className="col-12 col-xl-5">
        <section className="panel-card h-100">
          <div className="panel-heading"><div><h2>My employee profile</h2><p>Details linked to your signed-in account</p></div><Link className="panel-heading-link" to="/profile">View profile <i className="bi bi-arrow-right" aria-hidden="true" /></Link></div>
          {dashboard.profile ? <dl className="employee-profile-grid mb-0">
            <div><dt>Employee ID</dt><dd>{dashboard.profile.employeeCode || "—"}</dd></div>
            <div><dt>Department</dt><dd>{dashboard.profile.departmentName || "—"}</dd></div>
            <div><dt>Designation</dt><dd>{dashboard.profile.designation || "—"}</dd></div>
            <div><dt>Joining date</dt><dd>{dashboard.profile.joiningDate ? new Date(`${dashboard.profile.joiningDate}T00:00:00`).toLocaleDateString() : "—"}</dd></div>
            <div><dt>Employment status</dt><dd>{dashboard.profile.status || "—"}</dd></div>
          </dl> : <div className="employee-empty-inline"><i className="bi bi-person-badge" /><span>Employee profile details are not available for this account.</span></div>}
        </section>
      </div>
      <div className="col-12 col-xl-7">
        <section className="panel-card h-100">
          <div className="panel-heading"><div><h2>Attendance overview</h2><p>Your recorded attendance only</p></div><Link className="panel-heading-link" to="/attendance">View history <i className="bi bi-arrow-right" aria-hidden="true" /></Link></div>
          <div className="employee-attendance-summary">
            <div><strong>{presentDays}</strong><span>Present days</span></div>
            <div><strong>{absentDays}</strong><span>Absent days</span></div>
            <div><strong>{attendancePercentage}%</strong><span>Attendance rate</span></div>
          </div>
          {recentAttendance.length === 0
            ? <div className="employee-empty-inline"><i className="bi bi-clock-history" /><span>No attendance records are available yet.</span></div>
            : <div className="employee-list compact">{recentAttendance.map((record) => <div className="employee-list-row" key={record.id}>
              <div className="employee-list-copy"><strong>{record.employeeCode || dashboard.profile?.employeeCode || "My attendance"}</strong><small>{record.attendanceDate ? new Date(`${record.attendanceDate}T00:00:00`).toLocaleDateString() : "Date unavailable"}</small></div>
              <span className={`employee-status status-${String(record.status || "pending").toLowerCase()}`}>{String(record.status || "Unknown").replaceAll("_", " ")}</span>
            </div>)}</div>}
        </section>
      </div>
    </div>

    <div className="row g-3">
      <div className="col-12 col-xl-7">
        <section className="panel-card h-100">
          <div className="panel-heading">
            <div><h2>Upcoming leave</h2><p>Your scheduled and pending leave requests</p></div>
            <Link className="panel-heading-link" to="/leaves">View all <i className="bi bi-arrow-right" aria-hidden="true" /></Link>
          </div>
          {upcomingLeaves.length === 0
            ? <div className="employee-empty-inline"><i className="bi bi-calendar2-check" /><span>No upcoming leave requests. Plan time off when you need it.</span></div>
            : <div className="employee-list">
              {upcomingLeaves.map((leave) => (
                <div className="employee-list-row" key={leave.id}>
                  <div className="employee-list-icon"><i className="bi bi-calendar-event" aria-hidden="true" /></div>
                  <div className="employee-list-copy">
                    <strong>{leave.leaveType || "Leave"} leave</strong>
                    <small>{new Date(`${leave.startDate}T00:00:00`).toLocaleDateString()} – {new Date(`${leave.endDate}T00:00:00`).toLocaleDateString()}</small>
                  </div>
                  <span className={`employee-status status-${String(leave.status || "pending").toLowerCase()}`}>{leave.status || "Pending"}</span>
                </div>
              ))}
            </div>}
          <Link className="btn btn-gold btn-sm mt-3" to="/leaves"><i className="bi bi-plus-lg me-1" />Request leave</Link>
        </section>
      </div>

      <div className="col-12 col-xl-5">
        <section className="panel-card h-100">
          <div className="panel-heading">
            <div><h2>Latest salary payment</h2><p>Your personal payroll summary</p></div>
            <i className="bi bi-credit-card panel-heading-icon" aria-hidden="true" />
          </div>
          {latestPayment
            ? <div className="employee-payment-summary">
              <span className="employee-payment-amount">{moneyFormat.format(Number(latestPayment.netSalary || 0))}</span>
              <span className={`employee-status status-${String(latestPayment.paymentStatus || "pending").toLowerCase()}`}>{latestPayment.paymentStatus || "Pending"}</span>
              <small>{latestPayment.salaryMonth ? new Date(`${latestPayment.salaryMonth}T00:00:00`).toLocaleDateString(undefined, { month: "long", year: "numeric" }) : latestPayment.paymentDate || "Payment period unavailable"}</small>
            </div>
            : <div className="employee-empty-inline"><i className="bi bi-wallet2" /><span>No salary payment record is available yet.</span></div>}
          <Link className="quick-link mt-3" to="/payments">
            <span className="quick-link-icon"><i className="bi bi-arrow-up-right" aria-hidden="true" /></span>
            <span className="quick-link-copy"><strong>Open payment history</strong><small>View your salary records</small></span>
            <i className="bi bi-chevron-right quick-link-arrow" aria-hidden="true" />
          </Link>
        </section>
      </div>
    </div>

    <div className="row g-3 mt-1">
      <div className="col-12 col-xl-7">
        <section className="panel-card h-100">
          <div className="panel-heading">
            <div><h2>Recent reimbursements</h2><p>Track your submitted expense claims</p></div>
            <Link className="panel-heading-link" to="/reimbursements">View all <i className="bi bi-arrow-right" aria-hidden="true" /></Link>
          </div>
          {latestReimbursements.length === 0
            ? <div className="employee-empty-inline"><i className="bi bi-receipt" /><span>No expense claims yet. Submitted claims will appear here.</span></div>
            : <div className="employee-list">
              {latestReimbursements.map((item) => (
                <div className="employee-list-row" key={item.id}>
                  <div className="employee-list-icon"><i className="bi bi-receipt-cutoff" aria-hidden="true" /></div>
                  <div className="employee-list-copy">
                    <strong>{item.expenseTitle || item.expenseCategory || "Expense claim"}</strong>
                    <small>{item.expenseDate ? new Date(`${item.expenseDate}T00:00:00`).toLocaleDateString() : "Date unavailable"} · {item.currency || "INR"} {item.amount ?? "—"}</small>
                  </div>
                  <span className={`employee-status status-${String(item.approvalStatus || "pending").toLowerCase()}`}>{item.approvalStatus || "Pending"}</span>
                </div>
              ))}
            </div>}
          <Link className="btn btn-outline-secondary btn-sm mt-3" to="/reimbursements"><i className="bi bi-plus-lg me-1" />Submit an expense</Link>
        </section>
      </div>

      <div className="col-12 col-xl-5">
        <section className="panel-card h-100">
          <div className="panel-heading">
            <div><h2>Learning progress</h2><p>{availableQuizzes.length} available quiz{availableQuizzes.length === 1 ? "" : "zes"}{dashboard.profile?.departmentName ? ` for ${dashboard.profile.departmentName}` : ""} · Keep building your skills</p></div>
          </div>
          {availableQuizzes.length > 0 && <div className="employee-list compact">
            {availableQuizzes.map((quiz) => <div className="employee-list-row" key={quiz.id}>
              <div className="employee-list-icon"><i className="bi bi-patch-question" aria-hidden="true" /></div>
              <div className="employee-list-copy">
                <strong>{quiz.title || "Workplace quiz"}</strong>
                <small>{quiz.topicTitle || quiz.topic?.title || quiz.topicName || quiz.learningTopic || "Available to take"}{quiz.durationMinutes ? ` · ${quiz.durationMinutes} min` : ""}</small>
              </div>
              <Link className="panel-heading-link" to="/quizzes">Take quiz <i className="bi bi-arrow-right" aria-hidden="true" /></Link>
            </div>)}
          </div>}
          {dashboard.profile?.departmentName && availableQuizzes.length === 0 && <div className="employee-empty-inline"><i className="bi bi-patch-question" /><span>No published quizzes are available for {dashboard.profile.departmentName} yet.</span></div>}
          <div className="learning-progress-summary">
            <div className="learning-progress-icon"><i className="bi bi-mortarboard" aria-hidden="true" /></div>
            <div><strong>{completedQuizzes.length} completed</strong><small>{dashboard.quizResults.length} total quiz attempts</small></div>
          </div>
          {completedQuizzes.length > 0 && (
            <div className="employee-list compact">
              {completedQuizzes.slice(0, 2).map((quiz) => (
                <div className="employee-list-row" key={quiz.attemptId}>
                  <div className="employee-list-copy">
                    <strong>{quiz.quizTitle || "Workplace quiz"}</strong>
                    <small>Score {quiz.score} / {quiz.totalMarks} · {Number(quiz.percentage || 0).toFixed(0)}%</small>
                  </div>
                </div>
              ))}
            </div>
          )}
          <Link className="btn btn-gold btn-sm mt-3" to="/quizzes">{availableQuizzes.length > 0 ? "Explore all quizzes" : "Explore quizzes"} <i className="bi bi-arrow-up-right ms-1" /></Link>
        </section>
      </div>
    </div>
  </div>;
}
