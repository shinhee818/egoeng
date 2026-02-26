package sh.egoeng.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtils {

    /**
     * 현재 인증된 사용자의 userId를 반환합니다.
     * SecurityContext의 Authentication principal에서 userId를 추출하여 반환합니다.
     *
     * @return 현재 사용자의 userId, 인증되지 않은 경우 null
     */
    public static Long currentId() {
        try {
            // SecurityContext에서 Authentication 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("⚠️ 인증되지 않은 사용자입니다.");
                return null;
            }

            // principal에서 userId 추출
            // JwtAuthenticationFilter에서 UsernamePasswordAuthenticationToken의 principal로 userId를 저장함
            Object principal = authentication.getPrincipal();

            if (principal instanceof Long userId) {
                log.debug("✅ currentId() - userId={}", userId);
                return userId;
            } else if (principal instanceof String userIdStr) {
                try {
                    Long userId = Long.parseLong(userIdStr);
                    log.debug("✅ currentId() - userId={} (String 변환)", userId);
                    return userId;
                } catch (NumberFormatException e) {
                    log.warn("⚠️ userId를 숫자로 변환할 수 없습니다: {}", userIdStr);
                    return null;
                }
            } else {
                log.warn("⚠️ 예상치 못한 principal 타입: {}", principal.getClass().getSimpleName());
                return null;
            }

        } catch (Exception e) {
            log.error("❌ userId 추출 실패: {}", e.getMessage(), e);
            return null;
        }
    }
}
