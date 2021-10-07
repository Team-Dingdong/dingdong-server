package dingdong.dingdong.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.chat.*;
import dingdong.dingdong.domain.post.*;
import dingdong.dingdong.domain.user.*;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.dto.post.PostRequestDto;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import dingdong.dingdong.service.post.PostService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dingdong.dingdong.domain.chat.MessageType.TALK;
import static dingdong.dingdong.domain.chat.PromiseType.END;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

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

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Value("${test.server.http.scheme}")
    String scheme;
    @Value("${test.server.http.host}")
    String host;
    @Value("${test.server.http.port}")
    int port;

    @BeforeEach
    public void setUp(){
        // user 설정
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
                .build();

        authRepository.save(auth);

        //profile 설정
        Profile profile = Profile.builder()
                .id(1L)
                .good(0L)
                .bad(0L)
                .build();

        String authority = "ROLE_USER";
        User user = User.builder()
                .id(id)
                .phone(phone)
                .profile(profile)
                .authority(authority)
                .build();

        profileRepository.save(profile);
        userRepository.save(user);

        String categoryName = "test";
        Category category = Category.builder()
                .id(1L)
                .name(categoryName)
                .build();
        categoryRepository.save(category);

        String tagName = "test";
        Tag tag = Tag.builder()
                .id(1L)
                .name(tagName)
                .build();
        tagRepository.save(tag);

        String title = "test";
        int people = 10;
        int cost = 1000;
        int gatheredPeople = 1;
        String bio = "test";
        String local = "test";
        String imageUrl1 = "test_url1";
        String imageUrl2 = "test_url2";
        String imageUrl3 = "test_url3";

        Post post = Post.builder()
                .id(1L)
                .title(title)
                .cost(cost)
                .people(people)
                .gatheredPeople(gatheredPeople)
                .bio(bio)
                .local(local)
                .imageUrl1(imageUrl1)
                .imageUrl2(imageUrl2)
                .imageUrl3(imageUrl3)
                .user(user)
                .category(category)
                .done(false)
                .build();
        postRepository.save(post);

        PostTag postTag = PostTag.builder()
                .post(post)
                .tag(tag)
                .build();
        postTagRepository.save(postTag);

        ChatRoom chatRoom = ChatRoom.builder()
                .id(1L)
                .post(post)
                .endDate(LocalDateTime.now())
                .lastChatTime(LocalDateTime.now())
                .build();
        chatRoomRepository.save(chatRoom);

        ChatJoin chatJoin = ChatJoin.builder()
                .id(1L)
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatJoinRepository.save(chatJoin);

        ChatPromise chatPromise = ChatPromise.builder()
                .id(1L)
                .chatRoom(chatRoom)
                .promiseDate(LocalDate.now())
                .promiseTime(LocalTime.now())
                .promiseEndTime(LocalDateTime.now().plusHours(3))
                .promiseLocal("test")
                .type(END)
                .build();
        chatPromiseRepository.save(chatPromise);
        
        ChatPromiseVote chatPromiseVote = ChatPromiseVote.builder()
                .id(1L)
                .chatRoom(chatRoom)
                .user(user)
                .build();
        chatPromiseVoteRepository.save(chatPromiseVote);

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

    @Test
    @DisplayName("나누기 생성")
    public void createPost() throws Exception{
        TokenDto tokenDto = getTokenDto();

        Post post = postRepository.findById(1L).get();
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .people(post.getPeople())
                .cost(post.getCost())
                .bio(post.getBio())
                .local(post.getLocal())
                .categoryId(post.getCategory().getId())
                .postTag("#test")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/post")
                .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
                .content(objectMapper.writeValueAsString(postRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
                .andDo(print()).andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
                )
        ));
    }

    @Test
    @DisplayName("홈화면, 모든 나누기 불러오기(정렬방식: 최신순)")
    public void findPostsSortByCreatedDate() throws Exception {
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/sorted_by=desc(createdDate)")
                .param("page", "1")
                .param("size", "5")
                .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
                .andDo(print()).andDo(document("{class-name}/{method-name}",
                preprocessRequest(modifyUris().scheme(scheme).host(host).port(port), prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer Type의 AccessToken 값")
                ),
                relaxedResponseFields(
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                        fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수"),
                        fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER).description("나누기의 비용"),
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                        fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                        fieldWithPath("data.content[].local").type("String").description("나누기의 장소"),
                        fieldWithPath("data.content[].createdDate").type("LocalDateTime").description("나누기의 생성날짜"),
                        fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1")
                )
        ));
    }

    @Test
    @DisplayName("홈화면, 모든 나누기 불러오기(정렬방식: 마감임박순)")
    public void findPostsSortByEndDate() throws Exception{
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/sorted_by=desc(endDate)")
                .param("page", "1")
                .param("size", "5")
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
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                        fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수"),
                        fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER).description("나누기의 비용"),
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                        fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                        fieldWithPath("data.content[].local").type("String").description("나누기의 장소"),
                        fieldWithPath("data.content[].createdDate").type("LocalDateTime").description("나누기의 생성날짜"),
                        fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1")
                )
        ));
    }

    @Test
    @DisplayName("카테고리별 나누기 피드들 불러오기(카테고리 화면)(정렬 방식: 최신순)")
    public void findPostByCategoryIdSortByCreatedDate() throws Exception{
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/category/sorted_by=desc(createdDate)/{categoryId}", 1L)
                .param("page", "1")
                .param("size", "5")
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
                pathParameters(
                        parameterWithName("categoryId").description("조회하고자 하는 카테고리의 고유값")
                ),
                relaxedResponseFields(
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                        fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수"),
                        fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER).description("나누기의 비용"),
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                        fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                        fieldWithPath("data.content[].local").type("String").description("나누기의 장소"),
                        fieldWithPath("data.content[].createdDate").type("LocalDateTime").description("나누기의 생성날짜"),
                        fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1")
                )
        ));
    }

    @Test
    @DisplayName("카테고리별 나누기 피드들 불러오기(카테고리 화면)(정렬 방식: 마감임박순)")
    public void findPostByCategoryIdSortByEndDate() throws Exception{
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/category/sorted_by=desc(endDate)/{categoryId}", 1L)
                .param("page", "1")
                .param("size", "5")
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
                pathParameters(
                        parameterWithName("categoryId").description("조회하고자 하는 카테고리의 고유값")
                ),
                relaxedResponseFields(
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                        fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수"),
                        fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER).description("나누기의 비용"),
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                        fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                        fieldWithPath("data.content[].local").type("String").description("나누기의 장소"),
                        fieldWithPath("data.content[].createdDate").type("LocalDateTime").description("나누기의 생성날짜"),
                        fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1")
                )
        ));
    }


    @Test
    @DisplayName("나누기 상세 보기")
    public void findPostById() throws Exception{
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
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
                        parameterWithName("id").description("조회하고자 하는 나누기의 고유값")
                ),
                relaxedResponseFields(
                        fieldWithPath("data.category").type("String").description("나누기가 속한 카테고리"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("나누기를 생성한 유저 Id"),
                        fieldWithPath("data.nickname").type("String").description("나누기를 생성한 유저의 닉네임"),
                        fieldWithPath("data.profileImageUrl").type("String").description("나누기를 생성한 유저의 프로필 이미지 Url"),
                        fieldWithPath("data.title").type("String").description("나누기의 제목"),
                        fieldWithPath("data.good").type(JsonFieldType.NUMBER).description("나누기 생성한 유저의 좋아요 평가 수"),
                        fieldWithPath("data.bad").type(JsonFieldType.NUMBER).description("나누기 생성한 유저의 싫어요 평가 수"),
                        fieldWithPath("data.cost").type(JsonFieldType.NUMBER).description("나누기의 비용"),
                        fieldWithPath("data.bio").type("String").description("나누기의 설명글"),
                        fieldWithPath("data.local").type("String").description("나누기의 장소"),
                        fieldWithPath("data.createdDate").type("LocalDateTime").description("나누기의 생성날짜"),
                        fieldWithPath("data.modifiedDate").type("LocalDateTime").description("나누기의 수정날짜"),
                        fieldWithPath("data.people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수"),
                        fieldWithPath("data.gatheredPeople").type(JsonFieldType.NUMBER).description("나누기의 현재 모집된 인원수"),
                        fieldWithPath("data.imageUrl1").type("String").description("나누기의 이미지1"),
                        fieldWithPath("data.imageUrl2").type("String").description("나누기의 이미지2"),
                        fieldWithPath("data.imageUrl3").type("String").description("나누기의 이미지3")
                )
        ));
    }

    @Test
    @DisplayName("나누기 삭제")
    public void deletePost() throws Exception {
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/post/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
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
    public void updatePost() throws Exception {
        TokenDto tokenDto = getTokenDto();

        Post post = postRepository.findById(1L).get();
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .title(post.getTitle())
                .people(post.getPeople())
                .cost(post.getCost())
                .bio(post.getBio())
                .local(post.getLocal())
                .categoryId(post.getCategory().getId())
                .postTag("#test")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/post/{id}", 1L)
                .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
                .content(objectMapper.writeValueAsString(postRequestDto))
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
                        parameterWithName("id").description("조회하고자 하는 나누기의 고유값")
                )
        ));
    }

    @Test
    @DisplayName("현재 유저가 올린 나누기 목록 보기(프로필 판매내역 보기 화면)")
    public void findPostByUserId() throws Exception{
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/user/sell")
                .param("page", "1")
                .param("size", "5")
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
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목"),
                        fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수"),
                        fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER).description("나누기의 비용"),
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목값"),
                        fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글"),
                        fieldWithPath("data.content[].local").type("String").description("나누기의 장소"),
                        fieldWithPath("data.content[].createdDate").type("LocalDateTime").description("나누기의 생성날짜"),
                        fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1")
                )
        ));
    }

    @Test
    @DisplayName("현재 유저가 올린 나누기 목록 보기(프로필 구매내역 보기 화면)")
    public void findPostByUserIdOnChatJoin() throws Exception{
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/user/buy")
                .param("page", "1")
                .param("size", "5")
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
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목").optional(),
                        fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수").optional(),
                        fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER).description("나누기의 비용").optional(),
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목값").optional(),
                        fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글").optional(),
                        fieldWithPath("data.content[].local").type("String").description("나누기의 장소").optional(),
                        fieldWithPath("data.content[].createdDate").type("LocalDateTime").description("나누기의 생성날짜").optional(),
                        fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1").optional()
                )
        ));
    }

    @Test
    @DisplayName("나누기 거래 확정")
    public void confirmed() throws Exception {
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/post/confirmed/{:postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, tokenDto.getAccessToken())
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
    public void search() throws Exception {
        TokenDto tokenDto = getTokenDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/search")
                .param("keyword", "title")
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
                        fieldWithPath("data.content[].title").type("String").description("나누기의 제목").optional(),
                        fieldWithPath("data.content[].people").type(JsonFieldType.NUMBER).description("나누기의 모집인원수").optional(),
                        fieldWithPath("data.content[].cost").type(JsonFieldType.NUMBER).description("나누기의 비용").optional(),
                        fieldWithPath("data.content[].bio").type("String").description("나누기의 설명글").optional(),
                        fieldWithPath("data.content[].local").type("String").description("나누기의 장소").optional(),
                        fieldWithPath("data.content[].createdDate").type("LocalDateTime").description("나누기의 생성날짜").optional(),
                        fieldWithPath("data.content[].imageUrl1").type("String").description("나누기의 이미지1").optional()
                )
        ));
    }

}

