package org.prgms.locomocoserver.mogakkos.application.mapsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.prgms.locomocoserver.mogakkos.dto.Place;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoMapSearch { // https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-category
    private static final String SEARCH_BASE_URI = "https://dapi.kakao.com/v2/local/search/category.json";
    private static final int MAX_RADIUS = 20000;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${oauth.kakao.REST_API_KEY}")
    private String kakaoApiKey;

    public @NotNull List<Place> getSearchResponse(double latitude, double longitude, String categoryGroupCode) {
        URI uri = UriComponentsBuilder.fromHttpUrl(SEARCH_BASE_URI)
            .queryParams(getSearchQueryParamMap(categoryGroupCode, latitude, longitude))
            .build().toUri();

        List<Place> searchResponse = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + kakaoApiKey);

            String exchangeBody = restTemplate.exchange(uri, HttpMethod.GET,
                new HttpEntity<>(headers), String.class).getBody();// TODO: 타임아웃 5초, 재시도 없음

            JsonNode placesNode = objectMapper.readTree(exchangeBody).path("documents");
            placesNode.forEach(p -> {
                if (isRightPlace(p)) {
                    searchResponse.add(new Place(p.path("place_name").asText(),
                        p.path("road_address_name").asText(),
                        p.path("x").asDouble(),
                        p.path("y").asDouble(),
                        p.path("distance").asInt()));
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("카카오 API로 주변 정보를 가져오던 중 에러가 발생했습니다.", e);
        }

        log.info("카카오 검색 API 실행 성공. latitude: {}, longitude: {}", latitude, longitude);

        return searchResponse;
    }

    private boolean isRightPlace(JsonNode p) {
        return !p.path("category_name").asText().contains("테마카페") && !p.path("category_name")
            .asText().contains("놀이시설");
    }

    private MultiValueMap<String, String> getSearchQueryParamMap(String categoryGroupCode, double latitude, double longitude) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("category_group_code", categoryGroupCode);
        paramMap.add("x", String.valueOf(longitude));
        paramMap.add("y", String.valueOf(latitude));
        paramMap.add("radius", String.valueOf(MAX_RADIUS));
        paramMap.add("sort", "distance");

        return paramMap;
    }
}
