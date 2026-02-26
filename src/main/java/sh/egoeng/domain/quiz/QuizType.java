package sh.egoeng.domain.quiz;

public enum QuizType {
    TRANSLATE,
    BLANK,
    SENTENCE_ARRANGE,
    WORD_PRACTICE,  // 유저 단어장 기반 암기 연습 (LLM 불필요)
    WORD_QUIZ  // 일반 단어 퀴즈 (전체 Word 풀 기반, 카테고리별 랜덤)
}
