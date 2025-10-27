package com.f1korea.app.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecuritySmokeTest {

  @Autowired MockMvc mvc;

  @Test
  void public_endpoint_ok() throws Exception {
    mvc.perform(get("/api/public/ping"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.ok").value(true));
  }

  @Test
  void me_requires_auth() throws Exception {
    mvc.perform(get("/api/me"))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void login_then_me_ok() throws Exception {
    mvc.perform(post("/api/auth/login")
        .with(SecurityMockMvcRequestPostProcessors.csrf()) // 로그인도 CSRF 대상
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("username", "user")
        .param("password", "password"))
      .andExpect(status().isNoContent());

    mvc.perform(get("/api/me").with(SecurityMockMvcRequestPostProcessors.user("user")))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.user").value("user"));
  }

  @Test
  void secure_post_requires_csrf() throws Exception {
    // 미인증 + CSRF 없음 → 401 또는 403 중 정책에 따라 다를 수 있음
    mvc.perform(post("/api/secure/ping"))
      .andExpect(status().isUnauthorized());

    // 인증 + CSRF 없음 → 403
    mvc.perform(post("/api/secure/ping")
        .with(SecurityMockMvcRequestPostProcessors.user("user")))
      .andExpect(status().isForbidden());

    // 인증 + CSRF 있음 → 204
    mvc.perform(post("/api/secure/ping")
        .with(SecurityMockMvcRequestPostProcessors.user("user"))
        .with(SecurityMockMvcRequestPostProcessors.csrf()))
      .andExpect(status().isNoContent());
  }
}
