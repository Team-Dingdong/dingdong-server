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
import dingdong.dingdong.domain.user.Role;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    PasswordEncoder passwordEncoder;

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
            .authNumber(passwordEncoder.encode(authNumber))
            .requestId(requestId)
            .requestTime(requestTime)
            .attemptCount(0)
            .build();

        authRepository.save(auth);

        Profile profile = Profile.builder()
            .id(id)
            .nickname("testNickname1")
            .build();

        User user = User.builder()
            .id(id)
            .phone(phone)
            .authority(Role.REGULAR)
            .profile(profile)
            .build();

        profileRepository.save(profile);
        userRepository.save(user);
    }

    TokenDto getTokenDto() {
        String phone = "01012345678";
        String authNumber = "123456";
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(phone)
            .authNumber(authNumber)
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        return data.get(AuthType.LOGIN);
    }

//    @Test
//    @DisplayName("????????? ?????? ?????? ?????? ?????????")
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
//                fieldWithPath("to").type(JsonFieldType.STRING).description("?????? ????????? ????????? ????????? ??????")
//            ),
//            relaxedResponseFields(
//                fieldWithPath("data.requestId").type(JsonFieldType.STRING).description("????????? ?????? ?????? ?????? ?????? ????????? ???"),
//                fieldWithPath("data.requestTime").type(JsonFieldType.STRING).description("????????? ?????? ?????? ?????? ?????? ??????")
//            )
//        ));
//    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????????")
    void auth() throws Exception {
        String phone = "01012345678";
        String authNumber = "123456";
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(phone)
            .authNumber(authNumber)
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
                fieldWithPath("phone").type(JsonFieldType.STRING).description("?????? ????????? ?????? ????????? ??????"),
                fieldWithPath("authNumber").type(JsonFieldType.STRING)
                    .description("??????????????? ?????? ?????? ?????? ??????")
            ),
            relaxedResponseFields(
                fieldWithPath("data.grantType").type(JsonFieldType.STRING)
                    .description("JWT Token ??????"),
                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                    .description("JWT Access Token ???"),
                fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                    .description("JWT Access Token ????????????"),
                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                    .description("JWT Refresh Token ???")
            )
        ));
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????????-??????")
    void authFail() throws Exception {
        String phone = "01012345678";
        String authNumber = "111111";
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(phone)
            .authNumber(authNumber)
            .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth")
            .content(objectMapper.writeValueAsString(authRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is4xxClientError())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("phone").type(JsonFieldType.STRING).description("?????? ????????? ?????? ????????? ??????"),
                fieldWithPath("authNumber").type(JsonFieldType.STRING)
                    .description("??????????????? ?????? ?????? ?????? ??????")
            ),
            relaxedResponseFields(
                fieldWithPath("data.attemptCount").type(JsonFieldType.NUMBER)
                    .description("?????? ?????? ??????")
            )
        ));
    }

    @Test
    @DisplayName("???????????? ?????????")
    void logout() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/auth/logout")
            .header(HttpHeaders.AUTHORIZATION, token)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type??? AccessToken ???")
                )
            ));
    }

    @Test
    @DisplayName("?????? ????????? ?????????")
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
                    .description("???????????? ?????? AccessToken ???"),
                fieldWithPath("refreshToken").type(JsonFieldType.STRING)
                    .description("???????????? ?????? RefreshToken ???")
            ),
            relaxedResponseFields(
                fieldWithPath("data.grantType").type(JsonFieldType.STRING)
                    .description("JWT Token ??????"),
                fieldWithPath("data.accessToken").type(JsonFieldType.STRING)
                    .description("JWT Access Token ???"),
                fieldWithPath("data.accessTokenExpiresIn").type(JsonFieldType.NUMBER)
                    .description("JWT Access Token ????????????"),
                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING)
                    .description("JWT Refresh Token ???")
            )
        ));
    }

    @Test
    @DisplayName("????????? ?????? ?????????")
    void nickname() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();
        NicknameRequestDto nicknameRequestDto = NicknameRequestDto.builder()
            .nickname("testNickname2")
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
                        .description("Bearer Type??? AccessToken ???")
                ),
                requestFields(
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("????????? ????????? ???")
                )
            ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    void getLocals() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/auth/local?city=???????????????&district=?????????")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type??? AccessToken ???")
                ),
                requestParameters(
                    parameterWithName("city").description("????????? ????????? ???, ??? ??????"),
                    parameterWithName("district").description("????????? ????????? ???, ??? ??????")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.[].id").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????"),
                    fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("?????? ??????")
                )
            ));
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    void local() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();
        Local local1 = localRepository.findById(1L).get();
        Local local2 = localRepository.findById(2L).get();
        LocalRequestDto localRequestDto = LocalRequestDto.builder()
            .local1(local1.getId())
            .local2(local2.getId())
            .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth/local")
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(objectMapper.writeValueAsString(localRequestDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type??? AccessToken ???")
                ),
                requestFields(
                    fieldWithPath("local1").type(JsonFieldType.NUMBER).description("????????? ?????? ?????????1"),
                    fieldWithPath("local2").type(JsonFieldType.NUMBER).description("????????? ?????? ??????2")
                )
            ));
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    void unsubscribeUser() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(patch("/api/v1/auth/unsubscribe")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type??? AccessToken ???")
                )
            ));
    }
}
