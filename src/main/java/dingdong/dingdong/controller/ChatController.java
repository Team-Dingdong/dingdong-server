package dingdong.dingdong.controller;

import dingdong.dingdong.config.TokenProvider;
import dingdong.dingdong.domain.chat.MessageType;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chat.RedisChatMessage;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    private final TokenProvider tokenProvider;

    private final AuthService authService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(RedisChatMessage message, @Header("Authorization") String token) {
        User user = null;
        String jwt = token.substring(7);
        if(StringUtils.hasText(jwt) &&tokenProvider.validateToken(jwt)) {
            log.info("authentication : {}", tokenProvider.getAuthentication(jwt));
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            user = authService.getUserInfo();
            if(user == null) {
                throw new ResourceNotFoundException(ResultCode.USER_NOT_FOUND);
            }
        }

        log.info("nickname : {}", user.getProfile().getNickname());
        log.info("/pub/chat/message : {}", message);
        log.info("pub/chat/message-type : {}", message.getType());
        log.info("message type equals : {}", MessageType.ENTER.equals(message.getType()));

        // 로그인 회원 정보로 대화명 설정
//         String nickname = user.getProfile().getNickname();

        // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
//        if (MessageType.ENTER.equals(message.getType())) {
//            message.setSender("[띵-동]");
//            message.setMessage(nickname + "님이 입장하셨습니다");
//        } else if(MessageType.QUIT.equals(message.getType())) {
//            message.setSender("[띵-동]");
//            message.setMessage(nickname + "님이 퇴장하셨습니다");
//        } else {
//            message.setSender(nickname);
//        }

        message.setSender(user.getId().toString());

        log.info("channelTopic : {}", channelTopic.getTopic());
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
