package com.dnfproject.root.user.adventure.db.repository;

import com.dnfproject.root.user.adventure.db.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    Optional<RefreshTokenEntity> findByAdventureIdAndDeviceId(Long adventureId, String deviceId);
    void deleteByAdventureIdAndDeviceId(Long adventureId, String deviceId);
}
