package org.prgms.locomocoserver.mogakkos.application.recommend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prgms.locomocoserver.mogakkos.application.findroad.FindRoadFactory;
import org.prgms.locomocoserver.mogakkos.application.findroad.FindRoadInfo;
import org.prgms.locomocoserver.mogakkos.application.mapsearch.KakaoMapSearch;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;
import org.prgms.locomocoserver.mogakkos.dto.Place;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class MidpointRecommendServiceTest {
    private static final String CAFE = "CE7";
    private static final String SUBWAY = "SW8";

    @InjectMocks
    private MidpointRecommendService midpointRecommendService;
    @Mock
    private  FindRoadFactory findRoadFactory;
    @Mock
    private KakaoMapSearch kakaoMapSearch;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("중간 지점을 제대로 가져올 수 있다.")
    void success_recommend_midpoint_given_healthy_args_within_5_km() throws Exception {
        // given
        double longitude1 = 126.70214557487238d, longitude2 = 126.89359950377788d, longitudeAvg = (longitude1 + longitude2) / 2;
        double latitude1 = 37.45724860799384d, latitude2 = 37.456243020842734d, latitudeAvg = (latitude1 + latitude2) / 2;
        List<MogakkoLocation> testLoc = List.of(
            MogakkoLocation.builder().longitude(longitude1).latitude(latitude1)
                .build(),
            MogakkoLocation.builder().longitude(longitude2).latitude(latitude2)
                .build());

        double placeLng = longitude1 + 1.0d;
        double placeLat = latitude1 - 0.1d;
        given(kakaoMapSearch.getSearchResponse(latitudeAvg, longitudeAvg, CAFE))
            .willReturn(List.of(new Place("", "", placeLng, placeLat, 10)));
        given(findRoadFactory.getFindRoadInfo(any())).willReturn(new FindRoadInfoTest());
        given(restTemplate.exchange(null, HttpMethod.GET, null, String.class))
            .willReturn(ResponseEntity.ok(""));
        given(objectMapper.readTree(any(String.class))).willReturn(null);

        // when
        Midpoint recommendMid = midpointRecommendService.recommend(testLoc);

        // then
        assertThat(recommendMid.getLatitude()).isEqualTo(placeLat);
        assertThat(recommendMid.getLongitude()).isEqualTo(placeLng);
    }

    static class FindRoadInfoTest implements FindRoadInfo {

        @Override
        public URI getUri(double originLng, double originLat, double destLng, double destLat) {
            return null;
        }

        @Override
        public int parseAndGetDistance(JsonNode root) {
            return 1;
        }
    }
}
