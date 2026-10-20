import api from "../api/api";
import ResourcePage from "../components/ResourcePage";
import useAuth from "../hooks/useAuth";

const staffRoles = ["ADMIN", "CEO", "HR", "MANAGER"];
const reviewerRoles = ["ADMIN", "HR", "MANAGER"];
const today = new Date();
const todayValue = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}`;

const employeeColumns = [
  { key: "leaveType", label: "Type" },
  { key: "startDate", label: "From" },
  { key: "endDate", label: "To" },
  { key: "reason", label: "Reason" },
  { key: "status", label: "Status" },
  { key: "reviewedBy", label: "Reviewed by" },
  { key: "reviewedAt", label: "Decision date", render: (leave) => leave.reviewedAt ? new Date(leave.reviewedAt).toLocaleString() : "—" },
  { key: "reviewComment", label: "Reviewer note" }
];

const staffColumns = [
  { key: "id", label: "Request ID" },
  {
    key: "employee",
    label: "Employee",
    render: (leave) => `${leave.employee?.firstName || ""} ${leave.employee?.lastName || ""}`.trim() || leave.employee?.email || "—",
    searchValue: (leave) => `${leave.employee?.firstName || ""} ${leave.employee?.lastName || ""}`
  },
  { key: "employeeEmail", label: "Email", render: (leave) => leave.employee?.email || "—", searchValue: (leave) => leave.employee?.email },
  { key: "leaveType", label: "Type" },
  { key: "startDate", label: "From" },
  { key: "endDate", label: "To" },
  { key: "reason", label: "Reason" },
  { key: "status", label: "Status" },
  { key: "reviewedBy", label: "Reviewed by" },
  { key: "reviewedAt", label: "Decision date" },
  { key: "reviewComment", label: "Reviewer note" }
];

const employeeFields = [
  {
    name: "leaveType",
    label: "Leave type",
    type: "select",
    required: true,
    options: [
      { value: "Annual", label: "Annual leave" },
      { value: "Sick", label: "Sick leave" },
      { value: "Casual", label: "Casual leave" },
      { value: "Unpaid", label: "Unpaid leave" },
      { value: "Other", label: "Other" }
    ]
  },
  { name: "startDate", label: "Start date", type: "date", min: todayValue, required: true },
  { name: "endDate", label: "End date", type: "date", min: todayValue, required: true },
  { name: "reason", label: "Reason", type: "textarea", maxLength: 1000, wide: true, required: true }
];

const historyAction = {
  label: "History",
  icon: "bi-clock-history",
  className: "btn-outline-secondary",
  run: async (leave) => {
    const { data } = await api.get(`/Leave/history/${leave.id}`);
    if (!data.length) {
      window.alert("No review activity has been recorded for this request yet.");
      return "No review history is available.";
    }
    const summary = data.map((entry) =>
      `${entry.decision} by ${entry.reviewerUsername} on ${new Date(entry.decidedAt).toLocaleString()}${entry.comment ? `\n${entry.comment}` : ""}`
    ).join("\n\n");
    window.alert(summary);
    return "Review history loaded.";
  }
};

const actions = [
  {
    label: "Approve",
    icon: "bi-check2",
    className: "btn-outline-success",
    disabled: (leave) => String(leave.status || "").toUpperCase() !== "PENDING",
    run: async (leave) => {
      const comment = window.prompt("Optional approval comment:", "");
      if (comment === null) return null;
      await api.put(`/Leave/approve/${leave.id}`, { comment: comment.trim() || null });
      return "Leave request approved.";
    }
  },
  {
    label: "Reject",
    icon: "bi-x-lg",
    className: "btn-outline-danger",
    disabled: (leave) => String(leave.status || "").toUpperCase() !== "PENDING",
    run: async (leave) => {
      const comment = window.prompt("Enter the reason for rejecting this leave request:");
      if (comment === null) return null;
      if (!comment.trim()) throw new Error("A rejection reason is required.");
      await api.put(`/Leave/reject/${leave.id}`, { comment: comment.trim() });
      return "Leave request rejected.";
    }
  },
  historyAction
];

export default function Leaves() {
  const { role } = useAuth();
  const normalizedRole = String(role || "").replace(/^ROLE_/, "").toUpperCase();

  if (normalizedRole === "EMPLOYEE") {
    return <ResourcePage
      title="Leaves"
      description="Apply for leave and track your request status."
      icon="bi-calendar2-check"
      endpoint="/Leave/my"
      columns={employeeColumns}
      fields={employeeFields}
      create={{ method: "post", url: "/Leave/my" }}
      allowRoles={["EMPLOYEE"]}
      manageRoles={["EMPLOYEE"]}
      rowActions={[historyAction]}
      emptyMessage="You haven't submitted any leave requests yet."
    />;
  }

  return <ResourcePage
    title="Leaves"
    description="Review employee leave requests and their approval history."
    icon="bi-calendar2-check"
    endpoint="/Leave/getAll"
    columns={staffColumns}
    allowRoles={staffRoles}
    manageRoles={reviewerRoles}
    rowActions={actions}
  />;
}
