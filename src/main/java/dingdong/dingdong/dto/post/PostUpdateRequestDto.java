package dingdong.dingdong.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequestDto {

    private String title;

    private String people;

    private String cost;

    private String bio;

    private String local;

    private String postTag;

    private Long categoryId;

    @JsonIgnore
    private List<MultipartFile> postImages;
}
