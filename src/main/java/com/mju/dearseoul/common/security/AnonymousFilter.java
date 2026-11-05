package com.mju.dearseoul.common.security;

import com.mju.dearseoul.common.exception.ErrorCode;
import com.mju.dearseoul.common.response.ErrorResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Duration;
import java.util.*;

/** A persistent, random bearer cookie. Only its hash is stored as the record owner. */
@Component
public class AnonymousFilter extends OncePerRequestFilter {
    private final SecureRandom random = new SecureRandom();
    private final boolean secure;
    private final JsonMapper jsonMapper;

    public AnonymousFilter(@Value("${server.servlet.session.cookie.secure:false}") boolean secure, JsonMapper jsonMapper) {
        this.secure = secure;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("X-Frame-Options", "DENY");
        if (!request.getRequestURI().startsWith("/api/")) { chain.doFilter(request, response); return; }
        response.setHeader("Cache-Control", "no-store");
        if (!Set.of("GET", "HEAD", "OPTIONS").contains(request.getMethod())) {
            String origin = request.getHeader("Origin");
            String expected = request.getScheme() + "://" + request.getHeader("Host");
            if ("cross-site".equals(request.getHeader("Sec-Fetch-Site")) || (origin != null && !origin.equals(expected))) {
                response.setStatus(ErrorCode.ORIGIN_REJECTED.getStatus().value());
                response.setContentType("application/json;charset=UTF-8");
                jsonMapper.writeValue(response.getWriter(), ErrorResponse.of(ErrorCode.ORIGIN_REJECTED));
                return;
            }
        }
        String token = null;
        if (request.getCookies() != null) for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("mailbox_session") && cookie.getValue().matches("[a-f0-9]{64}")) token = cookie.getValue();
        }
        if (token == null) {
            byte[] bytes = new byte[32]; random.nextBytes(bytes); token = HexFormat.of().formatHex(bytes);
            response.addHeader("Set-Cookie", ResponseCookie.from("mailbox_session", token).httpOnly(true).secure(secure)
                    .sameSite("Strict").path("/").maxAge(Duration.ofDays(30)).build().toString());
        }
        try {
            request.setAttribute("anonymousOwner", HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
        chain.doFilter(request, response);
    }
}
