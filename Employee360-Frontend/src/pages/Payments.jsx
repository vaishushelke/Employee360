import api from "../api/api";
import ResourcePage from "../components/ResourcePage";
import useAuth from "../hooks/useAuth";

const authorizedRoles = ["ADMIN", "CEO", "HR", "EMPLOYEE"];
const columns = [
  { key: "id", label: "Payment ID" },
  { key: "employeeUsername", label: "Employee" },
  { key: "netSalary", label: "Amount", render: (payment) => `${payment.currency || "INR"} ${payment.netSalary ?? "—"}` },
  { key: "paymentDate", label: "Payment date" },
  { key: "transactionReference", label: "Reference" },
  { key: "paymentStatus", label: "Status" }
];
const rowActions = [
  {
    label: "Record payment",
    icon: "bi-credit-card",
    className: "btn-outline-success",
    disabled: (payment) => ["PAID", "FAILED"].includes(String(payment.paymentStatus || "").toUpperCase()),
    run: async (payment) => {
      const transactionReference = window.prompt("Enter the payment transaction reference (required):");
      if (transactionReference === null) return null;
      const reference = transactionReference.trim();
      if (!reference) throw new Error("A transaction reference is required.");
      if (reference.length > 150) throw new Error("Transaction reference cannot exceed 150 characters.");
      if (!window.confirm("Record this salary payment as paid?")) return null;
      const params = new URLSearchParams({ status: "PAID", transactionReference: reference });
      await api.patch(`/api/salary-payments/${payment.id}/status?${params.toString()}`);
      return "Payment recorded as paid.";
    }
  }
];

export default function Payments() {
  const { role } = useAuth();
  const normalizedRole = (role || "").replace(/^ROLE_/, "").toUpperCase();
  const isEmployee = normalizedRole === "EMPLOYEE";
  return <ResourcePage
    title="Payments"
    description={isEmployee ? "View your salary payment history." : "Review salary payment records and their transaction status."}
    icon="bi-credit-card"
    endpoint={isEmployee ? "/api/salary-payments/my" : "/api/salary-payments"}
    columns={isEmployee ? columns.filter((column) => column.key !== "employeeUsername") : columns}
    allowRoles={authorizedRoles}
    manageRoles={["ADMIN", "HR"]}
    rowActions={rowActions}
    emptyMessage="No payment records are available for this account."
  />;
}
