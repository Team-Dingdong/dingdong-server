package dingdong.dingdong.domain.user;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlackList {

    @Id
    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String reason;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
