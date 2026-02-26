package sh.egoeng.api.word.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.learning.controller.dto.response.LearningWordQueryResponse;
import sh.egoeng.api.word.controller.query.dto.TagInfo;
import sh.egoeng.domain.word.LearningStatus;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.domain.word.UserWordTagRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWordStudyQueryService {

    private final UserWordRepository userWordRepository;
    private final UserWordTagRepository userWordTagRepository;

    public LearningWordQueryResponse getLearningWords(
            Long userId,
            Long cursor,
            int limit,
            List<LearningStatus> statuses
    ) {
        // cursor보다 작은 ID의 항목들을 limit+1개 조회
        List<UserWord> userWords;
        if (statuses == null || statuses.isEmpty()) {
            // statuses가 비어있으면 필터링 없이 조회
            userWords = userWordRepository.findByUserIdAndCursor(
                    userId, cursor, PageRequest.of(0, limit + 1)
            );
        } else {
            // statuses로 필터링하여 조회
            userWords = userWordRepository.findByUserIdAndCursorAndStatuses(
                    userId, cursor, statuses, PageRequest.of(0, limit + 1)
            );
        }

        boolean hasNext = userWords.size() > limit;
        if (hasNext) {
            userWords = userWords.subList(0, limit);
        }

        Long nextCursor = hasNext && !userWords.isEmpty()
                ? userWords.get(userWords.size() - 1).getId()
                : null;

        // 태그 정보를 배치로 조회
        List<Long> userWordIds = userWords.stream()
                .map(UserWord::getId)
                .toList();
        
        Map<Long, List<TagInfo>> tagsByUserWordId = userWordTagRepository
                .findByUserWordIdIn(userWordIds)
                .stream()
                .collect(Collectors.groupingBy(
                        uwt -> uwt.getUserWord().getId(),
                        Collectors.mapping(
                                uwt -> new TagInfo(
                                        uwt.getTag().getId(),
                                        uwt.getTag().getName(),
                                        uwt.getTag().getColor()
                                ),
                                Collectors.toList()
                        )
                ));

        List<LearningWordQueryResponse.LearningWordItem> items = userWords.stream()
                .map(userWord -> {
                    String meaningKo = userWord.getMeaningKo(); // 헬퍼 메서드 사용
                    if (meaningKo == null) {
                        meaningKo = "";
                    }

                    List<TagInfo> tags = tagsByUserWordId.getOrDefault(userWord.getId(), List.of());

                    return new LearningWordQueryResponse.LearningWordItem(
                            userWord.getId(),
                            userWord.getText(), // 헬퍼 메서드 사용
                            meaningKo,
                            userWord.getLearningStatus(),
                            userWord.getMasteryLevel() != null ? userWord.getMasteryLevel() : 0,
                            userWord.getReviewCount() != null ? userWord.getReviewCount() : 0,
                            userWord.getNextReviewDate(),
                            userWord.getLastStudiedAt(),
                            userWord.getIsFavourite() != null ? userWord.getIsFavourite() : false,
                            tags
                    );
                })
                .toList();

        return new LearningWordQueryResponse(items, hasNext, nextCursor);
    }
}

