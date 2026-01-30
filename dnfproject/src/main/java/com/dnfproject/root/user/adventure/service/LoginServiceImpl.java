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

    private static final String ROLE_USER = "USER";

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

        String accessToken = jwtUtil.createAccessToken(adventure.getId(), adventure.getAdventureName(), ROLE_USER);
        String refreshToken = createAndStoreRefreshToken(adventure);
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
                .build();
        adventure = adventureRepository.save(adventure);

        String accessToken = jwtUtil.createAccessToken(adventure.getId(), adventure.getAdventureName(), ROLE_USER);
        String refreshToken = createAndStoreRefreshToken(adventure);
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

        Long adventureId = jwtUtil.getAdventureId(token);
        RefreshTokenEntity stored = refreshTokenRepository.findById(adventureId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_INVALID));
        if (stored.isExpired() || !stored.getToken().equals(token)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        AdventureEntity adventure = adventureRepository.findById(adventureId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_INVALID));

        String newAccessToken = jwtUtil.createAccessToken(adventure.getId(), adventure.getAdventureName(), ROLE_USER);
        String newRefreshToken = createAndStoreRefreshToken(adventure);
        return LoginRes.of(adventure, newAccessToken, newRefreshToken);
    }

    private String createAndStoreRefreshToken(AdventureEntity adventure) {
        String refreshToken = jwtUtil.createRefreshToken(adventure.getId(), adventure.getAdventureName(), ROLE_USER);
        LocalDateTime expiresAt = LocalDateTime.now().plus(Duration.ofMillis(jwtUtil.getRefreshExpirationMs()));

        refreshTokenRepository.deleteById(adventure.getId());
        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .adventure(adventure)
                .token(refreshToken)
                .expiresAt(expiresAt)
                .build());

        return refreshToken;
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
