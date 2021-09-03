package dingdong.dingdong.service.chat;

import dingdong.dingdong.domain.chat.*;
import dingdong.dingdong.dto.chat.RedisChatRoom;
import dingdong.dingdong.dto.chat.RedisChatRoomRepository;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅 조회
    @Transactional
    public List<ChatMessage> findAllChatMessage(String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoom(chatRoom);
        return chatMessages;
    }
}
