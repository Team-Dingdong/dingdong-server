package dingdong.dingdong.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.user.Auth;
import dingdong.dingdong.domain.user.AuthRepository;
import dingdong.dingdong.domain.user.Local;
import dingdong.dingdong.domain.user.LocalRepository;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.LocalRequestDto;
import dingdong.dingdong.dto.auth.NicknameRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.dto.auth.TokenRequestDto;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthService authService;

    @Autowired
    AuthRepository authRepository;

    @Autowired
    LocalRepository localRepository;

    @Value("${test.server.http.scheme}")
    String scheme;
    @Value("${test.server.http.host}")
    String host;
    @Value("${test.server.http.port}")
    int port;

    @BeforeEach
    void setUp() {
        String phone = "01012345678";
        String authNumber = "123456";
        String requestId = "testRequestId";
        LocalDateTime requestTime = LocalDateTime.now();
        Auth auth = new Auth(phone, authNumber, requestId, requestTime);
        authRepository.save(auth);
    }

    TokenDto getTokenDto() {
        Auth auth = authRepository.findByPhone("01012345678");
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        return data.get(AuthType.SIGNUP);
    }

//    @Test
//    @DisplayName("휴대폰 인증 번호 전송 테스트")
//    void sendSms() throws Exception {
//        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
//            .to("01084071066")
//            .build();
//
//        mockMvc.perform(post("/api/v1/auth/send-sms")
//            .content(objectMapper.writeValueAsString(messageRequestDto))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//            .andDo(print()).andExpect(status().is2xxSuccessful())
//            .andDo(print()).andDo(document("{class-name}/{method-name}",
//            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
//            preprocessResponse(prettyPrint())));
//    }

    @Test
    @DisplayName("휴대폰 인증 번호 확인 테스트")
    void auth() throws Exception {
        Auth auth = authRepository.findByPhone("01012345678");
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();

        mockMvc.perform(post("/api/v1/auth")
            .content(objectMapper.writeValueAsString(authRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void reissue() throws Exception {
        Auth auth = authRepository.findByPhone("01012345678");
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
            .accessToken(data.get(AuthType.SIGNUP).getAccessToken())
            .refreshToken(data.get(AuthType.SIGNUP).getRefreshToken())
            .build();

        mockMvc.perform(post("/api/v1/auth/reissue")
            .content(objectMapper.writeValueAsString(tokenRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("닉네임 설정 테스트")
    void nickname() throws Exception {
        Auth auth = authRepository.findByPhone("01012345678");
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        String header = data.get(AuthType.SIGNUP).getAccessToken();
        NicknameRequestDto nicknameRequestDto = NicknameRequestDto.builder()
            .nickname("testNickname")
            .build();

        mockMvc.perform(post("/api/v1/auth/nickname")
            .header("Authorization", header)
            .content(objectMapper.writeValueAsString(nicknameRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Bearer Type의 AccessToken 값")
                )
            ));
    }

    @Test
    @DisplayName("동네 인증 테스트")
    void local() throws Exception {
        Auth auth = authRepository.findByPhone("01012345678");
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        Local local1 = localRepository.findById(1L).get();
        Local local2 = localRepository.findById(2L).get();

        String header = data.get(AuthType.SIGNUP).getAccessToken();
        LocalRequestDto localRequestDto = LocalRequestDto.builder()
            .local1(local1.getName())
            .local2(local2.getName())
            .build();

        mockMvc.perform(post("/api/v1/auth/local")
            .header("Authorization", header)
            .content(objectMapper.writeValueAsString(localRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Bearer Type의 AccessToken 값")
                )
            ));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void unsubscribeUser() throws Exception {
        Auth auth = authRepository.findByPhone("01012345678");
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        String header = data.get(AuthType.SIGNUP).getAccessToken();

        mockMvc.perform(patch("/api/v1/auth/unsubscribe")
            .header("Authorization", header)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Bearer Type의 AccessToken 값")
                )
            ));
    }
}
