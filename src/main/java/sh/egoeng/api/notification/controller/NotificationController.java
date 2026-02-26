package sh.egoeng.api.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.notification.service.NotificationDto;
import sh.egoeng.api.notification.service.NotificationService;
import sh.egoeng.jwt.JwtProvider;

import java.util.List;

/**
 * 알림 조회 및 관리 API
 *
 * 로그인 시 또는 특정 페이지 이동 시에 클라이언트가 호출하여
 * 사용자의 알림 목록을 조회합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtProvider jwtProvider;

    /**
     * 미읽은 알림 목록 조회
     *
     * @param authHeader Authorization 헤더 (Bearer token)
     * @return 미읽은 알림 목록 (최신순)
     */
    @GetMapping("/unread")
    public List<NotificationDto> getUnreadNotifications(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        return notificationService.getUnreadNotifications(userId);
    }

    /**
     * 모든 알림 목록 조회
     *
     * @param authHeader Authorization 헤더 (Bearer token)
     * @return 모든 알림 목록 (최신순)
     */
    @GetMapping
    public List<NotificationDto> getAllNotifications(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        return notificationService.getAllNotifications(userId);
    }

    /**
     * 미읽은 알림 개수 조회
     *
     * @param authHeader Authorization 헤더 (Bearer token)
     * @return 미읽은 알림 개수
     */
    @GetMapping("/unread/count")
    public UnreadCountResponse getUnreadCount(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        long count = notificationService.getUnreadCount(userId);
        return new UnreadCountResponse(count);
    }

    /**
     * 알림 읽음 처리
     *
     * @param id 알림 ID
     * @param authHeader Authorization 헤더 (Bearer token)
     */
    @PutMapping("/{id}/read")
    public void markAsRead(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromToken(authHeader);
        notificationService.markAsRead(id, userId);
    }

    /**
     * 토큰에서 userId 추출
     * @param token JWT 토큰 (Authorization 헤더)
     * @return userId
     */
    private Long extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Authorization header is missing");
        }

        // Bearer 접두사 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return jwtProvider.getUserId(token);
    }

    /**
     * 미읽은 알림 개수 응답
     */
    record UnreadCountResponse(long count) {}
}

