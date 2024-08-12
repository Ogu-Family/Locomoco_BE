package org.prgms.locomocoserver.mogakkos.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.recommend.MidpointRecommendService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.MidpointRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.response.MidpointDto;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoErrorType;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MidpointService {
    private final ParticipantRepository participantRepository;
    private final MidpointRecommendService midpointRecommendService;
    private final MidpointRepository midpointRepository;
    private final MogakkoRepository mogakkoRepository;

    @Transactional
    public MidpointDto recommend(long mogakkoId) {
        Midpoint midpoint = midpointRepository.findByMogakkoId(mogakkoId)
            .orElseGet(() -> Midpoint.builder().build());

        List<Participant> participants = participantRepository.findAllByMogakkoId(mogakkoId);

        List<MogakkoLocation> participantsLoc = participants.stream().map(
            p -> {
                if (p.getLongitude() == null || p.getLatitude() == null) {
                    throw new RuntimeException("id:" + p.getUser().getId() + " 참가자의 출발 위치가 존재하지 않습니다."); // TODO: 중간 지점 예외 반환
                }

                return MogakkoLocation.builder().latitude(p.getLatitude()).longitude(p.getLongitude())
                    .build();
            }).toList();

        Midpoint recommend = midpointRecommendService.recommend(participantsLoc);

        Mogakko mogakko = mogakkoRepository.findById(mogakkoId).orElseThrow(() -> new MogakkoException(
            MogakkoErrorType.NOT_FOUND));

        midpoint.updateInfo(recommend.getLatitude(), recommend.getLongitude(), recommend.getAddressInfo(), recommend.getPlaceName(), mogakko);

        midpointRepository.save(midpoint);

        return MidpointDto.from(midpoint);
    }
}
