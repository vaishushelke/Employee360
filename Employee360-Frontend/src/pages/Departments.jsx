import ResourcePage from "../components/ResourcePage";

const columns = [
  { key: "name", label: "Department" },
  { key: "description", label: "Description" }
];
const fields = [
  { name: "name", label: "Department name", required: true },
  { name: "description", label: "Description", type: "textarea", required: true, wide: true }
];

export default function Departments() {
  return <ResourcePage
    title="Departments"
    description="Maintain your organization's department structure."
    icon="bi-diagram-3"
    endpoint="/Department/getAll"
    columns={columns}
    fields={fields}
    create={{ method: "post", url: "/Department/saved" }}
    update={{ method: "put", url: (department) => `/Department/updateById/${department.id}` }}
    remove={(department) => `/Department/deleteById/${department.id}`}
    allowRoles={["ADMIN", "CEO", "HR", "MANAGER"]}
    manageRoles={["ADMIN", "CEO", "HR"]}
  />;
}
