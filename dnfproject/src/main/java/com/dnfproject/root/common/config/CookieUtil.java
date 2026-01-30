package com.dnfproject.root.common.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private static final String COOKIE_PATH = "/";
    private static final String SAME_SITE = "Lax";

    private CookieUtil() {
    }

    public static String createAccessTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .path(COOKIE_PATH)
                .httpOnly(true)
                .secure(false)
                .sameSite(SAME_SITE)
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build()
                .toString();
    }

    public static String createRefreshTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .path(COOKIE_PATH)
                .httpOnly(true)
                .secure(false)
                .sameSite(SAME_SITE)
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build()
                .toString();
    }

    public static List<String> createTokenCookies(String accessToken, String refreshToken,
                                                   long accessMaxAgeSeconds, long refreshMaxAgeSeconds) {
        List<String> cookies = new ArrayList<>();
        cookies.add(createAccessTokenCookie(accessToken, accessMaxAgeSeconds));
        cookies.add(createRefreshTokenCookie(refreshToken, refreshMaxAgeSeconds));
        return cookies;
    }

    public static String createLogoutAccessTokenCookie() {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
                .path(COOKIE_PATH)
                .httpOnly(true)
                .maxAge(0)
                .build()
                .toString();
    }

    public static String createLogoutRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .path(COOKIE_PATH)
                .httpOnly(true)
                .maxAge(0)
                .build()
                .toString();
    }
}
