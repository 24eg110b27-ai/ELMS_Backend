package com.example.leave_management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave_management.dto.ManagerLeaveActionRequest;
import com.example.leave_management.dto.ManagerLeaveResponse;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.repository.LeaveRequestRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final LeaveRequestRepository leaveRequestRepository;

    public ManagerController(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<ManagerLeaveResponse>> getAllLeaves() {

        List<ManagerLeaveResponse> leaves =
                leaveRequestRepository
                        .findAllByOrderByAppliedAtDesc()
                        .stream()
                        .map(ManagerLeaveResponse::new)
                        .toList();

        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/leaves/{id}")
    public ResponseEntity<?> getLeave(@PathVariable Long id) {

        LeaveRequest leave = leaveRequestRepository
                .findById(id)
                .orElse(null);

        if (leave == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ManagerLeaveResponse(leave));
    }

@PutMapping("/leaves/{id}/action")
public ResponseEntity<?> updateLeaveStatus(
        @PathVariable Long id,
        @Valid @RequestBody ManagerLeaveActionRequest request
) {

    LeaveRequest leave = leaveRequestRepository
            .findById(id)
            .orElse(null);

    if (leave == null) {
        return ResponseEntity.notFound().build();
    }

    if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
        return ResponseEntity.badRequest()
                .body("Only pending leave requests can be approved or rejected");
    }

    if (request.getApprove()) {
        leave.setStatus(LeaveRequest.LeaveStatus.APPROVED);
    } else {
        leave.setStatus(LeaveRequest.LeaveStatus.REJECTED);
    }

    leave.setManagerComment(request.getManagerComment());

    leaveRequestRepository.save(leave);

    LeaveRequest updatedLeave = leaveRequestRepository
            .findById(id)
            .orElseThrow();

    return ResponseEntity.ok(new ManagerLeaveResponse(updatedLeave));
}

    @GetMapping("/test")
    public String managerTest() {
        return "MANAGER CONTROLLER IS ACTIVE";
    }
}