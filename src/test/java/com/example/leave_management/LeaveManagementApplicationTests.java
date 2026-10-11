package com.example.leave_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.leave_management.repository.LeaveRequestRepository;
import com.example.leave_management.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = {
    "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
    "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration",
    "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
})
class LeaveManagementApplicationTests {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LeaveRequestRepository leaveRequestRepository;

	@Test
	void contextLoads() {
	}

}
