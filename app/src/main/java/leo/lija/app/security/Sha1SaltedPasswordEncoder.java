package leo.lija.app.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Sha1SaltedPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        int open  = encodedPassword.lastIndexOf('{');
        int close = encodedPassword.lastIndexOf('}');

        if (open == -1 || close == -1 || close < open) {
            return false;
        }

        String hash = encodedPassword.substring(0, open);
        String salt = encodedPassword.substring(open + 1, close);

        String candidateHash = sha1("%s{%s}".formatted(rawPassword, salt));
        return candidateHash.equals(hash);
    }

    private String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 not available", e);
        }
    }
}
