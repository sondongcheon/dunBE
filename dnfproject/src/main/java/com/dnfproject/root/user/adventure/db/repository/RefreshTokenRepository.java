package com.dnfproject.root.user.adventure.db.repository;

import com.dnfproject.root.user.adventure.db.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    Optional<RefreshTokenEntity> findByAdventure_IdAndDeviceId(Long adventureId, String deviceId);
    void deleteByAdventure_IdAndDeviceId(Long adventureId, String deviceId);
    
    /**
     * 만료 시간이 지정된 시간보다 이전인 토큰들을 삭제합니다.
     * @param expiresAt 기준 시간
     * @return 삭제된 레코드 수
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiresAt < :expiresAt")
    long deleteByExpiresAtBefore(@Param("expiresAt") LocalDateTime expiresAt);
}
