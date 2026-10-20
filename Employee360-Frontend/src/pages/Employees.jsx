import ResourcePage from "../components/ResourcePage";

const roles = ["ADMIN", "CEO", "HR", "MANAGER"];
const currentDate = new Date();
const today = [
  currentDate.getFullYear(),
  String(currentDate.getMonth() + 1).padStart(2, "0"),
  String(currentDate.getDate()).padStart(2, "0")
].join("-");
const fields = [
  { name: "employeeCode", label: "Employee ID", required: true },
  { name: "firstName", label: "First name", required: true },
  { name: "lastName", label: "Last name", required: true },
  { name: "email", label: "Email", type: "email", required: true },
  { name: "designation", label: "Designation", required: true },
  { name: "joiningDate", label: "Joining date", type: "date", max: today, required: true },
  { name: "salary", label: "Salary (INR)", type: "number", min: "0.01", step: "0.01", required: true },
  { name: "status", label: "Status", required: true },
  { name: "departmentId", label: "Department ID", type: "number", min: "1", step: "1", required: true }
];
const numericFields = new Set(["salary", "departmentId"]);
const create = { method: "post", url: "/api/v2/employees", serialize: (values) => Object.fromEntries(Object.entries(values).map(([key, value]) => [key, numericFields.has(key) ? Number(value) : value])) };
const update = { method: "put", url: (employee) => `/api/v2/employees/${employee.id}`, serialize: create.serialize };
const remove = (employee) => `/api/v2/employees/${employee.id}`;
const columns = [
  { key: "employeeCode", label: "Employee ID" },
  { key: "firstName", label: "First name" },
  { key: "lastName", label: "Last name" },
  { key: "email", label: "Email" },
  { key: "designation", label: "Designation" },
  { key: "departmentName", label: "Department" },
  { key: "status", label: "Status" }
];

export default function Employees() {
  return <ResourcePage title="Employees" description="Manage employee records and directory information." icon="bi-people" endpoint="/api/v2/employees" columns={columns} fields={fields} create={create} update={update} remove={remove} allowRoles={roles} />;
}
