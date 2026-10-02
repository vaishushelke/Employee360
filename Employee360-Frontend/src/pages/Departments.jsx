import { useEffect, useState } from "react";
import api from "../services/api";
import "../styles/departments.css";

function Departments() {

    const role = localStorage.getItem("role");
    const canManageDepartments = role === "ADMIN" || role === "HR";

    const [departments, setDepartments] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        description: ""
    });

    const [editingId, setEditingId] = useState(null);
    const [search, setSearch] = useState("");

    // ==============================
    // GET ALL DEPARTMENTS
    // ==============================
    const loadDepartments = async () => {

        try {

            const response = await api.get("/Department/getalld");

            console.log("Departments:", response.data);

            setDepartments(response.data);

        } catch (error) {

            console.error(
                "Error loading departments:",
                error
            );

            if (error.response) {
                console.error(
                    "Backend response:",
                    error.response.data
                );
            }
        }
    };


    // ==============================
    // LOAD DATA WHEN PAGE OPENS
    // ==============================
    useEffect(() => {
        loadDepartments();
    }, []);


    // ==============================
    // HANDLE INPUT CHANGE
    // ==============================
    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };


    // ==============================
    // ADD / UPDATE DEPARTMENT
    // ==============================
    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!canManageDepartments) return;

        try {

            if (editingId) {

                // UPDATE
                await api.put(
                    `/Department/updateById/${editingId}`,
                    formData
                );

                alert("Department updated successfully");

            } else {

                // CREATE
                await api.post(
                    "/Department/saved",
                    formData
                );

                alert("Department created successfully");
            }


            // Clear form
            setFormData({
                name: "",
                description: ""
            });

            // Exit edit mode
            setEditingId(null);

            // Reload departments
            loadDepartments();

        } catch (error) {

            console.error(
                "Error saving department:",
                error
            );

            alert(
                error.response?.data ||
                "Something went wrong"
            );
        }
    };


    // ==============================
    // EDIT DEPARTMENT
    // ==============================
    const handleEdit = (department) => {

        if (!canManageDepartments) return;

        setEditingId(department.id);

        setFormData({
            name: department.name || "",
            description: department.description || ""
        });

        // Scroll to top
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    };


    // ==============================
    // DELETE DEPARTMENT
    // ==============================
    const handleDelete = async (id) => {

        if (!canManageDepartments) return;

        const confirmDelete = window.confirm(
            "Are you sure you want to delete this department?"
        );

        if (!confirmDelete) {
            return;
        }

        try {

            await api.delete(
                `/Department/deleteById/${id}`
            );

            alert("Department deleted successfully");

            loadDepartments();

        } catch (error) {

            console.error(
                "Error deleting department:",
                error
            );

            alert(
                error.response?.data ||
                "Unable to delete department"
            );
        }
    };


    // ==============================
    // CANCEL EDIT
    // ==============================
    const handleCancel = () => {

        setEditingId(null);

        setFormData({
            name: "",
            description: ""
        });
    };


    // ==============================
    // SEARCH DEPARTMENT
    // ==============================
    const filteredDepartments =
        departments.filter((department) =>

            department.name
                ?.toLowerCase()
                .includes(search.toLowerCase()) ||

            department.description
                ?.toLowerCase()
                .includes(search.toLowerCase())
        );


    return (

        <div className="departments-page">

            {/* ==============================
                PAGE HEADER
            ============================== */}

            <div className="page-header">

                <div>

                    <h1>Departments</h1>

                    <p>
                        Manage company departments
                    </p>

                </div>


                <div className="department-count">

                    <i className="bi bi-building"></i>

                    {departments.length} Departments

                </div>

            </div>


            {/* ==============================
                ADD / UPDATE FORM
            ============================== */}

            {canManageDepartments && (
            <div className="department-form-card">

                <h2>
                    {editingId
                        ? "Update Department"
                        : "Add Department"}
                </h2>


                <form onSubmit={handleSubmit}>

                    <div className="form-row">


                        {/* DEPARTMENT NAME */}

                        <div className="form-group">

                            <label>
                                Department Name
                            </label>

                            <input
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                placeholder="Enter department name"
                                required
                            />

                        </div>


                        {/* DESCRIPTION */}

                        <div className="form-group">

                            <label>
                                Description
                            </label>

                            <input
                                type="text"
                                name="description"
                                value={formData.description}
                                onChange={handleChange}
                                placeholder="Enter department description"
                                required
                            />

                        </div>

                    </div>


                    {/* BUTTONS */}

                    <div className="form-buttons">

                        <button
                            type="submit"
                            className="btn-primary"
                        >

                            <i
                                className={
                                    editingId
                                        ? "bi bi-pencil"
                                        : "bi bi-plus-lg"
                                }
                            ></i>

                            {editingId
                                ? " Update Department"
                                : " Add Department"}

                        </button>


                        {editingId && (

                            <button
                                type="button"
                                className="btn-secondary"
                                onClick={handleCancel}
                            >

                                <i className="bi bi-x-lg"></i>

                                {" "}Cancel

                            </button>

                        )}

                    </div>

                </form>

            </div>
            )}


            {/* ==============================
                DEPARTMENT LIST
            ============================== */}

            <div className="department-list-card">


                {/* HEADER */}

                <div className="list-header">

                    <div>

                        <h2>
                            Department List
                        </h2>

                        <p>
                            View and manage all departments
                        </p>

                    </div>


                    {/* SEARCH */}

                    <div className="search-box">

                        <i className="bi bi-search"></i>

                        <input
                            type="text"
                            placeholder="Search department..."
                            value={search}
                            onChange={(e) =>
                                setSearch(e.target.value)
                            }
                        />

                    </div>

                </div>


                {/* ==============================
                    TABLE
                ============================== */}

                <div className="table-container">

                    <table>

                        <thead>

                            <tr>

                                <th>
                                    ID
                                </th>

                                <th>
                                    Department Name
                                </th>

                                <th>
                                    Description
                                </th>

                                {canManageDepartments && (
                                    <th>
                                        Actions
                                    </th>
                                )}

                            </tr>

                        </thead>


                        <tbody>

                            {filteredDepartments.length > 0 ? (

                                filteredDepartments.map(
                                    (department) => (

                                        <tr
                                            key={department.id}
                                        >

                                            {/* ID */}

                                            <td>
                                                #{department.id}
                                            </td>


                                            {/* NAME */}

                                            <td>

                                                <strong>
                                                    {department.name}
                                                </strong>

                                            </td>


                                            {/* DESCRIPTION */}

                                            <td>
                                                {department.description}
                                            </td>


                                            {/* ACTIONS */}

                                            {canManageDepartments && (
                                            <td>

                                                <button
                                                    className="action-edit"
                                                    onClick={() =>
                                                        handleEdit(
                                                            department
                                                        )
                                                    }
                                                    title="Edit"
                                                >

                                                    <i className="bi bi-pencil"></i>

                                                </button>


                                                <button
                                                    className="action-delete"
                                                    onClick={() =>
                                                        handleDelete(
                                                            department.id
                                                        )
                                                    }
                                                    title="Delete"
                                                >

                                                    <i className="bi bi-trash"></i>

                                                </button>

                                            </td>
                                            )}

                                        </tr>

                                    )
                                )

                            ) : (

                                <tr>

                                    <td
                                        colSpan={canManageDepartments ? 4 : 3}
                                        className="no-data"
                                    >

                                        {search
                                            ? "No departments found for your search"
                                            : "No departments found"}

                                    </td>

                                </tr>

                            )}

                        </tbody>

                    </table>

                </div>

            </div>

        </div>
    );
}

export default Departments;