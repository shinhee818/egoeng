package sh.egoeng.domain.word.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sh.egoeng.domain.word.UserWord;

/**
 * 사용자가 단어를 학습할 때 발생하는 이벤트
 */
@Getter
@AllArgsConstructor
public class UserWordAddedEvent {
    private UserWord userWord;
    private Long userId;

    // 단어 텍스트 (커스텀 단어면 customText, 아니면 word.text)
    public String getWordText() {
        if (userWord.isCustomWord()) {
            return userWord.getCustomText();
        }
        return userWord.getWord().getText();
    }

    // 단어 의미 (커스텀 단어면 customMeaningKo, 아니면 word meaning)
    public String getWordMeaning() {
        if (userWord.isCustomWord()) {
            return userWord.getCustomMeaningKo();
        }
        return userWord.getWord().getMeanings().stream()
                .findFirst()
                .map(m -> m.getMeaningKo())
                .orElse("");
    }

    // 단어 ID (커스텀 단어면 null, 아니면 word.id)
    public Long getWordId() {
        if (userWord.isCustomWord()) {
            return null;
        }
        return userWord.getWord().getId();
    }
}

