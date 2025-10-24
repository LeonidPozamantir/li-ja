package leo.lija.cli;

import leo.lija.app.command.GameFinishCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.function.Supplier;

@SpringBootApplication(scanBasePackages = "leo.lija")
@EnableJpaRepositories({"leo.lija.app.db"})
@EntityScan({"leo.lija.app.entities"})
@ConfigurationPropertiesScan({"leo.lija.app.config"})
@RequiredArgsConstructor
public class CliApplication implements CommandLineRunner {

	private final Info info;
	private final GameFinishCommand gameFinishCommand;

	public static void main(String[] args) {
		SpringApplication.run(CliApplication.class, args);
	}

	@Override
	public void run(String... args) {

		Command defaultCommand = () -> System.out.println("Usage: run command args");

		Command command = ((Supplier<Command>) () -> {
			if (args.length != 1) {
				return defaultCommand;
			}

			return switch (args[0]) {
				case "info" -> info;
				case "finish" -> (Command) () -> gameFinishCommand.apply();
                default -> defaultCommand;
            };

		}).get();

		command.apply();
	}
}
