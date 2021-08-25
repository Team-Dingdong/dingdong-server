package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.post.Post;
import lombok.Data;

import java.io.Serializable;


@Data
public class ChatRoom implements Serializable {

    private Long id;

    private String title;

    public static ChatRoom create(Post post) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.id = post.getId();
        chatRoom.title = post.getTitle();
        return chatRoom;
    }
}