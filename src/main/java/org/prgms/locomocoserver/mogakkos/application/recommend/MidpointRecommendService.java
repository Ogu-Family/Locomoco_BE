package org.prgms.locomocoserver.mogakkos.application.recommend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.common.Pair;
import org.prgms.locomocoserver.mogakkos.application.findroad.FindRoadInfo;
import org.prgms.locomocoserver.mogakkos.application.findroad.FindRoadFactory;
import org.prgms.locomocoserver.mogakkos.application.mapsearch.KakaoMapSearch;
import org.prgms.locomocoserver.mogakkos.domain.Location;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;
import org.prgms.locomocoserver.mogakkos.dto.Place;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MidpointRecommendService {
    private static final double SUBWAY_DIST_KM = 5.0d;
    private static final double LIMIT_DIST_KM = 100.0d;
    private static final String CAFE = "CE7";
    private static final String SUBWAY = "SW8";

    private final FindRoadFactory findRoadFactory;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KakaoMapSearch kakaoMapSearch;

    public Midpoint recommend(List<? extends Location> locations) {
        double maximumDistance = getMaximumDistance(locations);

        if (maximumDistance >= LIMIT_DIST_KM) {
            throw new RuntimeException("거리가 너무 멀어 계산할 수 없습니다."); // TODO: 중간 지점 예외로 교체
        }

        double latitudeAvg = locations.stream().collect(Collectors.averagingDouble(Location::getLatitude));
        double longitudeAvg = locations.stream().collect(Collectors.averagingDouble(Location::getLongitude));

        // 비동기로 요청 (카페, 지하철)
        CompletableFuture<Pair<Midpoint, Integer>> cafeMidpointFuture = CompletableFuture.supplyAsync(
            () -> getMidpoint(latitudeAvg, longitudeAvg, locations, CAFE));

        CompletableFuture<Pair<Midpoint, Integer>> subwayMidpointFuture = null;

        if (maximumDistance > SUBWAY_DIST_KM) {
            subwayMidpointFuture = CompletableFuture.supplyAsync(() -> getMidpoint(latitudeAvg, longitudeAvg, locations, SUBWAY));
        }

        List<Pair<Midpoint, Integer>> midpointCandidate = new ArrayList<>();

        try {
            midpointCandidate.add(cafeMidpointFuture.get());
            midpointCandidate.add(subwayMidpointFuture != null ? subwayMidpointFuture.get() : null);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("중간 지점 계산 비동기 처리 중 예외가 발생했습니다."); // TODO: 중간 지점 예외로 교체
        }

        return selectMidpoint(midpointCandidate);
    }

    private Pair<Midpoint, Integer> getMidpoint(double latitudeAvg, double longitudeAvg, List<? extends Location> locations, String categoryGroup) {
        Midpoint ret = null;
        int minDist = Integer.MAX_VALUE;

        List<Place> places = kakaoMapSearch.getSearchResponse(latitudeAvg, longitudeAvg, categoryGroup);

        for (Place place : places) {
            int sumDist = locations.stream().mapToInt(l -> getRoadDistance(l, place, categoryGroup)).sum();

            log.info("정상적으로 길찾기 API가 실행됨.");

            if (minDist > sumDist) {
                minDist = sumDist;
                ret = place.toMidpoint();
            }
        }

        return new Pair<>(ret, minDist);
    }

    private double getMaximumDistance(List<? extends Location> locations) {
        double maxDistance = 0d;
        int locationSize = locations.size();

        for (int i = 0; i < locationSize - 1; i++) {
            for (int j = i + 1; j < locationSize; j++) {
                maxDistance = Math.max(maxDistance, locations.get(i).calDistance(locations.get(j)));
            }
        }

        return maxDistance;
    }

    private int getRoadDistance(Location origin, Place dest, String categoryGroup) {
        int ret;
        FindRoadInfo findRoadInfo = findRoadFactory.getFindRoadInfo(categoryGroup);

        URI uri = findRoadInfo.getUri(origin.getLongitude(), origin.getLatitude(), dest.longitude(), dest.latitude());
        String response = restTemplate.exchange(uri, HttpMethod.GET, findRoadInfo.getHttpInfo(), String.class).getBody();

        try {
            JsonNode root = objectMapper.readTree(response);

            ret = findRoadInfo.parseAndGetDistance(root);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("길찾기 응답 처리에 실패했습니다.", e); // TODO: 중간 지점 예외로 교체
        }

        return ret;
    }

    private Midpoint selectMidpoint(List<Pair<Midpoint, Integer>> midpoints) {
        Midpoint ret = null;
        double minDist = Double.MAX_VALUE;

        for (Pair<Midpoint, Integer> p : midpoints) {
            if (p == null)
                continue;

            if (minDist > p.getR()) {
                minDist = p.getR();
                ret = p.getT();
            }
        }

        if (ret == null) {
            throw new RuntimeException("적절한 중간 지점을 생성할 수 없습니다."); // TODO: 중간 지점 예외로 교체
        }

        return ret;
    }
}
