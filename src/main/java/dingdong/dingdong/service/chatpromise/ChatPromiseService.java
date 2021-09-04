package dingdong.dingdong.service.chatpromise;

import dingdong.dingdong.domain.chat.ChatJoin;
import dingdong.dingdong.domain.chat.ChatJoinRepository;
import dingdong.dingdong.domain.chat.ChatPromiseRepository;
import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseResponseDto;
import dingdong.dingdong.dto.post.PostGetResponseDto;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static dingdong.dingdong.util.exception.ResultCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatPromiseService {

    private final ChatPromiseRepository chatPromiseRepository;
    private final ChatJoinRepository chatJoinRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ChatPromiseResponseDto findByPostId(Long id){
        ChatPromise chatPromise = chatPromiseRepository.findByPost_Id(id).orElseThrow(() -> new ResourceNotFoundException(CHAT_PROMISE_NOT_FOUND));
        ChatPromiseResponseDto promiseResponseDto = new ChatPromiseResponseDto(chatPromise);
        return promiseResponseDto;
    }

    public void updatePromise(Long id, ChatPromiseRequestDto request){
        ChatPromise chatPromise = chatPromiseRepository.findByPost_Id(id).orElseThrow(() -> new ResourceNotFoundException(CHAT_PROMISE_NOT_FOUND));
        chatPromise.setChatPromise(request);

        // LocalDate과 LocalTime을 합쳐 LocalDateTime으로 변환
        LocalDate date = request.getPromiseDate();
        LocalTime time = request.getPromiseTime();
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        chatPromise.setPromiseDateTime(dateTime);

        chatPromise.setPromiseEndTime(dateTime.plusHours(3));
        chatPromiseRepository.save(chatPromise);
    }

    // 채팅 약속 생성
    public void createChatPromise(Long id,ChatPromiseRequestDto request){
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        List<ChatJoin> chatJoins =  chatJoinRepository.findAllByPost_Id(post.getId());
        List<User> users = new ArrayList<>();
        for(int i =0; i < chatJoins.size(); i++){
            ChatJoin chatJoin = chatJoins.get(i);
            users.add(chatJoin.getUser());
        }
        ChatPromise chatPromise = new ChatPromise(post, users);

        LocalDate date = request.getPromiseDate();
        LocalTime time = request.getPromiseTime();
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        chatPromise.setPromiseDateTime(dateTime);

        chatPromise.setPromiseEndTime(dateTime.plusHours(3));

        chatPromiseRepository.save(chatPromise);

    }
}
