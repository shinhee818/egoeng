package sh.egoeng.domain.word.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자가 오늘 3개 이상 단어를 등록했을 때 발생하는 이벤트
 * (3, 6, 9, 12, ... 개마다 발생)
 */
@Getter
@AllArgsConstructor
public class DailyWordCountEvent {
    private Long userId;
    private Long count;  // 오늘 등록한 단어 총 개수
}

