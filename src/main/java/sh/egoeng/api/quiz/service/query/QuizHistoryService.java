package sh.egoeng.api.quiz.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.quiz.controller.dto.response.BlankAnswerDetail;
import sh.egoeng.api.quiz.controller.dto.response.QuizHistoryGroupResponse;
import sh.egoeng.api.quiz.controller.dto.response.QuizHistoryResponse;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.domain.quiz.UserQuiz;
import sh.egoeng.domain.quiz.UserQuizAnswer;
import sh.egoeng.domain.quiz.UserQuizAnswerQuerydslRepository;
import sh.egoeng.domain.quiz.UserQuizQuerydslRepository;
import sh.egoeng.domain.quiz.UserQuizAnswerRepository;
import sh.egoeng.domain.word.Word;
import sh.egoeng.domain.word.WordRepository;
import sh.egoeng.dsl.BlankBody;
import sh.egoeng.dsl.SentenceArrangeBody;
import sh.egoeng.dsl.TranslateBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class QuizHistoryService {
    private final UserQuizQuerydslRepository userQuizQuerydslRepository;
    private final UserQuizAnswerQuerydslRepository userQuizAnswerQuerydslRepository;
    private final UserQuizAnswerRepository userQuizAnswerRepository;
    private final WordRepository wordRepository;
    private final ObjectMapper objectMapper;

    /**
     * 퀴즈 히스토리 조회 (UserQuiz 기준 그룹핑)
     */
    public Page<QuizHistoryGroupResponse> getQuizHistory(
            Long userId,
            String quizType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        QuizType type = parseQuizType(quizType);
        
        Page<UserQuiz> userQuizzes = userQuizQuerydslRepository.findUserQuizHistory(
                userId,
                type,
                fromDate,
                toDate,
                pageable
        );

        return userQuizzes.map(this::toGroupDto);
    }

    /**
     * 기존 방식 (호환성 유지용, deprecated)
     */
    @Deprecated
    public Page<QuizHistoryResponse> getQuizHistoryLegacy(
            Long userId,
            String quizType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        QuizType type = parseQuizType(quizType);
        
        Page<UserQuizAnswer> answers = userQuizAnswerQuerydslRepository.findUserQuizHistory(
                userId,
                type,
                fromDate,
                toDate,
                pageable
        );

        return answers.map(this::toDto);
    }

    /**
     * UserQuiz를 QuizHistoryGroupResponse로 변환
     */
    private QuizHistoryGroupResponse toGroupDto(UserQuiz userQuiz) {
        Quiz quiz = userQuiz.getQuiz();
        QuizType quizType = quiz.getType();

        // 해당 Quiz의 모든 UserQuizAnswer 조회
        List<UserQuizAnswer> answers = userQuizAnswerRepository.findByQuiz(quiz);
        // 같은 사용자의 답안만 필터링 (보안)
        answers = answers.stream()
                .filter(answer -> answer.getUser().getId().equals(userQuiz.getUser().getId()))
                .sorted(Comparator.comparing(UserQuizAnswer::getAnsweredAt))
                .collect(Collectors.toList());

        // 각 답안을 QuizHistoryItemResponse로 변환
        List<QuizHistoryGroupResponse.QuizHistoryItemResponse> answerItems = answers.stream()
                .map(answer -> toHistoryItem(answer, quizType))
                .collect(Collectors.toList());

        // 전체 문제 개수 계산
        int totalQuestions = switch (quizType) {
            case WORD_QUIZ -> {
                Map<String, Object> questionMap = quiz.getQuestion();
                if (questionMap != null && questionMap.containsKey("wordIds")) {
                    List<?> wordIds = (List<?>) questionMap.get("wordIds");
                    yield wordIds != null ? wordIds.size() : answers.size();
                }
                yield answers.size();
            }
            default -> answers.size();  // LLM 퀴즈는 1개
        };

        // 제출 시간은 첫 답안 시간 또는 UserQuiz 생성 시간
        LocalDateTime submittedAt = !answers.isEmpty() 
                ? answers.get(0).getAnsweredAt() 
                : userQuiz.getCreatedAt();

        return new QuizHistoryGroupResponse(
                userQuiz.getId(),
                quiz.getId(),
                formatQuizType(quizType),
                userQuiz.getScore(),
                totalQuestions,
                submittedAt,
                answerItems
        );
    }

    /**
     * UserQuizAnswer를 QuizHistoryItemResponse로 변환
     */
    private QuizHistoryGroupResponse.QuizHistoryItemResponse toHistoryItem(UserQuizAnswer answer, QuizType quizType) {
        Quiz quiz = answer.getQuiz();
        Map<String, Object> questionMap = quiz.getQuestion();

        String question = extractQuestion(quizType, questionMap, answer.getAnswer());
        String koreanSentence = extractKoreanSentence(quizType, questionMap);
        String correctAnswer = extractCorrectAnswer(quizType, questionMap);
        List<BlankAnswerDetail> blankAnswers = extractBlankAnswers(quizType, questionMap, answer.getAnswer(), answer);

        // 단어 퀴즈의 경우 wordId 추출 및 단어 텍스트 조회
        Long wordId = null;
        String wordText = null;
        if (quizType == QuizType.WORD_QUIZ && answer.getAnswer() != null) {
            // answer 형식: "wordId:selectedIndex"
            String[] parts = answer.getAnswer().split(":");
            if (parts.length == 2) {
                try {
                    wordId = Long.parseLong(parts[0]);
                    // Word 엔티티 조회하여 단어 텍스트 가져오기
                    wordText = wordRepository.findById(wordId)
                            .map(Word::getText)
                            .orElse(null);
                } catch (NumberFormatException e) {
                    // 파싱 실패 시 null 유지
                }
            }
        }

        return new QuizHistoryGroupResponse.QuizHistoryItemResponse(
                answer.getId(),
                wordId,
                wordText,
                question,
                koreanSentence,
                answer.getAnswer(),
                correctAnswer,
                answer.isCorrect(),
                null,  // feedback은 현재 저장 안됨
                answer.getAnsweredAt(),
                blankAnswers
        );
    }

    /**
     * 기존 방식의 toDto (호환성 유지용)
     */
    @Deprecated
    private QuizHistoryResponse toDto(UserQuizAnswer answer) {
        var quiz = answer.getQuiz();
        Map<String, Object> questionMap = quiz.getQuestion();
        
        String question = extractQuestion(quiz.getType(), questionMap, answer.getAnswer());
        String koreanSentence = extractKoreanSentence(quiz.getType(), questionMap);
        String correctAnswer = extractCorrectAnswer(quiz.getType(), questionMap);
        List<BlankAnswerDetail> blankAnswers = extractBlankAnswers(quiz.getType(), questionMap, answer.getAnswer(), answer);

        return new QuizHistoryResponse(
                quiz.getId(),
                quiz.getId(),  // questionId는 현재 구조에 없으므로 quizId 사용
                formatQuizType(quiz.getType()),
                question,
                koreanSentence,
                answer.getAnswer(),
                correctAnswer,
                answer.isCorrect(),
                null,  // feedback은 현재 저장 안됨
                answer.getAnsweredAt(),
                blankAnswers
        );
    }

    @SuppressWarnings("unchecked")
    private String extractQuestion(QuizType type, Map<String, Object> questionMap, String userAnswer) {
        if (questionMap == null) {
            return null;
        }

        try {
            Map<String, Object> body = (Map<String, Object>) questionMap.get("body");
            if (body == null) {
                return null;
            }

            return switch (type) {
                case TRANSLATE -> {
                    TranslateBody translateBody = objectMapper.convertValue(body, TranslateBody.class);
                    yield translateBody.getKoreanSentence();
                }
                case BLANK -> {
                    BlankBody blankBody = objectMapper.convertValue(body, BlankBody.class);
                    yield formatBlankQuestion(blankBody.getSentence(), userAnswer);
                }
                case SENTENCE_ARRANGE -> {
                    SentenceArrangeBody sentenceArrangeBody = objectMapper.convertValue(body, SentenceArrangeBody.class);
                    yield sentenceArrangeBody.getCorrectSentence();
                }
                case WORD_PRACTICE -> null;  // 단어 연습은 히스토리에서 question 추출 불필요
                case WORD_QUIZ -> null;  // 단어 퀴즈는 히스토리에서 question 추출 불필요
            };
        } catch (Exception e) {
            return null;
        }
    }

    private String formatBlankQuestion(String sentence, String userAnswer) {
        if (sentence == null || userAnswer == null || userAnswer.trim().isEmpty()) {
            return sentence;
        }

        // 사용자 답안을 공백으로 분리
        String[] answers = userAnswer.trim().split("\\s+");
        int answerIndex = 0;

        // [BLANK]를 찾아서 사용자 답안으로 치환
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < sentence.length()) {
            if (i < sentence.length() - 6 && sentence.substring(i, i + 7).equals("[BLANK]")) {
                if (answerIndex < answers.length) {
                    result.append("(").append(answers[answerIndex]).append(")");
                    answerIndex++;
                } else {
                    result.append("[BLANK]");
                }
                i += 7;
            } else {
                result.append(sentence.charAt(i));
                i++;
            }
        }

        return result.toString();
    }

    @SuppressWarnings("unchecked")
    private String extractKoreanSentence(QuizType type, Map<String, Object> questionMap) {
        if (questionMap == null) {
            return null;
        }

        try {
            Map<String, Object> body = (Map<String, Object>) questionMap.get("body");
            if (body == null) {
                return null;
            }

            return switch (type) {
                case TRANSLATE -> null;  // 번역 퀴즈는 koreanSentence가 question
                case BLANK -> {
                    BlankBody blankBody = objectMapper.convertValue(body, BlankBody.class);
                    yield blankBody.getKoreanHint();
                }
                case SENTENCE_ARRANGE -> {
                    SentenceArrangeBody sentenceArrangeBody = objectMapper.convertValue(body, SentenceArrangeBody.class);
                    yield sentenceArrangeBody.getKoreanHint();
                }
                case WORD_PRACTICE -> null;  // 단어 연습은 koreanSentence 없음
                case WORD_QUIZ -> null;  // 단어 퀴즈는 koreanSentence 없음
            };
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private String extractCorrectAnswer(QuizType type, Map<String, Object> questionMap) {
        if (questionMap == null) {
            return null;
        }

        try {
            Map<String, Object> body = (Map<String, Object>) questionMap.get("body");
            if (body == null) {
                return null;
            }

            return switch (type) {
                case TRANSLATE -> {
                    TranslateBody translateBody = objectMapper.convertValue(body, TranslateBody.class);
                    List<String> answers = translateBody.getAnswers();
                    yield answers != null && !answers.isEmpty() ? String.join(", ", answers) : null;
                }
                case BLANK -> {
                    BlankBody blankBody = objectMapper.convertValue(body, BlankBody.class);
                    List<BlankBody.BlankItem> blanks = blankBody.getBlanks();
                    if (blanks == null || blanks.isEmpty() || blankBody.getSentence() == null) {
                        yield null;
                    }
                    // 정답을 문장에 괄호로 표시
                    String sentence = blankBody.getSentence();
                    List<String> correctAnswers = blanks.stream()
                            .sorted((a, b) -> Integer.compare(a.getIndex(), b.getIndex()))
                            .map(item -> item.getAnswer().isEmpty() ? "" : item.getAnswer().get(0))  // 각 빈칸의 첫 번째 정답만 사용
                            .toList();
                    yield formatBlankQuestion(sentence, String.join(" ", correctAnswers));
                }
                case SENTENCE_ARRANGE -> {
                    SentenceArrangeBody sentenceArrangeBody = objectMapper.convertValue(body, SentenceArrangeBody.class);
                    yield sentenceArrangeBody.getCorrectSentence();
                }
                case WORD_PRACTICE -> null;  // 단어 연습은 정답 추출 불필요
                case WORD_QUIZ -> null;  // 단어 퀴즈는 정답 추출 불필요
            };
        } catch (Exception e) {
            return null;
        }
    }

    private QuizType parseQuizType(String quizType) {
        if (quizType == null || quizType.trim().isEmpty()) {
            return null;
        }
        try {
            return QuizType.valueOf(quizType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String formatQuizType(QuizType type) {
        return switch (type) {
            case TRANSLATE -> "Translate";
            case BLANK -> "Blank";
            case SENTENCE_ARRANGE -> "SentenceArrange";
            case WORD_PRACTICE -> "WordPractice";
            case WORD_QUIZ -> "WordQuiz";
        };
    }

    @SuppressWarnings("unchecked")
    private List<BlankAnswerDetail> extractBlankAnswers(QuizType type, Map<String, Object> questionMap, String userAnswer, UserQuizAnswer answer) {
        if (type != QuizType.BLANK || questionMap == null || userAnswer == null) {
            return null;
        }

        try {
            Map<String, Object> body = (Map<String, Object>) questionMap.get("body");
            if (body == null) {
                return null;
            }

            BlankBody blankBody = objectMapper.convertValue(body, BlankBody.class);
            List<BlankBody.BlankItem> blanks = blankBody.getBlanks();
            if (blanks == null || blanks.isEmpty()) {
                return null;
            }

            // 사용자 답안을 공백으로 분리
            String[] userAnswers = userAnswer.trim().split("\\s+");
            
            // 정답들을 인덱스 순서대로 정렬
            List<BlankBody.BlankItem> sortedBlanks = blanks.stream()
                    .sorted((a, b) -> Integer.compare(a.getIndex(), b.getIndex()))
                    .toList();

            // 저장된 perBlank 정보가 있으면 사용, 없으면 계산
            List<Boolean> perBlankCorrect = answer.getPerBlankCorrect();
            boolean useStoredPerBlank = perBlankCorrect != null && perBlankCorrect.size() == sortedBlanks.size();

            List<BlankAnswerDetail> details = new ArrayList<>();
            for (int i = 0; i < sortedBlanks.size(); i++) {
                BlankBody.BlankItem blankItem = sortedBlanks.get(i);
                String userAns = i < userAnswers.length ? userAnswers[i] : "";
                String correctAns = blankItem.getAnswer().isEmpty() ? "" : blankItem.getAnswer().get(0);
                
                // 저장된 perBlank 정보가 있으면 사용, 없으면 계산
                boolean isCorrect;
                if (useStoredPerBlank) {
                    isCorrect = perBlankCorrect.get(i);
                } else {
                    // 정답 여부 판단 (대소문자 무시)
                    isCorrect = userAns.equalsIgnoreCase(correctAns);
                }
                
                details.add(new BlankAnswerDetail(userAns, correctAns, isCorrect));
            }

            return details;
        } catch (Exception e) {
            return null;
        }
    }
}

