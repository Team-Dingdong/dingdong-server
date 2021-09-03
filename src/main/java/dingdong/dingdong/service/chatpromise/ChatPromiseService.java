package dingdong.dingdong.service.chatpromise;

import dingdong.dingdong.domain.chat.ChatPromiseRepository;
import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseResponseDto;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

        // LocalDate과 LocalTime을 합쳐 LocalDateTime으로 변환
        LocalDate date = request.getPromiseDate();
        LocalTime time = request.getPromiseTime();
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        chatPromise.setPromiseDateTime(dateTime);

        chatPromise.setPromiseEndTime(dateTime.plusHours(3));
        chatPromiseRepository.save(chatPromise);
    }

    public void createChatPromise(Post post){
        ChatPromise chatPromise = new ChatPromise(post);
        chatPromiseRepository.save(chatPromise);
    }
}
