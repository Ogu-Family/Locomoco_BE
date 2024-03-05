package org.prgms.locomocoserver.mogakkos.application;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.response.ParticipationCheckingDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MogakkoParticipationService {

    private final ParticipantRepository participantRepository;


    public ParticipationCheckingDto check(Long mogakkoId, Long userId) {
        Optional<Participant> optionalParticipant = participantRepository.findByMogakkoIdAndUserId(
            mogakkoId, userId);

        if (optionalParticipant.isPresent()) {
            return new ParticipationCheckingDto(true);
        }

        return new ParticipationCheckingDto(false);
    }
}
