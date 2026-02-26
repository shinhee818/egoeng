package sh.egoeng.api.word.controller.dto.response;

/**
 * 커스텀 단어 등록 응답
 */
public record RegisterCustomWordResponse(
        Long userWordId,       // 등록된 UserWord ID
        String word,           // 등록된 단어
        String meaningKo,      // 등록된 뜻
        String message         // 성공 메시지
) {
    public static RegisterCustomWordResponse success(Long userWordId, String word, String meaningKo) {
        return new RegisterCustomWordResponse(
                userWordId,
                word,
                meaningKo,
                "단어가 성공적으로 등록되었습니다."
        );
    }
}











