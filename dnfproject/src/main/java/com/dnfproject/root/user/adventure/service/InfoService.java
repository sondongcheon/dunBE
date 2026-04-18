package com.dnfproject.root.user.adventure.service;

import com.dnfproject.root.user.adventure.db.dto.res.MemoUpdateRes;
import com.dnfproject.root.user.adventure.db.dto.res.MyInfoRes;

public interface InfoService {

    MemoUpdateRes memoUpdate(String adventureName, boolean check);

    MemoUpdateRes memoUpdateFromHtml(String html);

    MyInfoRes getMyInfo(Long adventureId);

    MyInfoRes getMyInfoByAdventureName(String adventureName);
}
