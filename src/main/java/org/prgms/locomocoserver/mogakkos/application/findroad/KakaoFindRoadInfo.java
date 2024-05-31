package org.prgms.locomocoserver.mogakkos.application.findroad;


import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class KakaoFindRoadInfo implements FindRoadInfo {
    private static final String FIND_CAR_ROAD_BASE_URI = "https://apis-navi.kakaomobility.com/v1/directions";

    private final String kakaoApiKey;

    @Override
    public URI getUri(double originLng, double originLat, double destLng, double destLat) {
        return UriComponentsBuilder.fromHttpUrl(FIND_CAR_ROAD_BASE_URI)
            .queryParam("origin", originLng + "," + originLat)
            .queryParam("destination", destLng + "," + destLat)
            .build().toUri();
    }

    @Override
    public HttpEntity<?> getHttpInfo() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + kakaoApiKey);

        return new HttpEntity<>(headers);
    }

    @Override
    public int parseAndGetDistance(JsonNode root) {
        return root.path("routes").get(0).path("summary").path("distance").asInt();
    }
}
