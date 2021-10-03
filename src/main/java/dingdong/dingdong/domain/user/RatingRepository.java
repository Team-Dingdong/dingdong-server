package dingdong.dingdong.domain.user;

import dingdong.dingdong.service.rating.RatingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findBySenderAndReceiver(User sender, User receiver);
    Long countByReceiverAndType(User receiver, RatingType type);
}
