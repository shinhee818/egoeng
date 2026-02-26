package sh.egoeng.domain.word;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sh.egoeng.domain.word.service.WordSearchDtoProjection;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WordRepositoryTest {
    @Autowired
    private WordRepository wordRepository;

    // @Test
    // @DisplayName("FTS 검색 - banana")
    // void findWordsByFTS_banana() {
    //     // given
    //     String query = "banana";
    //     Pageable pageable = PageRequest.of(0, 20);

    //     // when
    //     long startTime = System.currentTimeMillis();
    //     Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);
    //     long endTime = System.currentTimeMillis();
    //     long executionTime = endTime - startTime;

    //     // then
    //     assertThat(result).isNotNull();
    //     assertThat(result.getContent()).isNotEmpty();
    //     assertThat(result.getTotalElements()).isGreaterThan(0);
        
    //     // 성능 확인 (인덱스 사용 시 매우 빠름)
    //     System.out.println("실행 시간: " + executionTime + "ms");
    //     System.out.println("결과 개수: " + result.getContent().size());
    //     System.out.println("전체 개수: " + result.getTotalElements());
        
    //     // 결과 출력
    //     List<WordSearchDtoProjection> content = result.getContent();
    //     for (WordSearchDtoProjection projection : content) {
    //         System.out.println("ID: " + projection.getId() + 
    //                          ", Text: " + projection.getText() + 
    //                          ", Meaning: " + projection.getMeaningKo());
    //     }
        
    //     // 성능 검증 (인덱스 사용 시 100ms 이하, 미사용 시 1000ms 이상)
    //     // 주의: 첫 실행 시에는 느릴 수 있으므로 완화된 기준 사용
    //     assertThat(executionTime).isLessThan(5000); // 5초 이하
    // }

    // @Test
    // @DisplayName("FTS 검색 - hi")
    // void findWordsByFTS_hi() {
    //     // given
    //     String query = "hi";
    //     Pageable pageable = PageRequest.of(0, 10);

    //     // when
    //     Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);

    //     // then
    //     assertThat(result).isNotNull();
    //     assertThat(result.getContent()).isNotEmpty();
        
    //     System.out.println("'hi' 검색 결과 개수: " + result.getContent().size());
    // }

    // @Test
    // @DisplayName("FTS 검색 - 페이징 테스트")
    // void findWordsByFTS_paging() {
    //     // given
    //     String query = "test";
    //     Pageable firstPage = PageRequest.of(0, 10);
    //     Pageable secondPage = PageRequest.of(1, 10);

    //     // when
    //     Page<WordSearchDtoProjection> firstPageResult = wordRepository.findWordsByFTS(query, firstPage);
    //     Page<WordSearchDtoProjection> secondPageResult = wordRepository.findWordsByFTS(query, secondPage);

    //     // then
    //     assertThat(firstPageResult.getContent().size()).isLessThanOrEqualTo(10);
    //     assertThat(secondPageResult.getContent().size()).isLessThanOrEqualTo(10);
    //     assertThat(firstPageResult.getTotalElements()).isEqualTo(secondPageResult.getTotalElements());
        
    //     System.out.println("첫 페이지: " + firstPageResult.getContent().size() + "개");
    //     System.out.println("두 번째 페이지: " + secondPageResult.getContent().size() + "개");
    //     System.out.println("전체: " + firstPageResult.getTotalElements() + "개");
    // }

    // @Test
    // @DisplayName("FTS 검색 - 빈 결과 테스트")
    // void findWordsByFTS_emptyResult() {
    //     // given
    //     String query = "nonexistentword12345";
    //     Pageable pageable = PageRequest.of(0, 10);

    //     // when
    //     Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);

    //     // then
    //     assertThat(result).isNotNull();
    //     assertThat(result.getContent()).isEmpty();
    //     assertThat(result.getTotalElements()).isEqualTo(0);
    // }
}