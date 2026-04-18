package com.dnfproject.root.user.adventure.service;

import com.dnfproject.root.common.Enums.Servers;
import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.dnfproject.root.user.adventure.db.dto.res.MemoUpdateRes;
import com.dnfproject.root.user.adventure.db.dto.res.MyInfoRes;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    private static final long MEMO_UPDATE_INTERVAL_SECONDS = 1 * 1 * 60;
    private static final long DUNDAM_CHARACTER_PAGE_DELAY_MS = 2000L;

    private final AdventureRepository adventureRepository;
    private final CharacterService characterService;
    private final CharactersRepository charactersRepository;

    @Override
    @Transactional(readOnly = true)
    public MyInfoRes getMyInfo(Long adventureId) {
        return MyInfoRes.from(charactersRepository.findByAdventureIdWithClearStateFetched(adventureId));
    }

    @Override
    @Transactional(readOnly = true)
    public MyInfoRes getMyInfoByAdventureName(String adventureName) {
        return MyInfoRes.from(charactersRepository.findByAdventureNameWithClearStateFetched(adventureName));
    }

    @Override
    @Transactional
    public MemoUpdateRes memoUpdateFromHtml(String html) {
        if (html == null || html.isBlank()) {
            throw new CustomException(ErrorCode.HTML_BODY_REQUIRED);
        }
        Document doc;
        try {
            doc = Jsoup.parse(html);
        } catch (Exception e) {
            log.warn("memoUpdateFromHtml: Jsoup parse failed", e);
            return MemoUpdateRes.builder()
                    .message("HTML을 파싱할 수 없습니다.")
                    .build();
        }

        Elements items = selectSconElements(doc);
        if (items.isEmpty()) {
            return MemoUpdateRes.builder()
                    .message("HTML에서 div.scon을 찾을 수 없습니다. 던담 검색 결과 블록이 포함되었는지 확인해 주세요.")
                    .build();
        }

        StringBuilder nameErr = new StringBuilder();
        String adventureName = resolveConsistentAdventureNameFromScons(items, nameErr);
        if (adventureName == null) {
            return MemoUpdateRes.builder()
                    .message(nameErr.length() > 0 ? nameErr.toString()
                            : "모험단명을 추출할 수 없습니다. span.introd.server[name=서버] 태그를 확인해 주세요.")
                    .build();
        }

        Optional<AdventureEntity> adventureOptional = adventureRepository.findByAdventureName(adventureName);
        if (adventureOptional.isEmpty()) {
            return MemoUpdateRes.builder()
                    .message("등록되지 않은 모험단입니다. (추출된 모험단명: " + adventureName + ")")
                    .build();
        }

        AdventureEntity adventure = adventureOptional.get();
        try {
            return processMemoUpdates(doc, adventure);
        } catch (Exception e) {
            log.error("memoUpdateFromHtml: apply failed adventureName={}", adventureName, e);
            return MemoUpdateRes.builder()
                    .message("메모 갱신 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public MemoUpdateRes memoUpdate(String adventureName, boolean check) {
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

            // 던담 자동 검색(memoUpdate) 전용. HTML 복붙(memoUpdateFromHtml) 경로에는 넣지 않음.
            if(check) {
                visitDundamCharacterPagesForAdventure(page, adventure.getId());
            }

            page.navigate("https://dundam.xyz/search?server=adven&name=" + adventureName);
            String html = page.content();
            Document doc = Jsoup.parse(html);
            Element container = doc.selectFirst("div.sr-result");
            if (container == null) {
                return MemoUpdateRes.builder()
                        .message("검색 결과를 찾을 수 없습니다.")
                        .build();
            }



            return processMemoUpdates(doc, adventure);
        } catch (Exception e) {
            log.error("메모 업데이트 중 오류가 발생했습니다. adventureName={}", adventureName, e);
            return MemoUpdateRes.builder()
                    .message("메모 갱신 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * DB에 등록된 모험단 소속 캐릭터마다 던담 캐릭터 페이지를 한 번씩 연다.
     * {@code key}는 Neople 캐릭터 ID({@link CharactersEntity#getCharactersId()}), DB PK {@code id}가 아니다.
     * <p>{@code memoUpdate}에서만 호출한다. 수동 HTML({@code memoUpdateFromHtml})에는 대응하지 않는다.
     */
    private void visitDundamCharacterPagesForAdventure(Page page, Long adventureId) {
        if (page == null || adventureId == null) {
            return;
        }
        List<CharactersEntity> characters = charactersRepository.findByAdventureId(adventureId);
        for (CharactersEntity ch : characters) {
            try {
                String serverKo = ch.getServer();
                String charactersId = ch.getCharactersId();

                Servers serverEnum = Servers.getByName(serverKo);
                if (serverEnum == Servers.NULL_OPTION || serverEnum.getEnglishName().isBlank()) {
                    log.warn("visitDundamCharacterPagesForAdventure: 알 수 없는 서버 [{}] charactersId={}", serverKo, charactersId);
                    continue;
                }

                String url = "https://dundam.xyz/character?server=" + serverEnum.getEnglishName() + "&key=" + charactersId;
                page.navigate(url);
                log.info("페이지 방문 : {}", url);
                if (!sleepBetweenDundamCharacterVisits()) {
                    break;
                }
            } catch (Exception e) {
                log.warn("visitDundamCharacterPagesForAdventure: 방문 실패 — {}", e.toString());
            }
        }
    }

    /** 던담 캐릭터 페이지 연속 방문 시 부하 완화 */
    private static boolean sleepBetweenDundamCharacterVisits() {
        try {
            Thread.sleep(DUNDAM_CHARACTER_PAGE_DELAY_MS);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * div.sr-result 가 있으면 그 안의 div.scon 만, 없으면 문서 전체에서 div.scon (수동 붙여넣기 HTML 대응).
     */
    private static Elements selectSconElements(Document doc) {
        Element container = doc.selectFirst("div.sr-result");
        if (container != null) {
            return container.select("div.scon");
        }
        return doc.select("div.scon");
    }

    /**
     * 던담 HTML: div.scon 내부 {@code span.introd.server[name=서버]} 텍스트가 모험단 표기(예: 장식).
     */
    private static String extractAdventureNameFromScon(Element scon) {
        Element span = scon.selectFirst("span[class~=introd][class~=server][name=서버]");
        if (span == null) {
            span = scon.selectFirst("span[class~=introd][class~=server]");
        }
        if (span == null) {
            span = scon.selectFirst("span[class~=introdd][class~=server][name=서버]");
        }
        if (span == null) {
            span = scon.selectFirst("span[class~=introdd][class~=server]");
        }
        if (span == null) {
            return null;
        }
        String text = span.text().trim();
        return text.isEmpty() ? null : text;
    }

    /**
     * @param errMsg 실패 시 사용자에게 보여줄 메시지
     * @return 모험단명, 실패 시 null
     */
    private static String resolveConsistentAdventureNameFromScons(Elements items, StringBuilder errMsg) {
        String first = null;
        int index = 0;
        for (Element scon : items) {
            index++;
            String extracted = extractAdventureNameFromScon(scon);
            if (extracted == null) {
                errMsg.append(index).append("번째 div.scon에서 모험단명(span.introd.server)을 찾을 수 없습니다.");
                return null;
            }
            if (first == null) {
                first = extracted;
            } else if (!first.equals(extracted)) {
                errMsg.append("모험단명이 일치하지 않습니다. 첫 블록: \"").append(first)
                        .append("\", ").append(index).append("번째 블록: \"").append(extracted).append("\"");
                return null;
            }
        }
        return first;
    }

    private MemoUpdateRes processMemoUpdates(Document doc, AdventureEntity adventure) {
        Elements items = selectSconElements(doc);
        int updatedCount = applySconMemoUpdates(items);
        LocalDateTime now = LocalDateTime.now();
        adventure.updateLastMemoUpdateAt(now);
        adventureRepository.save(adventure);
        return MemoUpdateRes.builder()
                .message("메모 갱신이 완료되었습니다. 총 " + updatedCount + "건 업데이트되었습니다.")
                .build();
    }

    private int applySconMemoUpdates(Elements items) {
        int updatedCount = 0;
        for (Element item : items) {
            try {
                Element sev = item.select("div.seh_sever").select(".sev").first();
                String server = sev != null ? sev.text().trim() : "";
                Element nameEl = item.select(".name").first();
                String name = nameEl != null ? nameEl.ownText().trim() : "";
                String memo = item.select("div.seh_stat").select(".val").text().trim();

                if (name.isBlank()) {
                    log.warn("memoUpdate: skip scon — 캐릭터명이 비어 있음");
                    continue;
                }

                Servers serverEnum = Servers.getByName(server);
                if (serverEnum == Servers.NULL_OPTION || serverEnum.getEnglishName().isBlank()) {
                    log.warn("memoUpdate: skip scon — 알 수 없는 서버 [{}] 캐릭터 [{}]", server, name);
                    continue;
                }
                String serverEnglish = serverEnum.getEnglishName();

                Optional<CharactersEntity> charactersEntity = charactersRepository.findByServerAndCharactersName(server, name);
                CharactersEntity characters;
                if (charactersEntity.isPresent()) {
                    characters = charactersEntity.get();
                } else {
                    characters = characterService.addCharacterInternal(serverEnglish, name);
                }
                if (characters == null) {
                    continue;
                }
                characters.updateMemo(memo);
                charactersRepository.save(characters);
                updatedCount++;
            } catch (Exception e) {
                log.warn("memoUpdate: scon 한 건 처리 실패 — {}", e.toString());
            }
        }
        return updatedCount;
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
