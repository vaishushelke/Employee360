
package com.employee360.backend.services.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.backend.dto.response.DashboardResponse;
import com.employee360.backend.enums.AttendanceStatus;
import com.employee360.backend.enums.LeaveStatus;
import com.employee360.backend.enums.PaymentStatus;
import com.employee360.backend.enums.ReimbursementStatus;
import com.employee360.backend.repos.AttendanceRepo;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.repos.EmployeeRepo;
import com.employee360.backend.repos.LeaveRepo;
import com.employee360.backend.repos.ReimbursementRepo;
import com.employee360.backend.repos.SalaryPaymentRepo;
import com.employee360.backend.services.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final AttendanceRepo attendanceRepo;
    private final LeaveRepo leaveRepo;
    private final SalaryPaymentRepo salaryPaymentRepo;
    private final ReimbursementRepo reimbursementRepo;

    public DashboardServiceImpl(
            EmployeeRepo employeeRepo,
            DepartmentRepo departmentRepo,
            AttendanceRepo attendanceRepo,
            LeaveRepo leaveRepo,
            SalaryPaymentRepo salaryPaymentRepo,
            ReimbursementRepo reimbursementRepo) {

        this.employeeRepo = employeeRepo;
        this.departmentRepo = departmentRepo;
        this.attendanceRepo = attendanceRepo;
        this.leaveRepo = leaveRepo;
        this.salaryPaymentRepo = salaryPaymentRepo;
        this.reimbursementRepo = reimbursementRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardSummary() {

        LocalDate today = LocalDate.now();

        long totalEmployees = employeeRepo.count();
        long totalDepartments = departmentRepo.count();

        long totalAttendanceToday =
                attendanceRepo.countByAttendanceDate(today);

        long presentToday =
                attendanceRepo.countByAttendanceDateAndStatus(
                        today, AttendanceStatus.PRESENT);

        long totalLeaveRequests = leaveRepo.count();

        long pendingLeaveRequests =
                leaveRepo.countByStatus(LeaveStatus.PENDING);

        long totalSalaryPayments = salaryPaymentRepo.count();

        long paidSalaryPayments =
                salaryPaymentRepo.countByPaymentStatus(
                        PaymentStatus.PAID);

        long pendingSalaryPayments =
                salaryPaymentRepo.countByPaymentStatus(
                        PaymentStatus.PENDING);

        long pendingReimbursements =
                reimbursementRepo.countByApprovalStatus(
                        ReimbursementStatus.PENDING);

        long paidReimbursements =
                reimbursementRepo.countByPaymentStatus(
                        PaymentStatus.PAID);

        return new DashboardResponse(
                totalEmployees,
                totalDepartments,
                totalAttendanceToday,
                presentToday,
                totalLeaveRequests,
                pendingLeaveRequests,
                totalSalaryPayments,
                paidSalaryPayments,
                pendingSalaryPayments,
                pendingReimbursements,
                paidReimbursements);
    }
}