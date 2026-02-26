package sh.egoeng.api.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sh.egoeng.api.notification.service.NotificationService;
import sh.egoeng.domain.notification.NotificationType;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.UserRepository;
import sh.egoeng.domain.word.UserWordRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final UserWordRepository userWordRepository;
    
    /**
     * 매일 오전 9시 복습 알림
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyReviewReminder() {
        log.info("Sending daily review reminders...");
        
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            try {
                long wordCount = userWordRepository.countByUserId(user.getId());
                
                if (wordCount > 0) {
                    notificationService.createNotification(
                            user.getId(),
                            NotificationType.WORD_REVIEW,
                            "📚 오늘의 복습 시간!",
                            "등록한 단어 " + wordCount + "개를 복습해보세요!",
                            "/words/review"
                    );
                }
            } catch (Exception e) {
                log.error("Failed to send review reminder to user: {}", user.getId(), e);
            }
        }
    }
    
    /**
     * 매주 월요일 오전 10시 주간 요약
     */
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendWeeklySummary() {
        log.info("Sending weekly summary notifications...");
        
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            try {
                // TODO: 지난 주 학습 통계 계산
                notificationService.createNotification(
                        user.getId(),
                        NotificationType.LEARNING_STREAK,
                        "📊 이번 주 학습 요약",
                        "지난 주 학습 기록을 확인해보세요!",
                        "/stats"
                );
            } catch (Exception e) {
                log.error("Failed to send weekly summary to user: {}", user.getId(), e);
            }
        }
    }
}
