package com.example.f1.security;

import com.example.f1.entity.User;
import com.example.f1.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void csrfEndpointIssuesCookie() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn();

        List<String> cookies = result.getResponse().getHeaders("Set-Cookie");
        assertThat(cookies).isNotEmpty();
        assertThat(cookies.stream().anyMatch(cookie -> cookie.startsWith("XSRF-TOKEN=") && cookie.contains("SameSite=Lax")))
            .isTrue();
    }

    @Test
    void loginSuccessCreatesSessionCookie() throws Exception {
        User user = new User();
        user.setUsername("crew");
        user.setEmail("crew@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "crew")
                .param("password", "password123")
                .with(csrf().asHeader()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.user.username").value("crew"))
            .andReturn();

        MockHttpServletResponse response = result.getResponse();
        javax.servlet.http.Cookie sessionCookie = response.getCookie("SESSION");
        assertThat(sessionCookie).isNotNull();
        assertThat(sessionCookie.isHttpOnly()).isTrue();

        List<String> setCookies = response.getHeaders("Set-Cookie");
        assertThat(setCookies.stream().anyMatch(cookie -> cookie.contains("SESSION=") && cookie.contains("SameSite=Lax")))
            .isTrue();
        assertThat(setCookies.stream().anyMatch(cookie -> cookie.contains("SESSION=") && cookie.contains("Secure")))
            .isFalse();
    }

    @Test
    void loginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "unknown")
                .param("password", "wrong")
                .with(csrf().asHeader()))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void protectedPostWithoutSessionReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"postId\":1,\"content\":\"hello\"}")
                .with(csrf().asHeader()))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void logoutClearsSessionAndReturnsNoContent() throws Exception {
        User user = new User();
        user.setUsername("logout-user");
        user.setEmail("logout@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "logout-user")
                .param("password", "password123")
                .with(csrf().asHeader()))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse loginResponse = loginResult.getResponse();
        javax.servlet.http.Cookie sessionCookie = loginResponse.getCookie("SESSION");
        assertThat(sessionCookie).isNotNull();
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        MvcResult logoutResult = mockMvc.perform(post("/api/auth/logout")
                .session(session)
                .cookie(sessionCookie)
                .with(csrf().asHeader()))
            .andExpect(status().isNoContent())
            .andReturn();

        List<String> logoutCookies = logoutResult.getResponse().getHeaders("Set-Cookie");
        assertThat(logoutCookies.stream().anyMatch(cookie -> cookie.startsWith("SESSION=") && cookie.contains("Max-Age=0")))
            .isTrue();
    }
}
