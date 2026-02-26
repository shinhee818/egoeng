package sh.egoeng.api.word.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import sh.egoeng.api.exception.UserNotFoundException;
import sh.egoeng.api.word.exception.WordNotFoundException;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordTag;
import sh.egoeng.domain.word.Word;
import sh.egoeng.domain.word.WordRepository;
import sh.egoeng.domain.word.WordTag;
import sh.egoeng.domain.word.service.UserWordService;
import sh.egoeng.domain.word.service.WordService;
import sh.egoeng.domain.word.WordTagRepository;
import sh.egoeng.domain.word.UserWordTagRepository;
import sh.egoeng.feign.papago.PapagoTargetLanguage;
import sh.egoeng.feign.papago.TranslatePapagoClient;
import sh.egoeng.feign.papago.response.PapagoTranslationResponse;
import sh.egoeng.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class RegisterUserWordService {
    private final UserWordService userWordService;
    private final UserService userService;
    private final WordService wordService;
    private final WordRepository wordRepository;
    private final WordTagRepository wordTagRepository;
    private final UserWordTagRepository userWordTagRepository;
    private final TranslatePapagoClient papagoClient;

    public Long registerUserWord(Long wordId, List<String> tagNames) {
        Long userId = SecurityUtils.currentId();
        
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Word word = wordService.findById(wordId)
                .orElseThrow(() -> new WordNotFoundException(wordId));

        UserWord userWord = UserWord.builder()
                .user(user)
                .word(word)
                .build();

        UserWord savedUserWord = userWordService.registerUserWord(userWord);

        // 태그 처리
        if (tagNames != null && !tagNames.isEmpty()) {
            addTagsToUserWord(savedUserWord, userId, tagNames);
        }


        return savedUserWord.getId();
    }

    public Long registerCustomUserWord(String text, String meaningKo, List<String> tagNames) {
        Long userId = SecurityUtils.currentId();
        
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserWord userWord = UserWord.createCustomWord(user, text.trim(), meaningKo.trim());

        UserWord savedUserWord = userWordService.registerUserWord(userWord);

        // 태그 처리
        if (tagNames != null && !tagNames.isEmpty()) {
            addTagsToUserWord(savedUserWord, userId, tagNames);
        }

        return savedUserWord.getId();
    }

    private void addTagsToUserWord(UserWord userWord, Long userId, List<String> tagNames) {
        List<UserWordTag> userWordTags = new ArrayList<>();

        for (String tagName : tagNames) {
            if (tagName == null || tagName.trim().isEmpty()) {
                continue;
            }

            String trimmedTagName = tagName.trim();

            // 태그가 이미 존재하는지 확인
            WordTag tag = wordTagRepository.findByUserIdAndName(userId, trimmedTagName)
                    .orElseGet(() -> {
                        // 태그가 없으면 생성
                        WordTag newTag = WordTag.builder()
                                .user(userWord.getUser())
                                .name(trimmedTagName)
                                .build();
                        return wordTagRepository.save(newTag);
                    });

            // UserWordTag 연결이 이미 존재하는지 확인
            if (!userWordTagRepository.existsByUserWordIdAndTagId(userWord.getId(), tag.getId())) {
                UserWordTag userWordTag = UserWordTag.builder()
                        .userWord(userWord)
                        .tag(tag)
                        .build();
                userWordTags.add(userWordTag);
            }
        }

        // 일괄 저장
        if (!userWordTags.isEmpty()) {
            userWordTagRepository.saveAll(userWordTags);
        }
    }

    /**
     * OCR 텍스트 일괄 등록
     * OCR 결과에서 선택한 텍스트들을 한번에 커스텀 단어로 등록
     * 
     * @param words 등록할 단어 정보 리스트 (text, meaningKo)
     * @param tagNames 태그 이름 리스트 (선택적)
     * @return 등록 결과 (등록된 ID 목록, 총 개수, 등록된 개수, 스킵된 개수)
     */
    public OcrWordRegisterResult registerOcrWords(List<RegisterUserWordService.OcrWordText> words, List<String> tagNames) {
        Long userId = SecurityUtils.currentId();
        
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Long> registeredIds = new ArrayList<>();
        int skippedCount = 0;
        int totalCount = words.size();
        
        for (OcrWordText wordText : words) {
            if (wordText.text() == null || wordText.text().trim().isEmpty()) {
                skippedCount++;
                continue; // 빈 텍스트는 스킵
            }

            String text = wordText.text().trim();
            
            // 기존 Word 테이블에 있으면 스킵
            Optional<Word> existingWord = wordRepository.findByText(text);
            if (existingWord.isPresent()) {
                log.info("⏭️ [기존 Word 스킵] {} - 이미 Word 테이블에 존재", text);
                skippedCount++;
                continue; // 기존 Word가 있으면 등록하지 않고 스킵
            }
            
            String meaningKo = wordText.meaningKo();
            
            // meaningKo가 null이면 파파고로 자동 번역 시도
            if (meaningKo == null || meaningKo.trim().isEmpty()) {
                String translated = translateToKorean(text);
                // 번역 성공하면 사용, 실패해도 null로 저장 (나중에 사용자가 입력 가능)
                meaningKo = translated; // null일 수 있음
            } else {
                meaningKo = meaningKo.trim();
            }

            UserWord userWord = UserWord.createCustomWord(user, text, meaningKo);
            UserWord savedUserWord = userWordService.registerUserWord(userWord);

            // 태그 처리
            if (tagNames != null && !tagNames.isEmpty()) {
                addTagsToUserWord(savedUserWord, userId, tagNames);
            }

            registeredIds.add(savedUserWord.getId());
        }

        return new OcrWordRegisterResult(
                registeredIds,
                totalCount,
                registeredIds.size(),
                skippedCount
        );
    }

    /**
     * OCR 단어 텍스트 정보
     */
    public record OcrWordText(String text, String meaningKo) {}

    /**
     * OCR 텍스트 일괄 등록 결과
     */
    public record OcrWordRegisterResult(
            List<Long> registeredIds,
            int totalCount,
            int registeredCount,
            int skippedCount
    ) {}

    /**
     * 파파고 API를 사용하여 영어를 한국어로 번역
     * 
     * @param englishText 영어 텍스트
     * @return 번역된 한국어 텍스트 (실패 시 null)
     */
    private String translateToKorean(String englishText) {
        try {
            log.info("🔍 [파파고 번역 요청] 텍스트: {}", englishText);
            
            PapagoTranslationResponse response = papagoClient.translate(
                    PapagoTargetLanguage.ENGLISH.getCode(),
                    PapagoTargetLanguage.KOREAN.getCode(),
                    englishText
            );

            log.info("📥 [파파고 번역 응답] 전체 응답: {}", response);
            
            if (response != null) {
                log.info("📥 [파파고 번역 응답] response: {}", response != null ? "not null" : "null");
                
                if (response.message() != null) {
                    log.info("📥 [파파고 번역 응답] message: {}", response.message() != null ? "not null" : "null");
                    
                    if (response.message().result() != null) {
                        log.info("📥 [파파고 번역 응답] result: {}", response.message().result());
                        
                        if (response.message().result().translatedText() != null) {
                            String translated = response.message().result().translatedText();
                            log.info("✅ [파파고 번역 성공] {} -> {}", englishText, translated);
                            return translated;
                        } else {
                            log.warn("⚠️ [파파고 번역 응답] translatedText가 null입니다.");
                        }
                    } else {
                        log.warn("⚠️ [파파고 번역 응답] result가 null입니다.");
                    }
                } else {
                    log.warn("⚠️ [파파고 번역 응답] message가 null입니다.");
                }
            } else {
                log.warn("⚠️ [파파고 번역 응답] response가 null입니다.");
            }
        } catch (Exception e) {
            log.error("❌ [파파고 번역 실패] 텍스트: {}, 에러: {}", englishText, e.getMessage(), e);
        }
        
        // 번역 실패 시 null 반환
        log.warn("❌ [파파고 번역 실패] 번역 실패로 null 반환: {}", englishText);
        return null;
    }
}
