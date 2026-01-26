package com.dnfproject.root.content.db.repository;

import com.dnfproject.root.content.db.dto.GroupListDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupRepositoryImpl implements GroupRepositoryCustom {
    
    private final JdbcTemplate jdbcTemplate;
    
    public GroupRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
    public void deleteMembersByGroupId(Long groupId, String contentName) {
        String memberTableName = "content_" + contentName + "_member";
        String sql = "DELETE FROM " + memberTableName + " WHERE group_id = ?";
        jdbcTemplate.update(sql, groupId);
    }
    
    @Override
    public void addMember(Long groupId, Long characterId, String contentName) {
        String memberTableName = "content_" + contentName + "_member";
        String sql = "INSERT INTO " + memberTableName + " (group_id, character_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, groupId, characterId);
    }
}
