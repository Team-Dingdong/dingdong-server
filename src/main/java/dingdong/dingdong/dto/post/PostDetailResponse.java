package dingdong.dingdong.dto.post;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class PostDetailResponse {

    @NotNull
    private String title;

    @NotNull
    private String nickname;

    @NotNull
    private String profile_bio;

    @NotNull
    private Number good;

    @NotNull
    private Number bad;

    //private String tag_name;

    @NotNull
    private int cost;

    @NotNull
    private String bio;

    @NotNull
    private String local;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
    private LocalDateTime modifiedDate;

    @NotNull
    private int people;

    @NotNull
    private int gatheredPeople;

    private String imageUrl;

    public PostDetailResponse(String title, int cost, String bio, String imageUrl, LocalDateTime createdDate, LocalDateTime modifiedDate,
                              int people, int gatheredPeople,String local, String nickname, String profile_bio, int good, int bad){
        this.title = title;
        this.cost = cost;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.people = people;
        this.gatheredPeople = gatheredPeople;
        this.local = local;
        this.nickname = nickname;
        this.profile_bio = profile_bio;
        this.good = good;
        this.bad = bad;

    }
}
