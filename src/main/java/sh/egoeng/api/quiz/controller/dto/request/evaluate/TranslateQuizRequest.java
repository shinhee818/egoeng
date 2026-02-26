package sh.egoeng.api.quiz.controller.dto.request.evaluate;

import sh.egoeng.api.quiz.service.dto.TranslateEvaluateRequest;

import java.util.List;

public record TranslateQuizRequest(
        Long quizId,              // 퀴즈 ID
        String quizType,           // "Translate" 또는 "Blank" (선택사항, 기본값: "Translate")
        String koreanSentence,     // 번역 퀴즈: 한국어 문장, 빈칸 퀴즈: null
        String userAnswer,         // 사용자 답변
        String questionSentence,   // 빈칸 퀴즈: 빈칸 포함 문장 (선택사항)
        List<String> blanks  // 빈칸 퀴즈: 빈칸 목록 (선택사항)
) {
    public TranslateEvaluateRequest toServiceRequest() {
        return new TranslateEvaluateRequest(
                quizId,
                koreanSentence,
                userAnswer
        );
    }
}
