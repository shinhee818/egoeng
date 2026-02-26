package sh.egoeng.api.word.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.word.service.query.dto.QueryUserWordSearchResult;
import sh.egoeng.api.word.service.query.dto.QueryUserWordResult;
import sh.egoeng.domain.word.LearningStatus;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.domain.word.UserWordTagRepository;
import sh.egoeng.domain.word.WordTag;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWordReviewService {
    private final UserWordRepository userWordRepository;
    private final UserWordTagRepository userWordTagRepository;

    /**
     * 오늘 복습할 단어 조회
     */
    public QueryUserWordSearchResult getTodayReviewWords(
            Long userId,
            List<Long> tagIds,
            PageRequest pageRequest) {
        LocalDate today = LocalDate.now();
        List<LearningStatus> statuses = List.of(LearningStatus.LEARNING, LearningStatus.REVIEWING);

        Page<UserWord> page;
        if (tagIds == null || tagIds.isEmpty()) {
            page = userWordRepository.findTodayReviewWords(userId, today, statuses, pageRequest);
        } else {
            page = userWordRepository.findTodayReviewWordsWithTags(userId, today, statuses, tagIds, tagIds.size(), pageRequest);
        }

        // UserWord ID 목록 추출
        List<Long> userWordIds = page.getContent().stream()
                .map(UserWord::getId)
                .toList();

        // 태그 정보를 배치로 조회 (N+1 방지)
        Map<Long, List<QueryUserWordResult.TagInfo>> tagsByUserWordId =
                fetchTagsByUserWordIds(userWordIds);

        // 결과 생성
        List<QueryUserWordResult> results = page.getContent().stream()
                .map(userWord -> {
                    List<QueryUserWordResult.TagInfo> tags =
                            tagsByUserWordId.getOrDefault(userWord.getId(), List.of());

                    // 커스텀 단어 처리: word가 null일 경우 userWord.getId() 사용
                    Long wordId = userWord.isCustomWord() ? userWord.getId() : userWord.getWord().getId();
                    String meaningKo = userWord.getMeaningKo(); // 헬퍼 메서드 사용
                    String text = userWord.getText(); // 헬퍼 메서드 사용

                    return new QueryUserWordResult(
                            wordId,
                            userWord.getId(), // userWordId 추가
                            meaningKo,
                            text,
                            userWord.getCreatedAt(),
                            tags,
                            userWord.getLearningStatus() != null ? userWord.getLearningStatus() : LearningStatus.NEW
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

    /**
     * 지연된 복습 단어 조회
     */
    public QueryUserWordSearchResult getOverdueReviewWords(
            Long userId,
            List<Long> tagIds,
            PageRequest pageRequest) {
        LocalDate today = LocalDate.now();
        List<LearningStatus> statuses = List.of(LearningStatus.LEARNING, LearningStatus.REVIEWING);

        Page<UserWord> page;
        if (tagIds == null || tagIds.isEmpty()) {
            page = userWordRepository.findOverdueReviewWords(userId, today, statuses, pageRequest);
        } else {
            page = userWordRepository.findOverdueReviewWordsWithTags(userId, today, statuses, tagIds, tagIds.size(), pageRequest);
        }

        // UserWord ID 목록 추출
        List<Long> userWordIds = page.getContent().stream()
                .map(UserWord::getId)
                .toList();

        // 태그 정보를 배치로 조회
        Map<Long, List<QueryUserWordResult.TagInfo>> tagsByUserWordId =
                fetchTagsByUserWordIds(userWordIds);

        // 결과 생성
        List<QueryUserWordResult> results = page.getContent().stream()
                .map(userWord -> {
                    List<QueryUserWordResult.TagInfo> tags =
                            tagsByUserWordId.getOrDefault(userWord.getId(), List.of());

                    // 커스텀 단어 처리: word가 null일 경우 userWord.getId() 사용
                    Long wordId = userWord.isCustomWord() ? userWord.getId() : userWord.getWord().getId();
                    String meaningKo = userWord.getMeaningKo(); // 헬퍼 메서드 사용
                    String text = userWord.getText(); // 헬퍼 메서드 사용

                    return new QueryUserWordResult(
                            wordId,
                            userWord.getId(), // userWordId 추가
                            meaningKo,
                            text,
                            userWord.getCreatedAt(),
                            tags,
                            userWord.getLearningStatus() != null ? userWord.getLearningStatus() : LearningStatus.NEW
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

    /**
     * 여러 UserWord ID에 대한 태그 정보를 배치로 조회
     */
    private Map<Long, List<QueryUserWordResult.TagInfo>> fetchTagsByUserWordIds(List<Long> userWordIds) {
        if (userWordIds.isEmpty()) {
            return Map.of();
        }

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

}

