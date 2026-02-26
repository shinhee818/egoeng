package sh.egoeng.domain.notification;

public enum NotificationType {
    WORD_REVIEW,      // 단어 복습 알림
    QUIZ_AVAILABLE,   // 퀴즈 가능 알림
    LEARNING_STREAK,  // 연속 학습 달성
    DAILY_GOAL,       // 일일 목표 달성
    NEW_WORD_ADDED,   // 새 단어 추가됨
    WORD_MASTERED     // 단어 마스터 완료
}
