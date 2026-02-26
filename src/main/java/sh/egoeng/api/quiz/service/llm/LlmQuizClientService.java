package sh.egoeng.api.quiz.service.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sh.egoeng.api.quiz.service.dto.TranslateEvaluateRequest;
import sh.egoeng.api.quiz.service.dto.BlankEvaluateRequest;
import sh.egoeng.feign.llm.quiz.dto.request.ArrangeQuizRequest;
import sh.egoeng.feign.llm.quiz.dto.request.FillInTheBlankQuizRequest;
import sh.egoeng.feign.llm.quiz.LlmQuizFeignClient;
import sh.egoeng.feign.llm.quiz.dto.response.LlmResponse;
import sh.egoeng.feign.llm.quiz.dto.request.LlmGenerateQuizRequest;
import sh.egoeng.feign.llm.quiz.dto.request.TranslateSentenceQuizRequest;
import sh.egoeng.feign.llm.quiz.dto.response.ArrangeResult;
import sh.egoeng.feign.llm.quiz.dto.response.BlankResult;
import sh.egoeng.feign.llm.quiz.dto.response.GenerateQuizResult;
import sh.egoeng.feign.llm.quiz.dto.response.TranslateResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmQuizClientService {
    private final LlmQuizFeignClient llmQuizFeignClient;
    private final ObjectMapper objectMapper;

    public BlankResult callBlankLLM(BlankEvaluateRequest request) {
        FillInTheBlankQuizRequest feignRequest =
                FillInTheBlankQuizRequest.builder()
                        .questionSentence(request.sentence())
                        .userAnswers(request.blanks())
                        .koreanSentence(null)
                        .originalSentence(null)
                        .build();

        LlmResponse<BlankResult> response = llmQuizFeignClient.evaluateBlank(feignRequest);

        // 원본 LLM 응답 전체 로그 출력
        try {
            log.info("=== 원본 LLM Feign 응답 ===");
            log.info("success: {}", response.isSuccess());
            log.info("type: {}", response.getType());
            log.info("result 전체: {}", objectMapper.writeValueAsString(response.getResult()));
            log.info("===========================");
        } catch (Exception e) {
            log.error("LLM 응답 로그 출력 실패", e);
        }

        return response.getResult();
    }

    public TranslateResult callTranslateLLM(TranslateEvaluateRequest request) {
        TranslateSentenceQuizRequest feignReq =
                TranslateSentenceQuizRequest.builder()
                        .koreanSentence(request.koreanSentence())
                        .userAnswer(request.userAnswer())
                        .build();

        return llmQuizFeignClient.evaluateTranslate(feignReq).getResult();
    }

    public ArrangeResult callArrangeLLM(ArrangeQuizRequest request) {
        LlmResponse<ArrangeResult> response = llmQuizFeignClient.evaluateArrange(request);

        try {
            log.info("=== 원본 LLM Arrange Feign 응답 ===");
            log.info("success: {}", response != null && response.isSuccess());
            log.info("type: {}", response != null ? response.getType() : null);
            log.info("result 전체: {}", response != null ? objectMapper.writeValueAsString(response.getResult()) : null);
            log.info("==================================");
        } catch (Exception e) {
            log.error("LLM Arrange 응답 로그 출력 실패", e);
        }

        if (response == null || !response.isSuccess() || response.getResult() == null) {
            throw new RuntimeException("LLM 서버에서 문장 순서 맞추기 채점 실패");
        }

        return response.getResult();
    }

    public GenerateQuizResult callGenerateQuiz(String quizType) {
        String llmQuizType = convertToLlmFormat(quizType);

        LlmGenerateQuizRequest request = LlmGenerateQuizRequest.builder()
                .quizType(llmQuizType)
                .build();

        LlmResponse<GenerateQuizResult> response = llmQuizFeignClient.generateQuiz(request);

        if (response == null || !response.isSuccess() || response.getResult() == null) {
            throw new RuntimeException("LLM 서버에서 퀴즈 생성 실패");
        }

        return response.getResult();
    }

    /**
     * 내부 QuizType 형식을 LLM 서버 형식으로 변환
     */
    private String convertToLlmFormat(String quizType) {
        return switch (quizType.toUpperCase()) {
            case "SENTENCE_ARRANGE" -> "SentenceArrange";
            case "TRANSLATE" -> "Translate";
            case "BLANK" -> "Blank";
            case "WORD" -> "SentenceArrange";
            default -> "SentenceArrange";
        };
    }
}