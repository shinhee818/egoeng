package sh.egoeng.api.word.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.word.service.UserWordService;
import sh.egoeng.security.SecurityUtils;

@Transactional
@Service
@RequiredArgsConstructor
public class UserWordCommandService {
    private final UserWordService userWordService;

    public void deleteWord(Long wordId) {
        Long userId = SecurityUtils.currentId();
        var userWord = userWordService.findByUserIdAndWordId(userId, wordId)
                .orElseThrow(() -> new IllegalArgumentException("등록하지 않은 단어입니다."));
        userWordService.delete(userWord);
    }
}