package sh.egoeng.domain.word;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.BaseEntity;
import sh.egoeng.domain.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

// word 1

// user word 는 위에 word 유저 단어로 등록 하는거 (매핑 테이블)

// user 1, 2

// user word user1인 사람이 1 word 등록
// user word user2인 사람이 1 word 등록

// 내가 최근에 등록한 단어 보여줘


@Entity
@Table(name="user_word", indexes = {
    @Index(name = "idx_user_word_created_at", columnList = "created_at"),
    @Index(name = "idx_user_word_learning_status", columnList = "learning_status"),
    @Index(name = "idx_user_word_next_review_date", columnList = "next_review_date"),
    @Index(name = "idx_user_word_user_next_review", columnList = "user_id, next_review_date")
})
@Getter
@NoArgsConstructor
public class UserWord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;  // nullable: 커스텀 단어일 경우 null

    /**
     * 유저가 직접 입력한 단어의 텍스트
     * word가 null일 때만 사용
     */
    @Column(name = "custom_text", length = 512)
    private String customText;

    /**
     * 유저가 직접 입력한 단어의 한국어 뜻
     * word가 null일 때만 사용
     */
    @Column(name = "custom_meaning_ko")
    private String customMeaningKo;

    /**
     * 즐겨찾기 여부
     * 사용자가 중요한 단어를 즐겨찾기로 표시할 수 있음
     */
    @Column
    private Boolean isFavourite;

    /**
     * 복습 여부
     * 단어를 복습했는지 여부
     */
    @Column
    private Boolean isReviewed;

    /**
     * 학습 상태
     * NEW: 처음 등록한 단어 (아직 학습 안 함)
     * LEARNING: 학습 중인 단어
     * REVIEWING: 복습 중인 단어
     * MASTERED: 암기 완료된 단어
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "learning_status")
    private LearningStatus learningStatus = LearningStatus.NEW;

    /**
     * 마지막 학습 시간
     * 이 단어를 마지막으로 학습한 시간
     * 학습 시작(startStudy) 또는 복습 완료(completeReview) 시 업데이트됨
     */
    @Column(name = "last_studied_at")
    private LocalDateTime lastStudiedAt;

    /**
     * 복습 횟수
     * 이 단어를 몇 번 복습했는지 카운트
     * 복습 완료 시마다 1씩 증가
     * 스페이싱 리피티션 알고리즘에서 복습 간격 계산에 사용됨
     */
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    /**
     * 다음 복습 예정일
     * 이 단어를 언제 다시 복습해야 하는지 날짜
     * 스페이싱 리피티션 알고리즘에 따라 계산됨
     * - 정답 시: 간격 증가 (1일 → 3일 → 7일 → 14일 → 30일 → 60일)
     * - 오답 시: 간격 단축 (이전 간격의 1/2)
     * MASTERED 상태일 때는 null
     */
    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    /**
     * 암기 정도 (0-100)
     * 이 단어를 얼마나 잘 암기했는지 나타내는 점수
     * - 0-30: 초보 (처음 배우는 단계)
     * - 31-60: 학습 중 (어느 정도 알지만 미완성)
     * - 61-89: 복습 중 (거의 다 외웠지만 더 복습 필요)
     * - 90-100: 암기 완료 (완벽하게 외움, MASTERED 상태로 전환)
     * 복습 완료 시 정답/오답에 따라 증가 또는 감소
     */
    @Column(name = "mastery_level")
    private Integer masteryLevel = 0;

    /**
     * 단어 암기 연습 횟수
     * 유저 단어 퀴즈에서 단어 숨김/뜻 숨김 모드로 연습한 횟수
     * 단순히 단어를 보고 암기하는 연습을 할 때마다 1씩 증가
     */
    @Column(name = "practice_count")
    private Integer practiceCount = 0;

    /**
     * 마지막 연습 시간
     * 유저 단어 퀴즈에서 마지막으로 연습한 시간
     * 단어 숨김/뜻 숨김 모드로 연습할 때마다 업데이트됨
     */
    @Column(name = "last_practiced_at")
    private LocalDateTime lastPracticedAt;

    /**
     * 학습 목표 개수
     * 이 단어를 학습할 때 설정한 목표 개수
     * 예: "오늘 10개 단어 학습하기" 중 이 단어가 포함된 경우
     * 학습 시작 시 설정되며, 학습 완료 여부 추적에 사용됨
     */
    @Column(name = "learning_goal")
    private Integer learningGoal;

    /**
     * 단어 타입
     * SEARCH: 검색해서 등록한 단어 (Word 테이블 참조)
     * CUSTOM: 사용자가 직접 입력한 단어
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "word_type")
    private WordType wordType;

    @Builder
    public UserWord(User user, Word word) {
        this.user = user;
        this.word = word;
        this.wordType = WordType.SEARCH;  // 검색 단어
        this.learningStatus = LearningStatus.NEW;
        this.reviewCount = 0;
        this.masteryLevel = 0;
        this.practiceCount = 0;
    }

    /**
     * 커스텀 단어 생성자 (word 없이)
     */
    public static UserWord createCustomWord(User user, String customText, String customMeaningKo) {
        UserWord userWord = new UserWord();
        userWord.user = user;
        userWord.word = null;
        userWord.wordType = WordType.CUSTOM;  // 커스텀 단어
        userWord.customText = customText;
        userWord.customMeaningKo = customMeaningKo;
        userWord.learningStatus = LearningStatus.NEW;
        userWord.reviewCount = 0;
        userWord.masteryLevel = 0;
        userWord.practiceCount = 0;
        return userWord;
    }

    /**
     * 단어 텍스트 반환 (기존 Word 또는 커스텀 텍스트)
     */
    public String getText() {
        return word != null ? word.getText() : customText;
    }

    /**
     * 한국어 뜻 반환 (기존 WordMeaning 또는 커스텀 뜻)
     */
    public String getMeaningKo() {
        if (word != null && !word.getMeanings().isEmpty()) {
            return word.getMeanings().get(0).getMeaningKo();
        }
        return customMeaningKo;
    }

    /**
     * 유저가 직접 입력한 단어인지 확인
     */
    public boolean isCustomWord() {
        return word == null;
    }

    /**
     * Word 참조 설정 (파파고 처리 등에서 사용)
     */
    public void setWord(Word word) {
        this.word = word;
        if (word != null) {
            this.customText = null;
            this.customMeaningKo = null;
        }
    }

    /**
     * 학습 시작
     * 단어 학습을 시작할 때 호출
     * - NEW 상태면 LEARNING으로 변경
     * - lastStudiedAt을 현재 시간으로 업데이트
     * 
     * @param learningGoal 학습 목표 개수 (선택적)
     */
    public void startStudy(Integer learningGoal) {
        if (this.learningStatus == LearningStatus.NEW) {
            this.learningStatus = LearningStatus.LEARNING;
        }
        this.lastStudiedAt = LocalDateTime.now();
        if (learningGoal != null) {
            this.learningGoal = learningGoal;
        }
    }

    /**
     * 학습 시작 (목표 없이)
     * 기존 호환성을 위한 오버로드 메서드
     */
    public void startStudy() {
        startStudy(null);
    }

    /**
     * 복습 완료
     * 단어 복습을 완료했을 때 호출
     * 
     * @param isCorrect 정답 여부
     * @param reviewCount 업데이트할 복습 횟수 (이미 계산된 값)
     * @param masteryLevel 업데이트할 암기 정도 (이미 계산된 값, 0-100)
     * @param nextReviewDate 계산된 다음 복습 예정일
     * 
     * 동작:
     * - reviewCount, masteryLevel, nextReviewDate 업데이트
     * - lastStudiedAt을 현재 시간으로 업데이트
     * - learningStatus 자동 업데이트:
     *   - NEW → LEARNING
     *   - LEARNING → REVIEWING
     *   - masteryLevel >= 90 → MASTERED (nextReviewDate = null)
     */
    public void completeReview(boolean isCorrect, int reviewCount, int masteryLevel, LocalDate nextReviewDate) {
        this.reviewCount = reviewCount;
        this.masteryLevel = masteryLevel;
        this.nextReviewDate = nextReviewDate;
        this.lastStudiedAt = LocalDateTime.now();

        // 학습 상태 업데이트
        if (this.learningStatus == LearningStatus.NEW) {
            this.learningStatus = LearningStatus.LEARNING;
        } else if (this.learningStatus == LearningStatus.LEARNING) {
            this.learningStatus = LearningStatus.REVIEWING;
        }

        // 암기 완료 체크
        if (masteryLevel >= 90) {
            this.learningStatus = LearningStatus.MASTERED;
            this.nextReviewDate = null;
        }
    }

    /**
     * 학습 완료
     * 학습 시작 후 단어를 보고 "알겠어요"를 누를 때 호출
     * - NEW 상태면 LEARNING으로 변경
     * - lastStudiedAt을 현재 시간으로 업데이트
     * - 복습 관련 필드는 변경하지 않음 (복습이 아닌 첫 학습 완료)
     */
    public void completeLearning() {
        if (this.learningStatus == LearningStatus.NEW) {
            this.learningStatus = LearningStatus.LEARNING;
        }
        this.lastStudiedAt = LocalDateTime.now();
    }

    /**
     * 암기 완료
     * 사용자가 수동으로 암기 완료 처리할 때 호출
     * - learningStatus를 MASTERED로 변경
     * - masteryLevel을 100으로 설정
     * - nextReviewDate를 null로 설정 (더 이상 복습 불필요)
     * - lastStudiedAt을 현재 시간으로 업데이트
     */
    public void master() {
        this.learningStatus = LearningStatus.MASTERED;
        this.masteryLevel = 100;
        this.nextReviewDate = null;
        this.lastStudiedAt = LocalDateTime.now();
    }

    /**
     * 단어 암기 연습 완료
     * 유저 단어 퀴즈에서 단어 숨김/뜻 숨김 모드로 연습을 완료했을 때 호출
     * - practiceCount를 1 증가
     * - lastPracticedAt을 현재 시간으로 업데이트
     */
    public void completePractice() {
        this.practiceCount = (this.practiceCount != null ? this.practiceCount : 0) + 1;
        this.lastPracticedAt = LocalDateTime.now();
    }
}
