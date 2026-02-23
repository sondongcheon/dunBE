package com.dnfproject.root.user.characters.db.repository;

import com.dnfproject.root.common.Enums.Servers;
import com.dnfproject.root.user.characters.db.dto.CharacterListDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class CharactersRepositoryImpl implements CharactersRepositoryCustom {

    private static final String IMG_URL_FORMAT = "https://img-api.neople.co.kr/df/servers/%s/characters/%s?zoom=1";

    private static final Map<String, Integer> CONTENT_MIN_FAME = Map.of(
            "azure_main", 44928,
            "goddess_of_death_temple", 48987,
            "venus_goddess_of_beauty", 41928,
            "nabel", 47683,
            "inae", 72687,
            "diregie", 63256
    );

    private final JdbcTemplate jdbcTemplate;

    public CharactersRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CharacterListDTO> findCharactersByAdventureId(Long adventureId, String content) {
        Integer minFame = CONTENT_MIN_FAME.get(content);
        String fameCondition = minFame != null ? " AND c.fame >= ? " : "";

        String sql = "SELECT " +
                "c.id AS id, " +
                "c.server AS server, " +
                "c.characters_id AS charactersId, " +
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
                "WHERE c.adventure_id = ? " + fameCondition +
                "ORDER BY c.fame DESC";

        return minFame != null
                ? jdbcTemplate.query(sql, this::mapToCharacterListDTO, adventureId, minFame)
                : jdbcTemplate.query(sql, this::mapToCharacterListDTO, adventureId);
    }

    private CharacterListDTO mapToCharacterListDTO(ResultSet rs, int rowNum) throws SQLException {
        String server = rs.getString("server");
        String charactersId = rs.getString("charactersId");
        String serverEnglish = Servers.getByName(server != null ? server : "").getEnglishName();
        String img = (charactersId != null && !charactersId.isBlank() && !serverEnglish.isBlank())
                ? String.format(IMG_URL_FORMAT, serverEnglish, charactersId)
                : null;

        return CharacterListDTO.builder()
                .id(rs.getLong("id"))
                .characterId(charactersId)
                .server(server)
                .img(img)
                .nickname(rs.getString("nickname"))
                .job(rs.getString("job"))
                .fame(getIntegerOrNull(rs, "fame"))
                .memo(rs.getString("memo"))
                .groupId(getLongOrNull(rs, "groupId"))
                .groupNum(getLongOrNull(rs, "groupNum"))
                .clearState(toBoolean(rs.getObject("clearState")))
                .build();
    }

    private static Long getLongOrNull(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private static Integer getIntegerOrNull(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static Boolean toBoolean(Object value) {
        if (value == null) return null;
        if (value instanceof Boolean b) return b;
        if (value instanceof Number n) return n.byteValue() != 0;
        return null;
    }
    
    @Override
    public void updateCharacterName(String characterId, String characterName) {
        String sql = "UPDATE characters SET characters_name = ?, update_at = NOW() WHERE characters_id = ?";
        jdbcTemplate.update(sql, characterName, characterId);
    }
}
