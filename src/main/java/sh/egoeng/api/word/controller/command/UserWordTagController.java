package sh.egoeng.api.word.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.word.controller.dto.request.AddTagsToUserWordRequest;
import sh.egoeng.api.word.service.command.UserWordTagService;

@RestController
@RequestMapping("/api/user-words")
@RequiredArgsConstructor
public class UserWordTagController {
    private final UserWordTagService userWordTagService;

    /**
     * 기존 단어에 태그 추가
     */
    @PostMapping("/{userWordId}/tags")
    public ResponseEntity<Void> addTagsToUserWord(
            @PathVariable Long userWordId,
            @RequestBody AddTagsToUserWordRequest request
    ) {
        userWordTagService.addTagsToUserWord(userWordId, request.tagNames());
        return ResponseEntity.noContent().build();
    }

    /**
     * 단어에서 태그 제거
     */
    @DeleteMapping("/{userWordId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromUserWord(
            @PathVariable Long userWordId,
            @PathVariable Long tagId
    ) {
        userWordTagService.removeTagFromUserWord(userWordId, tagId);
        return ResponseEntity.noContent().build();
    }
}
















