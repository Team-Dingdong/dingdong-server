package dingdong.dingdong.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Profile")
public class Profile {

    @Column(length = 12, nullable = false)
    private String nickname;

    @Column(length = 20)
    private String bio;

    @Column(length = 100)
    private String profile_image_url;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private long user_id;

}
