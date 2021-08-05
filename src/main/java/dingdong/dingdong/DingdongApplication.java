package dingdong.dingdong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DingdongApplication {

	public static void main(String[] args) {
		SpringApplication.run(DingdongApplication.class, args);
	}

}
