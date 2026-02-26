package sh.egoeng.feign.llm.quiz.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ArrangeQuizRequest {
    private final String question;
    private final List<String> shuffledWords;
    private final String userSentence;

    @Builder
    public ArrangeQuizRequest(String question, List<String> shuffledWords, String userSentence) {
        this.question = question;
        this.shuffledWords = shuffledWords;
        this.userSentence = userSentence;
    }
}


















