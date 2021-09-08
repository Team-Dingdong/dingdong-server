package dingdong.dingdong.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class S3RequestDto {

    private List<MultipartFile> postImages;
    private List<String> image_urls;

}
