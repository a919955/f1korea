package com.f1korea.app.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeController {

  @GetMapping("/public/ping")
  public Map<String, Object> publicPing() {
    return Map.of("ok", true);
  }

  // 보호 엔드포인트: 인증 필요
  @GetMapping("/me")
  public Map<String, Object> me(Authentication auth) {
    return Map.of("user", auth.getName(), "authorities", auth.getAuthorities());
  }

  // CSRF 검증 대상 예시(POST). 성공 시 204
  @PostMapping("/secure/ping")
  public void securePing() { /* no body */ }
}
