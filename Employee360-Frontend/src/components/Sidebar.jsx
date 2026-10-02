import { NavLink } from "react-router-dom"

function  Sidebar(){

    return (

        <div
        className="bg-dark text-white p-3"
        style={{
            width : "230px",
            minHeight : "100vh"
        }}
        >
            <h4 className="text-center mb-4">
                Employee360
            </h4>

        <div className="d-grid gap-2">
            
 <NavLink
                    to="/dashboard"
                    className="btn btn-outline-light text-start"
                >
                    <i className="bi bi-speedometer2 me-2"></i>
                    Dashboard
                </NavLink>

                <NavLink
                    to="/employees"
                    className="btn btn-outline-light text-start"
                >
                    <i className="bi bi-people me-2"></i>
                    Employees
                </NavLink>

                <NavLink
                    to="/departments"
                    className="btn btn-outline-light text-start"
                >
                    <i className="bi bi-building me-2"></i>
                    Departments
                </NavLink>

                <NavLink
                    to="/leaves"
                    className="btn btn-outline-light text-start"
                >
                    <i className="bi bi-calendar-event me-2"></i>
                    Leaves
                </NavLink>

                <NavLink
                    to="/attendance"
                    className="btn btn-outline-light text-start"
                >
                    <i className="bi bi-calendar-check me-2"></i>
                    Attendance
                </NavLink>

                <NavLink
                    to="/payroll"
                    className="btn btn-outline-light text-start"
                >
                    <i className="bi bi-cash-stack me-2"></i>
                    Payroll
                </NavLink>


        </div>


        </div>


    );
}
export default Sidebar;