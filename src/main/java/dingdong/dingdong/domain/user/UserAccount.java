package dingdong.dingdong.domain.user;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {
//    private dingdong.dingdong.domain.user.User user;
//
//    public UserAccount(dingdong.dingdong.domain.user.User user) {
//        super(user.getPhone(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
//        this.user = user;
//    }
    private Auth auth;

    public UserAccount(Auth auth) {
        super(auth.getPhone(), auth.getAuthNumber(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.auth = auth;
    }
}
