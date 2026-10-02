import { useEffect, useState } from "react";
import api from "../services/api";
import "../styles/employees.css";

function Employees() {

    const role = localStorage.getItem("role");
    const canManageEmployees = role === "ADMIN" || role === "HR";

    const [employees, setEmployees] = useState([]);
    const [departments, setDepartments] = useState([]);

    const [showForm, setShowForm] = useState(false);
    const [editingId, setEditingId] = useState(null);

    const [search, setSearch] = useState("");

    const [formData, setFormData] = useState({
        employeeCode: "",
        firstName: "",
        lastName: "",
        email: "",
        designation: "",
        joiningDate: "",
        salary: "",
        status: "Active",
        departmentId: ""
    });

    // =========================
    // LOAD EMPLOYEES
    // =========================

    const loadEmployees = async () => {

        try {

            const response = await api.get("/Employee/getAll");

            setEmployees(response.data);

        } catch (error) {

            console.error("Employee loading error:", error);

            alert("Unable to load employees.");

        }
    };


    // =========================
    // LOAD DEPARTMENTS
    // =========================

    const loadDepartments = async () => {

        try {

            const response = await api.get("/Department/getalld");

            setDepartments(response.data);

        } catch (error) {

            console.error("Department loading error:", error);

        }
    };


    // =========================
    // PAGE LOAD
    // =========================

    useEffect(() => {

        loadEmployees();
        loadDepartments();

    }, []);


    // =========================
    // INPUT CHANGE
    // =========================

    const handleChange = (e) => {

        const { name, value } = e.target;

        setFormData({
            ...formData,
            [name]: value
        });
    };


    // =========================
    // RESET FORM
    // =========================

    const resetForm = () => {

        setFormData({
            employeeCode: "",
            firstName: "",
            lastName: "",
            email: "",
            designation: "",
            joiningDate: "",
            salary: "",
            status: "Active",
            departmentId: ""
        });

        setEditingId(null);
        setShowForm(false);
    };


    // =========================
    // ADD EMPLOYEE
    // =========================

    const handleAddEmployee = async (e) => {

        e.preventDefault();

        if (!canManageEmployees) return;

        try {

            if (!formData.departmentId) {

                alert("Please select a department.");
                return;
            }

            const employeeData = {
                employeeCode: formData.employeeCode,
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                designation: formData.designation,
                joiningDate: formData.joiningDate,
                salary: Number(formData.salary),
                status: formData.status
            };

            await api.post(
                `/Employee/saveE/${formData.departmentId}`,
                employeeData
            );

            alert("Employee created successfully.");

            resetForm();

            loadEmployees();

        } catch (error) {

            console.error("Add employee error:", error);

            if (error.response) {

                alert(
                    "Unable to create employee: " +
                    (error.response.data || "Validation error")
                );

            } else {

                alert("Cannot connect to backend.");

            }
        }
    };


    // =========================
    // EDIT EMPLOYEE
    // =========================

    const handleEdit = (employee) => {

        if (!canManageEmployees) return;

        setEditingId(employee.id);

        setFormData({
            employeeCode: employee.employeeCode || "",
            firstName: employee.firstName || "",
            lastName: employee.lastName || "",
            email: employee.email || "",
            designation: employee.designation || "",
            joiningDate: employee.joiningDate || "",
            salary: employee.salary || "",
            status: employee.status || "Active",
            departmentId: employee.department?.id || ""
        });

        setShowForm(true);

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    };


    // =========================
    // UPDATE EMPLOYEE
    // =========================

    const handleUpdateEmployee = async (e) => {

        e.preventDefault();

        if (!canManageEmployees) return;

        try {

            const employeeData = {
                employeeCode: formData.employeeCode,
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                designation: formData.designation,
                joiningDate: formData.joiningDate,
                salary: Number(formData.salary),
                status: formData.status,
                department: formData.departmentId
                    ? {
                        id: Number(formData.departmentId)
                    }
                    : null
            };

            await api.put(
                `/Employee/update/${editingId}`,
                employeeData
            );

            alert("Employee updated successfully.");

            resetForm();

            loadEmployees();

        } catch (error) {

            console.error("Update employee error:", error);

            if (error.response) {

                alert(
                    "Unable to update employee: " +
                    (error.response.data || "Something went wrong")
                );

            } else {

                alert("Cannot connect to backend.");

            }
        }
    };


    // =========================
    // DELETE EMPLOYEE
    // =========================

    const handleDelete = async (id) => {

        if (!canManageEmployees) return;

        const confirmDelete = window.confirm(
            "Are you sure you want to delete this employee?"
        );

        if (!confirmDelete) {
            return;
        }

        try {

            await api.delete(`/Employee/delete/${id}`);

            alert("Employee deleted successfully.");

            loadEmployees();

        } catch (error) {

            console.error("Delete employee error:", error);

            alert("Unable to delete employee.");

        }
    };


    // =========================
    // SEARCH
    // =========================

    const filteredEmployees = employees.filter((employee) => {

        const searchText = search.toLowerCase();

        return (
            employee.employeeCode?.toLowerCase().includes(searchText) ||
            employee.firstName?.toLowerCase().includes(searchText) ||
            employee.lastName?.toLowerCase().includes(searchText) ||
            employee.email?.toLowerCase().includes(searchText) ||
            employee.designation?.toLowerCase().includes(searchText) ||
            employee.status?.toLowerCase().includes(searchText) ||
            employee.department?.name?.toLowerCase().includes(searchText)
        );

    });


    return (

        <div className="employees-page">

            {/* =========================
                TOP HEADER
            ========================= */}

            <div className="employees-header">

                <div>

                    <p className="page-label">
                        EMPLOYEE360 PORTAL
                    </p>

                    <h1>Employees</h1>

                    <p className="page-description">
                        Manage your organization's employees.
                    </p>

                </div>

                {canManageEmployees && (
                <button
                    className="add-employee-btn"
                    onClick={() => {

                        if (showForm) {
                            resetForm();
                        } else {
                            setShowForm(true);
                        }

                    }}
                >

                    <i
                        className={
                            showForm
                                ? "bi bi-x-lg"
                                : "bi bi-person-plus"
                        }
                    ></i>

                    {showForm
                        ? "Close"
                        : "Add Employee"}

                </button>
                )}

            </div>


            {/* =========================
                EMPLOYEE FORM
            ========================= */}

            {canManageEmployees && showForm && (

                <div className="employee-form-card">

                    <div className="form-header">

                        <div>

                            <p>EMPLOYEE DETAILS</p>

                            <h2>
                                {editingId
                                    ? "Update Employee"
                                    : "Add New Employee"}
                            </h2>

                        </div>

                        <i className="bi bi-person-badge"></i>

                    </div>


                    <form
                        onSubmit={
                            editingId
                                ? handleUpdateEmployee
                                : handleAddEmployee
                        }
                    >

                        <div className="form-grid">

                            <div className="form-group">

                                <label>
                                    Employee Code
                                </label>

                                <input
                                    type="text"
                                    name="employeeCode"
                                    placeholder="EMP001"
                                    value={formData.employeeCode}
                                    onChange={handleChange}
                                    required
                                />

                            </div>


                            <div className="form-group">

                                <label>
                                    First Name
                                </label>

                                <input
                                    type="text"
                                    name="firstName"
                                    placeholder="Enter first name"
                                    value={formData.firstName}
                                    onChange={handleChange}
                                    required
                                />

                            </div>


                            <div className="form-group">

                                <label>
                                    Last Name
                                </label>

                                <input
                                    type="text"
                                    name="lastName"
                                    placeholder="Enter last name"
                                    value={formData.lastName}
                                    onChange={handleChange}
                                    required
                                />

                            </div>


                            <div className="form-group">

                                <label>
                                    Email
                                </label>

                                <input
                                    type="email"
                                    name="email"
                                    placeholder="employee@email.com"
                                    value={formData.email}
                                    onChange={handleChange}
                                    required
                                />

                            </div>


                            <div className="form-group">

                                <label>
                                    Designation
                                </label>

                                <input
                                    type="text"
                                    name="designation"
                                    placeholder="Software Developer"
                                    value={formData.designation}
                                    onChange={handleChange}
                                    required
                                />

                            </div>


                            <div className="form-group">

                                <label>
                                    Joining Date
                                </label>

                                <input
                                    type="date"
                                    name="joiningDate"
                                    value={formData.joiningDate}
                                    onChange={handleChange}
                                    required
                                />

                            </div>


                            <div className="form-group">

                                <label>
                                    Salary
                                </label>

                                <input
                                    type="number"
                                    name="salary"
                                    placeholder="50000"
                                    min="1"
                                    value={formData.salary}
                                    onChange={handleChange}
                                    required
                                />

                            </div>


                            <div className="form-group">

                                <label>
                                    Status
                                </label>

                                <select
                                    name="status"
                                    value={formData.status}
                                    onChange={handleChange}
                                >

                                    <option value="Active">
                                        Active
                                    </option>

                                    <option value="Inactive">
                                        Inactive
                                    </option>

                                    <option value="On Leave">
                                        On Leave
                                    </option>

                                </select>

                            </div>


                            <div className="form-group">

                                <label>
                                    Department
                                </label>

                                <select
                                    name="departmentId"
                                    value={formData.departmentId}
                                    onChange={handleChange}
                                    required
                                >

                                    <option value="">
                                        Select Department
                                    </option>

                                    {departments.map((department) => (

                                        <option
                                            key={department.id}
                                            value={department.id}
                                        >
                                            {department.name}
                                        </option>

                                    ))}

                                </select>

                            </div>

                        </div>


                        <div className="form-actions">

                            <button
                                type="button"
                                className="cancel-btn"
                                onClick={resetForm}
                            >
                                Cancel
                            </button>

                            <button
                                type="submit"
                                className="save-btn"
                            >

                                <i
                                    className={
                                        editingId
                                            ? "bi bi-check-lg"
                                            : "bi bi-plus-lg"
                                    }
                                ></i>

                                {editingId
                                    ? "Update Employee"
                                    : "Save Employee"}

                            </button>

                        </div>

                    </form>

                </div>

            )}


            {/* =========================
                SEARCH + COUNT
            ========================= */}

            <div className="employees-toolbar">

                <div className="search-box">

                    <i className="bi bi-search"></i>

                    <input
                        type="text"
                        placeholder="Search employees..."
                        value={search}
                        onChange={(e) =>
                            setSearch(e.target.value)
                        }
                    />

                </div>

                <div className="employee-count">

                    <strong>
                        {filteredEmployees.length}
                    </strong>

                    <span>
                        Employees
                    </span>

                </div>

            </div>


            {/* =========================
                EMPLOYEE TABLE
            ========================= */}

            <div className="employee-table-card">

                <div className="table-heading">

                    <div>

                        <p>WORKFORCE</p>

                        <h2>Employee List</h2>

                    </div>

                    <i className="bi bi-people-fill"></i>

                </div>


                {filteredEmployees.length === 0 ? (

                    <div className="empty-state">

                        <i className="bi bi-people"></i>

                        <h3>
                            No employees found
                        </h3>

                        <p>
                            Add your first employee to get started.
                        </p>

                    </div>

                ) : (

                    <div className="table-wrapper">

                        <table>

                            <thead>

                                <tr>

                                    <th>Employee</th>
                                    <th>Code</th>
                                    <th>Designation</th>
                                    <th>Department</th>
                                    <th>Email</th>
                                    <th>Joining Date</th>
                                    <th>Salary</th>
                                    <th>Status</th>
                                    {canManageEmployees && <th>Actions</th>}

                                </tr>

                            </thead>


                            <tbody>

                                {filteredEmployees.map((employee) => (

                                    <tr key={employee.id}>

                                        <td>

                                            <div className="employee-name">

                                                <div className="employee-avatar">

                                                    {employee.firstName
                                                        ?.charAt(0)
                                                        .toUpperCase()}

                                                </div>

                                                <div>

                                                    <strong>
                                                        {employee.firstName}{" "}
                                                        {employee.lastName}
                                                    </strong>

                                                    <small>
                                                        ID #{employee.id}
                                                    </small>

                                                </div>

                                            </div>

                                        </td>


                                        <td>
                                            {employee.employeeCode}
                                        </td>


                                        <td>
                                            {employee.designation}
                                        </td>


                                        <td>

                                            {employee.department
                                                ?.name || "N/A"}

                                        </td>


                                        <td>
                                            {employee.email}
                                        </td>


                                        <td>
                                            {employee.joiningDate}
                                        </td>


                                        <td>
                                            ₹{Number(employee.salary).toLocaleString("en-IN")}
                                        </td>


                                        <td>

                                            <span
                                                className={`status-badge ${employee.status
                                                    ?.toLowerCase()
                                                    .replace(/\s+/g, "-")}`}
                                            >
                                                {employee.status}
                                            </span>

                                        </td>


                                        {canManageEmployees && (
                                        <td>

                                            <div className="action-buttons">

                                                <button
                                                    className="edit-btn"
                                                    onClick={() =>
                                                        handleEdit(employee)
                                                    }
                                                    title="Edit"
                                                >
                                                    <i className="bi bi-pencil"></i>
                                                </button>


                                                <button
                                                    className="delete-btn"
                                                    onClick={() =>
                                                        handleDelete(employee.id)
                                                    }
                                                    title="Delete"
                                                >
                                                    <i className="bi bi-trash"></i>
                                                </button>

                                            </div>

                                        </td>
                                        )}

                                    </tr>

                                ))}

                            </tbody>

                        </table>

                    </div>

                )}

            </div>

        </div>
    );
}

export default Employees;