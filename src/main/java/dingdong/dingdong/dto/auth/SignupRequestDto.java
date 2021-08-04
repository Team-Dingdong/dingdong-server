package dingdong.dingdong.dto.auth;

import dingdong.dingdong.domain.user.User;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    private String phone;

    private String password;

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .build();
    }
}
