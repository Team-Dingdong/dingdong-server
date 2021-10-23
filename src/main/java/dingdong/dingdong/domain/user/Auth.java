package dingdong.dingdong.domain.user;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id", nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String authNumber;

    @Column(nullable = false, unique = true)
    private String requestId;

    @Column(nullable = false)
    private LocalDateTime requestTime;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer attemptCount;

    private LocalDateTime coolTime;

    public void reauth(String authNumber, String requestId, LocalDateTime requestTime) {
        this.authNumber = authNumber;
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.attemptCount = 0;
    }

    @PrePersist
    public void prePersist() {
        this.attemptCount = this.attemptCount == null ? 0 : this.attemptCount;
    }

    public void plusAttemptCount() {
        this.attemptCount += 1;
    }

    public void reset() {
        this.attemptCount = 0;
        this.requestTime = LocalDateTime.now().plusMinutes(5);
    }

    public void setCoolTime(Long minute) {
        this.coolTime = LocalDateTime.now().plusMinutes(minute);
    }
}
