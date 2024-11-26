package org.prgms.locomocoserver.mogakkos.application.findroad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FindRoadFactory {
    private static final String CAFE = "CE7";
    private static final String SUBWAY = "SW8";

    @Value("${oauth.kakao.REST_API_KEY}")
    private String kakaoApiKey;
    @Value("${odsay.api-key}")
    private String odsayApiKey;

    public FindRoadInfo getFindRoadInfo(String categoryGroup) {
        return switch (categoryGroup) {
            case CAFE -> new KakaoFindRoadInfo(kakaoApiKey);
            // case SUBWAY -> new OdsayFindRoadInfo(odsayApiKey); // ODSay 기간 만료
            default -> null;
        };
    }
}
