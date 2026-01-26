/*
package com.dnfproject.root.test.controller;

import com.dnfproject.root.test.db.dto.req.TestReq;
import com.dnfproject.root.test.db.dto.res.TestRes;
import com.dnfproject.root.test.db.entity.CharactersEntity;
import com.dnfproject.root.test.db.repository.AdventureRepository;
import com.dnfproject.root.test.db.repository.CharactersRepository;
import com.dnfproject.root.test.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TestController {

    private final TestService testService;
    private final AdventureRepository adventureRepository;
    private final CharactersRepository charactersRepository;

    @GetMapping("/{id}")
    public ResponseEntity<TestRes> getTest(@PathVariable Long id) {
        TestRes response = testService.getTest(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TestRes> createTest(@RequestBody TestReq request) {
        TestRes response = testService.createTest(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestRes> updateTest(@PathVariable Long id, @RequestBody TestReq request) {
        TestRes response = testService.updateTest(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/characters")
    public Object getAllCharacters() {
        return charactersRepository.findAll();
    }
}
*/
