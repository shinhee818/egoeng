package sh.egoeng.feign.llm.quiz.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import sh.egoeng.feign.llm.quiz.dto.QuestionType;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FillInTheBlankQuizRequest extends QuizRequest {
    private final String questionSentence;
    private final List<String> userAnswers;
    private final String koreanSentence;
    private final String originalSentence;

    @Builder
    public FillInTheBlankQuizRequest(
            String questionSentence,
            List<String> userAnswers,
            String koreanSentence,
            String originalSentence
    ) {
        this.questionSentence = questionSentence;
        this.userAnswers = userAnswers;
        this.koreanSentence = koreanSentence;
        this.originalSentence = originalSentence;

        setQuestionType(QuestionType.BLANK);
        setPrompt(buildPrompt());
    }

    @Override
    @JsonIgnore
    public String getType() {
        return super.getType();
    }

    @Override
    @JsonIgnore
    public String getContext() {
        return super.getContext();
    }

    @Override
    @JsonIgnore
    public String getPrompt() {
        return super.getPrompt();
    }

    @Override
    @JsonIgnore
    public QuestionType getQuestionType() {
        return super.getQuestionType();
    }

    @Override
    public String buildPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fill in all the blanks in the sentence below:\n")
                .append(questionSentence)
                .append("\n\n");

        if (originalSentence != null && !originalSentence.isBlank()) {
            sb.append("Original sentence: ").append(originalSentence).append("\n\n");
        }

        if (koreanSentence != null && !koreanSentence.isBlank()) {
            sb.append("Korean hint: ").append(koreanSentence).append("\n\n");
        }

        if (userAnswers != null && !userAnswers.isEmpty()) {
            sb.append("User answers: ").append(String.join(", ", userAnswers)).append("\n\n");
        }

        sb.append("Provide the correct words for each blank and a short feedback.");

        return sb.toString();
    }
}
