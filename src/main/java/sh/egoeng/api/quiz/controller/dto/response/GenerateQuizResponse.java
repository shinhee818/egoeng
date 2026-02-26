package sh.egoeng.api.quiz.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerateQuizResponse(
        Long quizId,
        String quizType,
        String question,
        String koreanSentence,  // Blank/Translate/SentenceArrange/WordQuiz 한국어 문장
        List<String> words,  // SentenceArrange: 정답 순서 단어들
        List<String> shuffledWords,  // SentenceArrange: 섞인 단어들
        List<Integer> correctOrder  // SentenceArrange: 정답 순서
) {}


