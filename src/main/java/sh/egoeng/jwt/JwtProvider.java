package sh.egoeng.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(
            @Value("${jwt.secret}") String secret
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성 (30분 유효)
     */
    public String createAccessToken(Long userId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성 (14일 유효)
     */
    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰 파싱
     */
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에서 userId 추출
     */
    public Long getUserId(String token) {
        return Long.parseLong(parse(token).getSubject());
    }

    /**
     * 토큰이 유효한지 확인 (만료 여부 포함)
     */
    public boolean isTokenValid(String token) {
        try {
            parse(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;  // 토큰 만료됨
        } catch (io.jsonwebtoken.JwtException e) {
            return false;  // 토큰 유효하지 않음
        }
    }

    /**
     * Refresh Token이 만료되었는지 확인
     * @return true if token is expired, false if token is still valid
     */
    public boolean isRefreshTokenExpired(String token) {
        return !isTokenValid(token);  // 간단하고 명확함
    }

    /**
     * 토큰의 만료 시간까지 남은 시간(초) 반환
     * @return 남은 시간(초), 음수면 이미 만료됨
     */
    public long getTimeUntilExpiration(String token) {
        try {
            Claims claims = parse(token);
            long expirationTime = claims.getExpiration().getTime();
            long currentTime = System.currentTimeMillis();
            return (expirationTime - currentTime) / 1000;  // 초 단위
        } catch (io.jsonwebtoken.JwtException e) {
            return -1;  // 토큰 유효하지 않음
        }
    }

    /**
     * 토큰의 role 정보 추출 (Access Token용)
     */
    public String getRole(String token) {
        return (String) parse(token).get("role");
    }
}
