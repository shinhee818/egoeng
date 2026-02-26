package sh.egoeng.api.quiz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.quiz.controller.dto.request.evaluate.SentenceArrangeQuizRequest;
import sh.egoeng.api.quiz.controller.dto.request.evaluate.BlankQuizRequest;
import sh.egoeng.api.quiz.controller.dto.request.evaluate.TranslateQuizRequest;
import sh.egoeng.api.quiz.controller.dto.request.evaluate.WordQuizRequest;
import sh.egoeng.api.quiz.service.EvaluateQuizService;
import sh.egoeng.api.quiz.controller.dto.response.BaseEvaluationResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quiz")
public class EvaluateQuizController {
    private final EvaluateQuizService evaluateQuizService;

    @PostMapping("/translate")
    public ResponseEntity<BaseEvaluationResponse> evaluateTranslate(@RequestBody TranslateQuizRequest req) {
        return ResponseEntity.ok(evaluateQuizService.evaluate(req.toServiceRequest()));
    }

    @PostMapping("/blank")
    public ResponseEntity<BaseEvaluationResponse> evaluateBlank(@RequestBody BlankQuizRequest req) {
        return ResponseEntity.ok(evaluateQuizService.evaluate(req.toServiceRequest()));
    }

    @PostMapping("/sentence-arrange")
    public ResponseEntity<BaseEvaluationResponse> evaluateSentenceArrange(@RequestBody SentenceArrangeQuizRequest req) {
        return ResponseEntity.ok(evaluateQuizService.evaluate(req.toServiceRequest()));
    }

    @PostMapping("/word-quiz")
    public ResponseEntity<BaseEvaluationResponse> evaluateWordQuiz(@RequestBody WordQuizRequest req) {
        return ResponseEntity.ok(evaluateQuizService.evaluate(req.toServiceRequest()));
    }
}