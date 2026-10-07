package com.example.leave_management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave_management.dto.EmployeeLeaveResponse;
import com.example.leave_management.dto.LeaveRequestDTO;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.entity.User;
import com.example.leave_management.repository.LeaveRequestRepository;
import com.example.leave_management.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public EmployeeController(
            UserRepository userRepository,
            LeaveRequestRepository leaveRequestRepository) {
        this.userRepository = userRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(new EmployeeProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        ));
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<EmployeeLeaveResponse>> getMyLeaves(
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        List<EmployeeLeaveResponse> leaves =
                leaveRequestRepository
                        .findByEmployeeOrderByAppliedAtDesc(user)
                        .stream()
                        .map(EmployeeLeaveResponse::new)
                        .toList();

        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/leaves/{id}")
    public ResponseEntity<?> getLeave(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        LeaveRequest leaveRequest =
                leaveRequestRepository.findById(id).orElse(null);

        if (leaveRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if (!leaveRequest.getEmployee().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        return ResponseEntity.ok(new EmployeeLeaveResponse(leaveRequest));
    }

    @PostMapping("/leaves")
    public ResponseEntity<?> applyLeave(
            @Valid @RequestBody LeaveRequestDTO request,
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        if (request.getReturnDate().isBefore(request.getStartDate())) {
            return ResponseEntity.badRequest()
                    .body("Return date cannot be before start date");
        }

        LeaveRequest leaveRequest = new LeaveRequest();

        leaveRequest.setEmployee(user);
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setReturnDate(request.getReturnDate());
        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);

        LeaveRequest saved =
                leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok(
                new EmployeeLeaveResponse(saved)
        );
    }

    public record EmployeeProfileResponse(
            Long id,
            String username,
            String email,
            String role
    ) {
    }
}