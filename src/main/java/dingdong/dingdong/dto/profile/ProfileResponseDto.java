package dingdong.dingdong.dto.profile;

import dingdong.dingdong.domain.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    public static ProfileResponseDto from(Profile profile) {
        return ProfileResponseDto.builder()
                .userId(profile.getId())
                .nickname(profile.getNickname())
                .profileImageUrl(profile.getProfileImageUrl())
                .build();
    }
}
