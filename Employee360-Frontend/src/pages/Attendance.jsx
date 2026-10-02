import { useEffect, useState } from "react";
import api from "../services/api";

function Attendance() {

    const role = localStorage.getItem("role");
    const canManageAttendance = role === "ADMIN" || role === "HR";

    const [attendanceList, setAttendanceList] = useState([]);

    const [attendance, setAttendance] = useState({
        employeeId: "",
        attendanceDate: "",
        status: "",
        checkInTime: "",
        checkOutTime: ""
    });

    const [message, setMessage] = useState("");

    // Get all attendance
    const getAttendance = async () => {

        try {

            const response = await api.get("/Attendance/getAll");

            setAttendanceList(response.data);

        } catch (error) {

            console.error(error);

            setMessage("Unable to load attendance");
        }
    };

    // Load attendance when page opens
    useEffect(() => {
        let cancelled = false;

        const loadAttendance = async () => {
            try {
                const response = await api.get("/Attendance/getAll");

                if (!cancelled) {
                    setAttendanceList(response.data);
                }
            } catch (error) {
                console.error(error);

                if (!cancelled) {
                    setMessage("Unable to load attendance");
                }
            }
        };

        loadAttendance();

        return () => {
            cancelled = true;
        };
    }, []);

    // Handle input changes
    const handleChange = (e) => {

        const { name, value } = e.target;

        setAttendance({
            ...attendance,
            [name]: value
        });
    };

    // Save attendance
    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!canManageAttendance) return;

        try {

            await api.post("/Attendance/save", attendance);

            setMessage("Attendance added successfully!");

            setAttendance({
                employeeId: "",
                attendanceDate: "",
                status: "",
                checkInTime: "",
                checkOutTime: ""
            });

            getAttendance();

        } catch (error) {

            console.error(error);

            setMessage("Unable to save attendance");
        }
    };

    // Delete attendance
    const deleteAttendance = async (id) => {

        if (!canManageAttendance) return;

        if (!window.confirm(
            "Are you sure you want to delete this attendance?"
        )) {
            return;
        }

        try {

            await api.delete(`/Attendance/delete/${id}`);

            setMessage("Attendance deleted successfully!");

            getAttendance();

        } catch (error) {

            console.error(error);

            setMessage("Unable to delete attendance");
        }
    };

    return (
        <div className="container-fluid p-4">

            <h2 className="mb-4">
                Attendance Management
            </h2>

            {/* Message */}
            {message && (
                <div className="alert alert-info">
                    {message}
                </div>
            )}

            {/* Add Attendance */}
            {canManageAttendance && (
            <div className="card shadow-sm mb-4">

                <div className="card-header">

                    <h5 className="mb-0">
                        Add Attendance
                    </h5>

                </div>

                <div className="card-body">

                    <form onSubmit={handleSubmit}>

                        <div className="row">

                            {/* Employee ID */}
                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Employee ID
                                </label>

                                <input
                                    type="number"
                                    name="employeeId"
                                    className="form-control"
                                    placeholder="Enter employee ID"
                                    value={attendance.employeeId}
                                    onChange={handleChange}
                                    required
                                />

                            </div>

                            {/* Attendance Date */}
                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Attendance Date
                                </label>

                                <input
                                    type="date"
                                    name="attendanceDate"
                                    className="form-control"
                                    value={attendance.attendanceDate}
                                    onChange={handleChange}
                                    required
                                />

                            </div>

                            {/* Status */}
                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Status
                                </label>

                                <select
                                    name="status"
                                    className="form-select"
                                    value={attendance.status}
                                    onChange={handleChange}
                                    required
                                >

                                    <option value="">
                                        Select Status
                                    </option>

                                    <option value="PRESENT">
                                        Present
                                    </option>

                                    <option value="ABSENT">
                                        Absent
                                    </option>

                                    <option value="LEAVE">
                                        Leave
                                    </option>

                                </select>

                            </div>

                            {/* Check In */}
                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Check In Time
                                </label>

                                <input
                                    type="time"
                                    name="checkInTime"
                                    className="form-control"
                                    value={attendance.checkInTime}
                                    onChange={handleChange}
                                />

                            </div>

                            {/* Check Out */}
                            <div className="col-md-4 mb-3">

                                <label className="form-label">
                                    Check Out Time
                                </label>

                                <input
                                    type="time"
                                    name="checkOutTime"
                                    className="form-control"
                                    value={attendance.checkOutTime}
                                    onChange={handleChange}
                                />

                            </div>

                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary"
                        >
                            <i className="bi bi-clock"></i>
                            {" "}Save Attendance
                        </button>

                    </form>

                </div>

            </div>
            )}
            {/* Attendance List */}
            <div className="card shadow-sm">

                <div className="card-header">

                    <h5 className="mb-0">
                        Attendance List
                    </h5>

                </div>

                <div className="card-body">

                    <div className="table-responsive">

                        <table className="table table-bordered table-hover">

                            <thead className="table-dark">

                                <tr>

                                    <th>ID</th>
                                    <th>Employee ID</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th>Check In</th>
                                    <th>Check Out</th>
                                    {canManageAttendance && <th>Action</th>}

                                </tr>

                            </thead>

                            <tbody>

                                {attendanceList.length === 0 ? (

                                    <tr>

                                        <td
                                            colSpan={canManageAttendance ? 7 : 6}
                                            className="text-center"
                                        >
                                            No attendance records found
                                        </td>

                                    </tr>

                                ) : (

                                    attendanceList.map((item) => (

                                        <tr key={item.id}>

                                            <td>
                                                {item.id}
                                            </td>

                                            <td>
                                                {item.employeeId}
                                            </td>

                                            <td>
                                                {item.attendanceDate}
                                            </td>

                                            <td>

                                                {item.status === "PRESENT" ? (
                                                    <span className="badge bg-success">
                                                        Present
                                                    </span>
                                                ) : item.status === "ABSENT" ? (
                                                    <span className="badge bg-danger">
                                                        Absent
                                                    </span>
                                                ) : (
                                                    <span className="badge bg-warning text-dark">
                                                        {item.status}
                                                    </span>
                                                )}

                                            </td>

                                            <td>
                                                {item.checkInTime || "-"}
                                            </td>

                                            <td>
                                                {item.checkOutTime || "-"}
                                            </td>

                                            {canManageAttendance && (
                                            <td>

                                                <button
                                                    className="btn btn-danger btn-sm"
                                                    onClick={() =>
                                                        deleteAttendance(item.id)
                                                    }
                                                >
                                                    <i className="bi bi-trash"></i>
                                                    {" "}Delete
                                                </button>

                                            </td>
                                            )}

                                        </tr>

                                    ))

                                )}

                            </tbody>

                        </table>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Attendance;