package dingdong.dingdong.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "User")
public class User {

    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private long user_id;

    @Column(length = 20, nullable = false)
    private String password;

    @Column(length = 12,nullable = false)
    private String phone;

    @OneToOne
    @JoinColumn(name = "LOCAL_ID")
    private String local1;

    @OneToOne
    @JoinColumn(name = "LOCAL_ID")
    private String local2;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime regDate;

    @PrePersist // DB에 insert 되기 직전에 실행
    public void regDate() {
        this.regDate =  LocalDateTime.now();
    }

    private LocalDateTime local_date;
}
