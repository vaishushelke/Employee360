# Employee360 Frontend

React 19 + Vite frontend for the Employee360 Spring Boot application. The UI uses Bootstrap 5, Bootstrap Icons, Recharts, React Router, and Axios.

## Run locally

1. Start the Spring Boot API on `http://localhost:8080`.
2. In this directory, install frontend packages with `npm install`.
3. Create `.env.local` if the backend uses another URL:

   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```

4. Start Vite with `npm run dev` and open the URL it prints (normally `http://localhost:5173`).

The backend datasource defaults to `jdbc:mysql://localhost:3306/employee360`. Set `DB_USERNAME` and `DB_PASSWORD` in the backend process environment; for PowerShell, use `$env:DB_USERNAME="your-db-user"` and `$env:DB_PASSWORD="your-db-password"` before starting Spring Boot. Credentials are intentionally not stored in source control.

Check the backend console and browser Network panel for API errors. The browser frontend does not connect directly to MySQL.

## Available scripts

- `npm run dev` — local development server
- `npm run build` — production build
- `npm run preview` — serve the production build locally
- `npm run lint` — run ESLint

## Backend endpoints used

| Frontend feature | Spring Boot endpoint(s) |
| --- | --- |
| Login, registration, email OTP | `POST /auth/login`, `POST /auth/register`, `POST /auth/send-otp`, `POST /auth/verify-otp` |
| Dashboard | `GET /api/dashboard/summary` (backend roles: ADMIN, CEO, HR, MANAGER) |
| Employees | `/api/v2/employees` (list/create), `/api/v2/employees/{id}` (update/delete), `GET /api/v2/employees/search`, `GET /api/v2/employees/page` |
| Departments | `GET /Department/getAll` (`/Department/getalld` remains supported), `POST /Department/saved`, `PUT /Department/updateById/{id}`, `DELETE /Department/deleteById/{id}` |
| Attendance | `GET /Attendance/getAll`, `POST /Attendance/save`, `PUT /Attendance/update/{id}`, `DELETE /Attendance/delete/{id}` |
| Leaves | `GET /Leave/getAll`, `PUT /Leave/approve/{id}`, `PUT /Leave/reject/{id}` |
| Payroll / payments | `GET /api/salary-payments`, `GET /api/salary-payments/my`, `POST /api/salary-payments`, plus salary status/detail routes |
| Reimbursements | `GET /api/reimbursements`, `GET /api/reimbursements/my`, `POST /api/reimbursements`, review and payment routes |
| Documents | `GET /api/documents`, `POST /api/documents`, `GET /api/documents/{id}/download`, `DELETE /api/documents/{id}` |
| Notifications | `GET /api/notifications`, `GET /api/notifications/unread/count`, `PATCH /api/notifications/{id}/read`, `PATCH /api/notifications/read-all` |
| Quizzes | `/api/quizzes` list, details/questions, attempt start/submit, and `/api/quizzes/my-results` |
| Learning catalog | `GET /api/learning/catalog`; Admin topic CRUD via `POST /api/learning/catalog/topics`, `PUT /api/learning/catalog/topics/{topicId}`, and `DELETE /api/learning/catalog/topics/{topicId}` (archives the topic and preserves its slots) |

The Axios client attaches the JWT to protected API calls and skips sending it to login, registration, and OTP routes. Public registration submits only username, email, and password; the backend assigns the EMPLOYEE role.

On backend startup, the learning catalog seeder idempotently adds the nine department outlines, 10 topics per department, 10 slots per topic, and five difficulty levels (20 planned questions per level). It creates a department if missing and corrects the Marketing description only when it duplicates Finance's supplied description. Admin-created topics use the existing topic, slot, and level tables; deletion is a soft archive so associated slots and planned levels are retained. The catalog contains planned structure, not quiz questions.

## Backend work still needed

- **Password recovery:** the current API does not define forgot-password or reset-password endpoints. Those screens explain the missing backend flow instead of issuing failing requests.
- **Personal leaves:** the current API only provides the all-leaves route and takes an employee ID in its apply route. Add authenticated `/my` and apply endpoints that derive the employee from the logged-in principal before enabling employee self-service; until then, employee accounts are not sent the company-wide leave request.
- **Reports:** no reports endpoint is present. The page states this rather than calling a missing endpoint or showing fabricated reports.
- **Profile updates:** there is no profile read/update endpoint; the page only shows the authenticated username and role.
- **Security:** enforce authorization and ownership checks in Spring Security/controllers for every protected data-changing and salary/employee route. Frontend role checks only control presentation and are not a security boundary.

The current backend uses `http://localhost:5173` as the CORS origin in some controllers. Add the deployed frontend origin to the backend CORS configuration before deploying the UI.
