package sh.egoeng.api.quiz.service;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.exception.UserNotFoundException;
import sh.egoeng.api.quiz.controller.dto.request.generate.GenerateQuizRequest;
import sh.egoeng.api.quiz.controller.dto.response.GenerateQuizResponse;
import sh.egoeng.api.quiz.service.llm.LlmQuizClientService;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.domain.quiz.UserQuiz;
import sh.egoeng.domain.quiz.service.QuizService;
import sh.egoeng.domain.quiz.service.UserQuizService;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.dsl.QuizDslFactory;
import sh.egoeng.feign.llm.quiz.dto.response.GenerateQuizResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import sh.egoeng.security.SecurityUtils;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateQuizService {
    // 외부 시스템 연동 (API 레이어 책임)
    private final LlmQuizClientService llmQuizClientService;
    private final QuizDslFactory quizDslFactory;
    private final ObjectMapper objectMapper;
    
    // 도메인 서비스
    private final QuizService quizService;
    private final UserQuizService userQuizService;
    private final UserService userService;

    @Transactional
    public GenerateQuizResponse generateQuiz(GenerateQuizRequest req) {
        // 1) LLM 호출 (외부 시스템)
        GenerateQuizResult r = llmQuizClientService.callGenerateQuiz(req.quizType());

        // LLM이 quizType을 반환하지 않으면, 요청에서 받은 값 사용
        String type = (r.getQuizType() != null && !r.getQuizType().isEmpty()) 
                ? r.getQuizType() 
                : req.quizType();
        
        QuizType quizType = QuizType.valueOf(type);

        // 2) DSL 생성
        var dsl = quizDslFactory.buildDsl(type, r);

        // 3) DSL → Map 변환 (데이터 변환)
        Map<String, Object> questionMap = objectMapper.convertValue(dsl, new TypeReference<>() {
        });

        // 4) Quiz 생성 (도메인 로직)
        Quiz quiz = quizService.createQuiz(
                quizType,
                type + " Quiz",
                questionMap
        );

        // 5) User 조회
        User user = userService.findById(SecurityUtils.currentId())
                .orElseThrow(() -> new UserNotFoundException(SecurityUtils.currentId()));

        UserQuiz userQuiz = userQuizService.startUserQuiz(user, quiz);

        return new GenerateQuizResponse(
                quiz.getId(),
                type,
                r.getQuestion(),
                r.getKoreanSentence(),
                r.getWords(),
                r.getShuffledWords(),
                r.getCorrectOrder()
        );
    }
}
