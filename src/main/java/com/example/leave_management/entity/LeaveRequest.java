package com.example.leave_management.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "manager_comment", length = 1000)
    private String managerComment;

    public enum LeaveType {
        CASUAL,
        SICK,
        PERSONAL,
        EMERGENCY,
        OTHER
    }

    public enum LeaveStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public LeaveRequest() {
    }

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();

        if (status == null) {
            status = LeaveStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public User getEmployee() {
        return employee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public String getReason() {
        return reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }
}