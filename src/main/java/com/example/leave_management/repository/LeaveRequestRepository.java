package com.example.leave_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.entity.User;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeOrderByAppliedAtDesc(User employee);

    @EntityGraph(attributePaths = "employee")
    List<LeaveRequest> findAllByOrderByAppliedAtDesc();

    @EntityGraph(attributePaths = "employee")
    Optional<LeaveRequest> findById(Long id);

    List<LeaveRequest> findByStatusOrderByAppliedAtDesc(
            LeaveRequest.LeaveStatus status
    );
}