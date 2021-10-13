package dingdong.dingdong.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    private String title;

    private int people;

    private int cost;

    private String bio;

    private String local;

    private String postTag;

    private Long categoryId;

    @JsonIgnore
    private List<MultipartFile> postImages;

}
