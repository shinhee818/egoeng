package sh.egoeng.api.quiz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.quiz.controller.dto.request.generate.GenerateQuizRequest;
import sh.egoeng.api.quiz.controller.dto.request.generate.GenerateWordPracticeQuizRequest;
import sh.egoeng.api.quiz.controller.dto.request.generate.GenerateWordQuizRequest;
import sh.egoeng.api.quiz.controller.dto.response.GenerateQuizResponse;
import sh.egoeng.api.quiz.controller.dto.response.GenerateWordPracticeQuizResponse;
import sh.egoeng.api.quiz.controller.dto.response.GenerateWordQuizResponse;
import sh.egoeng.api.quiz.service.GenerateQuizService;
import sh.egoeng.api.quiz.service.GenerateWordPracticeQuizService;
import sh.egoeng.api.quiz.service.GenerateWordQuizService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quiz")
public class GenerateQuizController {
    private final GenerateQuizService generateQuizService;
    private final GenerateWordPracticeQuizService generateWordPracticeQuizService;
    private final GenerateWordQuizService generateWordQuizService;

    /**
     * LLM 퀴즈 만들기
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerateQuizResponse> generateQuiz(@RequestBody GenerateQuizRequest req) {
        return ResponseEntity.ok(generateQuizService.generateQuiz(req));
    }

    @PostMapping("/generate-word-practice")
    public ResponseEntity<GenerateWordPracticeQuizResponse> generateWordPracticeQuiz(
            @RequestBody GenerateWordPracticeQuizRequest request) {
        return ResponseEntity.ok(generateWordPracticeQuizService.generateWordPracticeQuiz(request));
    }

    /**
     * 단어 퀴즈 만들기
     */
    @PostMapping("/generate-word-quiz")
    public ResponseEntity<GenerateWordQuizResponse> generateWordQuiz(
            @RequestBody GenerateWordQuizRequest request) {
        return ResponseEntity.ok(generateWordQuizService.generateWordQuiz(request));
    }
}