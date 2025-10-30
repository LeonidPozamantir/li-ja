package leo.lija.app.lobby;

import com.google.common.cache.Cache;
import leo.lija.app.memo.Builder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class History {

    private int privateVersion = 0;

    private final Cache<@NonNull Integer, @NonNull Map<String, Object>> messages;

    History(@Value("${lobby.history.timeout}") int timeout) {
        this.messages = Builder.expiry(timeout);
    }

    public int version() {
        return privateVersion;
    }

    public List<Map<String, Object>> since(Integer v) {
        return IntStream.rangeClosed(v + 1, version()).mapToObj(this::message).filter(Optional::isPresent).map(Optional::get).toList();
    }

    private Optional<Map<String, Object>> message(Integer v) {
        return Optional.ofNullable(messages.getIfPresent(v));
    }

    public Map<String, Object> add(Map<String, Object> msg) {
        privateVersion++;
        Map<String, Object> vmsg = new HashMap<>(msg);
        vmsg.put("v", privateVersion);
        messages.put(privateVersion, vmsg);
        return vmsg;
    }
}
