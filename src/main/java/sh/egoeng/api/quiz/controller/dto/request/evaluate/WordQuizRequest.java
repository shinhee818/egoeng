package sh.egoeng.api.quiz.controller.dto.request.evaluate;

import sh.egoeng.api.quiz.service.dto.WordQuizEvaluateRequest;

import java.util.List;
import java.util.stream.Collectors;

public record WordQuizRequest(
        /**
         * 퀴즈 ID
         */
        Long quizId,
        /**
         * 각 문제별 답안
         * wordId: 단어 ID
         * selectedIndex: 사용자가 선택한 인덱스 (0~4)
         */
        List<WordAnswer> answers
) {
    public record WordAnswer(
            Long wordId,
            Integer selectedIndex
    ) {}

    public WordQuizEvaluateRequest toServiceRequest() {
        return new WordQuizEvaluateRequest(
                quizId,
                answers.stream()
                        .map(answer -> new WordQuizEvaluateRequest.WordAnswer(
                                answer.wordId(),
                                answer.selectedIndex()
                        ))
                        .collect(Collectors.toList())
        );
    }
}

