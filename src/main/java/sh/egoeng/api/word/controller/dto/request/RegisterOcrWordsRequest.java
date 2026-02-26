package sh.egoeng.api.word.controller.dto.request;

import java.util.List;

/**
 * OCR 텍스트 일괄 등록 요청
 */
public record RegisterOcrWordsRequest(
        /**
         * 등록할 단어 정보 리스트
         */
        List<WordText> words
) {
    public record WordText(
            /**
             * 단어 텍스트 (required)
             */
            String text,
            /**
             * 한국어 뜻 (optional, null 가능)
             */
            String meaningKo
    ) {}
}
