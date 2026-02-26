package sh.egoeng.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.role.Role;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    private String name;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "oauth2_provider")
    private String oAuth2Provider;

    @Column(name = "oauth2_id")
    private String oAuth2Id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public User(Long id, String email, String name, String password, Role role, String oAuth2Provider, String oAuth2Id) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.oAuth2Provider = oAuth2Provider;
        this.oAuth2Id = oAuth2Id;
    }

    public static User createOAuth2User(String email, String name, String oAuth2Provider, String oAuth2Id, Role role) {
        return User.builder()
                .email(email)
                .name(name)
                .oAuth2Provider(oAuth2Provider)
                .oAuth2Id(oAuth2Id)
                .role(role)
                .build();
    }

    public static User createLocalUser(String email, String name, String password, Role role) {
        return User.builder()
                .email(email)
                .name(name)
                .password(password)
                .role(role)
                .build();
    }
}
