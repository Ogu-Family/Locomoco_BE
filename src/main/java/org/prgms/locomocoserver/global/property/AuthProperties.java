package org.prgms.locomocoserver.global.property;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class AuthProperties {
    private final List<String> authRequired = List.of(
            "GET:/api/v1/chats/rooms/\\d+",
            "PATCH:/api/v1/mogakko/map/\\d+",
            "GET:/api/v1/users/\\d+",
            "GET:/api/v1/chats/(rooms|room)/\\\\d+(/messages)?\n"
    );
}
