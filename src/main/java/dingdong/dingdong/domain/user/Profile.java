package dingdong.dingdong.domain.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String nickname;

    private String profile_bio;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    public Profile(User user, String nickname) {
        this.id = user.getId();
        this.user = user;
        this.nickname = nickname;
    }
}
