package dingdong.dingdong.controller;

import static dingdong.dingdong.domain.chat.PromiseType.CONFIRMED;
import static dingdong.dingdong.domain.chat.PromiseType.END;
import static dingdong.dingdong.domain.chat.PromiseType.PROGRESS;
import static dingdong.dingdong.util.exception.ResultCode.POST_NOT_FOUND;
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
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.chat.ChatJoin;
import dingdong.dingdong.domain.chat.ChatJoinRepository;
import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.chat.ChatPromiseRepository;
import dingdong.dingdong.domain.chat.ChatPromiseVote;
import dingdong.dingdong.domain.chat.ChatPromiseVoteRepository;
import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.post.Category;
import dingdong.dingdong.domain.post.CategoryRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.post.PostRepository;
import dingdong.dingdong.domain.post.PostTag;
import dingdong.dingdong.domain.post.PostTagRepository;
import dingdong.dingdong.domain.post.Tag;
import dingdong.dingdong.domain.post.TagRepository;
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
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
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
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

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
    LocalRepository localRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostTagRepository postTagRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    ChatJoinRepository chatJoinRepository;

    @Autowired
    ChatPromiseRepository chatPromiseRepository;

    @Autowired
    ChatPromiseVoteRepository chatPromiseVoteRepository;

    @Value("${test.server.http.scheme}")
    String scheme;
    @Value("${test.server.http.host}")
    String host;
    @Value("${test.server.http.port}")
    int port;

    @BeforeEach
    void setUp(){
        Long id1 = 1L;
        String phone1 = "01012345678";
        String authNumber = "123456";
        String requestId = "testRequestId";
        LocalDateTime requestTime = LocalDateTime.now();
        Auth auth = Auth.builder()
            .id(id1)
            .phone(phone1)
            .authNumber(passwordEncoder.encode(authNumber))
            .attemptCount(0)
            .requestId(requestId)
            .requestTime(requestTime)
            .build();
        authRepository.save(auth);

        Local local1 = Local.builder()
            .id(1L)
            .city("city")
            .district("district")
            .dong("dong")
            .build();

        Local local2 = Local.builder()
            .id(2L)
            .city("city")
            .district("district")
            .dong("dong")
            .build();
        localRepository.save(local1);
        localRepository.save(local2);

        User user1 = User.builder()
            .id(id1)
            .phone(phone1)
            .authority(Role.REGULAR)
            .local1(local1)
            .local2(local2)
            .build();

        //profile 설정
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

        Long id2 = 2L;
        String phone2 = "02012345678";

        User user2 = User.builder()
            .id(id2)
            .phone(phone2)
            .authority(Role.REGULAR)
            .local1(local1)
            .local2(local2)
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

        String tagName = "test";
        Tag tag1 = Tag.builder()
            .id(1L)
            .name(tagName)
            .build();
        tagRepository.save(tag1);

        Category category1 = Category.builder()
            .id(1L)
            .name("야채")
            .build();

        Category category2 = Category.builder()
            .id(2L)
            .name("과일")
            .build();

        Post post1 = Post.builder()
            .id(1L)
            .title("title")
            .cost(1000)
            .people(10)
            .gatheredPeople(3)
            .bio("test_bio")
            .location("location")
            .imageUrl1("imageUrl1")
            .imageUrl2("imageUrl2")
            .imageUrl3("imageUrl3")
            .user(user1)
            .category(category1)
            .local(local1)
            .user(user1)
            .done(Boolean.FALSE)
            .build();
        postRepository.save(post1);

        Post post2 = Post.builder()
            .id(2L)
            .title("title")
            .cost(2000)
            .people(12)
            .gatheredPeople(2)
            .bio("bio")
            .location("location")
            .imageUrl1("imageUrl1")
            .imageUrl2("imageUrl2")
            .imageUrl3("imageUrl3")
            .user(user2)
            .local(local2)
            .category(category2)
            .done(Boolean.TRUE)
            .build();
        postRepository.save(post2);

        Post post3 = Post.builder()
            .id(3L)
            .title("title3")
            .cost(3000)
            .people(3)
            .gatheredPeople(3)
            .bio("bio3")
            .location("location3")
            .imageUrl1("imageUrl1")
            .imageUrl2("imageUrl2")
            .imageUrl3("imageUrl3")
            .user(user1)
            .local(local2)
            .category(category2)
            .done(Boolean.TRUE)
            .build();
        postRepository.save(post3);

        PostTag postTag = PostTag.builder()
            .id(1L)
            .post(post1)
            .tag(tag1)
            .build();
        postTagRepository.save(postTag);

        Post post = postRepository.findById(post1.getId())
            .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND));
        List<PostTag> postTags = new ArrayList<>();
        postTags.add(postTag);
        post.setPostTags(postTags);
        postRepository.save(post);

        ChatRoom chatRoom1 = ChatRoom.builder()
            .id(1L)
            .post(post1)
            .endDate(LocalDateTime.now())
            .lastChatTime(LocalDateTime.now())
            .build();
        chatRoomRepository.save(chatRoom1);

        ChatRoom chatRoom2 = ChatRoom.builder()
            .id(2L)
            .post(post2)
            .endDate(LocalDateTime.now())
            .lastChatTime(LocalDateTime.now())
            .build();
        chatRoomRepository.save(chatRoom2);

        ChatRoom chatRoom3 = ChatRoom.builder()
            .id(3L)
            .post(post3)
            .endDate(LocalDateTime.now())
            .lastChatTime(LocalDateTime.now())
            .build();
        chatRoomRepository.save(chatRoom3);

        ChatJoin chatJoin1 = ChatJoin.builder()
            .id(1L)
            .chatRoom(chatRoom1)
            .user(user1)
            .build();
        chatJoinRepository.save(chatJoin1);

        ChatJoin chatJoin2 = ChatJoin.builder()
            .id(2L)
            .chatRoom(chatRoom1)
            .user(user1)
            .build();
        chatJoinRepository.save(chatJoin2);

        ChatJoin chatJoin3 = ChatJoin.builder()
            .id(3L)
            .chatRoom(chatRoom3)
            .user(user2)
            .build();
        chatJoinRepository.save(chatJoin3);

        ChatPromise chatPromise1 = ChatPromise.builder()
                .id(1L)
                .chatRoom(chatRoom1)
                .promiseDate(LocalDate.now())
                .promiseTime(LocalTime.now().minusHours(5))
                .promiseLocal("test")
                .totalPeople(3)
                .votingPeople(3)
                .promiseEndTime(LocalDateTime.now())
                .type(CONFIRMED)
                .build();
        chatPromiseRepository.save(chatPromise1);

        ChatPromise chatPromise2 = ChatPromise.builder()
            .id(2L)
            .chatRoom(chatRoom2)
            .promiseDate(LocalDate.now())
            .promiseTime(LocalTime.now().minusHours(5))
            .promiseLocal("test")
            .totalPeople(3)
            .votingPeople(3)
            .promiseEndTime(LocalDateTime.now())
            .type(PROGRESS)
            .build();
        chatPromiseRepository.save(chatPromise2);

        ChatPromise chatPromise3 = ChatPromise.builder()
            .id(3L)
            .chatRoom(chatRoom3)
            .promiseDate(LocalDate.now())
            .promiseTime(LocalTime.now().minusHours(5))
            .promiseLocal("test")
            .totalPeople(3)
            .votingPeople(3)
            .promiseEndTime(LocalDateTime.now())
            .type(PROGRESS)
            .build();
        chatPromiseRepository.save(chatPromise3);

        ChatPromiseVote chatPromiseVote1 = ChatPromiseVote.builder()
            .id(1L)
            .chatRoom(chatRoom1)
            .user(user1)
            .build();
        chatPromiseVoteRepository.save(chatPromiseVote1);

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
    @DisplayName("나누기 생성")
    void createPost() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        MockMultipartFile postImages = new MockMultipartFile("postImages", "postImage.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/api/v1/post/{localId}", 1L)
            .file(postImages)
            .param("title","test")
            .param("people", "10")
            .param("cost", "1000")
            .param("bio", "test_bio")
            .param("location", "test_location")
            .param("categoryId", "1")
            .param("postTag","#test")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(HttpHeaders.AUTHORIZATION, token)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            requestParameters(
                parameterWithName("title").description("생성할 나누기 제목"),
                parameterWithName("people").description("생성할 나누기 모집 인원 수"),
                parameterWithName("cost").description("생성할 나누기 비용"),
                parameterWithName("bio").description("생성할 나누기 내용"),
                parameterWithName("location").description("생성할 나누기 장소"),
                parameterWithName("categoryId").description("생성할 나누기의 카테고리 고유 아이디 값"),
                parameterWithName("postTag").description("생성할 나누기의 태그")
            ),
            requestParts(
                partWithName("postImages").description("생성할 나누기의 이미지")
            ),
            pathParameters(
                parameterWithName("localId").description("설정하고자 하는 지역의 고유 아이디 값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                .description("생성된 나누기의 고유한 아이디 값")
            )

        ));
    }

    @Test
    @DisplayName("지역별 모든 나누기 불러오기(정렬방식: 최신순)")
    void findPostsSortByCreatedDate() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/v1/post/sort=desc&sortby=createdDate&local/{localId}",1L)
                .param("page", "1")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            pathParameters(
                parameterWithName("localId").description("조회하고자하는 로컬의 고유 아이디값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소"),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }

    @Test
    @DisplayName("지역별 모든 나누기 불러오기(정렬방식: 마감임박순)")
    void findPostsSortByEndDate() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/sort=desc&sortby=endDate&local/{localId}",1L)
            .param("page", "1")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            pathParameters(
                parameterWithName("localId").description("조회하고자하는 로컬의 고유 아이디값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소"),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }

    @Test
    @DisplayName("카테고리별 나누기 피드들 불러오기(정렬 방식: 최신순)")
    void findPostByCategoryIdSortByCreatedDate() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders
            .get("/api/v1/post/sort=desc&sortby=category&createdDate&local/{categoryId}/{localId}", 1L, 1L)
            .param("page", "1")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            pathParameters(
                parameterWithName("categoryId").description("조회하고자 하는 카테고리의 고유값"),
                parameterWithName("localId").description("조회하고자 하는 지역의 고유값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id").optional(),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목").optional(),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수").optional(),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수").optional(),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용").optional(),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값").optional(),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소").optional(),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글").optional(),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부").optional(),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1").optional(),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그").optional()
            )
        ));
    }

    @Test
    @DisplayName("카테고리별 나누기 피드들 불러오기(정렬 방식: 마감임박순)")
    void findPostByCategoryIdSortByEndDate() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders
            .get("/api/v1/post/sort=desc&sortby=category&endDate&local/{categoryId}/{localId}", 3L, 1L)
            .param("page", "1")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            pathParameters(
                parameterWithName("categoryId").description("조회하고자 하는 카테고리의 고유값"),
                parameterWithName("localId").description("조회하고자 하는 지역의 고유값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id").optional(),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목").optional(),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수").optional(),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수").optional(),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용").optional(),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값").optional(),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소").optional(),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글").optional(),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부").optional(),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1").optional(),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그").optional()
            )
        ));
    }

    @Test
    @DisplayName("나누기 상세 보기")
    void findPostById() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/{postId}", 10L)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            pathParameters(
                parameterWithName("postId").description("조회하고자 하는 나누기의 고유값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.category").type("String").description("나누기가 속한 카테고리"),
                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                    .description("나누기를 생성한 유저 Id"),
                fieldWithPath("data.nickname").type("String").description("나누기를 생성한 유저의 닉네임"),
                fieldWithPath("data.profileImageUrl").type("String")
                    .description("나누기를 생성한 유저의 프로필 이미지 Url"),
                fieldWithPath("data.title").type("String").description("나누기의 제목"),
                fieldWithPath("data.good").type(JsonFieldType.NUMBER)
                    .description("나누기 생성한 유저의 좋아요 평가 수"),
                fieldWithPath("data.bad").type(JsonFieldType.NUMBER)
                    .description("나누기 생성한 유저의 싫어요 평가 수"),
                fieldWithPath("data.cost").type(JsonFieldType.NUMBER).description("나누기의 비용"),
                fieldWithPath("data.bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.location").type("String").description("나누기의 장소"),
                fieldWithPath("data.createdDate").type("LocalDateTime").description("나누기의 생성날짜"),
                fieldWithPath("data.modifiedDate").type("LocalDateTime").description("나누기의 수정날짜"),
                fieldWithPath("data.people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수"),
                fieldWithPath("data.gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재 모집된 인원수"),
                fieldWithPath("data.done").type(JsonFieldType.BOOLEAN).description("나누기의 완료여부"),
                fieldWithPath("data.imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.imageUrl2").type("String").description("나누기의 이미지2"),
                fieldWithPath("data.imageUrl3").type("String").description("나누기의 이미지3"),
                fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("나누기의 태그 리스트")

            )
        ));
    }

    @Test
    @DisplayName("나누기 삭제")
    void deletePost() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/post/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
                .andDo(print()).andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
                ),
                pathParameters(
                        parameterWithName("postId").description("조회하고자 하는 나누기의 고유값")
                )
        ));
    }

    @Test
    @DisplayName("나누기 수정")
    void updatePost() throws Exception {
        TokenDto tokenDto = getTokenDto();
       String token = "Bearer " + tokenDto.getAccessToken();

       MockMultipartFile postImages = new MockMultipartFile("postImages", "postImage.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());

        mockMvc.perform(RestDocumentationRequestBuilders.fileUpload("/api/v1/post/edit/{postId}", 1L)
            .file(postImages)
            .param("title","test")
            .param("people", "10")
            .param("cost", "1000")
            .param("bio", "test_bio")
            .param("location", "test_location")
            .param("categoryId", "2")
            .param("postTag","#test")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(HttpHeaders.AUTHORIZATION, token)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            requestParameters(
                parameterWithName("title").description("생성할 나누기 제목"),
                parameterWithName("people").description("생성할 나누기 모집 인원 수"),
                parameterWithName("cost").description("생성할 나누기 비용"),
                parameterWithName("bio").description("생성할 나누기 내용"),
                parameterWithName("location").description("생성할 나누기 장소"),
                parameterWithName("categoryId").description("생성할 나누기의 카테고리 고유 아이디 값"),
                parameterWithName("postTag").description("생성할 나누기의 태그")
            ),
            requestParts(
                partWithName("postImages").description("생성할 나누기의 이미지")
            ),
            pathParameters(
                parameterWithName("postId").description("수정하고자 하는 나누기의 고유 아이디값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                    .description("생성된 나누기의 고유한 아이디 값")
            )
        ));
    }

    @Test
    @DisplayName("현재 유저가 올린 나누기 목록 보기(프로필 판매내역 보기 화면)")
    void findPostByUser() throws Exception{
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/user/sell")
            .param("page", "1")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.[].location").type("String").description("나누기의 장소"),
                fieldWithPath("data.[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }

    @Test
    @DisplayName("특정 유저가 생성한 나누기 피드들 불러오기")
    void findPostByUserId() throws Exception{
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/user/{userId}", 2L)
                .param("page", "1")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andDo(print())
                .andDo(print()).andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
                ),
                pathParameters(
                    parameterWithName("userId").description("조회하고자 하는 유저의 고유 아이디값")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.content[].id").type("Long").description("나누기의 Id").optional(),
                    fieldWithPath("data.content[].title").type("String").description("나누기의 제목").optional(),
                    fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                        .description("나누기의 모집인원수").optional(),
                    fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                        .description("나누기의 현재까지 모집된 인원수").optional(),
                    fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                        .description("나누기의 비용").optional(),
                    fieldWithPath("data.content[].title").type("String").description("나누기의 제목값").optional(),
                    fieldWithPath("data.content[].location").type("String").description("나누기의 장소").optional(),
                    fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글").optional(),
                    fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부").optional(),
                    fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                        .description("나누기의 생성날짜").optional(),
                    fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1").optional(),
                    fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그").optional()
                )
        ));
    }

    @Test
    @DisplayName("현재 유저가 참여한 나누기 목록 보기(프로필 구매내역 보기 화면)")
    void findPostByUserIdOnChatJoin() throws Exception{
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/user/buy")
            .param("page", "1")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.[].location").type("String").description("나누기의 장소"),
                fieldWithPath("data.[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜"),
                fieldWithPath("data.[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }

    @Test
    @DisplayName("나누기 거래 확정")
    void confirmed() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/post/confirmed/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
                .andDo(print()).andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
                ),
                pathParameters(
                        parameterWithName("postId").description("조회하고자 하는 나누기의 고유값")
                )
        ));
    }

    @Test
    @DisplayName("나누기 검색")
    void search() throws Exception {
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/search")
            .param("keyword", "나눠")
            .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소"),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜"),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }

    @Test
    @DisplayName("모든 나누기 불러오기(정렬방식: 최신순)(local 정보를 무시)")
    void findPostsSortByCreatedDateNotLocal() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/sort=desc&sortby=createdDate")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소"),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }

    @Test
    @DisplayName("모든 나누기 불러오기(정렬방식: 마감임박순)(local 정보를 무시)")
    void findPostsSortByEndDateNotLocal() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/sort=desc&sortby=endDate")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소"),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }

    @Test
    @DisplayName("카테고리별 나누기 피드들 불러오기(정렬 방식: 최신순)(local 정보를 무시)")
    void findPostByCategoryIdSortByCreatedDateNotLocal() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/sort=desc&sortby=category&createdDate/{categoryId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            pathParameters(
                parameterWithName("categoryId").description("찾고자하는 카테고리의 고유 아이디값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소").optional(),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }

    @Test
    @DisplayName("카테고리별 나누기 피드들 불러오기(정렬 방식: 마감임박순)(local 정보를 무시)")
    void findPostByCategoryIdSortByEndDateNotLocal() throws Exception {
        TokenDto tokenDto = getTokenDto();
        String token = "Bearer " + tokenDto.getAccessToken();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/sort=desc&sortby=category&endDate/{categoryId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print()).andExpect(status().isOk()).andDo(print())
            .andDo(print()).andDo(document("{class-name}/{method-name}",
            preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
            ),
            pathParameters(
                parameterWithName("categoryId").description("찾고자하는 카테고리의 고유 아이디값")
            ),
            relaxedResponseFields(
                fieldWithPath("data.content[].id").type("Long").description("나누기의 Id"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER)
                    .description("나누기의 모집인원수"),
                fieldWithPath("data.content[].gatheredPeople").type(JsonFieldType.NUMBER)
                    .description("나누기의 현재까지 모집된 인원수"),
                fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER)
                    .description("나누기의 비용"),
                fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                fieldWithPath("data.content[].location").type("String").description("나누기의 장소").optional(),
                fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                fieldWithPath("data.content[].done").type("boolean").description("나누기의 완료여부"),
                fieldWithPath("data.content[].createdDate").type("LocalDateTime")
                    .description("나누기의 생성날짜").optional(),
                fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1"),
                fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("나누기의 태그")
            )
        ));
    }
}