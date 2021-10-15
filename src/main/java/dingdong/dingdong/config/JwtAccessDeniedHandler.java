package dingdong.dingdong.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        // 필요한 권한이 없이 접근하려 할때 403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter()
            .write(objectMapper.writeValueAsString(Result.of(ResultCode.FORBIDDEN_MEMBER)));
        response.setContentType("application/json");
    }
}
