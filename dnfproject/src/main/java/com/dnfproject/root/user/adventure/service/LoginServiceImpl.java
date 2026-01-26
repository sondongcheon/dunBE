package com.dnfproject.root.user.adventure.service;

import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.user.adventure.db.dto.req.LoginReq;
import com.dnfproject.root.user.adventure.db.dto.res.LoginRes;
import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import com.dnfproject.root.user.adventure.db.repository.AdventureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {

    private final AdventureRepository adventureRepository;

    @Override
    public LoginRes login(LoginReq request) {
        System.out.println("request.getAdventureName() = " + request.getAdventureName());
        AdventureEntity adventure = adventureRepository.findByAdventureName(request.getAdventureName())
                .orElseThrow(() -> new CustomException(ErrorCode.TEST_EXCEPTION));
        
        return LoginRes.from(adventure);
    }
}
