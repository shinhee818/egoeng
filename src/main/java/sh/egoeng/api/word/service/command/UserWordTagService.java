package sh.egoeng.api.word.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.exception.UserNotFoundException;
import sh.egoeng.api.word.exception.UserWordNotFoundException;
import sh.egoeng.api.word.exception.WordTagNotFoundException;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordTag;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.domain.word.UserWordTagRepository;
import sh.egoeng.domain.word.WordTag;
import sh.egoeng.domain.word.WordTagRepository;
import sh.egoeng.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class UserWordTagService {
    private final UserWordRepository userWordRepository;
    private final WordTagRepository wordTagRepository;
    private final UserWordTagRepository userWordTagRepository;
    private final UserService userService;

    /**
     * 기존 단어에 태그 추가
     */
    public void addTagsToUserWord(Long userWordId, List<String> tagNames) {
        Long userId = SecurityUtils.currentId();
        
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserWord userWord = userWordRepository.findById(userWordId)
                .orElseThrow(() -> new UserWordNotFoundException(userWordId));

        // 본인의 단어인지 확인
        if (!userWord.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 단어만 수정할 수 있습니다.");
        }

        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        List<UserWordTag> userWordTags = new ArrayList<>();

        for (String tagName : tagNames) {
            if (tagName == null || tagName.trim().isEmpty()) {
                continue;
            }

            String trimmedTagName = tagName.trim();

            // 태그가 이미 존재하는지 확인 (없으면 생성)
            WordTag tag = wordTagRepository.findByUserIdAndName(userId, trimmedTagName)
                    .orElseGet(() -> {
                        WordTag newTag = WordTag.builder()
                                .user(user)
                                .name(trimmedTagName)
                                .build();
                        return wordTagRepository.save(newTag);
                    });

            // UserWordTag 연결이 이미 존재하는지 확인
            if (!userWordTagRepository.existsByUserWordIdAndTagId(userWordId, tag.getId())) {
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
     * 단어에서 태그 제거
     */
    public void removeTagFromUserWord(Long userWordId, Long tagId) {
        Long userId = SecurityUtils.currentId();

        UserWord userWord = userWordRepository.findById(userWordId)
                .orElseThrow(() -> new UserWordNotFoundException(userWordId));

        // 본인의 단어인지 확인
        if (!userWord.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 단어만 수정할 수 있습니다.");
        }

        WordTag tag = wordTagRepository.findById(tagId)
                .orElseThrow(() -> new WordTagNotFoundException(tagId));

        // 본인의 태그인지 확인
        if (!tag.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 태그만 제거할 수 있습니다.");
        }

        // UserWordTag 연결 삭제
        userWordTagRepository.findByUserWordIdAndTagId(userWordId, tagId)
                .ifPresent(userWordTagRepository::delete);
    }
}
















