package dingdong.dingdong.dto.post;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponseDto {

    private Long id;

    private String title;

    private int people;

    private int gatheredPeople;

    private int cost;

    private String bio;

    private String location;

    private Boolean done;

    private LocalDateTime createdDate;

    private String imageUrl1;

    private List<String> tags;

    public static PostGetResponseDto from(Post post) {
        return PostGetResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .people(post.getPeople())
            .gatheredPeople(post.getGatheredPeople())
            .cost(post.getCost())
            .bio(post.getBio())
            .location(post.getLocation())
            .done(post.getDone())
            .imageUrl1(post.getImageUrl1())
            .done(post.getDone())
            .createdDate(post.getCreatedDate())
            .build();
    }

    public void setTags(List<String> tags){
        this.tags = tags;
    }
}
