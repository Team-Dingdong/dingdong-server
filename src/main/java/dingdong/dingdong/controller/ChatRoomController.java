package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chat.ChatMessageResponseDto;
import dingdong.dingdong.dto.chat.ChatRoomResponseDto;
import dingdong.dingdong.dto.chat.ChatRoomUserResponseDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseResponseDto;
import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatRoomController {

    private final ChatService chatService;

    // 채팅방 목록 조회
    @GetMapping("/room")
    public ResponseEntity<Result<List<ChatRoomResponseDto>>> findChatRooms(@CurrentUser User user) {
        List<ChatRoomResponseDto> data = chatService.findAllRoom(user);
        return Result.toResult(ResultCode.CHAT_ROOM_READ_ALL_SUCCESS, data);
    }

    // 채팅방 정보 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Result<ChatRoomResponseDto>> findChatRoomByRoomId(@CurrentUser User user, @PathVariable String roomId) {
        ChatRoomResponseDto data = chatService.findRoomById(user, roomId);
        return Result.toResult(ResultCode.CHAT_ROOM_READ_SUCCESS, data);
    }

    // 채팅방 입장
    @PostMapping("/room/{roomId}")
    public ResponseEntity<Result> enterChatRoom(@CurrentUser User user, @PathVariable String roomId) {
        chatService.enterChatRoom(user, roomId);
        return Result.toResult(ResultCode.CHAT_ROOM_ENTER_SUCCESS);
    }

    // 채팅방 나가기
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Result> quitChatRoom(@CurrentUser User user, @PathVariable String roomId) {
        chatService.quitChatRoom(user, roomId);
        return Result.toResult(ResultCode.CHAT_ROOM_QUIT_SUCCESS);
    }

    // 채팅방 사용자 목록 조회
    @GetMapping("/user/{roomId}")
    public ResponseEntity<Result<List<ChatRoomUserResponseDto>>> findUsersByRoomId(@CurrentUser User user, @PathVariable String roomId) {
        List<ChatRoomUserResponseDto> data = chatService.findUsers(user, roomId);
        return Result.toResult(ResultCode.CHAT_ROOM_USER_READ_SUCCESS, data);
    }

    // 채팅 메세지 조회
    @GetMapping("/message/{roomId}")
    public ResponseEntity<Result<List<ChatMessageResponseDto>>> findChatMessagesByRoomId(@CurrentUser User user, @PathVariable String roomId) {
        List<ChatMessageResponseDto> data = chatService.findChatMessages(user, roomId);
        return Result.toResult(ResultCode.CHAT_MESSAGE_READ_SUCCESS, data);
    }

    // 채팅 약속 생성
    @PostMapping("/promise/{roomId}")
    public ResponseEntity<Result> createChatPromise(@CurrentUser User user, @PathVariable String roomId, @Valid @RequestBody ChatPromiseRequestDto request){
        chatService.createChatPromise(user, roomId, request);
        return Result.toResult(ResultCode.CHAT_PROMISE_CREATE_SUCCESS);
    }

    // 채팅 약속 수정
    @PatchMapping("/promise/{roomId}")
    public ResponseEntity<Result> updateChatPromise(@CurrentUser User user, @PathVariable String roomId, @Valid @RequestBody ChatPromiseRequestDto request){
        chatService.updatePromise(user, roomId, request);
        return Result.toResult(ResultCode.CHAT_PROMISE_UPDATE_SUCCESS);
    }

    // 채팅 약속 조회
    @GetMapping("/promise/{roomId}")
    public ResponseEntity<Result<ChatPromiseResponseDto>> findChatPromiseByPostId(@CurrentUser User user, @PathVariable String roomId){
        ChatPromiseResponseDto data = chatService.findByPostId(user, roomId);
        return Result.toResult(ResultCode.CHAT_PROMISE_READ_SUCCESS, data);
    }

    @PostMapping("/promise/vote/{post_id}")
    public ResponseEntity<Result> createVotePromsie(@CurrentUser User user, @PathVariable Long post_id){
        chatService.createVotePromise(user, post_id);
        return Result.toResult(ResultCode.CHAT_PROMISE_VOTE_CREATE_SUCCESS);
    }
}
