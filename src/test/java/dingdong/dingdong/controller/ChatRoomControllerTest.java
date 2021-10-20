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
import dingdong.dingdong.domain.chat.ChatJoin;
import dingdong.dingdong.domain.chat.ChatJoinRepository;
import dingdong.dingdong.domain.chat.ChatMessage;
import dingdong.dingdong.domain.chat.ChatMessageRepository;
import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.chat.MessageType;
import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.user.Auth;
import dingdong.dingdong.domain.user.AuthRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.Role;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.dto.chat.ChatPromiseRequestDto;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.service.chat.ChatSubscriber;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
class ChatRoomControllerTest {

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
    CategoryRepository categoryRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    ChatJoinRepository chatJoinRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ChatService chatService;

    @Autowired
    ChatSubscriber chatSubscriber;

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
        String requestId1 = "testRequestId1";
        LocalDateTime requestTime = LocalDateTime.now();
        Auth auth1 = Auth.builder()
            .id(id1)
            .phone(phone1)
            .authNumber(passwordEncoder.encode(authNumber))
            .requestId(requestId1)
            .requestTime(requestTime)
            .build();

        Long id2 = 2L;
        String phone2 = "02012345678";
        String requestId2 = "testRequestId2";
        Auth auth2 = Auth.builder()
            .id(id2)
            .phone(phone2)
            .authNumber(passwordEncoder.encode(authNumber))
            .requestId(requestId2)
            .requestTime(requestTime)
            .build();

        authRepository.save(auth1);
        authRepository.save(auth2);

        String nickname1 = "testNickname1";
        String profileImageUrl1 = "testProfileImageUrl1";
        Profile profile1 = Profile.builder()
            .id(id1)
            .nickname(nickname1)
            .profileImageUrl(profileImageUrl1)
            .good(0L)
            .bad(0L)
            .build();

        User user1 = User.builder()
            .id(id1)
            .phone(phone1)
            .authority(Role.REGULAR)
            .profile(profile1)
            .build();

        profileRepository.save(profile1);
        userRepository.save(user1);

        // user2 생성
        String nickname2 = "testNickname2";
        String profileImageUrl2 = "testProfileImageUrl2";
        Profile profile2 = Profile.builder()
            .id(id2)
            .nickname(nickname2)
            .profileImageUrl(profileImageUrl2)
            .good(0L)
            .bad(0L)
            .build();

        User user2 = User.builder()
            .id(id2)
            .phone(phone2)
            .authority(Role.REGULAR)
            .profile(profile2)
            .build();

        profileRepository.save(profile2);
        userRepository.save(user2);

        Category category = categoryRepository.findById(1L).get();
        Post post = Post.builder()
            .id(1L)
            .user(user1)
            .category(category)
            .location("test location")
            .people(2)
            .title("test title")
            .bio("test bio")
            .cost(1000)
            .imageUrl1("test image1")
            .imageUrl2("test image2")
            .imageUrl3("test image3")
            .gatheredPeople(2)
            .build();
        postRepository.save(post);

        ChatRoom chatRoom = ChatRoom.builder()
            .id(1L)
            .post(post)
            .build();
        chatRoomRepository.save(chatRoom);

        ChatJoin chatJoin1 = ChatJoin.builder()
            .id(1L)
            .chatRoom(chatRoom)
            .user(user1)
            .build();

        ChatJoin chatJoin2 = ChatJoin.builder()
            .id(2L)
            .chatRoom(chatRoom)
            .user(user2)
            .build();

        chatJoinRepository.save(chatJoin1);
        chatJoinRepository.save(chatJoin2);

        Post post2 = Post.builder()
            .id(2L)
            .user(user2)
            .category(category)
            .location("test location")
            .people(2)
            .title("test title")
            .bio("test bio")
            .cost(1000)
            .done(false)
            .imageUrl1("test image1")
            .imageUrl2("test image2")
            .imageUrl3("test image3")
            .gatheredPeople(1)
            .build();
        postRepository.save(post2);

        ChatRoom chatRoom2 = ChatRoom.builder()
            .id(2L)
            .post(post2)
            .build();
        chatRoomRepository.save(chatRoom2);

        ChatJoin chatJoin3 = ChatJoin.builder()
            .id(3L)
            .chatRoom(chatRoom2)
            .user(user2)
            .build();

        chatJoinRepository.save(chatJoin3);

        ChatMessage chatMessage = ChatMessage.builder()
            .id(1L)
            .chatRoom(chatRoom)
            .sender(user1)
            .type(MessageType.TALK)
            .message("test message")
            .sendTime(LocalDateTime.now())
            .build();

        chatMessageRepository.save(chatMessage);

        chatRoom.addMessages(chatMessage);
        chatRoomRepository.save(chatRoom);
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
    @DisplayName("채팅방 목록 조회")
    void findChatRooms() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/chat/room")
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
                        .description("채팅방의 고유한 아이디 값"),
                    fieldWithPath("data.[].title").type(JsonFieldType.STRING)
                        .description("채팅방이 생성된 나누기의 제목"),
                    fieldWithPath("data.[].lastChatMessage").type("String")
                        .description("채팅방의 마지막 메세지"),
                    fieldWithPath("data.[].lastChatTime").type("String")
                        .description("채팅방의 마지막 채팅 시간"),
                    fieldWithPath("data.[].imageUrl").type("String")
                        .description("채팅방이 생성된 나누기의 대표 이미지"),
                    fieldWithPath("data.[].userCount").type(JsonFieldType.NUMBER)
                        .description("채팅방에 참여한 인원 수"),
                    fieldWithPath("data.[].isOwner").type("Boolean").description("자신이 생성한 채팅방인지 확인")
                )
            ));
    }

    @Test
    @DisplayName("채팅방 정보 조회")
    void findChatRoomByRoomId() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/chat/room/{roomId}", 1L)
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
                pathParameters(
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                        .description("채팅방의 고유한 아이디 값"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING)
                        .description("채팅방이 생성된 나누기의 제목"),
                    fieldWithPath("data.lastChatMessage").type("String")
                        .description("채팅방의 마지막 메세지"),
                    fieldWithPath("data.lastChatTime").type("String")
                        .description("채팅방의 마지막 채팅 시간"),
                    fieldWithPath("data.imageUrl").type("String")
                        .description("채팅방이 생성된 나누기의 대표 이미지"),
                    fieldWithPath("data.userCount").type(JsonFieldType.NUMBER)
                        .description("채팅방에 참여한 인원 수"),
                    fieldWithPath("data.isOwner").type("Boolean").description("방장 여부")
                )
            ));
    }

    @Test
    @DisplayName("채팅방 사용자 목록 조회")
    void findUsersByRoomId() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/chat/user/{roomId}", 1L)
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
                pathParameters(
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER)
                        .description("사용자의 고유한 아이디 값"),
                    fieldWithPath("data.[].nickname").type("String").description("사용자의 닉네임"),
                    fieldWithPath("data.[].profileImageUrl").type("String")
                        .description("사용자의 프로필 이미지"),
                    fieldWithPath("data.[].isOwner").type("Boolean").description("방장 여부")
                )
            ));
    }

    @Test
    @DisplayName("채팅방 입장")
    void enterChatRoom() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/chat/room/{roomId}", 2L)
            .header(HttpHeaders.AUTHORIZATION, token)
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
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                )
            ));
    }

    @Test
    @DisplayName("채팅방 나가기")
    void quitChatRoom() throws Exception {
        String phone = "02012345678";
        String authNumber = "123456";
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(phone)
            .authNumber(authNumber)
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        TokenDto tokenDto = data.get(AuthType.LOGIN);
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/chat/room/{roomId}", 1L)
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
                pathParameters(
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                )
            ));
    }

    @Test
    @DisplayName("채팅 메세지 조회")
    void findChatMessagesByRoomId() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/chat/message/{roomId}", 1L)
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
                pathParameters(
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.[].userId").type(JsonFieldType.NUMBER).description("사용자의 고유한 아이디 값"),
                    fieldWithPath("data.[].nickname").type(JsonFieldType.STRING).description("사용자의 닉네임"),
                    fieldWithPath("data.[].profileImageUrl").type(JsonFieldType.STRING).description("사용자의 프로필 이미지"),
                    fieldWithPath("data.[].isOwner").type("Boolean").description("본인 메세지 여부"),
                    fieldWithPath("data.[].type").type(JsonFieldType.STRING).description("메세지 타입"),
                    fieldWithPath("data.[].message").type(JsonFieldType.STRING).description("메세지 내용"),
                    fieldWithPath("data.[].sendTime").type(JsonFieldType.STRING).description("메세지 전송 시간")
                )
            ));
    }

    @Test
    @DisplayName("채팅 약속 생성")
    void createChatPromise() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        ChatPromiseRequestDto chatPromiseRequestDto = ChatPromiseRequestDto.builder()
            .promiseDate(LocalDate.now())
            .promiseTime(LocalTime.now())
            .promiseLocal("test local")
            .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/chat/promise/{roomId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(objectMapper.writeValueAsString(chatPromiseRequestDto))
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
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                ),
                requestFields(
                    fieldWithPath("promiseDate").type(JsonFieldType.STRING).description("약속 날짜"),
                    fieldWithPath("promiseTime").type(JsonFieldType.STRING).description("약속 시간"),
                    fieldWithPath("promiseLocal").type(JsonFieldType.STRING).description("약속 장소")
                )
            ));
    }

    @Test
    @DisplayName("채팅 약속 수정")
    void updateChatPromise() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        User user = userRepository.findById(1L).get();
        ChatPromiseRequestDto chatPromiseRequestDto = ChatPromiseRequestDto.builder()
            .promiseDate(LocalDate.now())
            .promiseTime(LocalTime.now())
            .promiseLocal("test local")
            .build();
        chatService.createChatPromise(user, 1L, chatPromiseRequestDto);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/chat/promise/{roomId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, token)
            .content(objectMapper.writeValueAsString(chatPromiseRequestDto))
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
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                ),
                requestFields(
                    fieldWithPath("promiseDate").type(JsonFieldType.STRING).description("약속 날짜"),
                    fieldWithPath("promiseTime").type(JsonFieldType.STRING).description("약속 시간"),
                    fieldWithPath("promiseLocal").type(JsonFieldType.STRING).description("약속 장소")
                )
            ));
    }

    @Test
    @DisplayName("채팅 약속 조회")
    void findChatPromiseByPostId() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        User user = userRepository.findById(1L).get();
        ChatPromiseRequestDto chatPromiseRequestDto = ChatPromiseRequestDto.builder()
            .promiseDate(LocalDate.now())
            .promiseTime(LocalTime.now())
            .promiseLocal("test local")
            .build();
        chatService.createChatPromise(user, 1L, chatPromiseRequestDto);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/chat/promise/{roomId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, token)
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
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.promiseDate").type(JsonFieldType.STRING)
                        .description("약속 날짜"),
                    fieldWithPath("data.promiseTime").type(JsonFieldType.STRING)
                        .description("약속 시간"),
                    fieldWithPath("data.promiseLocal").type(JsonFieldType.STRING)
                        .description("약속 장소"),
                    fieldWithPath("data.totalPeople").type(JsonFieldType.NUMBER)
                        .description("전체 인원"),
                    fieldWithPath("data.votingPeople").type(JsonFieldType.NUMBER)
                        .description("투표 인원"),
                    fieldWithPath("data.promiseEndTime").type(JsonFieldType.STRING)
                        .description("약속 마감 시간"),
                    fieldWithPath("data.type").type(JsonFieldType.STRING).description("약속 상태")
                )
            ));
    }

    @Test
    @DisplayName("채팅 약속 투표")
    void createVotePromise() throws Exception {
        String phone = "02012345678";
        String authNumber = "123456";
        AuthRequestDto authRequestDto = AuthRequestDto.builder()
            .phone(phone)
            .authNumber(authNumber)
            .build();
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);

        TokenDto tokenDto = data.get(AuthType.LOGIN);
        String token = "Bearer " + tokenDto.getAccessToken();

        User user = userRepository.findById(1L).get();
        ChatPromiseRequestDto chatPromiseRequestDto = ChatPromiseRequestDto.builder()
            .promiseDate(LocalDate.now())
            .promiseTime(LocalTime.now())
            .promiseLocal("test local")
            .build();
        chatService.createChatPromise(user, 1L, chatPromiseRequestDto);

        mockMvc
            .perform(RestDocumentationRequestBuilders.post("/api/v1/chat/promise/vote/{roomId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token)
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
                    parameterWithName("roomId").description("채팅방의 고유한 아이디 값")
                )
            ));
    }
}