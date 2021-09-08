package dingdong.dingdong.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PromiseType {

    PROGRESS,
    CONFIRMED,
    END
}
