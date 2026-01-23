package com.dnfproject.root.test.service;

import com.dnfproject.root.test.db.dto.req.TestReq;
import com.dnfproject.root.test.db.dto.res.TestRes;

public interface TestService {
    TestRes getTest(Long id);
    TestRes createTest(TestReq request);
    TestRes updateTest(Long id, TestReq request);
    void deleteTest(Long id);
}
