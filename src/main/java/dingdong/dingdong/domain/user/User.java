package dingdong.dingdong.domain.user;

import dingdong.dingdong.domain.post.Post;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime regDate;

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

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

}
