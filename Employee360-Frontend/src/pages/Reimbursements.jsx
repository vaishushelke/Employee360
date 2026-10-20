import ResourcePage from "../components/ResourcePage";
import useAuth from "../hooks/useAuth";

const staffRoles = ["ADMIN", "CEO", "HR", "MANAGER"];
const fields = [
  { name: "expenseTitle", label: "Expense title", required: true },
  { name: "expenseCategory", label: "Category", required: true },
  { name: "amount", label: "Amount (INR)", type: "number", min: "0.01", step: "0.01", required: true },
  { name: "expenseDate", label: "Expense date", type: "date", required: true },
  { name: "description", label: "Description", type: "textarea", wide: true }
];
const columns = [
  { key: "expenseTitle", label: "Expense" },
  { key: "expenseCategory", label: "Category" },
  { key: "amount", label: "Amount", render: (item) => `${item.currency || "INR"} ${item.amount ?? "—"}` },
  { key: "expenseDate", label: "Date" },
  { key: "approvalStatus", label: "Approval status" },
  { key: "paymentStatus", label: "Payment status" }
];
const create = {
  method: "post",
  url: "/api/reimbursements",
  serialize: (values) => ({ ...values, amount: Number(values.amount) })
};

export default function Reimbursements() {
  const { role } = useAuth();
  const normalizedRole = (role || "").replace(/^ROLE_/, "").toUpperCase();
  const isStaff = staffRoles.includes(normalizedRole);
  return <ResourcePage
    title="Reimbursements"
    description="Submit and track employee reimbursement requests."
    icon="bi-receipt"
    endpoint={isStaff ? "/api/reimbursements" : "/api/reimbursements/my"}
    columns={isStaff ? [{ key: "employeeUsername", label: "Employee" }, ...columns] : columns}
    fields={fields}
    create={create}
    allowRoles={["ADMIN", "CEO", "HR", "MANAGER", "EMPLOYEE"]}
    manageRoles={["ADMIN", "CEO", "HR", "MANAGER", "EMPLOYEE"]}
  />;
}
