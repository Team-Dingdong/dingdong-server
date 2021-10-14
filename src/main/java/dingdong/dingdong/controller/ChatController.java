package dingdong.dingdong.controller;

import dingdong.dingdong.config.TokenProvider;
import dingdong.dingdong.domain.chat.RedisChatMessage;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

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
        String jwt = token.substring(7);
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = authService.getUserInfo();

            message.setSender(user.getId().toString());

            // Websocket에 발행된 메시지를 redis로 발행(publish)
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        }
    }
}
