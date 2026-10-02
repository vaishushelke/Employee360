
function Navbar() {

    const username = localStorage.getItem("username");
    const role = localStorage.getItem("role");

    return (
        <nav className="navbar navbar-dark bg-primary px-4">

            <span className="navbar-brand mb-0 h1">
                Employee360
            </span>

            <span className="text-white">
                Employee Management System
            </span>

            <div className="text-white text-end">
                <div>
                    👤 {username || "User"}
                </div>

                <small>
                    {role === "HR" ? "HR / Manager" : role || "Employee"}
                </small>
            </div>

        </nav>
    );
}

export default Navbar;
