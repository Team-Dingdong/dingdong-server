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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.user.Auth;
import dingdong.dingdong.domain.user.AuthRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.dto.profile.ProfileUpdateRequestDto;
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
import org.springframework.web.multipart.MultipartFile;

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
            .build();

        authRepository.save(auth);

        String authority = "ROLE_USER";
        User user = User.builder()
            .id(id)
            .phone(phone)
            .authority(authority)
            .build();

        String nickname = "testNickname";
        String profileImageUrl = "testProfileImageUrl";
        Profile profile = Profile.builder()
            .id(id)
            .user(user)
            .nickname(nickname)
            .profileImageUrl(profileImageUrl)
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

        MultipartFile profileImage = new MockMultipartFile("file", "profileImage.jpeg",
            "image/jpeg", "<<jpeg data>>".getBytes());
        ProfileUpdateRequestDto profileUpdateRequestDto = ProfileUpdateRequestDto.builder()
            .profileImage(profileImage)
            .nickname("testNickname2")
            .build();
    }
}