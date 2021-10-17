package dingdong.dingdong.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsBySenderAndReceiver(User sender, User receiver);

    long countByReceiver(User user);
}