package com.dnfproject.root.user.adventure.service;

import com.dnfproject.root.user.adventure.db.dto.res.MemoUpdateRes;

public interface InfoService {

    MemoUpdateRes memoUpdate(String adventureName);
}
