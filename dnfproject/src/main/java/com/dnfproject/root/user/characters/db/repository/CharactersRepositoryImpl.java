package com.dnfproject.root.user.characters.db.repository;

import com.dnfproject.root.user.characters.db.dto.CharacterListDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CharactersRepositoryImpl implements CharactersRepositoryCustom {
    
    private final JdbcTemplate jdbcTemplate;
    
    public CharactersRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public List<CharacterListDTO> findCharactersByAdventureId(Long adventureId, String content) {
        String sql = "SELECT " +
                "c.id AS id, " +
                "c.server AS server, " +
                "c.characters_name AS nickname, " +
                "c.job_grow_name AS job, " +
                "c.fame, " +
                "c.memo, " +
                "m.id AS groupId, " +
                "m.group_id AS groupNum, " +
                "state." + content + " AS clearState " +
                "FROM characters c " +
                "LEFT JOIN characters_clear_state state ON c.id = state.id " +
                "LEFT JOIN content_" + content + "_member m ON m.character_id = c.id " +
                "WHERE c.adventure_id = ? " +
                "ORDER BY c.characters_name";
        
        return jdbcTemplate.query(sql, 
                new BeanPropertyRowMapper<>(CharacterListDTO.class),
                adventureId);
    }
    
    @Override
    public void updateCharacterName(String characterId, String characterName) {
        String sql = "UPDATE characters SET characters_name = ?, update_at = NOW() WHERE characters_id = ?";
        jdbcTemplate.update(sql, characterName, characterId);
    }
}
