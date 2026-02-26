package sh.egoeng.dsl;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SentenceArrangeBody {
    private String koreanHint;        // 한국어 힌트/의미
    private String correctSentence;   // 정답 문장 (전체)
    private List<String> words;       // 스페이스로 나뉜 단어들 (정답 순서)
    private List<String> shuffledWords; // 클라이언트에 제공할 섞인 단어들
    private List<Integer> correctOrder; // 섞인 배열에서 정답 순서 (0부터 시작하는 인덱스)
    private String explanation;       // 설명
}
