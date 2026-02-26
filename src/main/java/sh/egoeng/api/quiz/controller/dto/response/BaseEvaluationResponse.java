package sh.egoeng.api.quiz.controller.dto.response;

/**
 * 퀴즈 평가 결과의 기본 인터페이스
 * 모든 타입의 평가 결과가 공통으로 포함해야 하는 메서드를 정의합니다.
 */
public interface BaseEvaluationResponse {

    Long quizId();           // 어떤 퀴즈에 대한 평가인지
    String prompt();
    String userAnswer();     // 사용자 답안
    String correctAnswer();  // 완전한 정답 문장 (모범답안)
    boolean correct();
    String feedback();
}

