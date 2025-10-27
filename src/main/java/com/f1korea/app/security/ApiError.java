package com.f1korea.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public record ApiError(String code, String message, String traceId) {
  public static void write(HttpServletResponse res, String code, String message) throws IOException {
    res.setContentType("application/json;charset=UTF-8");
    new ObjectMapper().writeValue(res.getOutputStream(), new ApiError(code, message, null));
  }
}
