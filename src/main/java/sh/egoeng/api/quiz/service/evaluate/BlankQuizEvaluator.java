package sh.egoeng.api.quiz.service.evaluate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sh.egoeng.api.quiz.controller.dto.response.BaseEvaluationResponse;
import sh.egoeng.api.quiz.controller.dto.response.BlankQuizEvaluationResponse;
import sh.egoeng.api.quiz.service.dto.BlankEvaluateRequest;
import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.api.quiz.service.llm.LlmQuizClientService;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.domain.quiz.service.UserQuizAnswerService;
import sh.egoeng.feign.llm.quiz.dto.response.BlankResult;
import sh.egoeng.security.SecurityUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class BlankQuizEvaluator implements QuizEvaluator {
    private final LlmQuizClientService llmQuizClientService;
    private final UserQuizAnswerService userQuizAnswerService;

    @Override
    public QuizType getType() {
        return QuizType.BLANK;
    }

    @Override
    public BaseEvaluationResponse evaluate(QuizEvaluateRequest request) {
        BlankEvaluateRequest blankReq = (BlankEvaluateRequest) request;
        BlankResult result = llmQuizClientService.callBlankLLM(blankReq);

        // LLM 응답 로그 출력
        log.info("=== LLM BlankResult 응답 ===");
        log.info("prompt: {}", result.getPrompt());
        log.info("userAnswer: {}", result.getUserAnswer());
        log.info("userAnswers: {}", result.getUserAnswers());
        log.info("correctAnswer: {}", result.getCorrectAnswer());
        log.info("correctWords: {}", result.getCorrectWords());
        log.info("llmAnswer: {}", result.getLlmAnswer());
        log.info("perBlank: {}", result.getPerBlank());
        log.info("correct: {}", result.isCorrect());
        log.info("feedback: {}", result.getFeedback());
        log.info("===========================");

        // 답변 저장 (userAnswers를 공백으로 연결한 형태로 저장)
        String userAnswerForSave = result.getUserAnswers() != null && !result.getUserAnswers().isEmpty()
                ? String.join(" ", result.getUserAnswers())
                : result.getUserAnswer();
        
        userQuizAnswerService.saveAnswer(
                blankReq.quizId(),
                SecurityUtils.currentId(),
                userAnswerForSave,
                result.isCorrect(),
                result.getPerBlank()  // 각 빈칸별 정답 여부 저장
        );

        // LLM 응답을 그대로 브라우저에 전달
        return new BlankQuizEvaluationResponse(
                blankReq.quizId(),
                result.getPrompt(),
                result.getUserAnswer(),  // LLM이 제공하는 userAnswer 그대로
                result.getCorrectAnswer(),  // LLM이 제공하는 correctAnswer 그대로
                result.isCorrect(),
                result.getFeedback(),
                result.getLlmAnswer(),  // LLM 모범답안 배열
                result.getCorrectWords(),  // 각 빈칸의 정답 단어 배열
                result.getUserAnswers(),  // 사용자 답안 배열
                result.getPerBlank()  // 각 빈칸별 정답 여부
        );
    }
}

