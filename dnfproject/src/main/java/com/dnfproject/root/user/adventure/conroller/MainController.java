package com.dnfproject.root.user.adventure.conroller;

import com.dnfproject.root.common.config.AdventurePrincipal;
import com.dnfproject.root.common.config.CookieUtil;
import com.dnfproject.root.common.config.JwtUtil;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.user.adventure.db.dto.req.JoinReq;
import com.dnfproject.root.user.adventure.db.dto.req.LoginReq;
import com.dnfproject.root.user.adventure.db.dto.req.MemoUpdateFromHtmlReq;
import com.dnfproject.root.user.adventure.db.dto.req.UpdatePasswordReq;
import com.dnfproject.root.user.adventure.db.dto.res.LoginRes;
import com.dnfproject.root.user.adventure.db.dto.res.LoginResBody;
import com.dnfproject.root.user.adventure.db.dto.res.MemoUpdateRes;
import com.dnfproject.root.user.adventure.db.dto.res.MyInfoRes;
import com.dnfproject.root.user.adventure.service.InfoService;
import com.dnfproject.root.user.adventure.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adventure")
@RequiredArgsConstructor
public class MainController {

    private final LoginService loginService;
    private final InfoService infoService;
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
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        LoginRes response = loginService.reissue(refreshToken);
        return buildResponseWithTokenCookies(response);
    }

    @GetMapping("/memoUpdate")
    public ResponseEntity<MemoUpdateRes> memoUpdate(@RequestParam("adventureName") String adventureName, @RequestParam("check") boolean check) {
        MemoUpdateRes response = infoService.memoUpdate(adventureName, check);
        return ResponseEntity.ok(response);
    }

    /**
     * 수동으로 붙여넣은 던담 HTML로 메모 갱신. 본문에서 모험단명을 추출해 등록된 모험단과 일치할 때만 처리.
     */
    @PostMapping("/memoUpdate/html")
    public ResponseEntity<MemoUpdateRes> memoUpdateFromHtml(@RequestBody MemoUpdateFromHtmlReq request) {
        if (request == null || request.getHtml() == null || request.getHtml().isBlank()) {
            throw new CustomException(ErrorCode.HTML_BODY_REQUIRED);
        }
        return ResponseEntity.ok(infoService.memoUpdateFromHtml(request.getHtml()));
    }

    /**
     * 모험단 소속 캐릭터 + 클리어 상태. {@code adventureId} 또는 {@code adventureName} 중 하나만 전달.
     */
    @GetMapping("/my-info")
    public ResponseEntity<MyInfoRes> myInfo(
            @RequestParam(value = "adventureId", required = false) Long adventureId,
            @RequestParam(value = "adventureName", required = false) String adventureName) {
        boolean hasId = adventureId != null;
        boolean hasName = adventureName != null && !adventureName.isBlank();
        if (hasId == hasName) {
            throw new CustomException(ErrorCode.MY_INFO_LOOKUP_PARAM);
        }
        if (hasId) {
            return ResponseEntity.ok(infoService.getMyInfo(adventureId));
        }
        return ResponseEntity.ok(infoService.getMyInfoByAdventureName(adventureName.trim()));
    }

    /** 테스트용: 모험단 비밀번호 수정 */
    @PostMapping("/test/update-password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordReq request) {
        loginService.updatePassword(request.getAdventureName(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test/pass")
    public boolean testPass(@RequestParam(value = "password") String password) {
        return password.equals("3754");
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


    /** 로그인 시 모험단 정보, 미로그인 시 비회원 응답 */
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal AdventurePrincipal principal) {
        if (principal == null) {
            Map<String, Object> body = new HashMap<>();
            body.put("loggedIn", false);
            body.put("adventureId", null);
            body.put("adventureName", null);
            return ResponseEntity.ok(body);
        }
        return ResponseEntity.ok(Map.of(
                "loggedIn", true,
                "adventureId", principal.adventureId(),
                "adventureName", principal.adventureName()
        ));
    }

}
