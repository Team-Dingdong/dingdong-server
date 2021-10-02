package dingdong.dingdong.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

    private String phone;
    private String authNumber;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(phone, authNumber);
    }
}
