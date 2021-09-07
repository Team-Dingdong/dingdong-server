package dingdong.dingdong.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor
public class RefreshToken {

    @Id
    private String phone;
    private String value;

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }

    public RefreshToken(String phone, String value) {
        this.phone = phone;
        this.value = value;
    }
}