package dingdong.dingdong.controller;

import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chat.ChatMessageResponseDto;
import dingdong.dingdong.dto.chat.ChatRoomResponseDto;
import dingdong.dingdong.dto.chat.ChatRoomUserResponseDto;
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
    public ResponseEntity<Result> createChatRoom(@CurrentUser User user) {
        int id = 1;
        Post post = postRepository.findById(Long.valueOf(id)).orElseThrow(() -> new ResourceNotFoundException(ResultCode.POST_NOT_FOUND));
        chatService.createChatRoom(post);
        return Result.toResult(ResultCode.CHAT_ROOM_CREATE_SUCCESS);
    }

    // 채팅방 목록 조회
    @GetMapping("/room")
    public ResponseEntity<Result<List<ChatRoomResponseDto>>> findChatRooms(@CurrentUser User user) {
        List<ChatRoomResponseDto> data = chatService.findAllRoom(user);
        return Result.toResult(ResultCode.CHAT_ROOM_READ_ALL_SUCCESS, data);
    }

    // 채팅방 정보 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Result<ChatRoomResponseDto>> findChatRoomByRoomId(@PathVariable String roomId) {
        ChatRoomResponseDto data = chatService.findRoomById(roomId);
        return Result.toResult(ResultCode.CHAT_ROOM_READ_SUCCESS, data);
    }

    // 채팅방 입장
    @PostMapping("/room/{roomId}")
    public ResponseEntity<Result> enterChatRoom(@PathVariable String roomId, @CurrentUser User user) {
        chatService.enterChatRoom(roomId, user);
        return Result.toResult(ResultCode.CHAT_ROOM_ENTER_SUCCESS);
    }

    // 채팅방 나가기
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Result> quitChatRoom(@PathVariable String roomId, @CurrentUser User user) {
        chatService.quitChatRoom(roomId, user);
        return Result.toResult(ResultCode.CHAT_ROOM_QUIT_SUCCESS);
    }

    // 채팅방 사용자 목록 조회
    @GetMapping("/user/{roomId}")
    public ResponseEntity<Result<List<ChatRoomUserResponseDto>>> findUsersByRoomId(@PathVariable String roomId) {
        List<ChatRoomUserResponseDto> data = chatService.findUsers(roomId);
        return Result.toResult(ResultCode.CHAT_ROOM_USER_READ_SUCCESS, data);
    }

    // 채팅 메세지 조회
    @GetMapping("/message/{roomId}")
    public ResponseEntity<Result<List<ChatMessageResponseDto>>> findChatMessagesByRoomId(@PathVariable String roomId) {
        List<ChatMessageResponseDto> data = chatService.findChatMessages(roomId);
        return Result.toResult(ResultCode.CHAT_MESSAGE_READ_SUCCESS, data);
    }
}
