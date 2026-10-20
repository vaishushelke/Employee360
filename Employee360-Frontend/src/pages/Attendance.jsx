import ResourcePage from "../components/ResourcePage";
import useAuth from "../hooks/useAuth";

const roles = ["ADMIN", "CEO", "HR", "MANAGER"];
const columns = [
  { key: "employeeCode", label: "Employee ID" },
  { key: "employeeName", label: "Employee name" },
  { key: "attendanceDate", label: "Date" },
  { key: "checkInTime", label: "Check-in" },
  { key: "checkOutTime", label: "Check-out" },
  { key: "status", label: "Status" }
];
const fields = [
  { name: "employeeCode", label: "Employee ID", required: true },
  { name: "attendanceDate", label: "Date", type: "date", required: true },
  { name: "checkInTime", label: "Check-in time", type: "time" },
  { name: "checkOutTime", label: "Check-out time", type: "time" },
  { name: "status", label: "Status", type: "select", required: true, options: [
    { value: "PRESENT", label: "Present" },
    { value: "ABSENT", label: "Absent" },
    { value: "HALF_DAY", label: "Half day" },
    { value: "LEAVE", label: "On leave" }
  ] }
];
const serialize = (values) => ({ ...values, employeeCode: values.employeeCode.trim() });

export default function Attendance() {
  const { role } = useAuth();
  const normalizedRole = String(role || "").replace(/^ROLE_/, "").toUpperCase();
  const isEmployee = normalizedRole === "EMPLOYEE";
  return <ResourcePage
    title="Attendance"
    description={isEmployee ? "View your own attendance history." : "View and maintain attendance records."}
    icon="bi-clock-history"
    endpoint={isEmployee ? "/Attendance/my" : "/Attendance/getAll"}
    columns={columns}
    fields={isEmployee ? [] : fields}
    create={isEmployee ? undefined : { method: "post", url: "/Attendance/save", serialize }}
    update={isEmployee ? undefined : { method: "put", url: (record) => `/Attendance/update/${record.id}`, serialize }}
    remove={isEmployee ? undefined : (record) => `/Attendance/delete/${record.id}`}
    allowRoles={[...roles, "EMPLOYEE"]}
    manageRoles={roles}
    emptyMessage="No attendance records are available for this account."
  />;
}
