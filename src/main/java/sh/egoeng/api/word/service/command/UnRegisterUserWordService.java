package sh.egoeng.api.word.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.word.exception.UserWordNotFoundException;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.domain.word.UserWordTagRepository;
import sh.egoeng.domain.word.service.UserWordService;
import sh.egoeng.security.SecurityUtils;

@Transactional
@Service
@RequiredArgsConstructor
public class UnRegisterUserWordService {
    private final UserWordService userWordService;
    private final UserWordRepository userWordRepository;
    private final UserWordTagRepository userWordTagRepository;

    public void unRegister(Long userWordId) {
        Long userId = SecurityUtils.currentId();
        
        // UserWord ID로 직접 조회
        UserWord userWord = userWordRepository.findById(userWordId)
                .orElseThrow(() -> new UserWordNotFoundException(userWordId));
        
        // 소유자 확인
        if (!userWord.getUser().getId().equals(userId)) {
            throw new SecurityException("User does not have permission to delete this word.");
        }

        // UserWordTag 먼저 삭제 (외래키 제약조건 해결)
        userWordTagRepository.deleteByUserWordId(userWordId);

        // UserWord 삭제
        userWordService.delete(userWord);
    }
}