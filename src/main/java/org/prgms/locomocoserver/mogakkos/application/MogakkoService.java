package org.prgms.locomocoserver.mogakkos.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTag;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoInfoDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoParticipantDto;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.dto.response.UserBriefInfoDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MogakkoService {

    private final MogakkoRepository mogakkoRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final MogakkoTagRepository mogakkoTagRepository;

    public MogakkoCreateResponseDto save(MogakkoCreateRequestDto requestDto) {
        Mogakko mogakko = createMogakkoBy(requestDto);

        User creator = userRepository.findById(requestDto.creatorId())
            .orElseThrow(RuntimeException::new);// TODO: 유저 에러 반환
        mogakko.updateCreator(creator);

        Mogakko savedMogakko = mogakkoRepository.save(mogakko);

        return new MogakkoCreateResponseDto(savedMogakko.getId()); // TODO: FE가 원하는 포맷이 있으면 그것으로 DTO 변환.
    }

    public MogakkoDetailResponseDto findDetail(Long id) {
        Mogakko foundMogakko = mogakkoRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(RuntimeException::new); // TODO: 모각코 에러 반환
        User creator = userRepository.findByIdAndDeletedAtIsNull(foundMogakko.getCreator().getId())
            .orElseGet(() -> User.builder().nickname("(알 수 없음)").build());
        List<User> participants = userRepository.findAllParticipantsByMogakko(foundMogakko);
        List<MogakkoTag> mogakkoTags = mogakkoTagRepository.findAllByMogakko(foundMogakko);

        UserBriefInfoDto creatorInfoDto = UserBriefInfoDto.create(creator);
        List<MogakkoParticipantDto> mogakkoParticipantDtos = participants.stream().map(MogakkoParticipantDto::create)
            .toList();
        List<Long> tagIds = mogakkoTags.stream().map(mogakkoTag -> mogakkoTag.getTag().getId())
            .toList();
        MogakkoInfoDto mogakkoInfoDto = MogakkoInfoDto.create(foundMogakko, tagIds);

        return new MogakkoDetailResponseDto(creatorInfoDto, mogakkoParticipantDtos, mogakkoInfoDto);
    }

    private Mogakko createMogakkoBy(MogakkoCreateRequestDto requestDto) {
        Mogakko mogakko = requestDto.toMogakkoWithoutTags();

        List<Long> tagIds = requestDto.tags();
        List<Tag> tags = tagRepository.findAllById(tagIds);

        tags.forEach(tag -> {
            MogakkoTag mogakkoTag = MogakkoTag.builder().mogakko(mogakko).tag(tag).build();
            mogakko.addMogakkoTag(mogakkoTag);
        });
        return mogakko;
    }
}
