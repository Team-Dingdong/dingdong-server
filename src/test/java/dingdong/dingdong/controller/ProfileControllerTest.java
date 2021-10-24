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
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.user.Auth;
import dingdong.dingdong.domain.user.AuthRepository;
import dingdong.dingdong.domain.user.LocalRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.Role;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.dto.profile.ReportRequestDto;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import dingdong.dingdong.service.profile.ProfileService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

    @Autowired
    ProfileService profileService;

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
        // user1 생성
        Long id1 = 1L;
        String phone1 = "01012345678";
        String authNumber = "123456";
        String requestId = "testRequestId";
        LocalDateTime requestTime = LocalDateTime.now();
        Auth auth = Auth.builder()
            .id(id1)
            .phone(phone1)
            .authNumber(passwordEncoder.encode(authNumber))
            .requestId(requestId)
            .requestTime(requestTime)
            .attemptCount(0)
            .build();

        authRepository.save(auth);

        User user1 = User.builder()
            .id(id1)
            .phone(phone1)
            .local1(localRepository.findById(1L).get())
            .local2(localRepository.findById(2L).get())
            .authority(Role.REGULAR)
            .build();

        String nickname1 = "testNickname1";
        String profileImageUrl1 = "testProfileImageUrl1";
        Profile profile1 = Profile.builder()
            .id(id1)
            .user(user1)
            .nickname(nickname1)
            .profileImageUrl(profileImageUrl1)
            .good(0L)
            .bad(0L)
            .build();

        userRepository.save(user1);
        profileRepository.save(profile1);

        // user2 생성
        Long id2 = 2L;
        String phone2 = "02012345678";

        User user2 = User.builder()
            .id(id2)
            .phone(phone2)
            .authority(Role.REGULAR)
            .build();

        String nickname2 = "testNickname2";
        String profileImageUrl2 = "testProfileImageUrl2";
        Profile profile2 = Profile.builder()
            .id(id2)
            .user(user2)
            .nickname(nickname2)
            .profileImageUrl(profileImageUrl2)
            .good(0L)
            .bad(0L)
            .build();

        userRepository.save(user2);
        profileRepository.save(profile2);
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

    @Test
    @DisplayName("본인 프로필 조회 테스트")
    void getMyProfile() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/profile")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                        .description("사용자의 고유한 아이디 값"),
                    fieldWithPath("data.nickname").type("String").description("사용자의 닉네임"),
                    fieldWithPath("data.profileImageUrl").type("String")
                        .description("사용자의 프로필 이미지 URL")
                )
            ));
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    void getProfile() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/profile/{userId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                pathParameters(
                    parameterWithName("userId").description("조회하고자 하는 사용자의 고유 아이디 값")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                        .description("사용자의 고유한 아이디 값"),
                    fieldWithPath("data.nickname").type("String").description("사용자의 닉네임"),
                    fieldWithPath("data.profileImageUrl").type("String")
                        .description("사용자의 프로필 이미지 URL")
                )
            ));
    }

    @Test
    @DisplayName("프로필 수정 테스트")
    void updateProfile() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "profileImage.jpeg",
            "image/jpeg", "<<jpeg data>>".getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/api/v1/profile")
            .file(profileImage)
            .param("nickname", "testNickname3")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(HttpHeaders.AUTHORIZATION, token)
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
                    parameterWithName("nickname").description("변경할 프로필 닉네임")
                ),
                requestParts(
                    partWithName("profileImage").description("변경할 프로필 이미지")
                )
            ));
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    void getMyLocals() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/profile/local")
            .header(HttpHeaders.AUTHORIZATION, token)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION)
                        .description("Bearer Type의 AccessToken 값")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.[].id").type(JsonFieldType.NUMBER)
                        .description("동네의 고유한 아이디 값"),
                    fieldWithPath("data.[].name").type("String").description("동네 이름")
                )
            ));
    }

    @Test
    @DisplayName("프로필 신고 테스트")
    void createReport() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        ReportRequestDto reportRequestDto = ReportRequestDto.builder()
            .reason("test reason")
            .build();

        System.out.println(userRepository.findById(2L));
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/profile/report/{userId}", 2L)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(objectMapper.writeValueAsString(reportRequestDto))
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
                pathParameters(
                    parameterWithName("userId").description("신하고자 하는 사용자의 고유 아이디 값")
                ),
                requestFields(
                    fieldWithPath("reason").type(JsonFieldType.STRING).description("신고 사유")
                )
            ));
    }
}