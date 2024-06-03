package org.prgms.locomocoserver.mogakkos.application.findroad;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class OdsayFindRoadInfo implements FindRoadInfo {
    private static final String FIND_TRANSPORTATION_ROAD_BASE_URI = "https://api.odsay.com/v1/api/searchPubTransPathT";

    private final String odsayApiKey;

    @Override
    public URI getUri(double originLng, double originLat, double destLng, double destLat) {
        return UriComponentsBuilder.fromHttpUrl(FIND_TRANSPORTATION_ROAD_BASE_URI)
            .queryParam("lang", "0")
            .queryParam("SX", originLng)
            .queryParam("SY", originLat)
            .queryParam("EX", destLng)
            .queryParam("EY", destLat)
            .queryParam("apiKey", odsayApiKey)
            .build().toUri();
    }

    @Override
    public int parseAndGetDistance(JsonNode root) {
        return root.path("result").path("path").get(0).path("info").path("distance").asInt();
    }
}
