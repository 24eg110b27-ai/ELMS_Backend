package com.example.leave_management.dto;

import java.time.LocalDate;

import com.example.leave_management.entity.LeaveRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeaveRequestDTO {

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate returnDate;

    @NotNull
    private LeaveRequest.LeaveType leaveType;

    @NotBlank
    private String reason;

    public LeaveRequestDTO() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LeaveRequest.LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveRequest.LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}