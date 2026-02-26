package sh.egoeng.api.word.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.word.exception.WordNotFoundException;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.service.UserWordService;
import sh.egoeng.security.SecurityUtils;

@Transactional
@Service
@RequiredArgsConstructor
public class DeleteUserWordService {
    private final UserWordService userWordService;

    public void deleteUserWord(Long wordId) {
        UserWord userWord = userWordService.findByUserIdAndWordId(SecurityUtils.currentId(), wordId)
                .orElseThrow(() -> new WordNotFoundException(wordId));
        userWordService.delete(userWord);
    }
}
