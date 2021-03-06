package dingdong.dingdong.dto.post;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponseDto {

    private String category;

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    private String title;

    private Number good;

    private Number bad;

    private int cost;

    private String bio;

    private String location;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private int people;

    private int gatheredPeople;

    private Boolean done;

    private String imageUrl1;

    private String imageUrl2;

    private String imageUrl3;

    private List<String> tags;

    public static PostDetailResponseDto from(Post post, List<Tag> tags) {
        return PostDetailResponseDto.builder()
            .category(post.getCategory().getName())
            .title(post.getTitle())
            .cost(post.getCost())
            .bio(post.getBio())
            .imageUrl1(post.getImageUrl1())
            .imageUrl2(post.getImageUrl2())
            .imageUrl3(post.getImageUrl3())
            .createdDate(post.getCreatedDate())
            .modifiedDate(post.getModifiedDate())
            .people(post.getPeople())
            .gatheredPeople(post.getGatheredPeople())
            .done(post.getDone())
            .location(post.getLocation())
            .userId(post.getUser().getId())
            .nickname(post.getUser().getProfile().getNickname())
            .profileImageUrl(post.getUser().getProfile().getProfileImageUrl())
            .good(post.getUser().getProfile().getGood())
            .bad(post.getUser().getProfile().getBad())
            .tags(tags.stream().map(t -> "#" + t.getName()).collect(Collectors.toList()))
            .build();
    }
}
