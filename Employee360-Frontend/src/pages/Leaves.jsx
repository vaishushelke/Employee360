
import { useEffect, useState } from "react";
import api from "../services/api";

function Leaves() {

    // ==========================================
    // STATE
    // ==========================================

    const [leaves, setLeaves] = useState([]);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");

    const role = localStorage.getItem("role");

const isAdmin = role === "ADMIN";
const isHR = role === "HR";
const isEmployee =
    role === "EMPLOYEE" || role === "USER";

const canApplyLeave = isEmployee || isHR || isAdmin;
const canManageLeave = isAdmin || isHR;
    
    const [leaveForm, setLeaveForm] = useState({
        employeeId: "",
        leaveType: "CASUAL",
        startDate: "",
        endDate: "",
        reason: ""
    });

    // ==========================================
    // LOAD LEAVES
    // ==========================================

    const loadLeaves = async () => {

        try {

            setLoading(true);
            setError("");

            const response =
                await api.get("/Leave/getAll");

            setLeaves(response.data);

        } catch (error) {

            console.error(
                "Error loading leaves:",
                error
            );

            setError(
                "Unable to load leave records."
            );

        } finally {

            setLoading(false);

        }
    };


    // ==========================================
    // LOAD DATA WHEN PAGE OPENS
    // ==========================================

    useEffect(() => {

        loadLeaves();

    }, []);


    // ==========================================
    // APPLY LEAVE
    // ==========================================

    const handleApplyLeave = async (e) => {
        e.preventDefault();

        if (!canApplyLeave) return;

        try {
            await api.post(
                `/Leave/apply/${Number(leaveForm.employeeId)}`,
                {
                    leaveType: leaveForm.leaveType,
                    startDate: leaveForm.startDate,
                    endDate: leaveForm.endDate,
                    reason: leaveForm.reason
                }
            );

            alert("Leave applied successfully.");

            setLeaveForm({
                employeeId: "",
                leaveType: "CASUAL",
                startDate: "",
                endDate: "",
                reason: ""
            });

            loadLeaves();
        } catch (error) {
            console.error("Apply leave error:", error);
            alert(
                error.response?.data ||
                "Unable to apply leave."
            );
        }
    };

    // ==========================================
    // APPROVE LEAVE
    // ==========================================

    const approveLeave = async (id) => {

        try {

            await api.put(
                `/Leave/approve/${id}`
            );

            alert(
                "Leave Approved Successfully"
            );

            loadLeaves();

        } catch (error) {

            console.error(
                "Approve leave error:",
                error
            );

            if (error.response?.status === 403) {

                alert(
                    "You are not authorized to approve this leave."
                );

            } else {

                alert(
                    "Failed to approve leave."
                );
            }
        }
    };


    // ==========================================
    // REJECT LEAVE
    // ==========================================

    const rejectLeave = async (id) => {

        try {

            await api.put(
                `/Leave/reject/${id}`
            );

            alert(
                "Leave Rejected Successfully"
            );

            loadLeaves();

        } catch (error) {

            console.error(
                "Reject leave error:",
                error
            );

            if (error.response?.status === 403) {

                alert(
                    "You are not authorized to reject this leave."
                );

            } else {

                alert(
                    "Failed to reject leave."
                );
            }
        }
    };


    // ==========================================
    // DELETE LEAVE
    // ==========================================

    const deleteLeave = async (id) => {

        if (!canManageLeave) return;

        const confirmDelete =
            window.confirm(
                "Are you sure you want to delete this leave?"
            );

        if (!confirmDelete) {
            return;
        }


        try {

            await api.delete(
                `/Leave/delete/${id}`
            );

            alert(
                "Leave Deleted Successfully"
            );

            loadLeaves();

        } catch (error) {

            console.error(
                "Delete leave error:",
                error
            );

            if (error.response?.status === 403) {

                alert(
                    "You are not authorized to delete this leave."
                );

            } else {

                alert(
                    "Failed to delete leave."
                );
            }
        }
    };


    // ==========================================
    // STATUS BADGE
    // ==========================================

    const getStatusClass = (status) => {

        if (status === "APPROVED") {
            return "bg-success";
        }

        if (status === "REJECTED") {
            return "bg-danger";
        }

        return "bg-warning text-dark";
    };


    // ==========================================
    // LOADING
    // ==========================================

    if (loading) {

        return (

            <div className="container-fluid p-4">

                <div className="text-center">

                    <div
                        className="spinner-border"
                        role="status"
                    ></div>

                    <p className="mt-2">
                        Loading leave records...
                    </p>

                </div>

            </div>

        );
    }


    // ==========================================
    // PAGE
    // ==========================================

    return (

        <div className="container-fluid p-4">

            {/* ==================================
                HEADER
            ================================== */}

            <div className="d-flex justify-content-between align-items-center mb-4">

                <div>

                    <h2 className="fw-bold mb-1">
                        Leave Management
                    </h2>

                    <p className="text-muted mb-0">
                        Manage employee leave requests
                    </p>

                </div>


                <div>

                    <span className="badge bg-dark p-2">

                        {role === "HR"
                            ? "HR / Manager"
                            : role || "Employee"}

                    </span>

                </div>

            </div>


            {/* ==================================
                ERROR
            ================================== */}

            {error && (

                <div
                    className="alert alert-danger"
                    role="alert"
                >
                    {error}
                </div>

            )}


            {/* ==================================
                APPLY LEAVE
            ================================== */}

            {canApplyLeave && (
                <div className="card shadow-sm border-0 mb-4">
                    <div className="card-header">
                        <h5 className="mb-0">Apply for Leave</h5>
                    </div>

                    <div className="card-body">
                        <form onSubmit={handleApplyLeave}>
                            <div className="row">
                                <div className="col-md-2 mb-3">
                                    <label className="form-label">Employee ID</label>
                                    <input
                                        type="number"
                                        min="1"
                                        className="form-control"
                                        value={leaveForm.employeeId}
                                        onChange={(e) =>
                                            setLeaveForm({
                                                ...leaveForm,
                                                employeeId: e.target.value
                                            })
                                        }
                                        required
                                    />
                                </div>

                                <div className="col-md-2 mb-3">
                                    <label className="form-label">Leave Type</label>
                                    <select
                                        className="form-select"
                                        value={leaveForm.leaveType}
                                        onChange={(e) =>
                                            setLeaveForm({
                                                ...leaveForm,
                                                leaveType: e.target.value
                                            })
                                        }
                                        required
                                    >
                                        <option value="CASUAL">Casual</option>
                                        <option value="SICK">Sick</option>
                                        <option value="EARNED">Earned</option>
                                        <option value="OTHER">Other</option>
                                    </select>
                                </div>

                                <div className="col-md-2 mb-3">
                                    <label className="form-label">Start Date</label>
                                    <input
                                        type="date"
                                        className="form-control"
                                        value={leaveForm.startDate}
                                        onChange={(e) =>
                                            setLeaveForm({
                                                ...leaveForm,
                                                startDate: e.target.value
                                            })
                                        }
                                        required
                                    />
                                </div>

                                <div className="col-md-2 mb-3">
                                    <label className="form-label">End Date</label>
                                    <input
                                        type="date"
                                        className="form-control"
                                        value={leaveForm.endDate}
                                        onChange={(e) =>
                                            setLeaveForm({
                                                ...leaveForm,
                                                endDate: e.target.value
                                            })
                                        }
                                        required
                                    />
                                </div>

                                <div className="col-md-4 mb-3">
                                    <label className="form-label">Reason</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={leaveForm.reason}
                                        onChange={(e) =>
                                            setLeaveForm({
                                                ...leaveForm,
                                                reason: e.target.value
                                            })
                                        }
                                        placeholder="Enter reason"
                                        required
                                    />
                                </div>
                            </div>

                            <button type="submit" className="btn btn-primary">
                                <i className="bi bi-send me-1"></i>
                                Apply Leave
                            </button>
                        </form>
                    </div>
                </div>
            )}

            {/* ==================================
                NO DATA
            ================================== */}

            {!error && leaves.length === 0 && (

                <div className="card shadow-sm border-0">

                    <div className="card-body text-center p-5">

                        <i
                            className="bi bi-calendar-x"
                            style={{
                                fontSize: "45px"
                            }}
                        ></i>

                        <h5 className="mt-3">
                            No Leave Records Found
                        </h5>

                        <p className="text-muted">
                            There are currently no leave requests.
                        </p>

                    </div>

                </div>

            )}


            {/* ==================================
                LEAVE TABLE
            ================================== */}

            {leaves.length > 0 && (

                <div className="card shadow-sm border-0">

                    <div className="card-body p-0">

                        <div className="table-responsive">

                            <table className="table table-hover align-middle mb-0">

                                <thead className="table-dark">

                                    <tr>

                                        <th>
                                            ID
                                        </th>

                                        <th>
                                            Employee
                                        </th>

                                        <th>
                                            Leave Type
                                        </th>

                                        <th>
                                            Start Date
                                        </th>

                                        <th>
                                            End Date
                                        </th>

                                        <th>
                                            Reason
                                        </th>

                                        <th>
                                            Status
                                        </th>

                                        <th>
                                            Action
                                        </th>

                                    </tr>

                                </thead>


                                <tbody>

                                    {leaves.map((leave) => (

                                        <tr key={leave.id}>

                                            {/* ID */}

                                            <td>
                                                {leave.id}
                                            </td>


                                            {/* EMPLOYEE */}

                                            <td>

                                                {leave.employee
                                                    ? (
                                                        <>
                                                            {leave.employee.firstName}{" "}
                                                            {leave.employee.lastName}
                                                        </>
                                                    )
                                                    : leave.employeeId ||
                                                      "N/A"}

                                            </td>


                                            {/* LEAVE TYPE */}

                                            <td>
                                                {leave.leaveType || "N/A"}
                                            </td>


                                            {/* START DATE */}

                                            <td>
                                                {leave.startDate || "N/A"}
                                            </td>


                                            {/* END DATE */}

                                            <td>
                                                {leave.endDate || "N/A"}
                                            </td>


                                            {/* REASON */}

                                            <td>

                                                {leave.reason || "N/A"}

                                            </td>


                                            {/* STATUS */}

                                            <td>

                                                <span
                                                    className={`badge ${getStatusClass(
                                                        leave.status
                                                    )}`}
                                                >
                                                    {leave.status || "PENDING"}
                                                </span>

                                            </td>


                                            {/* ACTION */}

                                            <td>

                                                <div className="d-flex gap-2">

                                                    {/* =========================
                                                        ADMIN / HR ACTIONS
                                                    ========================= */}

                                                    {canManageLeave &&
                                                        leave.status === "PENDING" && (
                                                            <>

                                                                <button
                                                                    className="btn btn-success btn-sm"
                                                                    onClick={() =>
                                                                        approveLeave(
                                                                            leave.id
                                                                        )
                                                                    }
                                                                >

                                                                    <i className="bi bi-check-lg me-1"></i>

                                                                    Approve

                                                                </button>


                                                                <button
                                                                    className="btn btn-danger btn-sm"
                                                                    onClick={() =>
                                                                        rejectLeave(
                                                                            leave.id
                                                                        )
                                                                    }
                                                                >

                                                                    <i className="bi bi-x-lg me-1"></i>

                                                                    Reject

                                                                </button>

                                                            </>
                                                        )}


                                                    {/* =========================
                                                        DELETE - ADMIN / HR ONLY
                                                    ========================= */}

                                                    {canManageLeave && (
                                                        <button
                                                            className="btn btn-outline-danger btn-sm"
                                                            onClick={() =>
                                                                deleteLeave(
                                                                    leave.id
                                                                )
                                                            }
                                                        >
                                                            <i className="bi bi-trash"></i>
                                                        </button>
                                                    )}

                                                </div>

                                            </td>

                                        </tr>

                                    ))}

                                </tbody>

                            </table>

                        </div>

                    </div>

                </div>

            )}

        </div>

    );
}

export default Leaves;

