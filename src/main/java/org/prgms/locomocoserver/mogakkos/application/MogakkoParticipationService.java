package org.prgms.locomocoserver.mogakkos.application;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.dto.request.ChatEnterRequestDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.ParticipationRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.ParticipationCheckingDto;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoErrorCode;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MogakkoParticipationService {

    private final ParticipantRepository participantRepository;
    private final MogakkoRepository mogakkoRepository;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    public ParticipationCheckingDto check(Long mogakkoId, Long userId) {
        Optional<Participant> optionalParticipant = participantRepository.findByMogakkoIdAndUserId(
            mogakkoId, userId);

        if (optionalParticipant.isPresent()) {
            return new ParticipationCheckingDto(true);
        }

        return new ParticipationCheckingDto(false);
    }

    public void participate(Long mogakkoId, ParticipationRequestDto requestDto) {
        Mogakko mogakko = mogakkoRepository.findByIdAndDeletedAtIsNull(mogakkoId)
            .orElseThrow(() -> new MogakkoException(MogakkoErrorCode.NOT_FOUND));
        User user = userService.getById(requestDto.userId());

        validateIfDeadlineIsPast(mogakko);

        Participant participant = Participant.builder().mogakko(mogakko).user(user).build();
        mogakko.addParticipant(participant);
        participantRepository.save(participant);

        chatRoomService.enterChatRoom(new ChatEnterRequestDto(mogakko.getChatRoom().getId(), user));
    }

    public void cancel(Long mogakkoId, Long userId) {
        Mogakko mogakko = mogakkoRepository.findByIdAndDeletedAtIsNull(mogakkoId)
            .orElseThrow(() -> new MogakkoException(MogakkoErrorCode.NOT_FOUND));

        validateIfEndTimeIsPast(mogakko);

        Participant participant = participantRepository.findByMogakkoIdAndUserId(mogakkoId, userId)
            .orElseThrow(RuntimeException::new);// TODO: 참여 예외 반환
        participantRepository.delete(participant);

        chatRoomService.leave(mogakko.getChatRoom(), userId);
    }

    private void validateIfDeadlineIsPast(Mogakko mogakko) {
        if (mogakko.getDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("신청 데드 라인이 지났습니다."); // TODO: 적절한 예외 처리
        }
    }

    private void validateIfEndTimeIsPast(Mogakko mogakko) {
        if (mogakko.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("이미 종료된 모각코입니다."); // TODO: 적절한 예외 처리
        }
    }
}
