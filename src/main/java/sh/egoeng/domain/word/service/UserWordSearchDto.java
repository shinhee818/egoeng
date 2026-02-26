package sh.egoeng.domain.word.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.word.WordType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWordSearchDto implements WordSearchDtoProjection {
    private Long id;
    private String meaningKo;
    private String text;
    private LocalDateTime createdAt;
    private Long userWordId;
    private WordType wordType;  // SEARCH 또는 CUSTOM
}











