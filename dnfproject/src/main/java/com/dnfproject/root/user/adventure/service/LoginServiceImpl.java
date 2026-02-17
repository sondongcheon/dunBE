package com.dnfproject.root.user.adventure.service;

import com.dnfproject.root.common.config.JwtUtil;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.user.adventure.db.dto.req.JoinReq;
import com.dnfproject.root.user.adventure.db.dto.req.LoginReq;
import com.dnfproject.root.user.adventure.db.dto.res.LoginRes;
import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import com.dnfproject.root.user.adventure.db.entity.RefreshTokenEntity;
import com.dnfproject.root.user.adventure.db.repository.AdventureRepository;
import com.dnfproject.root.user.adventure.db.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {

    private final AdventureRepository adventureRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public LoginRes login(LoginReq request) {
        if (isBlank(request.getAdventureName()) || isBlank(request.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
        AdventureEntity adventure = adventureRepository.findByAdventureName(request.getAdventureName())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (adventure.getPassword() == null || !passwordEncoder.matches(request.getPassword(), adventure.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        String deviceId = request.getDeviceId() != null ? request.getDeviceId() : generateDeviceId();
        String role = adventure.getRole() != null && !adventure.getRole().isBlank() ? adventure.getRole() : "USER";
        String accessToken = jwtUtil.createAccessToken(adventure.getId(), adventure.getAdventureName(), role);
        String refreshToken = createAndStoreRefreshToken(adventure, deviceId);
        return LoginRes.of(adventure, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginRes join(JoinReq request) {
        if (isBlank(request.getAdventureName()) || isBlank(request.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
        if (adventureRepository.existsByAdventureName(request.getAdventureName())) {
            throw new CustomException(ErrorCode.ADVENTURE_NAME_DUPLICATE);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        AdventureEntity adventure = AdventureEntity.builder()
                .adventureName(request.getAdventureName())
                .password(encodedPassword)
                .role("USER")
                .build();
        adventure = adventureRepository.save(adventure);

        String deviceId = request.getDeviceId() != null ? request.getDeviceId() : generateDeviceId();
        String accessToken = jwtUtil.createAccessToken(adventure.getId(), adventure.getAdventureName(), "USER");
        String refreshToken = createAndStoreRefreshToken(adventure, deviceId);
        return LoginRes.of(adventure, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginRes reissue(String refreshToken) {
        if (isBlank(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        String token = refreshToken;

        try {
            if (jwtUtil.isExpired(token)) {
                throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
            }
            if (!jwtUtil.isRefreshToken(token)) {
                throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
            }
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 토큰으로 직접 조회 (다중 기기 지원)
        RefreshTokenEntity stored = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_INVALID));
        
        if (stored.isExpired()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        AdventureEntity adventure = stored.getAdventure();
        String deviceId = stored.getDeviceId();
        String role = adventure.getRole() != null && !adventure.getRole().isBlank() ? adventure.getRole() : "USER";
        String newAccessToken = jwtUtil.createAccessToken(adventure.getId(), adventure.getAdventureName(), role);
        String newRefreshToken = createAndStoreRefreshToken(adventure, deviceId);
        return LoginRes.of(adventure, newAccessToken, newRefreshToken);
    }

    private String createAndStoreRefreshToken(AdventureEntity adventure, String deviceId) {
        String role = adventure.getRole() != null && !adventure.getRole().isBlank() ? adventure.getRole() : "USER";
        String refreshToken = jwtUtil.createRefreshToken(adventure.getId(), adventure.getAdventureName(), role);
        LocalDateTime expiresAt = LocalDateTime.now().plus(Duration.ofMillis(jwtUtil.getRefreshExpirationMs()));

        // 해당 기기의 기존 토큰 삭제 후 새 토큰 저장
        refreshTokenRepository.deleteByAdventure_IdAndDeviceId(adventure.getId(), deviceId);
        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .adventure(adventure)
                .deviceId(deviceId)
                .token(refreshToken)
                .expiresAt(expiresAt)
                .build());

        return refreshToken;
    }

    private String generateDeviceId() {
        // deviceId가 제공되지 않은 경우 서버에서 생성 (하지만 클라이언트에서 제공하는 것이 권장됨)
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    @Transactional
    public void updatePassword(String adventureName, String newPassword) {
        if (isBlank(adventureName) || isBlank(newPassword)) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
        AdventureEntity adventure = adventureRepository.findByAdventureName(adventureName)
                .orElseThrow(() -> new CustomException(ErrorCode.ADVENTURE_NOT_FOUND));
        String encodedPassword = passwordEncoder.encode(newPassword);
        adventure.updatePassword(encodedPassword);
        adventureRepository.save(adventure);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
