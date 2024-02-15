package org.prgms.locomocoserver.mogakkos.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTag;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SelectedTagsDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MogakkoService {

    private final MogakkoRepository mogakkoRepository;
    private final TagRepository tagRepository;

    public MogakkoCreateResponseDto save(MogakkoCreateRequestDto requestDto) { // TODO: 생성한 사용자 관련 로직 추가
        Mogakko mogakko = createMogakkoBy(requestDto);

        Mogakko savedMogakko = mogakkoRepository.save(mogakko);

        return new MogakkoCreateResponseDto(savedMogakko.getId()); // TODO: FE가 원하는 포맷이 있으면 그것으로 DTO 변환.
    }

    private Mogakko createMogakkoBy(MogakkoCreateRequestDto requestDto) {
        List<SelectedTagsDto> selectedTagsDtos = requestDto.tags();
        Mogakko mogakko = requestDto.toMogakkoWithoutTags();

        List<Long> tagIds = selectedTagsDtos.stream().flatMap(dto -> dto.tagIds().stream()).toList();
        List<Tag> tags = tagRepository.findAllById(tagIds);

        tags.forEach(tag -> {
            MogakkoTag mogakkoTag = MogakkoTag.builder().mogakko(mogakko).tag(tag).build();
            mogakko.addMogakkoTag(mogakkoTag);
        });
        return mogakko;
    }
}
