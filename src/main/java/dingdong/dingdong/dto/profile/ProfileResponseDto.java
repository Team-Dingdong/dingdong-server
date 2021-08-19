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

    private String nickname;

    private String profile_bio;

    private String profileImageUrl;

    public static ProfileResponseDto from(Profile profile) {
        return ProfileResponseDto.builder()
                .nickname(profile.getNickname())
                .profile_bio(profile.getProfile_bio())
                .profileImageUrl(profile.getProfileImageUrl())
                .build();
    }
}
