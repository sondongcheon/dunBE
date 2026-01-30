package com.dnfproject.root.user.adventure.service;

import com.dnfproject.root.user.adventure.db.dto.req.JoinReq;
import com.dnfproject.root.user.adventure.db.dto.req.LoginReq;
import com.dnfproject.root.user.adventure.db.dto.res.LoginRes;

public interface LoginService {
    LoginRes login(LoginReq request);
    LoginRes join(JoinReq request);
    LoginRes reissue(String refreshToken);
    void updatePassword(String adventureName, String newPassword);
}
