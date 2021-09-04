package dingdong.dingdong.controller;

import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;
import dingdong.dingdong.service.chatpromise.ChatPromiseService;
import dingdong.dingdong.service.post.PostService;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/promise")
public class ChatPromiseController {

    private final ChatPromiseService chatPromiseService;


    // 채팅 약속 생성
    @PostMapping("/{id}")
    public ResponseEntity<Result> createChatPromise(@Valid @RequestBody ChatPromiseRequestDto request, @PathVariable Long id){
        chatPromiseService.createChatPromise(id, request);
        return Result.toResult(ResultCode.CHAT_PROMISE_CREATE_SUCCESS);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Result> updateChatPromise(@Valid @RequestBody ChatPromiseRequestDto request, @PathVariable Long id){
        chatPromiseService.updatePromise(id, request);
        return Result.toResult(ResultCode.CHAT_PROMISE_UPDATE_SUCCESS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<ChatPromiseResponseDto>> findChatPromiseByPostId(@PathVariable Long id){
        ChatPromiseResponseDto data = chatPromiseService.findByPostId(id);
        return Result.toResult(ResultCode.CHAT_PROMISE_READ_SUCCESS, data);
    }

}
