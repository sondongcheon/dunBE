package com.dnfproject.root.user.adventure.service;

import com.dnfproject.root.common.Enums.Servers;
import com.dnfproject.root.user.adventure.db.dto.res.MemoUpdateRes;
import com.dnfproject.root.user.adventure.db.entity.AdventureEntity;
import com.dnfproject.root.user.adventure.db.repository.AdventureRepository;
import com.dnfproject.root.user.characters.db.entity.CharactersEntity;
import com.dnfproject.root.user.characters.db.repository.CharactersRepository;
import com.dnfproject.root.user.characters.service.CharacterService;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private static final long MEMO_UPDATE_INTERVAL_SECONDS = 1 * 60 * 60;

    private final AdventureRepository adventureRepository;
    private final CharacterService characterService;
    private final CharactersRepository charactersRepository;


    @Override
    @Transactional
    public MemoUpdateRes memoUpdate(String adventureName) {
        Optional<AdventureEntity> adventureOptional = adventureRepository.findByAdventureName(adventureName);
        if (adventureOptional.isEmpty()) {
            return MemoUpdateRes.builder()
                    .message("등록되지 않은 모험단입니다.")
                    .build();
        }

        AdventureEntity adventure = adventureOptional.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMemoUpdateAt = adventure.getLastMemoUpdateAt();
        LocalDateTime nextAvailableAt = lastMemoUpdateAt.plusSeconds(MEMO_UPDATE_INTERVAL_SECONDS);

        if (now.isBefore(nextAvailableAt)) {
            long remainingSeconds = Duration.between(now, nextAvailableAt).getSeconds();
            return MemoUpdateRes.builder()
                    .message("아직 갱신 시간이 아닙니다. 남은 시간: " + formatRemainingTime(remainingSeconds))
                    .build();
        }

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch()) {
            Page page = browser.newPage();

            page.navigate("https://dundam.xyz/search?server=adven&name=" + adventureName);
            String html = page.content();
            Document doc = Jsoup.parse(html);
            Element container = doc.selectFirst("div.sr-result");
            if (container == null) {
                return MemoUpdateRes.builder()
                        .message("검색 결과를 찾을 수 없습니다.")
                        .build();
            }

            Elements items = container.select("div.scon");
            int updatedCount = 0;
            for (Element item : items) {

                String server = item.select("div.seh_sever").select(".sev").text();
                String name = item.select(".name").first().ownText();
                String memo = item.select("div.seh_stat").select(".val").text().trim();

                Optional<CharactersEntity> charactersEntity = charactersRepository.findByServerAndCharactersName(server, name);
                CharactersEntity characters;
                if (charactersEntity.isPresent()) {
                    characters = charactersEntity.get();
                } else {
                    characters = characterService.addCharacterInternal(Servers.getByName(server).getEnglishName(), name);
                }
                if (characters == null) {
                    continue;
                }
                characters.updateMemo(memo);
                charactersRepository.save(characters);
                updatedCount++;
            }

            adventure.updateLastMemoUpdateAt(now);
            adventureRepository.save(adventure);

            return MemoUpdateRes.builder()
                    .message("메모 갱신이 완료되었습니다. 총 " + updatedCount + "건 업데이트되었습니다.")
                    .build();
        } catch (Exception e) {
            log.error("메모 업데이트 중 오류가 발생했습니다. adventureName={}", adventureName, e);
            return MemoUpdateRes.builder()
                    .message("메모 갱신 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    private String formatRemainingTime(long remainingSeconds) {
        long days = remainingSeconds / 86400;
        long hours = (remainingSeconds % 86400) / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;

        StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(days).append("일 ");
        }
        if (hours > 0) {
            builder.append(hours).append("시간 ");
        }
        if (minutes > 0) {
            builder.append(minutes).append("분 ");
        }
        if (builder.isEmpty()) {
            builder.append(seconds).append("초");
        }
        return builder.toString().trim();
    }
}
