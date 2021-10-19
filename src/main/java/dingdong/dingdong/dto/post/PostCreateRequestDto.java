package dingdong.dingdong.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDto {

    private String title;

    private String people;

    private String cost;

    private String bio;

    private String location;

    private Long categoryId;

    private String postTag;

    @JsonIgnore
    private List<MultipartFile> postImages;

}
