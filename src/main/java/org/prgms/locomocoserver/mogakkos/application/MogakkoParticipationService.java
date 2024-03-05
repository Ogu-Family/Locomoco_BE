package org.prgms.locomocoserver.mogakkos.application;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.ParticipationRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.ParticipationCheckingDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MogakkoParticipationService {

    private final ParticipantRepository participantRepository;
    private final MogakkoService mogakkoService;
    private final UserService userService;

    public ParticipationCheckingDto check(Long mogakkoId, Long userId) {
        Optional<Participant> optionalParticipant = participantRepository.findByMogakkoIdAndUserId(
            mogakkoId, userId);

        if (optionalParticipant.isPresent()) {
            return new ParticipationCheckingDto(true);
        }

        return new ParticipationCheckingDto(false);
    }

    public void participate(Long mogakkoId, ParticipationRequestDto requestDto) {
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(mogakkoId);
        User user = userService.getById(requestDto.userId());

        Participant participant = Participant.builder().mogakko(mogakko).user(user).build();
        mogakko.addParticipant(participant);
        participantRepository.save(participant);
    }
}
