package dingdong.dingdong.domain.user;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
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
public class Profile {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String nickname;

    @ColumnDefault("https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_profile.jpg")
    private String profileImageUrl;

    @ColumnDefault("0")
    private Long good;

    @ColumnDefault("0")
    private Long bad;

    @PrePersist
    public void prePersist() {
        this.good = this.good == null ? 0 : this.good;
        this.bad = this.bad == null ? 0 : this.bad;
        this.profileImageUrl = this.profileImageUrl == null
            ? "https://dingdongbucket.s3.ap-northeast-2.amazonaws.com/static/default_profile.jpg"
            : this.profileImageUrl;
    }

    public void setProfileImageUrl(String path) {
        this.profileImageUrl = path;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRating(Long good, Long bad) {
        this.good = good;
        this.bad = bad;
    }
}
