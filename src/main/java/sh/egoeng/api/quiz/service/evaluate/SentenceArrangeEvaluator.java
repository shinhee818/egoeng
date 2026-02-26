package sh.egoeng.api.quiz.service.evaluate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sh.egoeng.api.quiz.controller.dto.response.SentenceArrangeEvaluationResponse;
import sh.egoeng.api.quiz.controller.dto.response.BaseEvaluationResponse;
import sh.egoeng.api.quiz.service.dto.SentenceArrangeEvaluateRequest;
import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.QuizRepository;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.domain.quiz.service.UserQuizAnswerService;
import sh.egoeng.dsl.QuizDsl;
import sh.egoeng.dsl.SentenceArrangeBody;
import sh.egoeng.security.SecurityUtils;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SentenceArrangeEvaluator implements QuizEvaluator {
    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;
    private final UserQuizAnswerService userQuizAnswerService;

    @Override
    public QuizType getType() {
        return QuizType.SENTENCE_ARRANGE;
    }

    @Override
    public BaseEvaluationResponse evaluate(QuizEvaluateRequest req) {
        SentenceArrangeEvaluateRequest request = (SentenceArrangeEvaluateRequest) req;
        
        // 1. DB에서 퀴즈 조회
        Quiz quiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new IllegalArgumentException("퀴즈를 찾을 수 없습니다: " + request.quizId()));

        log.info("Quiz found: id={}, type={}, question={}", quiz.getId(), quiz.getType(), quiz.getQuestion());

        // 2. JSONB에서 QuizDsl 파싱
        QuizDsl quizDsl = objectMapper.convertValue(quiz.getQuestion(), QuizDsl.class);
        log.info("QuizDsl parsed: {}", quizDsl);

        SentenceArrangeBody body = objectMapper.convertValue(quizDsl.getBody(), SentenceArrangeBody.class);
        log.info("SentenceArrangeBody parsed: correctSentence={}, words={}",
                body.getCorrectSentence(), body.getWords());

        // 3. 정답 문장과 단어들 가져오기
        String correctSentence = body.getCorrectSentence();
        List<String> correctWords = body.getWords();

        if (correctSentence == null || correctWords == null) {
            throw new IllegalStateException("퀴즈 데이터가 올바르지 않습니다. correctSentence 또는 words가 null입니다.");
        }

        // 4. 사용자 답변 정리 (앞뒤 공백 제거, 연속 공백을 하나로, 구두점 제거)
        String userAnswer = request.userAnswer().trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[.,!?;:]", ""); // 구두점 제거
        
        // 정답 문장도 정규화 (공백 정리, 구두점 제거)
        String normalizedCorrectSentence = correctSentence.trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[.,!?;:]", ""); // 구두점 제거

        // 5. 대소문자 무시하고 비교
        boolean isCorrect = userAnswer.equalsIgnoreCase(normalizedCorrectSentence);

        // 6. 피드백 생성
        String feedback;
        if (isCorrect) {
            feedback = "정답입니다!";
        } else {
            // 사용자가 입력한 단어들 (대소문자 무시 비교를 위해 소문자로 변환)
            List<String> userWords = List.of(userAnswer.toLowerCase().split("\\s+"));
            List<String> normalizedCorrectWords = correctWords.stream()
                    .map(String::toLowerCase)
                    .toList();

            // 단어 개수가 다른 경우
            if (userWords.size() != normalizedCorrectWords.size()) {
                feedback = String.format("단어 개수가 틀렸습니다. (입력: %d개, 정답: %d개)\n정답: %s",
                        userWords.size(), normalizedCorrectWords.size(), normalizedCorrectSentence);
            } else {
                // 원본 단어 배열 (피드백용)
                String[] userWordArray = userAnswer.split("\\s+");
                
                // 어느 위치가 틀렸는지 찾기 (대소문자 무시 비교)
                StringBuilder wrongPositions = new StringBuilder("틀린 위치: ");
                boolean hasMistake = false;

                for (int i = 0; i < userWords.size(); i++) {
                    if (!userWords.get(i).equals(normalizedCorrectWords.get(i))) {
                        if (hasMistake) wrongPositions.append(", ");
                        // 피드백에서는 원본 단어 표시 (대소문자 구분)
                        wrongPositions.append(String.format("%d번째 단어 '%s' → '%s'",
                                i + 1, 
                                userWordArray[i],
                                correctWords.get(i)));
                        hasMistake = true;
                    }
                }

                feedback = hasMistake
                        ? wrongPositions + "\n정답: " + normalizedCorrectSentence
                        : "정답: " + normalizedCorrectSentence;
            }
        }

        // 답변 저장
        userQuizAnswerService.saveAnswer(
                request.quizId(),
                SecurityUtils.currentId(),
                userAnswer,
                isCorrect,
                null  // 문장 배열 퀴즈는 perBlank 없음
        );

        return new SentenceArrangeEvaluationResponse(
                request.quizId(),
                normalizedCorrectSentence,
                userAnswer,
                normalizedCorrectSentence,
                isCorrect,
                feedback
        );
    }
}
