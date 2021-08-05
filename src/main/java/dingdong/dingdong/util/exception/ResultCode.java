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

    POST_READ_SUCCESS(OK, "포스트 조회 성공"),
    POST_DELETE_SUCCESS(OK, "포스트 삭제 성공"),

    /* 201 CREATED */
    SIGNUP_SUCCESS(CREATED, "회원가입 성공"),
    SENDSMS_SUCCESS(CREATED, "인증번호 전송 성공"),

    POST_CREATE_SUCCESS(CREATED, "포스트 생성 성공"),
    POST_LIKE_CREATE_SUCCESS(CREATED, "포스트 좋아요 생성 성공"),
    POST_COMMENT_CREATE_SUCCESS(CREATED, "포스트 댓글 생성 성공"),

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
    CANNOT_FOLLOW_MYSELF(BAD_REQUEST, "자기 자신은 팔로우 할 수 없습니다"),

    VALID_ERROR(BAD_REQUEST, "유효성 검사 실패"),
    POST_CREATE_FAIL(BAD_REQUEST, "포스트 생성 실패"),
    POST_DELETE_FAIL(BAD_REQUEST, "포스트 삭제 실패"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다"),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    FORBIDDEN_MEMBER(FORBIDDEN, "해당 권한이 없습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),
    POST_NOT_FOUND(NOT_FOUND, "해당 포스트를 찾을 수 없습니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),
    EMAIL_DUPLICATION(CONFLICT,"이미 사용 중인 이메일입니다"),
    NICKNAME_DUPLICATION(CONFLICT, "이미 사용 중인 닉네임입니다"),
    LIKE_DUPLICATION(CONFLICT, "이미 좋아요한 글입니다"),

    /* 500 CONFLICT */
    INTER_SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 오류 발생"),
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
