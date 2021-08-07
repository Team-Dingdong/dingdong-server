package dingdong.dingdong.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private boolean done;

    public Auth(String phone, String authNumber, String requestId, LocalDateTime requestTime) {
        this.phone = phone;
        this.authNumber = authNumber;
        this.requestId = requestId;
        this.requestTime = requestTime;
    }

    public void reauth(String authNumber, String requestId, LocalDateTime requestTime, boolean done) {
        this.authNumber = authNumber;
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.done = done;
    }
}
