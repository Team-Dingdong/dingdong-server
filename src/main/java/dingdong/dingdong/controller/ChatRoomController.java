package dingdong.dingdong.controller;

import dingdong.dingdong.dto.chat.ChatRoom;
import dingdong.dingdong.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatService chatService;

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> getRooms() {
        return chatService.findAllRoom();
    }

    // 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom getRoom(@PathVariable String roomId) {
        return chatService.findRoomById(roomId);
    }
}
