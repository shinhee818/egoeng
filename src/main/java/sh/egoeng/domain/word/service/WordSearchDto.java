package sh.egoeng.domain.word.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordSearchDto implements WordSearchDtoProjection {
    private Long id;
    private String meaningKo;
    private String text;
    private LocalDateTime createdAt;
    private Long userWordId;
}











