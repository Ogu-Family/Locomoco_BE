package org.prgms.locomocoserver.global.property;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class AuthProperties {
    private final List<String> authRequired = List.of(
            // User
            "GET:/api/v1/users/\\d+",

            // Chatㅇ
            "GET:/api/v1/chats/room/\\d+(/messages(\\?[^\\s]*)?)?",
            "GET:/api/v1/chats/rooms/\\d+",

            // Mogakko
            "PATCH:/api/v1/mogakko/map/\\d+"
    );
}
