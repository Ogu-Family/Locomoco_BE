package org.prgms.locomocoserver.mogakkos.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.dto.request.ChatCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocationRepository;
import org.prgms.locomocoserver.mogakkos.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.application.searchpolicy.SearchPolicy;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTag;
import org.prgms.locomocoserver.mogakkos.domain.mogakkotags.MogakkoTagRepository;
import org.prgms.locomocoserver.mogakkos.dto.SearchRepositoryDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoUpdateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.ParticipationRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoInfoDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoParticipantDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoUpdateResponseDto;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoErrorType;
import org.prgms.locomocoserver.mogakkos.exception.MogakkoException;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.dto.response.UserBriefInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MogakkoService {
    private final MogakkoFindDetailService mogakkoFindDetailService;
    //private final PlatformTransactionManager transactionManager;
    private final MogakkoRepository mogakkoRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MogakkoLocationRepository mogakkoLocationRepository;
    private final MogakkoTagRepository mogakkoTagRepository;
    private final ChatRoomService chatRoomService;
    private final MogakkoParticipationService mogakkoParticipationService;

    @Transactional
    public MogakkoCreateResponseDto save(MogakkoCreateRequestDto requestDto) {
        User creator = userService.getById(requestDto.creatorId());
        Mogakko mogakko = createMogakkoBy(requestDto);

        MogakkoLocation mogakkoLocation = requestDto.toLocation();
        mogakkoLocation.updateMogakko(mogakko);

        mogakko.updateCreator(creator);

        Mogakko savedMogakko = mogakkoRepository.save(mogakko);
        mogakkoLocationRepository.save(mogakkoLocation);

        ChatRoom chatRoom = chatRoomService.createChatRoom(
            new ChatCreateRequestDto(savedMogakko, creator));
        savedMogakko.updateChatRoom(chatRoom);
        mogakkoParticipationService.participate(savedMogakko.getId(), new ParticipationRequestDto(
            requestDto.creatorId()));

        return new MogakkoCreateResponseDto(savedMogakko.getId());
    }

    public MogakkoDetailResponseDto findDetail(Long id) {
        Mogakko foundMogakko = getByIdNotDeleted(id);

        log.info("views count before increaseViews() : {}", foundMogakko.getViews());
        mogakkoFindDetailService.increaseViews(foundMogakko);
        foundMogakko = getByIdNotDeleted(id);
        log.info("views count after increaseViews() : {}", foundMogakko.getViews());

        User creator = userRepository.findByIdAndDeletedAtIsNull(foundMogakko.getCreator().getId())
            .orElseGet(() -> User.builder().nickname("(알 수 없음)").build());
        List<User> participants = userRepository.findAllParticipantsByMogakko(foundMogakko);
        List<MogakkoTag> mogakkoTags = mogakkoTagRepository.findAllByMogakko(foundMogakko);
        MogakkoLocation foundMogakkoLocation = mogakkoLocationRepository.findByMogakkoAndDeletedAtIsNull(foundMogakko)
            .orElseThrow(RuntimeException::new); // TODO: 장소 예외 반환

        return getMogakkoDetailResponseDto(creator, participants, mogakkoTags, foundMogakko,
            foundMogakkoLocation);
    }

    @Transactional(readOnly = true)
    public List<MogakkoSimpleInfoResponseDto> findAllByFilter(List<Long> tagIds, Long cursor,
        String searchVal, SearchType searchType, int pageSize) {
        SearchPolicy searchPolicy = searchType.getSearchPolicy(new SearchRepositoryDto(mogakkoRepository));
        List<Mogakko> searchedMogakkos;

        validateFilter(searchVal);

        searchedMogakkos = search(tagIds, cursor, searchVal, pageSize, searchPolicy);

        List<MogakkoLocation> mogakkoLocations = mogakkoLocationRepository.findAllByMogakkos(searchedMogakkos);
        Map<Long, Long> mogakkoLocationMap = new HashMap<>();
        mogakkoLocations.forEach(location -> mogakkoLocationMap.put(location.getMogakko().getId(), location.getId()));

        return searchedMogakkos.stream().map(mogakko -> {
            MogakkoLocation mogakkoLocation = mogakkoLocationRepository.findById(mogakkoLocationMap.get(mogakko.getId()))
                .orElseThrow(RuntimeException::new); // TODO: 장소 에러 반환
            return MogakkoSimpleInfoResponseDto.create(mogakko, mogakkoLocation);
        }).toList();
    }

    @Transactional
    public MogakkoUpdateResponseDto update(MogakkoUpdateRequestDto requestDto, Long id) {
        Mogakko foundMogakko = getByIdNotDeleted(id);

        validateCreator(requestDto, foundMogakko);

        foundMogakko.updateInfo(requestDto.title(), requestDto.content(), requestDto.startTime(),
            requestDto.endTime(), requestDto.deadline(), requestDto.maxParticipants());
        updateMogakkoLocation(foundMogakko, requestDto.location());
        updateMogakkoTags(foundMogakko, requestDto.tags());

        return new MogakkoUpdateResponseDto(foundMogakko.getId());
    }

    @Transactional
    public void delete(Long id) {
        Mogakko foundMogakko = getByIdNotDeleted(id);
        ChatRoom chatRoom = foundMogakko.getChatRoom();

        foundMogakko.delete();
        chatRoomService.delete(chatRoom);
    }

    public Mogakko getByIdNotDeleted(Long id) {
        return mogakkoRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new MogakkoException(MogakkoErrorType.NOT_FOUND));
    }

    private static void validateCreator(MogakkoUpdateRequestDto requestDto, Mogakko foundMogakko) {
        if (!foundMogakko.isSameCreatorId(requestDto.creatorId())) {
            throw new MogakkoException(MogakkoErrorType.PROCESS_FORBIDDEN);
        }
    }

    /*private void increaseViews(Mogakko foundMogakko) {
        // foundMogakko.increaseViews();

        TransactionStatus status = transactionManager.getTransaction(
            new DefaultTransactionDefinition());

        try {
            mogakkoRepository.increaseViews(foundMogakko);
            transactionManager.commit(status);
        }
        catch (Exception e){
            transactionManager.rollback(status);

            log.error("조회 수 처리 중 에러 발생");
            throw e;
        }

    }*/

    private static MogakkoDetailResponseDto getMogakkoDetailResponseDto(User creator,
        List<User> participants, List<MogakkoTag> mogakkoTags, Mogakko foundMogakko,
        MogakkoLocation foundMogakkoLocation) {
        UserBriefInfoDto creatorInfoDto = UserBriefInfoDto.of(creator);
        List<MogakkoParticipantDto> mogakkoParticipantDtos = participants.stream()
            .map(MogakkoParticipantDto::create)
            .toList();
        List<Long> tagIds = mogakkoTags.stream().map(mogakkoTag -> mogakkoTag.getTag().getId())
            .toList();
        MogakkoInfoDto mogakkoInfoDto = MogakkoInfoDto.create(foundMogakko,
            LocationInfoDto.create(foundMogakkoLocation), tagIds);

        return new MogakkoDetailResponseDto(creatorInfoDto, mogakkoParticipantDtos, mogakkoInfoDto);
    }

    private void updateMogakkoTags(Mogakko updateMogakko, List<Long> updateTagIds) {
        final int DELETE_TAG = 0;
        final int MAINTAIN_TAG = 1;
        final int INSERT_TAG = 2;
        final Map<Tag, Integer> tagMap = new HashMap<>();

        List<MogakkoTag> existingMogakkoTags = mogakkoTagRepository.findAllByMogakko(updateMogakko);
        List<Tag> updateTags = tagRepository.findAllById(updateTagIds);

        existingMogakkoTags.forEach(mogakkoTag -> tagMap.put(mogakkoTag.getTag(), DELETE_TAG));

        updateTags.forEach(tag -> {
            if (tagMap.get(tag) == null) {
                tagMap.put(tag, INSERT_TAG);
            } else {
                tagMap.put(tag, MAINTAIN_TAG);
            }
        });

        tagMap.forEach((tag, status) -> {
            switch (status) {
                case DELETE_TAG -> mogakkoTagRepository.deleteByTagAndMogakko(tag, updateMogakko);
                case INSERT_TAG -> mogakkoTagRepository.save(
                    MogakkoTag.builder().mogakko(updateMogakko).tag(tag).build());
            }
        });
    }

    private Mogakko createMogakkoBy(MogakkoCreateRequestDto requestDto) {
        Mogakko mogakko = requestDto.toDefaultMogakko();

        List<Long> tagIds = requestDto.tags();
        List<Tag> tags = tagRepository.findAllById(tagIds);

        tags.forEach(tag -> {
            MogakkoTag mogakkoTag = MogakkoTag.builder().mogakko(mogakko).tag(tag).build();
            mogakko.addMogakkoTag(mogakkoTag);
        });

        return mogakko;
    }

    private void updateMogakkoLocation(Mogakko mogakko, LocationInfoDto locationInfoDto) {
        MogakkoLocation mogakkoLocation = mogakkoLocationRepository.findByMogakkoAndDeletedAtIsNull(mogakko)
                .orElseThrow(RuntimeException::new); // TODO: 적절한 예외 반환 (유저 혹은 장소)

        mogakkoLocation.updateInfo(locationInfoDto.latitude(),
                locationInfoDto.longitude(),
                locationInfoDto.address(),
                locationInfoDto.city());
    }

    private void validateFilter(String searchVal) {
        if (searchVal.length() == 1) {
            throw new MogakkoException(MogakkoErrorType.TOO_LITTLE_INPUT.appendMessage("2"));
        }
    }

    private List<Mogakko> search(List<Long> tagIds, Long cursor, String searchVal,
        int pageSize, SearchPolicy searchPolicy) {
        List<Mogakko> searchedMogakkos;
        if (tagIds == null || tagIds.isEmpty()) {
            searchedMogakkos = searchPolicy.search(cursor, searchVal, pageSize);
        } else {
            searchedMogakkos = searchPolicy.search(cursor, searchVal, tagIds, pageSize);
        }
        return searchedMogakkos;
    }
}
