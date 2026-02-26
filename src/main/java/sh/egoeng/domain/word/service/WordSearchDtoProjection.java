package sh.egoeng.domain.word.service;


import java.time.LocalDateTime;

public interface WordSearchDtoProjection {
    Long getId();
    String getMeaningKo();
    String getText();
    LocalDateTime getCreatedAt();
    
    /**
     * UserWord의 ID (nullable, UserWord 조회 시에만 사용)
     * Spring Data JPA가 native query의 userWordId 컬럼을 자동 매핑합니다.
     */
    Long getUserWordId();
}

