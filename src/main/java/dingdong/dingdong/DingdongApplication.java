package dingdong.dingdong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class DingdongApplication {

	@PostConstruct
	void started(){ TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul")); }

	public static void main(String[] args) {
		SpringApplication.run(DingdongApplication.class, args);
	}

}
