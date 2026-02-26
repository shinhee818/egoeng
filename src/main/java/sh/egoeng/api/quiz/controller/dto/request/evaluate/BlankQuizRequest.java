package sh.egoeng.api.quiz.controller.dto.request.evaluate;

import sh.egoeng.api.quiz.service.dto.BlankEvaluateRequest;

import java.util.List;

public record BlankQuizRequest(
         Long quizId,              // 퀴즈 ID
         String questionSentence,  // 빈칸 포함 문장 (예: "After a long day at [BLANK]...")
         List<String> blanks,      // 정답 단어 리스트 (예: ["work", "book"])
         String userAnswers,       // 사용자 답변 (공백으로 구분된 문자열, 예: "a e")
         String koreanSentence,     // 한국어 힌트 (선택사항)
         String originalSentence    // 원문 (빈칸 없는 문장, 선택사항)
) {
    public BlankEvaluateRequest toServiceRequest() {
        return new BlankEvaluateRequest(
                quizId,
                questionSentence,
                blanks,
                userAnswers
        );
    }
}
