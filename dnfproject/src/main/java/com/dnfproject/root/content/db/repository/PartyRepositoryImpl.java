package com.dnfproject.root.content.db.repository;

import com.dnfproject.root.common.Enums.Servers;
import com.dnfproject.root.content.db.dto.res.AdventureCharacterInRes;
import com.dnfproject.root.content.db.dto.res.AdventureInPartyRes;
import com.dnfproject.root.content.db.dto.res.PartyCreateRes;
import com.dnfproject.root.content.db.dto.res.PartyGroupCreateRes;
import com.dnfproject.root.content.db.dto.res.PartyGroupInRes;
import com.dnfproject.root.content.db.dto.res.PartyInContentRes;
import com.dnfproject.root.content.db.dto.res.PartyMemberInRes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class PartyRepositoryImpl implements PartyRepositoryCustom {

    private static final Map<String, Integer> CONTENT_MIN_FAME = Map.of(
            "azure_main", 44928,
            "goddess_of_death_temple", 48987,
            "venus_goddess_of_beauty", 41927,
            "nabel", 47683,
            "inae", 72687,
            "diregie", 63256
    );
    private final JdbcTemplate jdbcTemplate;

    public PartyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PartyCreateRes createParty(String content, String name, String password, Long adventureId) {
        String partyTable = "content_" + content + "_party";
        String partyAdventureTable = "content_" + content + "_party_adventure";

        String partySql = "INSERT INTO " + partyTable + " (name, password, create_at, update_at) VALUES (?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(partySql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, password != null ? password : "");
            return ps;
        }, keyHolder);

        Long partyId = keyHolder.getKey().longValue();

        String partyAdventureSql = "INSERT INTO " + partyAdventureTable + " (party_id, adventure_id, leader, create_at, update_at) VALUES (?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(partyAdventureSql, partyId, adventureId, true);

        String selectSql = "SELECT id, name, password, create_at AS createAt, update_at AS updateAt FROM " + partyTable + " WHERE id = ?";
        return jdbcTemplate.queryForObject(selectSql,
                (rs, rowNum) -> PartyCreateRes.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .password(rs.getString("password"))
                        .createAt(rs.getTimestamp("createAt").toLocalDateTime())
                        .updateAt(rs.getTimestamp("updateAt").toLocalDateTime())
                        .build(),
                partyId);
    }

    @Override
    public Map<Long, PartyInContentRes> findPartiesByAdventureId(String content, Long adventureId) {
        String partyTable = "content_" + content + "_party";
        String partyAdventureTable = "content_" + content + "_party_adventure";
        String partyGroupTable = "content_" + content + "_party_group";
        String partyMemberTable = "content_" + content + "_party_member";
        Integer needFame = CONTENT_MIN_FAME.get(content);

        //String partyAdventureSql = "SELECT party_id, leader FROM " + partyAdventureTable + " WHERE adventure_id = ? ORDER BY party_id";
        String sql = "SELECT padv.id, padv.party_id, p.name, padv.adventure_id, cha.id as chaid, cha.characters_name, cha.server, cha.characters_id, cha.job_grow_name, cha.fame, cha.memo, padv.leader, adv.adventure_name, g.id as group_id, g.name AS group_name, sta." + content + " as state "
                + " FROM ( SELECT DISTINCT party_id FROM " + partyAdventureTable + " WHERE adventure_id = ? ) myp "
                + " JOIN " + partyAdventureTable + " padv ON padv.party_id = myp.party_id "
                + " JOIN adventure adv ON adv.id = padv.adventure_id "
                + " JOIN characters cha ON cha.adventure_id = padv.adventure_id "
                + " JOIN characters_clear_state sta ON sta.id = cha.id "
                + " JOIN " + partyTable + " p ON padv.party_id = p.id "
                + " LEFT JOIN " + partyMemberTable + " m ON m.character_id = cha.id "
                + " LEFT JOIN " + partyGroupTable + " g ON g.id = m.party_group_id AND g.party_id = padv.party_id "
                + " WHERE cha.fame > " + needFame
                + " ORDER BY padv.party_id, padv.adventure_id, cha.fame DESC";
        Map<Long, PartyInContentRes> partyMap = new HashMap<>();

        List<Map<String, Object>> partyRows = jdbcTemplate.queryForList(sql, adventureId);
        for ( Map<String, Object> row : partyRows) {
            Long partyId = ((Number) row.get("party_id")).longValue();
            PartyInContentRes party = partyMap.get(partyId);
            if(party == null) {
                party = new PartyInContentRes(partyId, (String) row.get("name"));
            }
            if( !party.isLeader() && toBoolean(row.get("leader")) && adventureId.equals(((Number) row.get("adventure_id")).longValue())) {
                party.setLeader(toBoolean(row.get("leader")));
            }

            Long rowAdventureId = ((Number) row.get("adventure_id")).longValue();
            AdventureInPartyRes adventure = party.getAdventures().get(rowAdventureId);
            if(adventure == null) {
                adventure = new AdventureInPartyRes(rowAdventureId, (String) row.get("adventure_name"));
                party.getAdventures().put(rowAdventureId, adventure);
            }
            adventure.addCharacter(
                    ((Number) row.get("chaid")).longValue(),
                    (String) row.get("characters_id"),
                    (String) row.get("characters_name"),
                    (String) row.get("server"),
                    (String) row.get("job_grow_name"),
                    toInteger(row.get("fame")),
                    (String) row.get("memo")
            );

            if( row.get("group_id") != null ) {
                Long rowGroupId = ((Number) row.get("group_id")).longValue();
                PartyGroupInRes partyRes = party.getGroups().get(rowGroupId);
                if (partyRes == null) {
                    partyRes = new PartyGroupInRes(rowGroupId, (String) row.get("group_name"));
                    party.getGroups().put(rowGroupId, partyRes);
                }
                partyRes.addMember(
                        ((Number) row.get("chaid")).longValue(),
                        ((Number) row.get("adventure_id")).longValue(),
                        (String) row.get("characters_id"),
                        (String) row.get("characters_name"),
                        (String) row.get("adventure_name"),
                        (String) row.get("server"),
                        (String) row.get("job_grow_name"),
                        toInteger(row.get("fame")),
                        (String) row.get("memo"),
                        toBoolean(row.get("state"))
                );
            }

            partyMap.put(partyId, party);
        }
        return partyMap;
    }
    private static List<AdventureInPartyRes> buildAdventuresList(
            List<Long> adventureIds,
            List<String> adventureNames,
            Map<Long, List<AdventureCharacterInRes>> adventureCharactersMap) {
        List<AdventureInPartyRes> result = new ArrayList<>();
        for (int i = 0; i < adventureIds.size(); i++) {
            Long advId = adventureIds.get(i);
            String advName = i < adventureNames.size() ? adventureNames.get(i) : "";
            List<AdventureCharacterInRes> chars = adventureCharactersMap.getOrDefault(advId, List.of());
            result.add(AdventureInPartyRes.builder()
                    .id(advId)
                    .name(advName)
                    .characters(chars)
                    .build());
        }
        return result;
    }

    @Override
    public boolean existsAdventureInParty(String content, Long partyId, Long adventureId) {
        String partyAdventureTable = "content_" + content + "_party_adventure";
        String sql = "SELECT 1 FROM " + partyAdventureTable + " WHERE party_id = ? AND adventure_id = ? LIMIT 1";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, partyId, adventureId);
        return !rows.isEmpty();
    }

    @Override
    public PartyGroupCreateRes createPartyGroup(String content, Long partyId, String name) {
        String partyGroupTable = "content_" + content + "_party_group";

        String insertSql = "INSERT INTO " + partyGroupTable + " (party_id, name, create_at, update_at) VALUES (?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, partyId);
            ps.setString(2, name);
            return ps;
        }, keyHolder);

        Long groupId = keyHolder.getKey().longValue();

        return PartyGroupCreateRes.builder()
                .id(groupId)
                .name(name)
                .members(List.of())
                .build();
    }

    @Override
    public Optional<Long> getPartyIdByGroupId(String content, Long partyGroupId) {
        String partyGroupTable = "content_" + content + "_party_group";
        String sql = "SELECT party_id FROM " + partyGroupTable + " WHERE id = ? LIMIT 1";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, partyGroupId);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(((Number) rows.get(0).get("party_id")).longValue());
    }

    @Override
    public boolean existsCharacterInPartyGroup(String content, Long partyGroupId, Long characterId) {
        String partyMemberTable = "content_" + content + "_party_member";
        String sql = "SELECT 1 FROM " + partyMemberTable + " WHERE party_group_id = ? AND character_id = ? LIMIT 1";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, partyGroupId, characterId);
        return !rows.isEmpty();
    }

    @Override
    public void addPartyMember(String content, Long partyGroupId, Long characterId) {
        String partyMemberTable = "content_" + content + "_party_member";
        String sql = "INSERT INTO " + partyMemberTable + " (party_group_id, character_id, create_at, update_at) VALUES (?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql, partyGroupId, characterId);
    }

    @Override
    public void removePartyMember(String content, Long partyGroupId, Long characterId) {
        String partyMemberTable = "content_" + content + "_party_member";
        String sql = "DELETE FROM " + partyMemberTable + " WHERE party_group_id = ? AND character_id = ?";
        jdbcTemplate.update(sql, partyGroupId, characterId);
    }

    @Override
    public Optional<PartyRepositoryCustom.PartyJoinInfo> findPartyToJoin(String content, String partyName, String leaderAdventureName) {
        String partyTable = "content_" + content + "_party";
        String partyAdventureTable = "content_" + content + "_party_adventure";
        String sql = "SELECT p.id, p.name, p.password " +
                "FROM " + partyTable + " p " +
                "JOIN " + partyAdventureTable + " pa ON p.id = pa.party_id AND pa.leader = true " +
                "JOIN adventure a ON pa.adventure_id = a.id " +
                "WHERE p.name = ? AND a.adventure_name = ? LIMIT 1";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, partyName, leaderAdventureName);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        Map<String, Object> row = rows.get(0);
        PartyRepositoryCustom.PartyJoinInfo info = new PartyRepositoryCustom.PartyJoinInfo(
                ((Number) row.get("id")).longValue(),
                (String) row.get("name"),
                row.get("password") != null ? row.get("password").toString() : ""
        );
        return Optional.of(info);
    }

    @Override
    public void addAdventureToParty(String content, Long partyId, Long adventureId) {
        String partyAdventureTable = "content_" + content + "_party_adventure";
        String sql = "INSERT INTO " + partyAdventureTable + " (party_id, adventure_id, leader, create_at, update_at) VALUES (?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql, partyId, adventureId, false);
    }

    @Override
    public boolean isPartyLeader(String content, Long partyId, Long adventureId) {
        String partyAdventureTable = "content_" + content + "_party_adventure";
        String sql = "SELECT 1 FROM " + partyAdventureTable + " WHERE party_id = ? AND adventure_id = ? AND leader = true LIMIT 1";
        return !jdbcTemplate.queryForList(sql, partyId, adventureId).isEmpty();
    }

    @Override
    public void updatePartyName(String content, Long partyId, String name) {
        String partyTable = "content_" + content + "_party";
        String sql = "UPDATE " + partyTable + " SET name = ?, update_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, name, partyId);
    }

    @Override
    public void updatePartyGroupName(String content, Long partyGroupId, String name) {
        String partyGroupTable = "content_" + content + "_party_group";
        String sql = "UPDATE " + partyGroupTable + " SET name = ?, update_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, name, partyGroupId);
    }

    @Override
    public void deleteMembersByPartyGroupId(String content, Long partyGroupId) {
        String partyMemberTable = "content_" + content + "_party_member";
        String sql = "DELETE FROM " + partyMemberTable + " WHERE party_group_id = ?";
        jdbcTemplate.update(sql, partyGroupId);
    }

    @Override
    public void deletePartyGroup(String content, Long partyGroupId) {
        String partyGroupTable = "content_" + content + "_party_group";
        String sql = "DELETE FROM " + partyGroupTable + " WHERE id = ?";
        jdbcTemplate.update(sql, partyGroupId);
    }

    @Override
    public void deleteParty(String content, Long partyId) {
        String partyTable = "content_" + content + "_party";
        String partyGroupTable = "content_" + content + "_party_group";
        String partyMemberTable = "content_" + content + "_party_member";
        String partyAdventureTable = "content_" + content + "_party_adventure";

        String deleteMembersSql = "DELETE FROM " + partyMemberTable + " WHERE party_group_id IN (SELECT id FROM " + partyGroupTable + " WHERE party_id = ?)";
        jdbcTemplate.update(deleteMembersSql, partyId);

        String deleteGroupsSql = "DELETE FROM " + partyGroupTable + " WHERE party_id = ?";
        jdbcTemplate.update(deleteGroupsSql, partyId);

        String deleteAdventuresSql = "DELETE FROM " + partyAdventureTable + " WHERE party_id = ?";
        jdbcTemplate.update(deleteAdventuresSql, partyId);

        String deletePartySql = "DELETE FROM " + partyTable + " WHERE id = ?";
        jdbcTemplate.update(deletePartySql, partyId);
    }

    private static boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof Number n) return n.byteValue() != 0;
        return false;
    }

    private static Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
