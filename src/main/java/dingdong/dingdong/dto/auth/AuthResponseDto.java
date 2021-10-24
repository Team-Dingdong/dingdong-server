package dingdong.dingdong.dto.auth;

import dingdong.dingdong.domain.user.Auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private Integer attemptCount;

    public static AuthResponseDto of(Auth auth) {
        return AuthResponseDto.builder()
            .attemptCount(auth.getAttemptCount())
            .build();
    }
}
