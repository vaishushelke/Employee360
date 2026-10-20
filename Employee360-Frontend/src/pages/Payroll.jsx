import ResourcePage from "../components/ResourcePage";
import useAuth from "../hooks/useAuth";

const staffRoles = ["ADMIN", "CEO", "HR"];
const allAllowedRoles = [...staffRoles, "EMPLOYEE"];
const fields = [
  { name: "employeeUsername", label: "Employee username", required: true },
  { name: "salaryMonth", label: "Salary month", type: "date", required: true },
  { name: "basicSalary", label: "Basic salary (INR)", type: "number", min: "0", step: "0.01", required: true },
  { name: "allowances", label: "Allowances (INR)", type: "number", min: "0", step: "0.01", required: true, defaultValue: "0" },
  { name: "deductions", label: "Deductions (INR)", type: "number", min: "0", step: "0.01", required: true, defaultValue: "0" }
];
const columns = [
  { key: "employeeUsername", label: "Employee" },
  { key: "salaryMonth", label: "Payroll month" },
  { key: "basicSalary", label: "Basic salary", render: (item) => `INR ${item.basicSalary ?? "—"}` },
  { key: "allowances", label: "Allowances", render: (item) => `INR ${item.allowances ?? "—"}` },
  { key: "deductions", label: "Deductions", render: (item) => `INR ${item.deductions ?? "—"}` },
  { key: "netSalary", label: "Net salary", render: (item) => `INR ${item.netSalary ?? "—"}` },
  { key: "paymentStatus", label: "Status" }
];
const create = {
  method: "post",
  url: "/api/salary-payments",
  serialize: (values) => ({
    ...values,
    allowances: Number(values.allowances),
    basicSalary: Number(values.basicSalary),
    deductions: Number(values.deductions)
  })
};

export default function Payroll() {
  const { role } = useAuth();
  const normalizedRole = (role || "").replace(/^ROLE_/, "").toUpperCase();
  const isEmployee = normalizedRole === "EMPLOYEE";
  return <ResourcePage
    title="Payroll"
    description={isEmployee ? "Review your own salary history." : "Review payroll records and prepare salary entries."}
    icon="bi-wallet2"
    endpoint={isEmployee ? "/api/salary-payments/my" : "/api/salary-payments"}
    columns={isEmployee ? columns.filter((column) => column.key !== "employeeUsername") : columns}
    fields={fields}
    create={isEmployee ? undefined : create}
    allowRoles={allAllowedRoles}
    manageRoles={["ADMIN", "HR"]}
    emptyMessage="No payroll records are available for this account."
  />;
}
