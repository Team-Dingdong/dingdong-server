package dingdong.dingdong.service.chatpromise;

import dingdong.dingdong.domain.chat.ChatPromiseRepository;
import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseResponseDto;
import dingdong.dingdong.dto.post.PostDetailResponseDto;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static dingdong.dingdong.util.exception.ResultCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatPromiseService {

    private final ChatPromiseRepository chatPromiseRepository;

    public ChatPromiseResponseDto findByPostId(Long id){
        ChatPromise chatPromise = chatPromiseRepository.findByPost_Id(id).orElseThrow(() -> new ResourceNotFoundException(CHAT_PROMISE_NOT_FOUND));
        ChatPromiseResponseDto promiseResponseDto = new ChatPromiseResponseDto(chatPromise);
        return promiseResponseDto;
    }

    public void updatePromise(Long id, ChatPromiseRequestDto request){
        ChatPromise chatPromise = chatPromiseRepository.findByPost_Id(id).orElseThrow(() -> new ResourceNotFoundException(CHAT_PROMISE_NOT_FOUND));
        chatPromise.setChatPromise(request);
        chatPromiseRepository.save(chatPromise);
    }

    public void createChatPromise(Post post){
        ChatPromise chatPromise = new ChatPromise(post);
        chatPromiseRepository.save(chatPromise);
    }
}
