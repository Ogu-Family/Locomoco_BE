package org.prgms.locomocoserver.mogakkos.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.recommend.MidpointRecommendService;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.MidpointRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.response.MidpointDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MidpointService {
    private final ParticipantRepository participantRepository;
    private final MidpointRecommendService midpointRecommendService;
    private final MidpointRepository midpointRepository;

    @Transactional
    public MidpointDto recommend(long mogakkoId) {
        Midpoint midpoint = midpointRepository.findByMogakkoId(mogakkoId)
            .orElseGet(() -> Midpoint.builder().build());

        List<Participant> participants = participantRepository.findAllByMogakkoId(mogakkoId);

        List<MogakkoLocation> participantsLoc = participants.stream().map(
            p -> MogakkoLocation.builder().latitude(p.getLatitude()).longitude(p.getLongitude())
                .build()).toList();

        Midpoint recommend = midpointRecommendService.recommend(participantsLoc);

        midpoint.update(recommend);

        midpointRepository.save(midpoint);

        return MidpointDto.from(midpoint);
    }
}
