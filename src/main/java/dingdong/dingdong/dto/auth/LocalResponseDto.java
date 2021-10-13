package dingdong.dingdong.dto.auth;

import dingdong.dingdong.domain.user.Local;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalResponseDto {

    private Long id;
    private String name;

    public static LocalResponseDto from(Local local) {
        return LocalResponseDto.builder()
            .id(local.getId())
            .name(local.getDong())
            .build();
    }
}
