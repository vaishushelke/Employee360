
import { useEffect, useState } from "react";
import api from "../services/api";
import  "../styles/payroll.css"

function Payroll() {

    const role = localStorage.getItem("role");
    const canManagePayroll = role === "ADMIN" || role === "HR";

    const emptyPayroll = {
        employeeId: "",
        payMonth: "",
        basicSalary: "",
        hra: "",
        allowance: "",
        pf: "",
        tax: "",
        deduction: "",
        netSalary: ""
    };

    const [payrollList, setPayrollList] = useState([]);
    const [payroll, setPayroll] = useState(emptyPayroll);
    const [editingId, setEditingId] = useState(null);
    const [search, setSearch] = useState("");
    const [message, setMessage] = useState("");

    // =========================
    // GET ALL PAYROLL
    // =========================
    const getPayroll = async () => {
        try {
            const response = await api.get("/Payroll/getAll");
            setPayrollList(response.data);
        } catch (error) {
            console.error("Error loading payroll:", error);
            setMessage("Unable to load payroll records");
        }
    };

    useEffect(() => {
        getPayroll();
    }, []);

    // =========================
    // HANDLE INPUT
    // =========================
    const handleChange = (e) => {

        const { name, value } = e.target;

        const updatedPayroll = {
            ...payroll,
            [name]: value
        };

        // Automatic Net Salary
        const basic = Number(updatedPayroll.basicSalary) || 0;
        const hra = Number(updatedPayroll.hra) || 0;
        const allowance = Number(updatedPayroll.allowance) || 0;

        const pf = Number(updatedPayroll.pf) || 0;
        const tax = Number(updatedPayroll.tax) || 0;
        const deduction = Number(updatedPayroll.deduction) || 0;

        updatedPayroll.netSalary =
            basic +
            hra +
            allowance -
            pf -
            tax -
            deduction;

        setPayroll(updatedPayroll);
    };

    // =========================
    // SAVE / UPDATE
    // =========================
    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!canManagePayroll) return;

        try {

            const payrollData = {
                employeeId: Number(payroll.employeeId),
                payMonth: payroll.payMonth,
                basicSalary: Number(payroll.basicSalary),
                hra: Number(payroll.hra) || 0,
                allowance: Number(payroll.allowance) || 0,
                pf: Number(payroll.pf) || 0,
                tax: Number(payroll.tax) || 0,
                deduction: Number(payroll.deduction) || 0,
                netSalary: Number(payroll.netSalary) || 0
            };

            if (editingId) {

                await api.put(
                    `/Payroll/update/${editingId}`,
                    payrollData
                );

                setMessage("Payroll updated successfully!");

            } else {

                await api.post(
                    "/Payroll/save",
                    payrollData
                );

                setMessage("Payroll added successfully!");
            }

            setPayroll(emptyPayroll);
            setEditingId(null);

            getPayroll();

        } catch (error) {

            console.error("Payroll error:", error);

            setMessage(
                error.response?.data ||
                "Unable to save payroll"
            );
        }
    };

    // =========================
    // EDIT
    // =========================
    const editPayroll = (item) => {

        if (!canManagePayroll) return;

        setEditingId(item.id);

        setPayroll({
            employeeId: item.employeeId || "",
            payMonth: item.payMonth || "",
            basicSalary: item.basicSalary || "",
            hra: item.hra || "",
            allowance: item.allowance || "",
            pf: item.pf || "",
            tax: item.tax || "",
            deduction: item.deduction || "",
            netSalary: item.netSalary || ""
        });

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    };

    // =========================
    // CANCEL EDIT
    // =========================
    const cancelEdit = () => {

        setEditingId(null);
        setPayroll(emptyPayroll);
        setMessage("");
    };

    // =========================
    // DELETE
    // =========================
    const deletePayroll = async (id) => {

        if (!canManagePayroll) return;

        if (
            !window.confirm(
                "Are you sure you want to delete this payroll record?"
            )
        ) {
            return;
        }

        try {

            await api.delete(
                `/Payroll/delete/${id}`
            );

            setMessage(
                "Payroll deleted successfully!"
            );

            getPayroll();

        } catch (error) {

            console.error(
                "Delete payroll error:",
                error
            );

            setMessage(
                error.response?.data ||
                "Unable to delete payroll"
            );
        }
    };

    // =========================
    // SEARCH
    // =========================
    const filteredPayroll = payrollList.filter((item) => {

        const searchText =
            search.toLowerCase();

        return (
            item.employeeId
                ?.toString()
                .includes(searchText) ||

            item.payMonth
                ?.toLowerCase()
                .includes(searchText)
        );
    });

    return (

        <div className="container-fluid p-4">

            {/* PAGE HEADER */}

            <div className="d-flex justify-content-between align-items-center mb-4">

                <div>
                    <h2>
                        Payroll Management
                    </h2>

                    <p className="text-muted mb-0">
                        Manage employee payroll records
                    </p>
                </div>

                <div className="badge bg-dark fs-6">
                    {payrollList.length} Payroll Records
                </div>

            </div>


            {/* MESSAGE */}

            {message && (

                <div className="alert alert-info">

                    {message}

                </div>

            )}


            {/* PAYROLL FORM */}

            {canManagePayroll && (
            <div className="card shadow-sm mb-4">

                <div className="card-header">

                    <h5 className="mb-0">

                        {editingId
                            ? "Update Payroll"
                            : "Add Payroll"}

                    </h5>

                </div>


                <div className="card-body">

                    <form onSubmit={handleSubmit}>

                        <div className="row">


                            {/* EMPLOYEE ID */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Employee ID
                                </label>

                                <input
                                    type="number"
                                    name="employeeId"
                                    className="form-control"
                                    value={payroll.employeeId}
                                    onChange={handleChange}
                                    placeholder="Enter employee ID"
                                    min="1"
                                    required
                                />

                            </div>


                            {/* PAY MONTH */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Pay Month
                                </label>

                                <input
                                    type="month"
                                    name="payMonth"
                                    className="form-control"
                                    value={payroll.payMonth}
                                    onChange={handleChange}
                                    required
                                />

                            </div>


                            {/* BASIC */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Basic Salary
                                </label>

                                <input
                                    type="number"
                                    name="basicSalary"
                                    className="form-control"
                                    value={payroll.basicSalary}
                                    onChange={handleChange}
                                    placeholder="Basic salary"
                                    min="0"
                                    required
                                />

                            </div>


                            {/* HRA */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    HRA
                                </label>

                                <input
                                    type="number"
                                    name="hra"
                                    className="form-control"
                                    value={payroll.hra}
                                    onChange={handleChange}
                                    placeholder="HRA"
                                    min="0"
                                />

                            </div>


                            {/* ALLOWANCE */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Allowance
                                </label>

                                <input
                                    type="number"
                                    name="allowance"
                                    className="form-control"
                                    value={payroll.allowance}
                                    onChange={handleChange}
                                    placeholder="Allowance"
                                    min="0"
                                />

                            </div>


                            {/* PF */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    PF
                                </label>

                                <input
                                    type="number"
                                    name="pf"
                                    className="form-control"
                                    value={payroll.pf}
                                    onChange={handleChange}
                                    placeholder="PF"
                                    min="0"
                                />

                            </div>


                            {/* TAX */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Tax
                                </label>

                                <input
                                    type="number"
                                    name="tax"
                                    className="form-control"
                                    value={payroll.tax}
                                    onChange={handleChange}
                                    placeholder="Tax"
                                    min="0"
                                />

                            </div>


                            {/* DEDUCTION */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Other Deduction
                                </label>

                                <input
                                    type="number"
                                    name="deduction"
                                    className="form-control"
                                    value={payroll.deduction}
                                    onChange={handleChange}
                                    placeholder="Other deduction"
                                    min="0"
                                />

                            </div>


                            {/* NET SALARY */}

                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Net Salary
                                </label>

                                <input
                                    type="number"
                                    name="netSalary"
                                    className="form-control"
                                    value={payroll.netSalary}
                                    readOnly
                                />

                            </div>

                        </div>


                        {/* BUTTONS */}

                        <button
                            type="submit"
                            className="btn btn-dark me-2"
                        >

                            <i
                                className={
                                    editingId
                                        ? "bi bi-pencil"
                                        : "bi bi-cash-stack"
                                }
                            ></i>

                            {" "}

                            {editingId
                                ? "Update Payroll"
                                : "Save Payroll"}

                        </button>


                        {editingId && (

                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={cancelEdit}
                            >

                                <i className="bi bi-x-lg"></i>

                                {" "}Cancel

                            </button>

                        )}

                    </form>

                </div>

            </div>
            )}
            {/* PAYROLL LIST */}

            <div className="card shadow-sm">

                <div className="card-header">

                    <div className="d-flex justify-content-between align-items-center">

                        <h5 className="mb-0">
                            Payroll List
                        </h5>


                        {/* SEARCH */}

                        <input
                            type="text"
                            className="form-control"
                            style={{ maxWidth: "280px" }}
                            placeholder="Search Employee ID / Month"
                            value={search}
                            onChange={(e) =>
                                setSearch(e.target.value)
                            }
                        />

                    </div>

                </div>


                <div className="card-body">

                    <div className="table-responsive">

                        <table className="table table-bordered table-hover align-middle">

                            <thead className="table-dark">

                                <tr>

                                    <th>ID</th>
                                    <th>Employee ID</th>
                                    <th>Month</th>
                                    <th>Basic</th>
                                    <th>HRA</th>
                                    <th>Allowance</th>
                                    <th>PF</th>
                                    <th>Tax</th>
                                    <th>Deduction</th>
                                    <th>Net Salary</th>
                                    {canManagePayroll && <th>Actions</th>}

                                </tr>

                            </thead>


                            <tbody>

                                {filteredPayroll.length > 0 ? (

                                    filteredPayroll.map((item) => (

                                        <tr key={item.id}>

                                            <td>
                                                #{item.id}
                                            </td>

                                            <td>
                                                {item.employeeId}
                                            </td>

                                            <td>
                                                {item.payMonth}
                                            </td>

                                            <td>
                                                ₹{item.basicSalary}
                                            </td>

                                            <td>
                                                ₹{item.hra}
                                            </td>

                                            <td>
                                                ₹{item.allowance}
                                            </td>

                                            <td>
                                                ₹{item.pf}
                                            </td>

                                            <td>
                                                ₹{item.tax}
                                            </td>

                                            <td>
                                                ₹{item.deduction}
                                            </td>

                                            <td>
                                                <strong>
                                                    ₹{item.netSalary}
                                                </strong>
                                            </td>

                                            {canManagePayroll && (
                                            <td>

                                                <button
                                                    className="btn btn-sm btn-outline-dark me-1"
                                                    onClick={() =>
                                                        editPayroll(item)
                                                    }
                                                    title="Edit"
                                                >

                                                    <i className="bi bi-pencil"></i>

                                                </button>


                                                <button
                                                    className="btn btn-sm btn-outline-danger"
                                                    onClick={() =>
                                                        deletePayroll(
                                                            item.id
                                                        )
                                                    }
                                                    title="Delete"
                                                >

                                                    <i className="bi bi-trash"></i>

                                                </button>

                                            </td>
                                            )}

                                        </tr>

                                    ))

                                ) : (

                                    <tr>

                                        <td
                                            colSpan={canManagePayroll ? 11 : 10}
                                            className="text-center py-4"
                                        >

                                            {search
                                                ? "No payroll records found for your search"
                                                : "No payroll records found"}

                                        </td>

                                    </tr>

                                )}

                            </tbody>

                        </table>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Payroll;

