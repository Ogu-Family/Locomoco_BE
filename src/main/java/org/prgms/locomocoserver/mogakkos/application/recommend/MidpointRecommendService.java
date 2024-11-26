package org.prgms.locomocoserver.mogakkos.application.recommend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        log.info("시작");

        if (maximumDistance >= LIMIT_DIST_KM) {
            throw new RuntimeException("거리가 너무 멀어 계산할 수 없습니다."); // TODO: 중간 지점 예외로 교체
        }

        double latitudeAvg = locations.stream().collect(Collectors.averagingDouble(Location::getLatitude));
        double longitudeAvg = locations.stream().collect(Collectors.averagingDouble(Location::getLongitude));

        List<Pair<Midpoint, Integer>> midpointCandidate = new ArrayList<>();

        // 비동기로 요청 (카페)
        CompletableFuture<Pair<Midpoint, Integer>> cafeMidpointFuture = CompletableFuture.supplyAsync(
            () -> getMidpoint(latitudeAvg, longitudeAvg, locations, CAFE));

        /*
        if (maximumDistance > SUBWAY_DIST_KM) {
             midpointCandidate.add(getMidpoint(latitudeAvg, longitudeAvg, locations, SUBWAY));
        }
        */ // ODSay 기간 만료

        try {
            midpointCandidate.add(cafeMidpointFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("중간 지점 계산 비동기 처리 중 예외가 발생했습니다.", e); // TODO: 중간 지점 예외로 교체
        }
        log.info("거의 종료");

        return selectMidpoint(midpointCandidate);
    }

    private Pair<Midpoint, Integer> getMidpoint(double latitudeAvg, double longitudeAvg, List<? extends Location> locations, String categoryGroup) { // TODO: 비동기 처리 더 최적화 가능할 듯
        Midpoint ret = null;
        int minDist = Integer.MAX_VALUE;

        try {
            List<Place> places = kakaoMapSearch.getSearchResponse(latitudeAvg, longitudeAvg,
                categoryGroup);

            for (Place place : places) {
                List<CompletableFuture<Integer>> futures = locations.stream().map(
                    l -> CompletableFuture.supplyAsync(
                        () -> getRoadDistance(l, place, categoryGroup))).toList();

                int sumDist = futures.stream().mapToInt(CompletableFuture::join).sum();

                if (minDist > sumDist) {
                    minDist = sumDist;
                    ret = place.toMidpoint();
                }
            }

        } catch (Exception e) {
            return null;
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

        if (Objects.isNull(findRoadInfo)) {
            throw new RuntimeException("길찾기를 위해 설정된 외부 API가 없습니다. (findRoadInfo == null)");
        }

        URI uri = findRoadInfo.getUri(origin.getLongitude(), origin.getLatitude(), dest.longitude(), dest.latitude());
        String response = restTemplate.exchange(uri, HttpMethod.GET, findRoadInfo.getHttpInfo(), String.class).getBody(); // TODO: 타임 아웃, API 장애 등 상황에 따른 설정

        try {
            JsonNode root = objectMapper.readTree(response);

            ret = findRoadInfo.parseAndGetDistance(root);
        } catch (Exception e) {
            log.warn("{} 카테고리에 따른 길찾기 응답 처리 실패", categoryGroup, e);
            throw new RuntimeException("길찾기 응답 처리에 실패했습니다.", e); // TODO: 중간 지점 예외로 교체
        }

        return ret;
    }

    private Midpoint selectMidpoint(List<Pair<Midpoint, Integer>> midpoints) {
        Midpoint ret = null;
        double minDist = Double.MAX_VALUE;

        for (Pair<Midpoint, Integer> p : midpoints) {
            if (p == null || p.getT() == null)
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
