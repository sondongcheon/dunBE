package com.dnfproject.root.common.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String CATEGORY_ACCESS = "Authorization";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractAccessToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(token)) {
                sendUnauthorized(response, "access token expired");
                return;
            }
        } catch (ExpiredJwtException e) {
            sendUnauthorized(response, "access token expired");
            return;
        }

        if (!CATEGORY_ACCESS.equals(jwtUtil.getCategory(token))) {
            sendUnauthorized(response, "invalid access token");
            return;
        }

        String plainRole = jwtUtil.getRole(token);  // token에는 USER, ADMIN만 저장
        String role = toRoleAuthority(plainRole);   // ROLE_USER, ROLE_ADMIN
        Long adventureId = jwtUtil.getAdventureId(token);
        String adventureName = jwtUtil.getAdventureName(token);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        Authentication auth = new UsernamePasswordAuthenticationToken(
                new AdventurePrincipal(adventureId, adventureName, role), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    /** plain role(USER, ADMIN) → ROLE_USER, ROLE_ADMIN */
    private static String toRoleAuthority(String plainRole) {
        if (plainRole == null || plainRole.isBlank()) return "ROLE_USER";
        return plainRole.startsWith("ROLE_") ? plainRole : "ROLE_" + plainRole;
    }

    private String extractAccessToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (CookieUtil.ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String bearer = request.getHeader("Authorization");
        return jwtUtil.resolveBearerToken(bearer);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.print(message);
    }
}
