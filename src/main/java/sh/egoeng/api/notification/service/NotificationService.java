package sh.egoeng.api.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.notification.Notification;
import sh.egoeng.domain.notification.NotificationRepository;
import sh.egoeng.domain.notification.NotificationType;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.UserRepository;
import sh.egoeng.domain.word.event.UserWordAddedEvent;
import sh.egoeng.domain.word.event.DailyWordCountEvent;

import java.util.List;

/**
 * 알림 서비스
 * - 알림 생성 (이벤트 리스너)
 * - 알림 조회 (미읽음, 모두)
 * - 알림 읽음 처리
 * SSE(Server-Sent Events) 없이 요청-응답 기반으로만 동작합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 알림 생성
     *
     * 이벤트 발생 시 자동으로 알림을 생성합니다.
     * 클라이언트는 로그인 또는 페이지 이동 시 API를 호출하여 조회합니다.
     */
    @Transactional
    public void createNotification(Long userId, NotificationType type, String title, String message, String link) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Notification notification = Notification.create(user, type, title, message, link);
        notificationRepository.save(notification);

        log.info("✅ 알림 생성 완료: userId={}, type={}, title={}", userId, type, title);
    }

    /**
     * 사용자가 단어를 추가할 때 발생하는 이벤트 리스너
     * 새 단어 추가 알림 생성
     *
     * ❌ 제거됨 - 단어 등록 시 알림 불필요
     */
    /*
    @EventListener
    public void onUserWordAdded(UserWordAddedEvent event) {
        try {
            createNotification(
                event.getUserId(),
                NotificationType.NEW_WORD_ADDED,
                "새로운 단어를 학습했습니다! 📚",
                String.format("'%s' (%s) 단어를 추가했습니다!", event.getWordText(), event.getWordMeaning()),
                "/words/" + (event.getWordId() != null ? event.getWordId() : "recent")
            );
            log.info("✅ 새 단어 추가 알림 생성: userId={}, word={}", event.getUserId(), event.getWordText());
        } catch (Exception e) {
            log.error("❌ 새 단어 추가 알림 생성 실패", e);
        }
    }
    */

    /**
     * 일일 단어 등록 개수 도달 시 발생하는 이벤트 리스너
     * (3, 6, 9, 12, ... 개마다 알림)
     */
    @EventListener
    public void onDailyWordCountReached(DailyWordCountEvent event) {
        log.info("📢 일일 단어 등록 이벤트 수신: userId={}, count={}", event.getUserId(), event.getCount());
        try {
            createNotification(
                event.getUserId(),
                NotificationType.DAILY_GOAL,
                "오늘 단어 학습 진행 중! 🌟",
                String.format("오늘 총 %d개의 단어를 등록했습니다! 계속 파이팅! 💪", event.getCount()),
                "/quiz/history"
            );
            log.info("✅ 일일 단어 알림 생성: userId={}, count={}", event.getUserId(), event.getCount());
        } catch (Exception e) {
            log.error("❌ 일일 단어 알림 생성 실패", e);
        }
    }

    /**
     * 미읽은 알림 조회
     *
     * @param userId 사용자 ID
     * @return 미읽은 알림 목록 (최신순)
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDto::from)
                .toList();
    }

    /**
     * 모든 알림 조회
     *
     * @param userId 사용자 ID
     * @return 모든 알림 목록 (최신순)
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDto::from)
                .toList();
    }

    /**
     * 알림 읽음 처리
     *
     * @param notificationId 알림 ID
     * @param userId 현재 사용자 ID (권한 검증)
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // 본인의 알림인지 확인
        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own notifications");
        }

        notification.markAsRead();
        log.info("✅ 알림 읽음 처리: notificationId={}, userId={}", notificationId, userId);
    }

    /**
     * 미읽은 알림 개수
     *
     * @param userId 사용자 ID
     * @return 미읽은 알림 개수
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}

