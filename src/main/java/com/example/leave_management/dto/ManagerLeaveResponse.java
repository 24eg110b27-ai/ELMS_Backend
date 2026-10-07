package com.example.leave_management.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.leave_management.entity.LeaveRequest;

public class ManagerLeaveResponse {

    private Long id;
    private Long employeeId;
    private String employeeUsername;
    private String employeeEmail;
    private LocalDate startDate;
    private LocalDate returnDate;
    private LeaveRequest.LeaveType leaveType;
    private String reason;
    private LeaveRequest.LeaveStatus status;
    private LocalDateTime appliedAt;
    private String managerComment;

    public ManagerLeaveResponse(LeaveRequest leave) {
        this.id = leave.getId();
        this.employeeId = leave.getEmployee().getId();
        this.employeeUsername = leave.getEmployee().getUsername();
        this.employeeEmail = leave.getEmployee().getEmail();
        this.startDate = leave.getStartDate();
        this.returnDate = leave.getReturnDate();
        this.leaveType = leave.getLeaveType();
        this.reason = leave.getReason();
        this.status = leave.getStatus();
        this.appliedAt = leave.getAppliedAt();
        this.managerComment = leave.getManagerComment();
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LeaveRequest.LeaveType getLeaveType() {
        return leaveType;
    }

    public String getReason() {
        return reason;
    }

    public LeaveRequest.LeaveStatus getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public String getManagerComment() {
        return managerComment;
    }
}