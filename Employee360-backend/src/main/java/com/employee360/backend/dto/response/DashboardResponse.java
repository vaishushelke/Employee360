
package com.employee360.backend.dto.response;

public class DashboardResponse {

    private long totalEmployees;
    private long totalDepartments;
    private long totalAttendanceToday;
    private long presentToday;
    private long totalLeaveRequests;
    private long pendingLeaveRequests;
    private long totalSalaryPayments;
    private long paidSalaryPayments;
    private long pendingSalaryPayments;
    private long pendingReimbursements;
    private long paidReimbursements;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalEmployees,
            long totalDepartments,
            long totalAttendanceToday,
            long presentToday,
            long totalLeaveRequests,
            long pendingLeaveRequests,
            long totalSalaryPayments,
            long paidSalaryPayments,
            long pendingSalaryPayments,
            long pendingReimbursements,
            long paidReimbursements) {

        this.totalEmployees = totalEmployees;
        this.totalDepartments = totalDepartments;
        this.totalAttendanceToday = totalAttendanceToday;
        this.presentToday = presentToday;
        this.totalLeaveRequests = totalLeaveRequests;
        this.pendingLeaveRequests = pendingLeaveRequests;
        this.totalSalaryPayments = totalSalaryPayments;
        this.paidSalaryPayments = paidSalaryPayments;
        this.pendingSalaryPayments = pendingSalaryPayments;
        this.pendingReimbursements = pendingReimbursements;
        this.paidReimbursements = paidReimbursements;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public long getTotalAttendanceToday() {
        return totalAttendanceToday;
    }

    public long getPresentToday() {
        return presentToday;
    }

    public long getTotalLeaveRequests() {
        return totalLeaveRequests;
    }

    public long getPendingLeaveRequests() {
        return pendingLeaveRequests;
    }

    public long getTotalSalaryPayments() {
        return totalSalaryPayments;
    }

    public long getPaidSalaryPayments() {
        return paidSalaryPayments;
    }

    public long getPendingSalaryPayments() {
        return pendingSalaryPayments;
    }

    public long getPendingReimbursements() {
        return pendingReimbursements;
    }

    public long getPaidReimbursements() {
        return paidReimbursements;
    }
}
