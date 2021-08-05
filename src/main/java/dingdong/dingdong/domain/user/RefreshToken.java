package dingdong.dingdong.domain.user;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class RefreshToken {

    @Id
    private String phone;

    private String tokenValue;
}