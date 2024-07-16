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
import org.prgms.locomocoserver.mogakkos.exception.MogakkoErrorType;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void participate(Long mogakkoId, ParticipationRequestDto requestDto) {
        Mogakko mogakko = mogakkoRepository.findByIdAndDeletedAtIsNull(mogakkoId)
            .orElseThrow(() -> new MogakkoException(MogakkoErrorType.NOT_FOUND));
        User user = userService.getById(requestDto.userId());

        validateParticipation(mogakko, requestDto.userId());

        Participant participant = Participant.builder().mogakko(mogakko).user(user).latitude(
            requestDto.latitude()).longitude(requestDto.longitude()).build();
        mogakko.addParticipant(participant);
        participantRepository.save(participant);

        chatRoomService.enterChatRoom(new ChatEnterRequestDto(mogakko.getChatRoom().getId(), user));
    }

    @Transactional
    public void update(Long mogakkoId, ParticipationRequestDto requestDto) {
        Participant participant = participantRepository.findByMogakkoIdAndUserId(mogakkoId,
            requestDto.userId()).orElseThrow(() -> new RuntimeException("해당하는 참여자가 존재하지 않습니다.")); // TODO: 참가 에러 반환

        participant.updateLocation(requestDto.latitude(), requestDto.longitude());
    }

    public void cancel(Long mogakkoId, Long userId) {
        Mogakko mogakko = mogakkoRepository.findByIdAndDeletedAtIsNull(mogakkoId)
            .orElseThrow(() -> new MogakkoException(MogakkoErrorType.NOT_FOUND));

        validateIfEndTimeIsPast(mogakko);

        Participant participant = participantRepository.findByMogakkoIdAndUserId(mogakkoId, userId)
            .orElseThrow(RuntimeException::new);// TODO: 참여 예외 반환
        participantRepository.delete(participant);

        chatRoomService.leave(mogakko.getChatRoom(), userId);
    }

    private void validateParticipation(Mogakko mogakko, long participantId) {
        if (mogakko.getDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("신청 데드 라인이 지났습니다."); // TODO: 참여 예외 반환
        }

        if (participantRepository.findByMogakkoIdAndUserId(mogakko.getId(), participantId).isPresent()){
            throw new RuntimeException("이미 참여한 유저입니다."); // TODO: 참여 예외 반환
        }
    }

    private void validateIfEndTimeIsPast(Mogakko mogakko) {
        if (mogakko.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("이미 종료된 모각코입니다."); // TODO: 참여 예외 반환
        }
    }
}
