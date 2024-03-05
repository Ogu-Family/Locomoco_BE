package org.prgms.locomocoserver.mogakkos.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MogakkoParticipationService;
import org.prgms.locomocoserver.mogakkos.dto.request.ParticipationRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.ParticipationCheckingDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mogakko Participation controller", description = "모각코 참여 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MogakkoParticipationController {

    private final MogakkoParticipationService participationService;

    @GetMapping("mogakko/map/{id}/participate")
    public ResponseEntity<ParticipationCheckingDto> checkParticipating(
        @PathVariable Long id, @RequestParam Long userId) {
        ParticipationCheckingDto responseDto = participationService.check(id, userId);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("mogakko/map/{id}/participate")
    public ResponseEntity<Void> participate(@PathVariable Long id,
        @RequestBody ParticipationRequestDto requestDto) {
        participationService.participate(id, requestDto);

        return ResponseEntity.ok().build();
    }
}
