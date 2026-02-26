package sh.egoeng.api.quiz.service.wordquiz;

import sh.egoeng.api.quiz.controller.dto.response.GenerateWordQuizResponse;
import sh.egoeng.domain.word.Word;

import java.util.*;

/**
 * 단어 퀴즈 아이템 생성기
 * 5지선다 문제 생성 로직을 캡슐화
 */
public class WordQuizItemGenerator {
    private final List<String> allMeaningTexts;
    private final Random random;

    public WordQuizItemGenerator(List<String> allMeaningTexts) {
        this.allMeaningTexts = allMeaningTexts;
        this.random = new Random();
    }

    /**
     * 단어로부터 퀴즈 아이템 생성
     * @return WordQuizItem과 정답 인덱스 정보를 담은 Result
     */
    public QuizItemResult generateQuizItem(Word word) {
        String correctAnswer = extractCorrectAnswer(word);
        List<String> wrongAnswers = selectWrongAnswers(correctAnswer);
        List<String> choices = createChoices(correctAnswer, wrongAnswers);
        int correctAnswerIndex = choices.indexOf(correctAnswer);

        return new QuizItemResult(
                word.getId(),
                word.getText(),
                choices,
                correctAnswerIndex
        );
    }

    /**
     * 정답 추출 (첫 번째 의미 사용)
     */
    private String extractCorrectAnswer(Word word) {
        return word.getMeanings().isEmpty()
                ? ""
                : word.getMeanings().get(0).getMeaningKo();
    }

    /**
     * 오답 4개 선택
     */
    private List<String> selectWrongAnswers(String correctAnswer) {
        List<String> wrongAnswers = allMeaningTexts.stream()
                .filter(meaning -> !meaning.equals(correctAnswer))
                .collect(java.util.stream.Collectors.toList());

        Collections.shuffle(wrongAnswers, random);
        return wrongAnswers.stream()
                .limit(4)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 5지선다 생성 (정답을 랜덤 위치에 삽입)
     */
    private List<String> createChoices(String correctAnswer, List<String> wrongAnswers) {
        List<String> choices = new ArrayList<>(wrongAnswers);
        int insertPosition = random.nextInt(5); // 0~4 사이 랜덤 위치
        choices.add(insertPosition, correctAnswer);
        return choices;
    }

    /**
     * 퀴즈 아이템 생성 결과
     */
    public record QuizItemResult(
            Long wordId,
            String word,
            List<String> choices,
            int correctAnswerIndex
    ) {
        public GenerateWordQuizResponse.WordQuizItem toResponseItem() {
            return new GenerateWordQuizResponse.WordQuizItem(
                    wordId,
                    word,
                    choices,
                    correctAnswerIndex
            );
        }

        public Map<String, Object> toAnswerMap() {
            Map<String, Object> answerMap = new HashMap<>();
            answerMap.put("wordId", wordId);
            answerMap.put("correctAnswerIndex", correctAnswerIndex);
            return answerMap;
        }
    }
}













