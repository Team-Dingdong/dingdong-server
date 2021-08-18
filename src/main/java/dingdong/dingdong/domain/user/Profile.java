package dingdong.dingdong.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String nickname;

    private String profile_bio;

    private String profileImageUrl;

    public Profile(User user) {
        this.id = user.getId();
        this.user = user;
    }

    public void createNickname(String nickname) {
        this.nickname = nickname;
    }
}
