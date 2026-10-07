package com.example.leave_management.dto;

import jakarta.validation.constraints.NotNull;

public class ManagerLeaveActionRequest {

    @NotNull
    private Boolean approve;

    private String managerComment;

    public ManagerLeaveActionRequest() {
    }

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }
}