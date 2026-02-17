package com.dnfproject.root.user.adventure.conroller;

import com.dnfproject.root.common.config.AdventurePrincipal;
import com.dnfproject.root.common.config.CookieUtil;
import com.dnfproject.root.common.config.JwtUtil;
import com.dnfproject.root.user.adventure.db.dto.req.JoinReq;
import com.dnfproject.root.user.adventure.db.dto.req.LoginReq;
import com.dnfproject.root.user.adventure.db.dto.req.UpdatePasswordReq;
import com.dnfproject.root.user.adventure.db.dto.res.LoginRes;
import com.dnfproject.root.user.adventure.db.dto.res.LoginResBody;
import com.dnfproject.root.user.adventure.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adventure")
@RequiredArgsConstructor
public class MainController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @PostMapping("/join")
    public ResponseEntity<LoginResBody> join(@RequestBody JoinReq request) {
        LoginRes response = loginService.join(request);
        return buildResponseWithTokenCookies(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResBody> login(@RequestBody LoginReq request) {
        LoginRes response = loginService.login(request);
        return buildResponseWithTokenCookies(response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResBody> reissue(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        LoginRes response = loginService.reissue(refreshToken);
        return buildResponseWithTokenCookies(response);
    }

    /** 테스트용: 모험단 비밀번호 수정 */
    @PostMapping("/test/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordReq request) {
        loginService.updatePassword(request.getAdventureName(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<LoginResBody> buildResponseWithTokenCookies(LoginRes response) {
        List<String> cookieHeaders = CookieUtil.createTokenCookies(
                response.getAccessToken(),
                response.getRefreshToken(),
                jwtUtil.getAccessExpirationSeconds(),
                jwtUtil.getRefreshExpirationSeconds()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieHeaders.get(0))
                .header(HttpHeaders.SET_COOKIE, cookieHeaders.get(1))
                .body(LoginResBody.from(response));
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (CookieUtil.REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }


    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal AdventurePrincipal principal) {
        Long adventureId = principal.adventureId();
        String adventureName = principal.adventureName();
        return ResponseEntity.ok(Map.of("adventureId", adventureId, "adventureName", adventureName));
    }

}
