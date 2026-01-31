package com.dnfproject.root.content.db.repository;

import com.dnfproject.root.content.db.dto.GroupListDTO;
import com.dnfproject.root.content.db.dto.res.GroupCreateRes;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class GroupRepositoryImpl implements GroupRepositoryCustom {
    
    private final JdbcTemplate jdbcTemplate;
    
    public GroupRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public GroupCreateRes createGroup(String contentName, Long adventureId, String name) {
        String tableName = "content_" + contentName + "_group";
        String insertSql = "INSERT INTO " + tableName + " (adventure_id, name, create_at, update_at) VALUES (?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, adventureId);
            ps.setString(2, name);
            return ps;
        }, keyHolder);

        Long groupId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        return GroupCreateRes.builder()
                .id(groupId)
                .name(name)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
    }
    
    @Override
    public List<GroupListDTO> findGroupsByAdventureId(Long adventureId, String contentName) {
        String tableName = "content_" + contentName + "_group";
        String sql = "SELECT " +
                "id, " +
                "adventure_id AS adventureId, " +
                "name, " +
                "create_at AS createAt, " +
                "update_at AS updateAt " +
                "FROM " + tableName + " " +
                "WHERE adventure_id = ? " +
                "ORDER BY name";
        
        return jdbcTemplate.query(sql, 
                new BeanPropertyRowMapper<>(GroupListDTO.class),
                adventureId);
    }
    
    @Override
    public void deleteMembersByGroupIdAndCharacterId(Long groupId, Long characterId, String contentName) {
        String memberTableName = "content_" + contentName + "_member";
        String sql = "DELETE FROM " + memberTableName + " WHERE group_id = ? and character_id = ?";
        jdbcTemplate.update(sql, groupId, characterId);
    }
    
    @Override
    public void addMember(Long groupId, Long characterId, String contentName) {
        String memberTableName = "content_" + contentName + "_member";
        String sql = "INSERT INTO " + memberTableName + " (group_id, character_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, groupId, characterId);
    }

    @Override
    public boolean existsGroupByAdventureId(String contentName, Long groupId, Long adventureId) {
        String tableName = "content_" + contentName + "_group";
        String sql = "SELECT 1 FROM " + tableName + " WHERE id = ? AND adventure_id = ? LIMIT 1";
        return !jdbcTemplate.queryForList(sql, groupId, adventureId).isEmpty();
    }

    @Override
    public void updateGroupName(String contentName, Long groupId, String name) {
        String tableName = "content_" + contentName + "_group";
        String sql = "UPDATE " + tableName + " SET name = ?, update_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, name, groupId);
    }

    @Override
    public void deleteGroup(String contentName, Long groupId) {
        String memberTableName = "content_" + contentName + "_member";
        String groupTableName = "content_" + contentName + "_group";

        jdbcTemplate.update("DELETE FROM " + memberTableName + " WHERE group_id = ?", groupId);
        jdbcTemplate.update("DELETE FROM " + groupTableName + " WHERE id = ?", groupId);
    }
}
