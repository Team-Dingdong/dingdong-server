package dingdong.dingdong.controller;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chat.ChatRoomResponseDto;
import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatRoomController {

    private final PostRepository postRepository;
    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping("/room")
    public ResponseEntity<Result> createRoom(@CurrentUser User user) {
        int id = 1;
        Post post = postRepository.findById(Long.valueOf(id)).orElseThrow(() -> new ResourceNotFoundException(ResultCode.POST_NOT_FOUND));
        chatService.createChatRoom(post);
        return Result.toResult(ResultCode.CHAT_ROOM_CREATE_SUCCESS);
    }

    // 채팅방 목록 조회
    @GetMapping("/room")
    public ResponseEntity<Result<List<ChatRoomResponseDto>>> getRooms(@CurrentUser User user) {
        List<ChatRoomResponseDto> data = chatService.findAllRoom(user);
        return Result.toResult(ResultCode.CHAT_ROOM_READ_ALL_SUCCESS, data);
    }

    // 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Result<ChatRoomResponseDto>> getRoom(@PathVariable String roomId) {
        ChatRoomResponseDto data = chatService.findRoomById(roomId);
        return Result.toResult(ResultCode.CHAT_ROOM_READ_SUCCESS, data);
    }
}
