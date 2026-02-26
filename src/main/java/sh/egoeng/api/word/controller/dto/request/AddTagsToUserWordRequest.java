package sh.egoeng.api.word.controller.dto.request;

import java.util.List;

public record AddTagsToUserWordRequest(
        List<String> tagNames  // 추가할 태그 이름 리스트
) {}
















