package dingdong.dingdong.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.config.TokenProvider;
import dingdong.dingdong.domain.user.Auth;
import dingdong.dingdong.domain.user.AuthRepository;
import dingdong.dingdong.domain.user.BlackListRepository;
import dingdong.dingdong.domain.user.Local;
import dingdong.dingdong.domain.user.LocalRepository;
import dingdong.dingdong.domain.user.Profile;
import dingdong.dingdong.domain.user.ProfileRepository;
import dingdong.dingdong.domain.user.RefreshToken;
import dingdong.dingdong.domain.user.RefreshTokenRepository;
import dingdong.dingdong.domain.user.Role;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserAccount;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.auth.ApplicationNaverSENS;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.AuthResponseDto;
import dingdong.dingdong.dto.auth.LocalRequestDto;
import dingdong.dingdong.dto.auth.LocalResponseDto;
import dingdong.dingdong.dto.auth.MessageRequestDto;
import dingdong.dingdong.dto.auth.MessageResponseDto;
import dingdong.dingdong.dto.auth.NicknameRequestDto;
import dingdong.dingdong.dto.auth.SendSmsMessage;
import dingdong.dingdong.dto.auth.SendSmsRequestDto;
import dingdong.dingdong.dto.auth.SendSmsResponseDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.dto.auth.TokenRequestDto;
import dingdong.dingdong.util.SecurityUtil;
import dingdong.dingdong.util.exception.AuthenticationException;
import dingdong.dingdong.util.exception.DuplicateException;
import dingdong.dingdong.util.exception.ForbiddenException;
import dingdong.dingdong.util.exception.JwtAuthException;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

    private final ApplicationNaverSENS applicationNaverSENS;

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final ProfileRepository profileRepository;
    private final BlackListRepository blackListRepository;
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
        Auth auth = authRepository.findByPhone(phone)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.AUTH_NOT_FOUND));
        User user = userRepository.findByPhone(phone);

        if (auth == null || user == null) {
            throw new UsernameNotFoundException(phone);
        }
        return new UserAccount(auth, user.getAuthority().name());
    }

    // 로그인
    @Transactional
    public TokenDto login(AuthRequestDto authRequestDto) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = authRequestDto.toAuthentication();

        // 2. 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
            .phone(authentication.getName())
            .value(tokenDto.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 5. 토큰 발급
        return tokenDto;
    }

    // 로그아웃
    @Transactional
    public void logout(User user) {
        //refresh token 삭제
        RefreshToken targetRefreshToken = refreshTokenRepository.findById(user.getPhone())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.USER_NOT_FOUND));
        refreshTokenRepository.delete(targetRefreshToken);
        //access token
    }

    // 회원 가입
    @Transactional
    public TokenDto signup(AuthRequestDto authRequestDto) {
        User user = User.builder()
            .phone(authRequestDto.getPhone())
            .authority(Role.REGULAR)
            .build();
        Profile profile = Profile.builder()
            .id(user.getId())
            .user(user)
            .build();
        userRepository.save(user);
        profileRepository.save(profile);

        return login(authRequestDto);
    }

    // 토큰 재발급
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new JwtAuthException(ResultCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider
            .getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findById(authentication.getName())
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

    // 블랙리스트 확인
    @Transactional(readOnly = true)
    public void checkBlackList(String phone) {
        if (blackListRepository.existsById(phone)) {
            throw new ForbiddenException(ResultCode.AUTH_FAIL_FORBIDDEN);
        }
    }

    // 탈퇴한 회원 확인
    @Transactional(readOnly = true)
    public void checkUnsub(String phone) {
        if (userRepository.existsByPhone(phone)) {
            if (userRepository.findByPhone(phone).getAuthority() == Role.UNSUB) {
                throw new ForbiddenException(ResultCode.AUTH_FAIL_UNSUB);
            }
        }
    }

    // 닉네임 중복 확인
    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if (profileRepository.existsByNickname(nickname)) {
            throw new DuplicateException(ResultCode.NICKNAME_DUPLICATION);
        }
    }

    // 닉네임 설정
    @Transactional
    public void setNickname(User user, NicknameRequestDto nicknameRequestDto) {
        if(user.getProfile().getNickname().equals(nicknameRequestDto.getNickname())) {
            return;
        }
        checkNickname(nicknameRequestDto.getNickname());
        Profile profile = profileRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.PROFILE_NOT_FOUND));
        profile.setNickname(nicknameRequestDto.getNickname());
        profileRepository.save(profile);
    }

    // 동네 목록 조회
    @Transactional(readOnly = true)
    public List<LocalResponseDto> getLocals(String city, String district) {
        List<Local> locals = localRepository.findByCityAndDistrict(city, district);
        return locals.stream().map(LocalResponseDto::from).collect(Collectors.toList());
    }

    // 동네 인증
    @Transactional
    public void setLocal(User user, LocalRequestDto localRequestDto) {
        Local local1 = localRepository.findById(localRequestDto.getLocal1())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.LOCAL_NOT_FOUND));
        Local local2 = localRepository.findById(localRequestDto.getLocal2())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.LOCAL_NOT_FOUND));
        if (local1 == null || local2 == null) {
            throw new ResourceNotFoundException(ResultCode.LOCAL_NOT_FOUND);
        }
        user.setLocal(local1, local2);
        userRepository.save(user);
    }

    // 휴대폰 인증 번호 확인
    @Transactional(noRollbackFor = {AuthenticationException.class, UsernameNotFoundException.class})
    public Map<AuthType, TokenDto> auth(AuthRequestDto authRequestDto) {
        checkBlackList(authRequestDto.getPhone());
        checkUnsub(authRequestDto.getPhone());

        Auth auth = authRepository.findByPhone(authRequestDto.getPhone())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.AUTH_NOT_FOUND));

        // 인증 쿨타임 시간일 경우
        if (auth.getCoolTime() != null && LocalDateTime.now().isBefore(auth.getCoolTime())) {
            throw new AuthenticationException(ResultCode.AUTH_COOL_TIME_LIMIT);
        }

        // 인증 시간이 지났을 경우
        Duration duration = Duration.between(auth.getRequestTime(), LocalDateTime.now());
        if (duration.getSeconds() > 300) {
            throw new AuthenticationException(ResultCode.AUTH_TIME_OUT);
        }

        // 인증 번호가 옳지 않을 경우
        // 인증 시도 횟수가 초과되었을 경우
        String encAuthNumber = auth.getAuthNumber();
        String authNumber = authRequestDto.getAuthNumber();
        if(!passwordEncoder.matches(authNumber, encAuthNumber)) {
            auth.plusAttemptCount();

            // 인증 시도 제한 횟수
            Integer limitAttemptCount = 5;
            if (auth.getAttemptCount() > limitAttemptCount) {
                // 인증 제한 쿨타임 시간(분)
                Long coolTimeMinute = 5L;
                auth.setCoolTime(coolTimeMinute);
                auth.reset();

                authRepository.save(auth);

                throw new AuthenticationException(ResultCode.AUTH_ATTEMPT_COUNT_LIMIT);
            }
            authRepository.save(auth);

            throw new AuthenticationException(ResultCode.AUTH_FAIL, AuthResponseDto.of(auth));
        }

        if (userRepository.existsByPhone(authRequestDto.getPhone())) {
            return Map.of(AuthType.LOGIN, login(authRequestDto));
        } else {
            return Map.of(AuthType.SIGNUP, signup(authRequestDto));
        }
    }

    // 테스트 전화번호 제외하도록 추가(테스트 기간 이후 삭제 예정)
    @Transactional(readOnly = true)
    public boolean checkTest(String phone) {
        if (phone.equals("01011111111") || phone.equals("01022222222") ||
            phone.equals("01033333333") || phone.equals("01044444444") || phone
            .equals("01055555555")) {
            return true;
        } else {
            return false;
        }
    }

    // 휴대폰 인증 번호 전송
    @Transactional
    public MessageResponseDto sendSms(MessageRequestDto messageRequestDto) {
        // 테스트 전화번호 제외하도록 추가(테스트 기간 이후 삭제 예정)
        if (checkTest(messageRequestDto.getTo())) {
            SendSmsResponseDto sendSmsResponseDto = SendSmsResponseDto.builder()
                .statusCode("202")
                .statusName("test status name")
                .requestId("test request id")
                .requestTime(LocalDateTime.now())
                .build();

            return MessageResponseDto.from(sendSmsResponseDto);
        }

        checkBlackList(messageRequestDto.getTo());
        checkUnsub(messageRequestDto.getTo());
        try {
            Long time = Timestamp.valueOf(LocalDateTime.now()).getTime();
            String code = makeRandom();
            String content = String.format("[띵-동] 인증번호 [%s] *타인에게 노출하지 마세요.", code);
            SendSmsMessage sendSmsMessage = SendSmsMessage.builder()
                .to(messageRequestDto.getTo())
                .content(content)
                .build();
            List<SendSmsMessage> messages = new ArrayList<>();

            // 보내는 사람에게 내용을 보냄
            messages.add(sendSmsMessage);

            // 전체 json에 대해 메시지를 만든다.
            SendSmsRequestDto sendSmsRequestDto = SendSmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(applicationNaverSENS.getSendFrom())
                .content("Default message")
                .messages(messages)
                .build();

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
            headers.set("x-ncp-apigw-signature-v2", sign);

            // 위에서 조립한 jsonBody와 헤더를 조립한다.
            HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

            // restTemplate로 post 요청을 보낸다. 성공하면 202 코드가 반환된다.
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            SendSmsResponseDto sendSmsResponseDto = restTemplate.postForObject(new URI(
                "https://sens.apigw.ntruss.com/sms/v2/services/" + applicationNaverSENS
                    .getServiceId()
                    + "/messages"), body, SendSmsResponseDto.class);

            if (sendSmsResponseDto.getStatusCode().equals("202")) {
                if (authRepository.existsByPhone(messageRequestDto.getTo())) {
                    Auth auth = authRepository.findByPhone(messageRequestDto.getTo()).orElseThrow(
                        () -> new ResourceNotFoundException(ResultCode.AUTH_NOT_FOUND));
                    auth.reauth(passwordEncoder.encode(code), sendSmsResponseDto.getRequestId(),
                        sendSmsResponseDto.getRequestTime());
                    authRepository.save(auth);
                } else {
                    Auth auth = Auth.builder()
                        .phone(messageRequestDto.getTo())
                        .authNumber(passwordEncoder.encode(code))
                        .requestId(sendSmsResponseDto.getRequestId())
                        .requestTime(sendSmsResponseDto.getRequestTime())
                        .build();
                    authRepository.save(auth);
                }
            }

            return MessageResponseDto.from(sendSmsResponseDto);
        } catch (JsonProcessingException | URISyntaxException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String makeSignature(Long time) {
        try {
            String space = " ";
            String newLine = "\n";
            String method = "POST";
            String url = "/sms/v2/services/" + applicationNaverSENS.getServiceId() + "/messages";
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

            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeBase64String(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String makeRandom() {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            StringBuilder code = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                // 0~9까지 난수 생성
                String num = Integer.toString(rand.nextInt(10));
                code.append(num);
            }

            return code.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 회원 탈퇴
    @Transactional
    public void unsubscribeUser(User user) {
        user.setUnsubscribe();
        user.getProfile().setUnsubscribe();
        userRepository.save(user);
        profileRepository.save(user.getProfile());
    }
}
