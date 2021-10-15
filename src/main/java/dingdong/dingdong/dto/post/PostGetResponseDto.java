package dingdong.dingdong.dto.post;

import dingdong.dingdong.domain.post.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponseDto {

    private Long id;

    private String title;

    private Integer people;

    private Integer cost;

    private String bio;

    private String local;

    private Boolean done;

    private LocalDateTime createdDate;

    private String imageUrl1;

    private String tag;

    public static PostGetResponseDto from(Post post) {
        return PostGetResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .people(post.getPeople())
            .cost(post.getCost())
            .bio(post.getBio())
            .local(post.getLocal())
            .done(post.getDone())
            .imageUrl1(post.getImageUrl1())
            .createdDate(post.getCreatedDate())
            .build();
    }
}
