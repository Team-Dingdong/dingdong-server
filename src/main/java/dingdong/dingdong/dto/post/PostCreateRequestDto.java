package dingdong.dingdong.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDto {

    private String title;

    private String people;

    private String cost;

    private String bio;

    private String local;

    private Long categoryId;

    private String postTag;

    @JsonIgnore
    private List<MultipartFile> postImages;

}
