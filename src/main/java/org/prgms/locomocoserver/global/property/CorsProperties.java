package org.prgms.locomocoserver.global.property;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

@Getter
@Component
public class CorsProperties {
    private final Set<String> allowedOrigins = new HashSet<>(Arrays.asList(
            "http://localhost:3000",
            "https://locomoco.kro.kr",
            "https://locomoco.shop",
            "http://localhost:8090"
    ));
}
