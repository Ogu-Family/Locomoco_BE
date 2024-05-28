package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MidpointService;
import org.prgms.locomocoserver.mogakkos.dto.response.MidpointDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mogakko Midpoint Controller", description = "모각코 중간 지점 컨트롤러")
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class MogakkoMidpointController {
    private final MidpointService midpointService;

    @GetMapping("/mogakko/recommend")
    public MidpointDto recommend(@RequestParam long mogakkoId) {
        return midpointService.recommend(mogakkoId);
    }

}
