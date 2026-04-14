package com.dnfproject.root.common.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class PlaywrightWarmupRunner {

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        CompletableFuture.runAsync(() -> {
            long startedAt = System.currentTimeMillis();
            log.info("Playwright warmup started.");
            try (Playwright playwright = Playwright.create();
                 Browser browser = playwright.chromium().launch()) {
                Page page = browser.newPage();
                page.navigate("about:blank");
                log.info("Playwright warmup finished in {} ms.", System.currentTimeMillis() - startedAt);
            } catch (Exception e) {
                log.warn("Playwright warmup failed: {}", e.getMessage());
            }
        });
    }
}
