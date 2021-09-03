package dingdong.dingdong.dto.chatpromise;

import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.Profile;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
public class ChatPromiseResponseDto {

    private Long post_id;

    private LocalDate promiseDate;

    private LocalTime promiseTime;

    private LocalDateTime promiseDateTime;

    // 약속 장소
    private String promiseLocal;

    // 약속 마감 시간
    private LocalDateTime promiseEndTime;

    // 채팅방 마감 시간
    private LocalDateTime ChatRoomEndTime;

    public ChatPromiseResponseDto(ChatPromise chatPromise){
        this.post_id = chatPromise.getPost().getId();
        this.ChatRoomEndTime = chatPromise.getChatRoomEndTime();
        this.promiseDate = chatPromise.getPromiseDate();
        this.promiseTime = chatPromise.getPromiseTime();
        this.promiseDateTime = chatPromise.getPromiseDateTime();
        this.promiseEndTime = chatPromise.getPromiseEndTime();
        this.promiseLocal = chatPromise.getPromiseLocal();
    }
}
