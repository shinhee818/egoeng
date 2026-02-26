package sh.egoeng.api.quiz.controller.dto.request.generate;

public record GenerateWordQuizRequest(
        /**
         * 카테고리 이름 (선택적)
         * 예: "일상", "비즈니스", "여행" 등
         * null이면 전체 카테고리에서 선택
         */
        String category,
        /**
         * 단어 레벨 (선택적)
         * BEGINNER: 초급
         * INTERMEDIATE: 중급
         * ADVANCED: 고급
         * null이면 모든 레벨에서 선택
         */
        String level,
        /**
         * 가져올 단어 개수 (기본값: 20)
         */
        Integer limit
) {}

