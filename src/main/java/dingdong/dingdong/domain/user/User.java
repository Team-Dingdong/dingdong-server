package dingdong.dingdong.domain.user;


import dingdong.dingdong.domain.BaseTimeEntity;
import dingdong.dingdong.domain.post.Post;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String role;

    @LastModifiedDate
    private LocalDateTime localDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local1")
    private Local local1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local2")
    private Local local2;

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @OneToOne(mappedBy = "user")
    private Rating rating;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();
}
