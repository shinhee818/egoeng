package sh.egoeng.feign.llm.quiz;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import sh.egoeng.feign.llm.quiz.dto.request.FillInTheBlankQuizRequest;
import sh.egoeng.feign.llm.quiz.dto.request.ArrangeQuizRequest;
import sh.egoeng.feign.llm.quiz.dto.response.LlmResponse;
import sh.egoeng.feign.llm.quiz.dto.request.LlmGenerateQuizRequest;
import sh.egoeng.feign.llm.quiz.dto.request.TranslateSentenceQuizRequest;
import sh.egoeng.feign.llm.quiz.dto.response.ArrangeResult;
import sh.egoeng.feign.llm.quiz.dto.response.BlankResult;
import sh.egoeng.feign.llm.quiz.dto.response.GenerateQuizResult;
import sh.egoeng.feign.llm.quiz.dto.response.TranslateResult;

@FeignClient(name = "quizeClient", url = "${llm.base-url}")
public interface LlmQuizFeignClient {

    @PostMapping("/api/quiz/translate")
    LlmResponse<TranslateResult> evaluateTranslate(@RequestBody TranslateSentenceQuizRequest request);

    @PostMapping("/api/quiz/generate")
    LlmResponse<GenerateQuizResult> generateQuiz(@RequestBody LlmGenerateQuizRequest request);

    @PostMapping("/api/quiz/blank")
    LlmResponse<BlankResult> evaluateBlank(@RequestBody FillInTheBlankQuizRequest request);

    @PostMapping("/api/quiz/arrange")
    LlmResponse<ArrangeResult> evaluateArrange(@RequestBody ArrangeQuizRequest request);
}