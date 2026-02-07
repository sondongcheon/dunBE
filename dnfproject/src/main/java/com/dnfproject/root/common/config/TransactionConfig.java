package com.dnfproject.root.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class TransactionConfig {

    /**
     * REQUIRES_NEW 전파로 동작하는 TransactionTemplate.
     * 외부 트랜잭션이 있어도 항상 새 트랜잭션에서 실행되어 rollback-only 전파를 막음.
     */
    @Bean("requiresNewTransactionTemplate")
    public TransactionTemplate requiresNewTransactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return template;
    }
}
