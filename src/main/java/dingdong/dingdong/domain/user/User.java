package dingdong.dingdong.domain.user;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import dingdong.dingdong.domain.BaseTimeEntity;
import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

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

    @Column(nullable = false, unique = true)
    private String phone;

    private String authority;

    private LocalDateTime localDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local1")
    private Local local1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local2")
    private Local local2;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Post> posts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "chatPromise_id")
    private ChatPromise chatPromise;

    @OneToOne(mappedBy = "user")
    private Profile profile;

    public User(String phone) {
        this.phone = phone;
        this.authority = "ROLE_USER";
    }

    public void setLocal(Local local1, Local local2) {
        this.local1 = local1;
        this.local2 = local2;
        this.localDate = LocalDateTime.now();
    }
}
