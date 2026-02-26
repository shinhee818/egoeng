package sh.egoeng.api.word.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.word.service.query.dto.QueryUserWordSearchResult;
import sh.egoeng.api.word.service.query.dto.QueryUserWordResult;
import sh.egoeng.api.word.service.query.dto.UserWordSearchResult;
import sh.egoeng.domain.word.LearningStatus;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.domain.word.UserWordSearchRepository;
import sh.egoeng.domain.word.UserWordTagRepository;
import sh.egoeng.domain.word.WordTag;
import sh.egoeng.domain.word.service.WordSearchDtoProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserWordQueryService {
    private final UserWordRepository userWordRepository;
    private final UserWordSearchRepository userWordSearchRepository;
    private final UserWordTagRepository userWordTagRepository;
    private final UserWordQuerydslRepository userWordQuerydslRepository;

    public QueryUserWordSearchResult searchMyWordsByText(
            Long userId, 
            String query, 
            LocalDate fromDate,
            LocalDate toDate,
            List<Long> tagIds,
            LearningStatus learningStatus,
            PageRequest pageRequest
    ) {
        // 태그 필터링이 없으면 태그 없는 쿼리, 있으면 태그 있는 쿼리 호출
        Page<WordSearchDtoProjection> page;
        if (tagIds == null || tagIds.isEmpty()) {
            page = userWordSearchRepository.findUserWordsWithoutTags(userId, query, fromDate, toDate, learningStatus, pageRequest);
        } else {
            page = userWordSearchRepository.findUserWordsWithTags(userId, query, fromDate, toDate, tagIds, learningStatus, pageRequest);
        }
        
        // UserWord ID 목록 추출
        List<Long> userWordIds = page.getContent().stream()
                .map(WordSearchDtoProjection::getUserWordId)
                .filter(java.util.Objects::nonNull)
                .toList();
        
        // 태그 정보를 배치로 조회 (N+1 방지)
        Map<Long, List<QueryUserWordResult.TagInfo>> tagsByUserWordId = 
                fetchTagsByUserWordIds(userWordIds);
        
        // 태그 정보 포함하여 결과 생성
        List<QueryUserWordResult> results = page.getContent().stream()
                .map(projection -> {
                    Long userWordId = projection.getUserWordId();
                    List<QueryUserWordResult.TagInfo> tags = 
                            (userWordId != null) 
                                    ? tagsByUserWordId.getOrDefault(userWordId, List.of())
                                    : List.of();
                    return new QueryUserWordResult(
                            projection.getId(),
                            userWordId,
                            projection.getMeaningKo(),
                            projection.getText(),
                            projection.getCreatedAt(),
                            tags,
                            learningStatus
                    );
                })
                .toList();
        
        return new QueryUserWordSearchResult(
                results,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public List<QueryUserWordResult> searchLatestWords() {
        List<WordSearchDtoProjection> projections = userWordRepository.findLatestUserWordsWithMeanings();
        
        // UserWord ID 목록 추출
        List<Long> userWordIds = projections.stream()
                .map(WordSearchDtoProjection::getUserWordId)
                .filter(java.util.Objects::nonNull)
                .toList();
        
        // 태그 정보를 배치로 조회
        Map<Long, List<QueryUserWordResult.TagInfo>> tagsByUserWordId = 
                fetchTagsByUserWordIds(userWordIds);
        
        // UserWord 정보를 배치로 조회하여 learningStatus 가져오기
        Map<Long, sh.egoeng.domain.word.LearningStatus> learningStatusByUserWordId =
                fetchLearningStatusByUserWordIds(userWordIds);

        // 태그 정보 및 학습 상태 포함하여 결과 생성
        return projections.stream()
                .map(projection -> {
                    Long userWordId = projection.getUserWordId();
                    List<QueryUserWordResult.TagInfo> tags = 
                            (userWordId != null) 
                                    ? tagsByUserWordId.getOrDefault(userWordId, List.of())
                                    : List.of();
                    sh.egoeng.domain.word.LearningStatus learningStatus =
                            (userWordId != null)
                                    ? learningStatusByUserWordId.getOrDefault(userWordId, sh.egoeng.domain.word.LearningStatus.NEW)
                                    : sh.egoeng.domain.word.LearningStatus.NEW;
                    return new QueryUserWordResult(
                            projection.getId(),
                            projection.getUserWordId(),
                            projection.getMeaningKo(),
                            projection.getText(),
                            projection.getCreatedAt(),
                            tags,
                            learningStatus
                    );
                })
                .toList();
    }

    /**
     * 여러 UserWord ID에 대한 태그 정보를 배치로 조회
     */
    private Map<Long, List<QueryUserWordResult.TagInfo>> fetchTagsByUserWordIds(List<Long> userWordIds) {
        if (userWordIds.isEmpty()) {
            return Map.of();
        }
        
        // UserWordTag를 태그 정보와 함께 조회
        return userWordTagRepository.findByUserWordIdIn(userWordIds).stream()
                .collect(Collectors.groupingBy(
                        uwt -> uwt.getUserWord().getId(),
                        Collectors.mapping(
                                uwt -> {
                                    WordTag tag = uwt.getTag();
                                    return new QueryUserWordResult.TagInfo(
                                            tag.getId(),
                                            tag.getName(),
                                            tag.getColor()
                                    );
                                },
                                Collectors.toList()
                        )
                ));
    }

    public List<UserWordSearchResult> searchUserWords(Long userId) {
        return userWordQuerydslRepository.search(userId);
    }

    public List<UserWordSearchResult> searchUserWordsByText(Long userId, String searchText) {
        return userWordQuerydslRepository.searchByText(userId, searchText);
    }

    public List<UserWordSearchResult> searchFavoriteWords(Long userId) {
        return userWordQuerydslRepository.searchFavorites(userId);
    }

    /**
     * 여러 UserWord ID에 대한 학습 상태를 배치로 조회
     */
    private Map<Long, sh.egoeng.domain.word.LearningStatus> fetchLearningStatusByUserWordIds(List<Long> userWordIds) {
        if (userWordIds.isEmpty()) {
            return Map.of();
        }

        // UserWord를 배치로 조회하여 learningStatus 추출
        return userWordRepository.findAllById(userWordIds).stream()
                .collect(Collectors.toMap(
                        sh.egoeng.domain.word.UserWord::getId,
                        userWord -> userWord.getLearningStatus() != null
                                ? userWord.getLearningStatus()
                                : sh.egoeng.domain.word.LearningStatus.NEW
                ));
    }
}
