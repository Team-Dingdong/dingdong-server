package dingdong.dingdong.service.chat;

import dingdong.dingdong.config.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("message : {}", message);
        log.info("header : {}", message.getHeaders());
        log.info("token : {}", accessor.getNativeHeader("Authorization"));
        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwt = accessor.getFirstNativeHeader("Authorization").substring(7);
            log.info("accessToken :  {}", jwt);
            if(StringUtils.hasText(jwt) &&tokenProvider.validateToken(jwt)) {
                log.info("authentication : {}", tokenProvider.getAuthentication(jwt));
                accessor.setUser(tokenProvider.getAuthentication(jwt));
            }
        }
        return message;
    }
}
