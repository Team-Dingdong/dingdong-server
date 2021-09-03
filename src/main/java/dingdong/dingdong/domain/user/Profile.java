package dingdong.dingdong.domain.user;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Profile {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String nickname;

    private String profile_bio;

    @Column(columnDefinition = "varchar(255) default 'https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_profile.jpg'")
    private String profileImageUrl;

    @ColumnDefault("0")
    private Long good;

    @ColumnDefault("0")
    private Long bad;

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     *
     * null을 제외하고 insert 하기 위해 사용.
     * 또는 @DynamicInsert를 사용하면 된다.
     * */
    @PrePersist
    public void prePersist() {
        this.good = this.good == null ? 0 : this.good;
        this.bad = this.bad == null ? 0 : this.bad;
    }

    public Profile(User user) {
        this.id = user.getId();
        this.user = user;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRating(Long good, Long bad) {
        this.good = good;
        this.bad = bad;
    }
}
