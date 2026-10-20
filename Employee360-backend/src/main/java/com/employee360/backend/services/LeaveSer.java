package com.employee360.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.employee360.backend.dto.request.LeaveRequest;
import com.employee360.backend.dto.response.LeaveApprovalHistoryResponse;
import com.employee360.backend.dto.response.LeaveResponse;
import com.employee360.backend.entities.Employee;
import com.employee360.backend.entities.Leave;
import com.employee360.backend.entities.LeaveApprovalHistory;
import com.employee360.backend.entities.User;
import com.employee360.backend.enums.LeaveStatus;
import com.employee360.backend.enums.Role;
import com.employee360.backend.exception.ResourceNotFoundException;
import com.employee360.backend.repos.EmployeeRepo;
import com.employee360.backend.repos.LeaveApprovalHistoryRepo;
import com.employee360.backend.repos.LeaveRepo;
import com.employee360.backend.repos.UserRepo;

@Service
@Transactional
public class LeaveSer {

    private static final List<LeaveStatus> ACTIVE_STATUSES =
            List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED);
    private static final List<Role> REVIEWER_ROLES =
            List.of(Role.ADMIN, Role.HR, Role.MANAGER);

    private final LeaveRepo leaveRepo;
    private final EmployeeRepo employeeRepo;
    private final UserRepo userRepo;
    private final LeaveApprovalHistoryRepo historyRepo;
    private final NotificationService notificationService;

    public LeaveSer(
            LeaveRepo leaveRepo,
            EmployeeRepo employeeRepo,
            UserRepo userRepo,
            LeaveApprovalHistoryRepo historyRepo,
            NotificationService notificationService) {
        this.leaveRepo = leaveRepo;
        this.employeeRepo = employeeRepo;
        this.userRepo = userRepo;
        this.historyRepo = historyRepo;
        this.notificationService = notificationService;
    }

    public LeaveResponse applyForCurrentUser(LeaveRequest request, String username) {
        User user = requireUser(username);
        Employee employee = requireEmployeeForUser(user);
        return createLeave(request, employee, username);
    }

    public LeaveResponse applyForEmployee(
            LeaveRequest request,
            Long employeeId,
            String actorUsername) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + employeeId));
        return createLeave(request, employee, actorUsername);
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getMyLeaves(String username) {
        Employee employee = requireEmployeeForUser(requireUser(username));
        return leaveRepo.findByEmployee_IdOrderByStartDateDesc(employee.getId())
                .stream()
                .map(LeaveResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> getAllLeaves() {
        return leaveRepo.findAll()
                .stream()
                .map(LeaveResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LeaveResponse getLeaveById(Long id) {
        return LeaveResponse.from(requireLeave(id));
    }

    @Transactional(readOnly = true)
    public List<LeaveApprovalHistoryResponse> getHistory(Long leaveId, String username) {
        Leave leave = requireLeave(leaveId);
        User user = requireUser(username);
        if (!canReview(user.getRole()) && !isOwner(leave, user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot view another employee's leave history");
        }

        return historyRepo.findByLeave_IdOrderByDecidedAtDesc(leaveId)
                .stream()
                .map(LeaveApprovalHistoryResponse::from)
                .toList();
    }

    public LeaveResponse updatePendingLeave(
            LeaveRequest request,
            Long id) {
        Leave leave = requireLeave(id);
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending leave requests can be updated");
        }
        copyRequest(request, leave);
        ensureNoOverlap(request, leave.getEmployee().getId(), leave.getId());
        return LeaveResponse.from(leaveRepo.save(leave));
    }

    public void deletePendingLeave(Long id) {
        Leave leave = requireLeave(id);
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending leave requests can be deleted");
        }
        leaveRepo.delete(leave);
    }

    public void cancelMyLeave(Long leaveId, String username) {
        User user = requireUser(username);
        Leave leave = requireLeave(leaveId);
        if (!isOwner(leave, user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only cancel your own leave requests");
        }
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending leave requests can be cancelled");
        }
        leaveRepo.delete(leave);
    }

    public LeaveResponse approveLeave(Long id, String reviewerUsername, String comment) {
        return reviewLeave(id, reviewerUsername, LeaveStatus.APPROVED, comment);
    }

    public LeaveResponse rejectLeave(Long id, String reviewerUsername, String comment) {
        if (comment == null || comment.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A reason is required when rejecting a leave request");
        }
        return reviewLeave(id, reviewerUsername, LeaveStatus.REJECTED, comment);
    }

    private LeaveResponse reviewLeave(
            Long id,
            String reviewerUsername,
            LeaveStatus decision,
            String comment) {
        Leave leave = requireLeave(id);
        User reviewer = requireUser(reviewerUsername);
        if (!canReview(reviewer.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your role cannot review leave requests");
        }
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This leave request has already been reviewed");
        }
        if (isOwner(leave, reviewer)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot review your own leave request");
        }

        String normalizedComment = comment == null || comment.isBlank()
                ? null
                : comment.trim();
        LocalDateTime decidedAt = LocalDateTime.now();
        leave.setStatus(decision);
        leave.setReviewedBy(reviewer.getUsername());
        leave.setReviewedAt(decidedAt);
        leave.setReviewComment(normalizedComment);
        Leave savedLeave = leaveRepo.save(leave);
        historyRepo.save(new LeaveApprovalHistory(
                savedLeave,
                decision,
                reviewer.getUsername(),
                normalizedComment,
                decidedAt));

        User recipient = userRepo.findByEmailIgnoreCase(leave.getEmployee().getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "The employee does not have a linked user account for notifications"));
        String decisionText = decision == LeaveStatus.APPROVED ? "approved" : "rejected";
        String notificationType = decision == LeaveStatus.APPROVED ? "SUCCESS" : "WARNING";
        notificationService.createNotification(
                recipient.getUsername(),
                "Leave request " + decisionText,
                "Your " + leave.getLeaveType() + " leave from " + leave.getStartDate()
                        + " to " + leave.getEndDate() + " was " + decisionText
                        + (normalizedComment == null ? "." : ". Reviewer note: " + normalizedComment),
                notificationType);

        return LeaveResponse.from(savedLeave);
    }

    private LeaveResponse createLeave(
            LeaveRequest request,
            Employee employee,
            String actorUsername) {
        ensureNoOverlap(request, employee.getId(), null);

        Leave leave = new Leave();
        copyRequest(request, leave);
        leave.setEmployee(employee);
        leave.setStatus(LeaveStatus.PENDING);
        Leave savedLeave = leaveRepo.save(leave);

        String employeeName = (employee.getFirstName() + " " + employee.getLastName()).trim();
        for (User reviewer : userRepo.findByRoleIn(REVIEWER_ROLES)) {
            if (!reviewer.getUsername().equalsIgnoreCase(actorUsername)) {
                notificationService.createNotification(
                        reviewer.getUsername(),
                        "Leave request awaiting review",
                        employeeName + " requested " + leave.getLeaveType() + " leave from "
                                + leave.getStartDate() + " to " + leave.getEndDate() + ".",
                        "INFO");
            }
        }

        return LeaveResponse.from(savedLeave);
    }

    private void ensureNoOverlap(LeaveRequest request, Long employeeId, Long currentLeaveId) {
        employeeRepo.lockById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + employeeId));
        boolean overlapping = leaveRepo
                .existsByEmployee_IdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employeeId,
                        ACTIVE_STATUSES,
                        request.getEndDate(),
                        request.getStartDate());
        if (overlapping && currentLeaveId == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This request overlaps an existing pending or approved leave");
        }
        if (overlapping && currentLeaveId != null) {
            boolean otherOverlap = leaveRepo.findByEmployee_IdOrderByStartDateDesc(employeeId)
                    .stream()
                    .anyMatch(existing -> !existing.getId().equals(currentLeaveId)
                            && ACTIVE_STATUSES.contains(existing.getStatus())
                            && !existing.getStartDate().isAfter(request.getEndDate())
                            && !existing.getEndDate().isBefore(request.getStartDate()));
            if (otherOverlap) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This request overlaps another pending or approved leave");
            }
        }
    }

    private void copyRequest(LeaveRequest request, Leave leave) {
        leave.setLeaveType(request.getLeaveType().trim());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason().trim());
    }

    private Employee requireEmployeeForUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Your account must have an email linked to an employee record before applying for leave");
        }
        Employee employee = employeeRepo.findByEmailIgnoreCase(user.getEmail());
        if (employee == null) {
            throw new ResourceNotFoundException(
                    "No employee record is linked to your account email");
        }
        return employee;
    }

    private User requireUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + username));
    }

    private Leave requireLeave(Long id) {
        return leaveRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave request not found with ID: " + id));
    }

    private boolean isOwner(Leave leave, User user) {
        return leave.getEmployee() != null
                && user.getEmail() != null
                && leave.getEmployee().getEmail() != null
                && leave.getEmployee().getEmail().equalsIgnoreCase(user.getEmail());
    }

    private boolean canReview(Role role) {
        return role != null && REVIEWER_ROLES.contains(role);
    }
}
