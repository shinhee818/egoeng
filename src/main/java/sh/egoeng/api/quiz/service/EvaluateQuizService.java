package sh.egoeng.api.quiz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sh.egoeng.api.notification.service.NotificationService;
import sh.egoeng.api.quiz.controller.dto.response.BaseEvaluationResponse;
import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.api.quiz.service.evaluate.QuizEvaluator;
import sh.egoeng.domain.notification.NotificationType;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.security.SecurityUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EvaluateQuizService {
    private final Map<QuizType, QuizEvaluator> evaluatorMap;
    private final NotificationService notificationService;

    public EvaluateQuizService(List<QuizEvaluator> evaluators, NotificationService notificationService) {
        this.evaluatorMap = evaluators.stream()
                .collect(Collectors.toMap(QuizEvaluator::getType, evaluator -> evaluator));
        this.notificationService = notificationService;
    }

    public BaseEvaluationResponse evaluate(QuizEvaluateRequest req) {
        QuizEvaluator evaluator = evaluatorMap.get(req.getQuizType());
        BaseEvaluationResponse response = evaluator.evaluate(req);

        // 🔔 퀴즈 완료 알림
        sendQuizCompletionNotification();

        return response;
    }
    
    private void sendQuizCompletionNotification() {
        try {
            Long userId = SecurityUtils.currentId();

            String title = "✅ 퀴즈 완료!";
            String message = "퀴즈를 완료했습니다. 수고하셨어요!";

            notificationService.createNotification(
                    userId,
                    NotificationType.QUIZ_AVAILABLE,
                    title,
                    message,
                    "/quiz"
            );
        } catch (Exception e) {
            log.error("Failed to send quiz completion notification", e);
        }
    }
}
