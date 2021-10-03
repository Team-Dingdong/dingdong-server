package dingdong.dingdong.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequestDto {

    private MultipartFile profileImage;
    private String nickname;
}
