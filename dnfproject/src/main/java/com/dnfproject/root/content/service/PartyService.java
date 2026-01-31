package com.dnfproject.root.content.service;

import com.dnfproject.root.content.db.dto.req.AddPartyMemberReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyGroupReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyReq;
import com.dnfproject.root.content.db.dto.req.InviteAdventureToPartyReq;
import com.dnfproject.root.content.db.dto.req.UpdatePartyGroupNameReq;
import com.dnfproject.root.content.db.dto.req.UpdatePartyNameReq;
import com.dnfproject.root.content.db.dto.req.CreatePartyGroupReq;
import com.dnfproject.root.content.db.dto.req.RemovePartyMemberReq;
import com.dnfproject.root.content.db.dto.req.CreatePartyReq;
import com.dnfproject.root.content.db.dto.req.JoinPartyReq;
import com.dnfproject.root.content.db.dto.res.PartyCreateRes;
import com.dnfproject.root.content.db.dto.res.PartyGroupCreateRes;

public interface PartyService {

    PartyCreateRes createParty(CreatePartyReq request, Long adventureId);

    PartyGroupCreateRes createPartyGroup(CreatePartyGroupReq request, Long adventureId);

    void addPartyMember(AddPartyMemberReq request, Long adventureId);

    void removePartyMember(RemovePartyMemberReq request, Long adventureId);

    PartyCreateRes joinParty(JoinPartyReq request, Long adventureId);

    void updatePartyName(UpdatePartyNameReq request, Long adventureId);

    void updatePartyGroupName(UpdatePartyGroupNameReq request, Long adventureId);

    void inviteAdventureToParty(InviteAdventureToPartyReq request, Long adventureId);

    void removePartyGroup(RemovePartyGroupReq request, Long adventureId);

    void removeParty(RemovePartyReq request, Long adventureId);
}
