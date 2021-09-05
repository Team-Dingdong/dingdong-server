package dingdong.dingdong.dto.chatpromise;

import dingdong.dingdong.domain.chat.ChatPromise;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatPromiseResponseDto {

    private Long roomId;

    private LocalDateTime promiseDateTime;

    // 약속 장소
    private String promiseLocal;

    // 약속 마감 시간
    private LocalDateTime promiseEndTime;

    public ChatPromiseResponseDto(ChatPromise chatPromise){
        this.roomId = chatPromise.getId();
        this.promiseDateTime = chatPromise.getPromiseDateTime();
        this.promiseEndTime = chatPromise.getPromiseEndTime();
        this.promiseLocal = chatPromise.getPromiseLocal();
    }
}
