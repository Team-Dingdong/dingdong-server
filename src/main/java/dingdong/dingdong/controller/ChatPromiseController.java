package dingdong.dingdong.controller;

import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;
import dingdong.dingdong.service.chatpromise.ChatPromiseService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/promise")
public class ChatPromiseController {

    private final ChatPromiseService chatPromiseService;

    @PatchMapping("/{id}")
    public ResponseEntity<Result> updatePost(@Valid @RequestBody ChatPromiseRequestDto request, @PathVariable Long id){
        chatPromiseService.updatePromise(id, request);
        return Result.toResult(ResultCode.POST_UPDATE_SUCCESS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<ChatPromiseResponseDto>> findChatPromiseByPostId(@PathVariable Long id){
        ChatPromiseResponseDto data = chatPromiseService.findByPostId(id);
        return Result.toResult(ResultCode.POST_READ_SUCCESS, data);
    }

}
