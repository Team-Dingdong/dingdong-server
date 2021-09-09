package dingdong.dingdong.domain.user;

import dingdong.dingdong.service.rating.RatingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findBySenderAndReceiver(User sender, User receiver);
    Long countByReceiverAndType(User receiver, RatingType type);
}
