package dingdong.dingdong.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.config.TokenProvider;
import dingdong.dingdong.domain.user.*;
import dingdong.dingdong.dto.auth.*;
import dingdong.dingdong.util.SecurityUtil;
import dingdong.dingdong.util.exception.DuplicateException;
import dingdong.dingdong.util.exception.JwtAuthException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

    private final ApplicationNaverSENS applicationNaverSENS;

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final ProfileRepository profileRepository;
    private final LocalRepository localRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 로그인한 유저 정보 반환 to @CurrentUser
    public User getUserInfo() {
        return userRepository.findByPhone(SecurityUtil.getUserName());
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Auth auth = authRepository.findByPhone(phone);
        User user = userRepository.findByPhone(phone);
        if(auth == null || user == null) {
            throw new UsernameNotFoundException(phone);
        }

        return new UserAccount(auth, user.getAuthority());
    }

    // 로그인
    @Transactional
    public TokenDto login(AuthRequestDto authRequestDto) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = authRequestDto.toAuthentication();

        // 2. 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = new RefreshToken(authentication.getName(), tokenDto.getRefreshToken());

        refreshTokenRepository.save(refreshToken);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 5. 토큰 발급
        return tokenDto;
    }

    // 회원가입
    @Transactional
    public TokenDto signup(AuthRequestDto authRequestDto) {
        User user = new User(authRequestDto.getPhone());
        Profile profile = new Profile(user);
        userRepository.save(user);
        profileRepository.save(profile);

        return login(authRequestDto);
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new JwtAuthException(ResultCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByPhone(authentication.getName())
                .orElseThrow(() -> new JwtAuthException(ResultCode.REFRESH_TOKEN_NOT_FOUND));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new JwtAuthException(ResultCode.MISMATCH_REFRESH_TOKEN);
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }

    // 닉네임 중복 확인
    @Transactional
    public void checkNickname(String nickname) {
        if(profileRepository.existsByNickname(nickname)) {
            throw new DuplicateException(ResultCode.NICKNAME_DUPLICATION);
        }
    }

    // 닉네임 설정
    @Transactional
    public void setNickname(User user, NicknameRequestDto nicknameRequestDto) {
        checkNickname(nicknameRequestDto.getNickname());
        Profile profile = profileRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));
        profile.setNickname(nicknameRequestDto.getNickname());
        profileRepository.save(profile);
    }

    // 동네 인증
    @Transactional
    public void setLocal(User user, LocalRequestDto localRequestDto) {
        Local local1 = localRepository.findByName(localRequestDto.getLocal1());
        Local local2 = localRepository.findByName(localRequestDto.getLocal2());
        if(local1 == null || local2 == null) {
            throw new ResourceNotFoundException(ResultCode.LOCAL_NOT_FOUND);
        }
        user.setLocal(local1, local2);
        userRepository.save(user);
    }

    // 휴대폰 인증 번호 확인
    @Transactional
    public Map<AuthType, TokenDto> auth(AuthRequestDto authRequestDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime requestTime = authRepository.findRequestTimeByPhone(authRequestDto.getPhone()).orElseThrow(() -> new UsernameNotFoundException(authRequestDto.getPhone()));
        log.info("now -> {}", now);
        log.info("requestTime -> {}", requestTime);

        // Test를 위해 주석 처리
//        Duration duration = Duration.between(requestTime, now);
//        log.info("duration seconds -> {}", duration.getSeconds());
//        if(duration.getSeconds() > 300) {
//            throw new JwtAuthException(ResultCode.AUTH_TIME_ERROR);
//        }

        if(userRepository.existsByPhone(authRequestDto.getPhone())) {
            return Map.of(AuthType.LOGIN, login(authRequestDto));
        } else {
            return Map.of(AuthType.SIGNUP, signup(authRequestDto));
        }
    }

    // 휴대폰 인증 번호 전송
    @Transactional
    public MessageResponseDto sendSms(MessageRequestDto messageRequestDto) throws JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {
        Long time = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String random = makeRandom();
        String content = String.format("[띵-동] 인증번호 [%s] *타인에게 노출하지 마세요.", random);
        SendSmsMessage sendSmsMessage = new SendSmsMessage(messageRequestDto.getTo(), content);
        List<SendSmsMessage> messages = new ArrayList<>();

        // 보내는 사람에게 내용을 보냄
        messages.add(sendSmsMessage);

        // 전체 json에 대해 메시지를 만든다.
        SendSmsRequestDto sendSmsRequestDto = new SendSmsRequestDto("SMS", "COMM", "82", applicationNaverSENS.getSendFrom(), "Default message", messages);

        // 쌓아온 바디를 json 형태로 변환시켜준다.
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(sendSmsRequestDto);

        // 헤더에서 여러 설정값들을 잡아준다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", applicationNaverSENS.getAccessKey());

        // 제일 중요한 signature 서명하기.
        String sign = makeSignature(time);
        log.info("signature -> " + sign);
        headers.set("x-ncp-apigw-signature-v2", sign);

        // 위에서 조립한 jsonBody와 헤더를 조립한다.
        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);
        log.info(body.getBody());

        // restTemplate로 post 요청을 보낸다. 성공하면 202 코드가 반환된다.
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SendSmsResponseDto sendSmsResponseDto = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+applicationNaverSENS.getServiceId()+"/messages"), body, SendSmsResponseDto.class);
        log.info(sendSmsResponseDto.getStatusCode());

        if(sendSmsResponseDto.getStatusCode().equals("202")) {
            if(authRepository.existsByPhone(messageRequestDto.getTo())) {
                Auth auth = authRepository.findByPhone(messageRequestDto.getTo());
                auth.reauth(random, sendSmsResponseDto.getRequestId(), sendSmsResponseDto.getRequestTime(), false);
                authRepository.save(auth);
            } else {
                Auth auth = new Auth(messageRequestDto.getTo(), random, sendSmsResponseDto.getRequestId(), sendSmsResponseDto.getRequestTime());
                authRepository.save(auth);
            }
        }

        return new MessageResponseDto(sendSmsResponseDto.getRequestId(), sendSmsResponseDto.getRequestTime());

    }

    public String makeSignature(Long time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+applicationNaverSENS.getServiceId()+"/messages";
        String timestamp = time.toString();
        String accessKey = applicationNaverSENS.getAccessKey();
        String secretKey = applicationNaverSENS.getSecretKey();

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    public String makeRandom() {
        Random rand = new Random();
        String numStr = "";

        for(int i=0; i<6; i++) {

            // 0~9까지 난수 생성
            String num = Integer.toString(rand.nextInt(10));

            // 중복된 값이 있는지 검사한다
            if(!numStr.contains(num)) {
                // 중복된 값이 없으면 numStr에 append
                numStr += num;
            } else {
                // 생성된 난수가 중복되면 루틴을 다시 실행한다
                i -= 1;
            }
        }
        return numStr;
    }

    // 회원 탈퇴
    @Transactional
    public void unsubscribeUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ResultCode.USER_NOT_FOUND));
        user.setAuthority("ROLE_UNSUB_USER");
        userRepository.save(user);
    }
}
