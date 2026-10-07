package com.example.leave_management.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.leave_management.entity.LeaveRequest;

public class EmployeeLeaveResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate returnDate;
    private LeaveRequest.LeaveType leaveType;
    private String reason;
    private LeaveRequest.LeaveStatus status;
    private LocalDateTime appliedAt;
    private String managerComment;

    public EmployeeLeaveResponse(LeaveRequest leave) {
        this.id = leave.getId();
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