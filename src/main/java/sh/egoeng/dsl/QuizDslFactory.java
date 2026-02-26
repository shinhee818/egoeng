package sh.egoeng.dsl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sh.egoeng.feign.llm.quiz.dto.response.GenerateQuizResult;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class QuizDslFactory {

    public QuizDsl buildDsl(String type, GenerateQuizResult r) {
        return switch (type.toUpperCase()) {
            case "BLANK" -> buildBlankDsl(r);
            case "TRANSLATE" -> buildTranslateDsl(r);
            case "WORD" -> buildWordDsl(r);
            case "SENTENCE_ARRANGE" -> buildSentenceArrangeDsl(r);
            default -> throw new IllegalArgumentException("Unknown quiz type: " + type);
        };
    }

    /** ---------------- BLANK ---------------- **/
    private QuizDsl buildBlankDsl(GenerateQuizResult r) {

        return QuizDsl.builder()
                .type("BLANK")
                .version("1.0")
                .meta(defaultMeta())
                .body(
                        BlankBody.builder()
                                .koreanHint(r.getKoreanSentence())
                                .sentence(r.getQuestion())
                                .blanks(
                                        IntStream.range(0, r.getAnswers().size())
                                                .mapToObj(i ->
                                                        BlankBody.BlankItem.builder()
                                                                .index(i + 1)
                                                                .answer(List.of(r.getAnswers().get(i)))
                                                                .explanation("")
                                                                .build()
                                                ).toList()
                                )
                                .build()
                )
                .build();
    }

    /** ---------------- TRANSLATE ---------------- **/
    private QuizDsl buildTranslateDsl(GenerateQuizResult r) {

        return QuizDsl.builder()
                .type("TRANSLATE")
                .version("1.0")
                .meta(defaultMeta())
                .body(
                        TranslateBody.builder()
                                .koreanSentence(r.getKoreanSentence())
                                .constraints(List.of("현재형", "문맥 자연스럽게"))
                                .answers(r.getAnswers()) // LLM이 문자열 배열 내려줌
                                .build()
                )
                .build();
    }

    /** ---------------- WORD ---------------- **/
    private QuizDsl buildWordDsl(GenerateQuizResult r) {

        return QuizDsl.builder()
                .type("WORD")
                .version("1.0")
                .meta(defaultMeta())
                .body(
                        WordBody.builder()
                                .question(r.getQuestion())
                                .options(
                                        r.getOptions().stream()
                                                .map(opt ->
                                                        WordBody.OptionItem.builder()
                                                                .id(opt.getId())
                                                                .text(opt.getText())
                                                                .build()
                                                ).toList()
                                )
                                .correctOptionId(r.getCorrectOptionId())
                                .refWordIds(r.getRefWordIds()) // LLM → List<Long> 내려주므로 그대로
                                .build()
                )
                .build();
    }

    /** ---------------- SENTENCE_ARRANGE ---------------- **/
    private QuizDsl buildSentenceArrangeDsl(GenerateQuizResult r) {

        return QuizDsl.builder()
                .type("SENTENCEARRANGE")
                .version("1.0")
                .meta(defaultMeta())
                .body(
                        SentenceArrangeBody.builder()
                                .koreanHint(r.getKoreanSentence())
                                .correctSentence(r.getQuestion())
                                .words(r.getWords())
                                .shuffledWords(r.getShuffledWords())
                                .correctOrder(r.getCorrectOrder())
                                .build()
                )
                .build();
    }

    /** ---------------- META DEFAULT ---------------- **/
    private QuizMeta defaultMeta() {
        return QuizMeta.builder()
                .difficulty("MEDIUM")
                .tags(List.of())
                .note("")
                .build();
    }


    
}
