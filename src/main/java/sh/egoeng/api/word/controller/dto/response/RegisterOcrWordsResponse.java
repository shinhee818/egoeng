package sh.egoeng.api.word.controller.dto.response;

import java.util.List;

/**
 * OCR 텍스트 일괄 등록 응답
 */
public record RegisterOcrWordsResponse(
        /**
         * 요청된 단어 개수
         */
        Integer totalCount,
        /**
         * 등록된 단어 개수
         */
        Integer count,
        /**
         * 스킵된 단어 개수 (예: 기존 Word 테이블에 이미 존재, 빈 텍스트 등)
         */
        Integer skippedCount,
        /**
         * 등록된 단어 ID 목록
         */
        List<Long> userWordIds
) {}
