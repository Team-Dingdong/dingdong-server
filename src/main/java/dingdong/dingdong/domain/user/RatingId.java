package dingdong.dingdong.domain.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class RatingId implements Serializable {

    private Long from;
    private Long to;
}
