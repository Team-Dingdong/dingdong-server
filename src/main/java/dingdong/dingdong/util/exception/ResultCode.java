package dingdong.dingdong.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ResultCode {

    /* 200 OK */
    LOGIN_SUCCESS(OK, "로그인 성공"),
    CHECK_EMAIL_SUCCESS(OK, "사용가능한 이메일입니다"),
    CHECK_NICKNAME_SUCCESS(OK, "사용가능한 닉네임입니다"),

    PROFILE_READ_SUCCESS(OK, "프로필 조회 성공"),
    PROFILE_UPDATE_SUCCESS(OK, "프로필 수정 성공"),

    RATING_READ_SUCCESS(OK, "평가 조회 성공"),

    POST_READ_SUCCESS(OK, "포스트 조회 성공"),
    POST_DELETE_SUCCESS(OK, "포스트 삭제 성공"),
    POST_UPDATE_SUCCESS(OK, "포스트 수정 성공"),

    CHAT_ROOM_READ_ALL_SUCCESS(OK, "채팅방 전체 조회 성공"),
    CHAT_ROOM_READ_SUCCESS(OK, "채팅방 조회 성공"),

    /* 201 CREATED */
    SIGNUP_SUCCESS(CREATED, "회원 가입 성공"),
    SEND_SMS_SUCCESS(CREATED, "인증 번호 전송 성공"),

    NICKNAME_CREATE_SUCCESS(CREATED, "닉네임 설정 성공"),
    LOCAL_CREATE_SUCCESS(CREATED, "동네 설정 성공"),

    RATING_CREATE_SUCCESS(CREATED, "평가 생성 성공"),

    POST_CREATE_SUCCESS(CREATED, "포스트 생성 성공"),
    POST_LIKE_CREATE_SUCCESS(CREATED, "포스트 좋아요 생성 성공"),
    POST_COMMENT_CREATE_SUCCESS(CREATED, "포스트 댓글 생성 성공"),

    CHAT_ROOM_CREATE_SUCCESS(CREATED, "채팅방 생성 성공"),

    /* 400 BAD_REQUEST : 잘못된 요청 */
    AUTH_NOT_FOUND(BAD_REQUEST, "해당 사용자의 인증 정보를 찾을 수 없습니다"),
    AUTH_FAIL(BAD_REQUEST, "인증 번호가 옳지 않습니다"),
    AUTH_TIME_ERROR(BAD_REQUEST, "인증 시간을 초과하였습니"),

    INVALID_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
    CANNOT_FOLLOW_MYSELF(BAD_REQUEST, "자기 자신은 팔로우 할 수 없습니다"),

    VALID_ERROR(BAD_REQUEST, "유효성 검사 실패"),
    POST_CREATE_FAIL(BAD_REQUEST, "포스트 생성 실패"),
    POST_DELETE_FAIL(BAD_REQUEST, "포스트 삭제 실패"),
    POST_UPDATE_FAIL(BAD_REQUEST, "포스트 수정 실패"),


    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    FORBIDDEN_MEMBER(FORBIDDEN, "해당 권한이 없습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),
    POST_NOT_FOUND(NOT_FOUND, "해당 포스트를 찾을 수 없습니다"),
    CATEGORY_NOT_FOUND(NOT_FOUND, "해당 카테고리를 찾을 수 없습니다"),
    PROFILE_NOT_FOUND(NOT_FOUND, "해당 프로필을 찾을 수 없습니다"),
    RATING_NOT_FOUND(NOT_FOUND, "해당 유저에 대한 평가를 찾을 수 없습니다"),
    CHAT_ROOM_NOT_FOUND(NOT_FOUND, "해당 채팅방을 찾을 수 없습니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),
    NICKNAME_DUPLICATION(CONFLICT, "이미 사용 중인 닉네임입니다"),

    /* 500 CONFLICT */
    AUTH_ERROR(INTERNAL_SERVER_ERROR, "인증 오류 발생"),
    INTER_SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 오류 발생"),
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
