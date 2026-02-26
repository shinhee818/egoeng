package sh.egoeng.feign.llm.quiz.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateQuizResult {
    private Long quizId;
    private Long questionId;
    private String quizType;     // BLANK / TRANSLATE / WORD

    // 공통
    private String question;
    private String koreanSentence;

    // BLANK/TRANSLATE 공통 정답 리스트
    private List<String> answers;

    // WORD 선택지 구조
    private List<OptionItem> options;   // WORD 옵션들
    private Integer correctOptionId;    // 정답 옵션 ID
    private List<Long> refWordIds;      // 연결된 단어 ID(optional)

    // SENTENCE_ARRANGE 구조
    private List<String> words;         // 스페이스로 나뉜 단어들 (정답 순서)
    private List<String> shuffledWords; // 클라이언트에 제공할 섞인 단어들
    private List<Integer> correctOrder; // 섞인 배열에서 정답 순서 (0부터 시작하는 인덱스)

    @Getter
    @Setter
    public static class OptionItem {
        private Integer id;
        private String text;
    }
}



