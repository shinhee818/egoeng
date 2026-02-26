package sh.egoeng.feign.llm.quiz.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sh.egoeng.api.quiz.controller.dto.request.evaluate.TranslateQuizRequest;
import sh.egoeng.feign.llm.quiz.dto.QuestionType;

@Getter
@Setter
public class TranslateSentenceQuizRequest extends QuizRequest {

    private String koreanSentence;
    private String userAnswer;

    @Builder
    public TranslateSentenceQuizRequest(String koreanSentence, String userAnswer) {
        setQuestionType(QuestionType.TRANSLATE);
        this.koreanSentence = koreanSentence;
        this.userAnswer = userAnswer;
        setPrompt(buildPrompt());
    }

    @Override
    public String buildPrompt() {
        return "Translate the following Korean sentence into natural English:\n" + koreanSentence;
    }

    public static TranslateSentenceQuizRequest from(TranslateQuizRequest req) {
        return TranslateSentenceQuizRequest.builder()
                .koreanSentence(req.koreanSentence())
                .userAnswer(req.userAnswer())
                .build();
    }
}
