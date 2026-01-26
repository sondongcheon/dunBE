package com.dnfproject.root.user.adventure.conroller;

import com.dnfproject.root.user.adventure.db.dto.req.LoginReq;
import com.dnfproject.root.user.adventure.db.dto.res.LoginRes;
import com.dnfproject.root.user.adventure.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adventure")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MainController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq request) {
        LoginRes response = loginService.login(request);
        return ResponseEntity.ok(response);
    }
}
