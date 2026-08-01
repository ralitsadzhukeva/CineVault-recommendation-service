package bg.softuni.cinevaultrecommendationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinevaultRecommendationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinevaultRecommendationServiceApplication.class, args);
	}

}
