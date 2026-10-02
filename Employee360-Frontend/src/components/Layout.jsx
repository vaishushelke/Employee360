import { Outlet } from "react-router-dom";

function Layout(){

    return(
        <div className="container-fluid">
            <Outlet />
        </div>
    );

}
export default Layout;