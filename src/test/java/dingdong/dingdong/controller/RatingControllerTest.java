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
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.user.Auth;
import dingdong.dingdong.domain.user.AuthRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.RatingRepository;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.dto.rating.RatingRequestDto;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import dingdong.dingdong.service.rating.RatingType;
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
class RatingControllerTest {

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
    RatingRepository ratingRepository;

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
            .authNumber(authNumber)
            .requestId(requestId)
            .requestTime(requestTime)
            .build();

        authRepository.save(auth);

        String authority = "ROLE_USER";
        User user1 = User.builder()
            .id(id1)
            .phone(phone1)
            .authority(authority)
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
            .authority(authority)
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
        // user1의 token
        Auth auth = authRepository.findById(1L).get();
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(auth.getPhone())
            .authNumber(auth.getAuthNumber())
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        return data.get(AuthType.LOGIN);
    }

    @Test
    @DisplayName("본인 평가 조회")
    void getMyRating() throws Exception {
        TokenDto tokenDto = getTokenDto();

        String tokenType = "Bearer ";
        String token = tokenType + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/rating")
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
                    fieldWithPath("data.good").type(JsonFieldType.NUMBER).description("사용자 추천 수"),
                    fieldWithPath("data.bad").type(JsonFieldType.NUMBER).description("사용자 비추천 수"),
                    fieldWithPath("data.total").type(JsonFieldType.NUMBER)
                        .description("사용자의 총 평가 수")
                )
            ));
    }

    @Test
    @DisplayName("평가 조회")
    void getRating() throws Exception {
        TokenDto tokenDto = getTokenDto();

        String tokenType = "Bearer ";
        String token = tokenType + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/rating/{userId}", 2L)
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
                    fieldWithPath("data.good").type(JsonFieldType.NUMBER).description("사용자 추천 수"),
                    fieldWithPath("data.bad").type(JsonFieldType.NUMBER).description("사용자 비추천 수"),
                    fieldWithPath("data.total").type(JsonFieldType.NUMBER)
                        .description("사용자의 총 평가 수")
                )
            ));
    }

    @Test
    @DisplayName("평가 생성")
    void createRating() throws Exception {
        TokenDto tokenDto = getTokenDto();

        String tokenType = "Bearer ";
        String token = tokenType + tokenDto.getAccessToken();

        RatingRequestDto ratingRequestDto = RatingRequestDto.builder()
            .type(RatingType.GOOD)
            .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/rating/{userId}", 2L)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(objectMapper.writeValueAsString(ratingRequestDto))
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
                    parameterWithName("userId").description("평가하고자 하는 사용자의 고유 아이디 값")
                ),
                requestFields(
                    fieldWithPath("type").type(JsonFieldType.STRING).description("GOOD 또는 BAD")
                )
            ));
    }
}