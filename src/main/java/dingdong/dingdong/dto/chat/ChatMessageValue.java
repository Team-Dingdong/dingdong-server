package dingdong.dingdong.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatMessageValue {

    ENTER_MESSAGE("님이 입장하셨습니다"),
    QUIT_MESSAGE("님이 퇴장하셨습니다"),
    PROMISE_CREATE_MESSAGE("에서 약속을 잡아요"),
    PROMISE_UPDATE_MESSAGE("에서 다시 약속을 잡아요"),
    PROMISE_CONFIRMED_MESSAGE("에서 만나요!");

    private final String message;
}
