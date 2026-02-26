package sh.egoeng.api.quiz.controller.dto.request.generate;

public record GenerateWordPracticeQuizRequest(
        /**
         * 연습 모드
         * WORD_HIDE: 단어 숨김 (뜻만 보여줌)
         * MEANING_HIDE: 뜻 숨김 (단어만 보여줌)
         */
        String mode,
        /**
         * 가져올 단어 개수 (기본값: 20)
         */
        Integer limit
) {}













