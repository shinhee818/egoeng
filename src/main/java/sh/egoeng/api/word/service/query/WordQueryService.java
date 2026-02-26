package sh.egoeng.api.word.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.word.service.query.dto.QueryWordSearchResult;
import sh.egoeng.api.word.service.query.dto.QueryWordSearchResultWithPaging;
import sh.egoeng.domain.word.WordMeaning;
import sh.egoeng.domain.word.WordMeaningRepository;
import sh.egoeng.domain.word.WordSearchRepository;
import sh.egoeng.domain.word.service.WordSearchDtoProjection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WordQueryService {
    private final WordSearchRepository wordSearchRepository;
    private final WordMeaningRepository wordMeaningRepository;



    public QueryWordSearchResultWithPaging searchWords(String query, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<WordSearchDtoProjection> searchPage = wordSearchRepository.findWordsByQuery(query, pageRequest);

        // Word ID 목록 추출
        List<Long> wordIds = searchPage.getContent().stream()
                .map(WordSearchDtoProjection::getId)
                .toList();
        
        if (wordIds.isEmpty()) {
            return new QueryWordSearchResultWithPaging(
                    List.of(),
                    searchPage.getTotalElements(),
                    searchPage.getTotalPages(),
                    searchPage.getNumber(),
                    searchPage.getSize(),
                    searchPage.hasNext(),
                    searchPage.hasPrevious()
            );
        }

        // 모든 의미를 배치로 조회
        Map<Long, List<WordMeaning>> meaningsByWordId = wordMeaningRepository.findAllByWordIdIn(wordIds).stream()
                .collect(Collectors.groupingBy(meaning -> meaning.getWord().getId()));
        
        // 결과 생성
        List<QueryWordSearchResult> results = searchPage.getContent().stream()
                .map(projection -> {
                    List<QueryWordSearchResult.MeaningInfo> meanings = meaningsByWordId
                            .getOrDefault(projection.getId(), List.of())
                            .stream()
                            .map(meaning -> new QueryWordSearchResult.MeaningInfo(
                                    meaning.getId(),
                                    meaning.getMeaningKo(),
                                    meaning.getPartOfSpeech(),
                                    meaning.getExample()
                            ))
                            .toList();
                    
                    return new QueryWordSearchResult(
                            projection.getId(),
                            projection.getText(),
                            projection.getCreatedAt(),
                            meanings
                    );
                })
                .toList();
        
        return new QueryWordSearchResultWithPaging(
                results,
                searchPage.getTotalElements(),
                searchPage.getTotalPages(),
                searchPage.getNumber(),
                searchPage.getSize(),
                searchPage.hasNext(),
                searchPage.hasPrevious()
        );
    }
}
