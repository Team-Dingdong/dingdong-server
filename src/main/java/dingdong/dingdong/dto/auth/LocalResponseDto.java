package dingdong.dingdong.dto.auth;

import dingdong.dingdong.domain.user.Local;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocalResponseDto {

    Long id;
    String name;

    public static LocalResponseDto from(Local local) {
        return LocalResponseDto.builder()
            .id(local.getId())
            .name(local.getDong())
            .build();
    }
}
