package dingdong.dingdong.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.user.Auth;
import dingdong.dingdong.domain.user.AuthRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.Role;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
class ChatRoomControllerTest {

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
        String phone1 = "01011111111";
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

        User user1 = User.builder()
            .id(id1)
            .phone(phone1)
            .authority(Role.REGULAR)
            .build();

        Profile profile1 = Profile.builder()
            .id(id1)
            .user(user1)
            .good(0L)
            .bad(0L)
            .build();

        userRepository.save(user1);
        profileRepository.save(profile1);

        // user2 생성
        Long id2 = 2L;
        String phone2 = "01022222222";

        User user2 = User.builder()
            .id(id2)
            .phone(phone2)
            .authority(Role.REGULAR)
            .build();

        Profile profile2 = Profile.builder()
            .id(id2)
            .user(user2)
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
    void findChatRooms() {
    }

    @Test
    void findChatRoomByRoomId() {
    }

    @Test
    void findUsersByRoomId() {
    }

    @Test
    void enterChatRoom() {
    }

    @Test
    void quitChatRoom() {
    }

    @Test
    void findChatMessagesByRoomId() {
    }

    @Test
    void createChatPromise() {
    }

    @Test
    void updateChatPromise() {
    }

    @Test
    void findChatPromiseByPostId() {
    }

    @Test
    void createVotePromsie() {
    }
}