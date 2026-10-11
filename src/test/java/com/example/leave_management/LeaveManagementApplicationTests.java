package com.example.leave_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import com.example.leave_management.entity.User;
import com.example.leave_management.repository.LeaveRequestRepository;
import com.example.leave_management.repository.UserRepository;
import com.example.leave_management.security.JwtService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(excludeName = {
    "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
    "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration",
    "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
})
class LeaveManagementApplicationTests {

    private static final String ALLOWED_FRONTEND_ORIGIN =
            "https://elmsfrontend.onrender.com";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LeaveRequestRepository leaveRequestRepository;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void registrationAndLoginRoutesArePublicAndValidateRequests() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrationStoresHashedPasswordAndForcesEmployeeRole() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "  new-employee  ",
                                  "email": "NEW-EMPLOYEE@EXAMPLE.TEST",
                                  "password": "safe-test-password",
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration successful"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User createdUser = userCaptor.getValue();

        org.junit.jupiter.api.Assertions.assertEquals(
                "new-employee", createdUser.getUsername());
        org.junit.jupiter.api.Assertions.assertEquals(
                "new-employee@example.test", createdUser.getEmail());
        org.junit.jupiter.api.Assertions.assertEquals(
                User.Role.EMPLOYEE, createdUser.getRole());
        org.junit.jupiter.api.Assertions.assertNotEquals(
                "safe-test-password", createdUser.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(
                passwordEncoder.matches(
                        "safe-test-password", createdUser.getPassword()));
    }

    @Test
    void duplicateRegistrationIsRejectedWithoutSaving() throws Exception {
        when(userRepository.existsByUsername("existing-user")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "existing-user",
                                  "email": "existing@example.test",
                                  "password": "safe-test-password"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registrationRejectsUsernamesContainingAtSign() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "employee@example.test",
                                  "email": "other@example.test",
                                  "password": "safe-test-password"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username cannot contain @"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validCredentialsReturnJwtAndUserRole() throws Exception {
        String encodedPassword = passwordEncoder.encode("safe-test-password");
        User user = new User(
                "login-user",
                "login-user@example.test",
                encodedPassword,
                User.Role.EMPLOYEE);
        when(userRepository.findByUsernameOrEmail("login-user", "login-user"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "login-user",
                                  "password": "safe-test-password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("login-user"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    void invalidCredentialsReturnGenericUnauthorizedResponse() throws Exception {
        String encodedPassword = passwordEncoder.encode("safe-test-password");
        User user = new User(
                "login-user",
                "login-user@example.test",
                encodedPassword,
                User.Role.EMPLOYEE);
        when(userRepository.findByUsernameOrEmail("login-user", "login-user"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "login-user",
                                  "password": "incorrect-test-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    @Test
    void emailCredentialsReturnJwtWithCanonicalUsername() throws Exception {
        String encodedPassword = passwordEncoder.encode("safe-test-password");
        User user = new User(
                "login-user",
                "login-user@example.test",
                encodedPassword,
                User.Role.EMPLOYEE);
        when(userRepository.findByUsernameOrEmail(
                "login-user@example.test", "login-user@example.test"))
                .thenReturn(Optional.of(user));
        when(userRepository.findByUsernameOrEmail("login-user", "login-user"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "  login-user@example.test  ",
                                  "password": "safe-test-password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("login-user"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));
    }

    @Test
    void unknownLoginIdentifierReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "unknown-user",
                                  "password": "safe-test-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    @Test
    void staleJwtDoesNotBlockPublicAuthenticationRoutes() throws Exception {
        UserDetails staleUser = org.springframework.security.core.userdetails.User
                .withUsername("deleted-test-user")
                .password("not-used")
                .authorities("ROLE_EMPLOYEE")
                .build();
        String staleToken = jwtService.generateToken(staleUser);

        mockMvc.perform(post("/api/auth/login")
                        .header("Authorization", "Bearer " + staleToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).findByUsername("deleted-test-user");
    }

    @Test
    void employeeAndManagerRoutesRemainProtected() throws Exception {
        mockMvc.perform(get("/api/employee/leaves"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/manager/leaves"))
                .andExpect(status().isForbidden());
    }

    @Test
    void employeeAndManagerRolesCannotCrossAccessEachOthersRoutes()
            throws Exception {
        User employee = new User(
                "employee-role-test",
                "employee-role-test@example.test",
                "already-encoded",
                User.Role.EMPLOYEE);
        when(userRepository.findByUsername("employee-role-test"))
                .thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/employee/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(
                                "employee-role-test").roles("EMPLOYEE")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/manager/test")
                        .with(SecurityMockMvcRequestPostProcessors.user(
                                "employee-role-test").roles("EMPLOYEE")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/manager/test")
                        .with(SecurityMockMvcRequestPostProcessors.user(
                                "manager-role-test").roles("MANAGER")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/employee/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(
                                "manager-role-test").roles("MANAGER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void corsAllowsConfiguredFrontendPreflightAndRejectsOtherOrigins()
            throws Exception {
        mockMvc.perform(options("/api/auth/register")
                        .header("Origin", ALLOWED_FRONTEND_ORIGIN)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers",
                                "content-type,authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        ALLOWED_FRONTEND_ORIGIN));

        mockMvc.perform(options("/api/auth/register")
                        .header("Origin", "https://untrusted.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
