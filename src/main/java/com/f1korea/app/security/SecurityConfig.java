package com.f1korea.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.*;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain security(HttpSecurity http,
                               AuthenticationEntryPoint entryPoint,
                               AccessDeniedHandler deniedHandler,
                               LogoutSuccessHandler logoutSuccessHandler) throws Exception {
    http
      .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // XSRF-TOKEN 쿠키 발급
      .cors(Customizer.withDefaults())
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
      .headers(h -> h
        .httpStrictTransportSecurity(Customizer.withDefaults())
        .xssProtection(Customizer.withDefaults())
        .contentTypeOptions(Customizer.withDefaults())
        .referrerPolicy(r -> r.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny))
      .exceptionHandling(e -> e
        .authenticationEntryPoint(entryPoint)   // 401 JSON
        .accessDeniedHandler(deniedHandler))    // 403 JSON
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()
        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/races/**").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated())
      .formLogin(form -> form
        .loginProcessingUrl("/api/auth/login")
        .usernameParameter("username").passwordParameter("password")
        .successHandler((req, res, auth) -> res.setStatus(204)) // 로그인 성공 → 204
        .failureHandler((req, res, ex) -> {
          res.setStatus(401);
          ApiError.write(res, "AUTH_FAILED", "인증 실패");
        }))
      .logout(l -> l
        .logoutUrl("/api/auth/logout")
        .logoutSuccessHandler(logoutSuccessHandler)); // 세션 무효화 → 204

    return http.build();
  }

  @Bean
  AuthenticationEntryPoint restAuthEntryPoint() {
    return (req, res, ex) -> {
      res.setStatus(401);
      ApiError.write(res, "UNAUTHORIZED", "인증 필요");
    };
  }

  @Bean
  AccessDeniedHandler restDeniedHandler() {
    return (req, res, ex) -> {
      res.setStatus(403);
      ApiError.write(res, "FORBIDDEN", "권한 없음");
    };
  }

  @Bean
  LogoutSuccessHandler logoutSuccessHandler() {
    return (req, res, auth) -> res.setStatus(204);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration c = new CorsConfiguration();
    // TODO: 팀 프론트엔드 Origin으로 교체 필요(확실하지 않음)
    c.setAllowedOrigins(List.of("https://dev.web.example", "http://localhost:5173"));
    c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("Content-Type","X-XSRF-TOKEN"));
    c.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }

  // 개발용 사용자(프로덕션에서는 DB/JPA 또는 SSO로 대체)
  @Bean @Profile({"dev","local"})
  UserDetailsService inMemoryUsers(PasswordEncoder encoder) {
    UserDetails user = User.withUsername("user")
      .password(encoder.encode("password"))
      .roles("USER").build();
    UserDetails admin = User.withUsername("admin")
      .password(encoder.encode("password"))
      .roles("ADMIN").build();
    return new InMemoryUserDetailsManager(user, admin);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
