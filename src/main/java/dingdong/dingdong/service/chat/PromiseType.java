package dingdong.dingdong.service.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PromiseType implements BaseEnumCode<String> {

    END("E"),
    PROGRESS("P");

    private final String value;
}
