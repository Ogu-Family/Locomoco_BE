package org.prgms.locomocoserver.mogakkos.application.findroad;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import org.springframework.http.HttpEntity;

public interface FindRoadInfo {
    URI getUri(double originLng, double originLat, double destLng, double destLat);
    default HttpEntity<?> getHttpInfo() { return null; }
    int parseAndGetDistance(JsonNode root);
}
