package leo.lija.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "leo.lija")
@EnableJpaRepositories({"leo.lija.system"})
@EntityScan({"leo.lija.system"})
@ConfigurationPropertiesScan({"leo.lija"})
public class CliApplication {

	public static void main(String[] args) {
		SpringApplication.run(CliApplication.class, args);
	}

}
