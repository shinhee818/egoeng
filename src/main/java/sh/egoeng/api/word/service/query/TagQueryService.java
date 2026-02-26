package sh.egoeng.api.word.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.word.controller.query.dto.TagListResponse;
import sh.egoeng.domain.word.UserWordTagRepository;
import sh.egoeng.domain.word.WordTag;
import sh.egoeng.domain.word.WordTagRepository;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TagQueryService {
    private final WordTagRepository wordTagRepository;
    private final UserWordTagRepository userWordTagRepository;

    /**
     * 사용자가 등록한 모든 태그 목록 조회 (최신순)
     * 각 태그별로 연결된 단어 개수도 포함
     */
    public List<TagListResponse> getAllTagsByUserId(Long userId) {
        List<WordTag> tags = wordTagRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return tags.stream()
                .map(tag -> {
                    // 각 태그에 연결된 단어 개수 조회
                    long wordCount = userWordTagRepository.countByTagId(tag.getId());
                    
                    return new TagListResponse(
                            tag.getId(),
                            tag.getName(),
                            tag.getColor(),
                            tag.getCreatedAt(),
                            wordCount
                    );
                })
                .toList();
    }
}
















