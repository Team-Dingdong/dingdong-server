package dingdong.dingdong.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.user.Auth;
import dingdong.dingdong.domain.user.AuthRepository;
import dingdong.dingdong.domain.user.Local;
import dingdong.dingdong.domain.user.LocalRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
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
    ProfileRepository profileRepository;

    @Autowired
    UserRepository userRepository;

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
        Long id = 1L;
        String phone = "01012345678";
        String authNumber = "123456";
        String requestId = "testRequestId";
        LocalDateTime requestTime = LocalDateTime.now();
        Auth auth = Auth.builder()
            .id(id)
            .phone(phone)
            .authNumber(authNumber)
            .requestId(requestId)
            .requestTime(requestTime)
            .done(false)
            .build();

        authRepository.save(auth);

        String authority = "ROLE_USER";
        User user = User.builder()
            .id(id)
            .phone(phone)
            .authority(authority)
            .build();

        Profile profile = Profile.builder()
            .id(id)
            .user(user)
            .build();

        userRepository.save(user);
        profileRepository.save(profile);
    }

    TokenDto getTokenDto() {
        Auth auth = authRepository.findById(1L).get();
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        return data.get(AuthType.LOGIN);
    }

//    @Test
//    @DisplayName("휴대폰 인증 번호 전송 테스트")
//    void sendSms() throws Exception {
//        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
//            .to("01084071066")
//            .build();
//
//        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/send-sms")
//            .content(objectMapper.writeValueAsString(messageRequestDto))
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//            .andDo(print()).andExpect(status().is2xxSuccessful())
//            .andDo(print()).andDo(document("{class-name}/{method-name}",
//            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
//            preprocessResponse(prettyPrint()),
//            requestFields(
//                fieldWithPath("to").type(JsonFieldType.STRING).description("인증 번호를 전송할 휴대폰 번호")
//            ),
//            relaxedResponseFields(
//                fieldWithPath("data.requestId").type(JsonFieldType.STRING).description("휴대폰 인증 번호 전송 요청 아이디 값"),
//                fieldWithPath("data.requestTime").type(JsonFieldType.STRING).description("휴대폰 인증 번호 전송 요청 시간")
//            )
//        ));
//    }

    @Test
    @DisplayName("휴대폰 인증 번호 확인 테스트")
    void auth() throws Exception {
        Auth auth = authRepository.findById(1L).get();
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth")
            .content(objectMapper.writeValueAsString(authRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("phone").type(JsonFieldType.STRING).description("인증 번호를 받은 휴대폰 번호"),
                fieldWithPath("authNumber").type(JsonFieldType.STRING)
                    .description("휴대폰으로 전송 받은 인증 번호")
            ),
            relaxedResponseFields(
                fieldWithPath("data.grantType").type(JsonFieldType.STRING)
                    .description("JWT Token 타입"),
                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                    .description("JWT Access Token 값"),
                fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                    .description("JWT Access Token 유효기간"),
                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                    .description("JWT Refresh Token 값")
            )
        ));
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void reissue() throws Exception {
        TokenDto tokenDto = getTokenDto();
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
            .accessToken(tokenDto.getAccessToken())
            .refreshToken(tokenDto.getRefreshToken())
            .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/reissue")
            .content(objectMapper.writeValueAsString(tokenRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("accessToken").type(JsonFieldType.STRING)
                    .description("사용자의 현재 AccessToken 값"),
                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                    .description("사용자의 현재 RefreshToken 값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.grantType").type(JsonFieldType.STRING)
                    .description("JWT Token 타입"),
                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                    .description("JWT Access Token 값"),
                fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                    .description("JWT Access Token 유효기간"),
                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                    .description("JWT Refresh Token 값")
            )
        ));
    }

    @Test
    @DisplayName("닉네임 설정 테스트")
    void nickname() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();
        NicknameRequestDto nicknameRequestDto = NicknameRequestDto.builder()
            .nickname("testNickname")
            .build();

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/auth/nickname")
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(objectMapper.writeValueAsString(nicknameRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                requestFields(
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("설정할 닉네임 값")
                )
            ));
    }

    @Test
    @DisplayName("동네 목록 조회 테스트")
    void getLocals() throws Exception {
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/auth/local?city=서울특별시&district=성북구")
                .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                requestParameters(
                    parameterWithName("city").description("동네를 조회할 시, 도 이름"),
                    parameterWithName("district").description("동네를 조회할 구, 군 이름")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("동네 고유 아이디"),
                    fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("동네 이름")
                )
            ));
    }

    @Test
    @DisplayName("동네 인증 테스트")
    void local() throws Exception {
        TokenDto tokenDto = getTokenDto();
        Local local1 = localRepository.findById(1L).get();
        Local local2 = localRepository.findById(2L).get();
        LocalRequestDto localRequestDto = LocalRequestDto.builder()
            .local1(local1.getId())
            .local2(local2.getId())
            .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/local")
            .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
            .content(objectMapper.writeValueAsString(localRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                requestFields(
                    fieldWithPath("local1").type(JsonFieldType.NUMBER).description("인증할 동네 아이디1"),
                    fieldWithPath("local2").type(JsonFieldType.NUMBER).description("인증할 동네 아이2")
                )
            ));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void unsubscribeUser() throws Exception {
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(patch("/api/v1/auth/unsubscribe")
            .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                )
            ));
    }
}
