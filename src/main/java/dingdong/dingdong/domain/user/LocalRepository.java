package dingdong.dingdong.domain.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalRepository extends JpaRepository<Local, Long> {

    List<Local> findByCityAndDistrict(String city, String district);
}
