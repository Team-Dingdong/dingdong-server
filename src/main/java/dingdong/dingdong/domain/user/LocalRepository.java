package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LocalRepository extends JpaRepository<Local, Long> {

    Local findByName(String name);
}
