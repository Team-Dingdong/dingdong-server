package dingdong.dingdong.domain.chat;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisChatRoom implements Serializable {

    private String roomId;
    private String title;

}
