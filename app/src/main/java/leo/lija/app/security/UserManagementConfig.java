package leo.lija.app.security;

import leo.lija.app.db.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class UserManagementConfig {

    private final UserRepo repo;

    @Bean
    public UserDetailsService userDetailsService() {
        return (username) -> {
            return repo.byUsername(username).map(dbUser ->
                User.withUsername(username)
                    .password("%s{%s}".formatted(dbUser.getPassword(), dbUser.getSalt()))
                    .authorities(dbUser.getRoles().toArray(new String[0]))
                    .build()
            ).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Sha1SaltedPasswordEncoder();
    }
}
