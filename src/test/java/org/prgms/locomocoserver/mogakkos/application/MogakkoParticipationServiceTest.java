package org.prgms.locomocoserver.mogakkos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.prgms.locomocoserver.global.TestFactory.createMogakko;
import static org.prgms.locomocoserver.global.TestFactory.createUser;

import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.bson.assertions.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.chat.domain.ChatParticipantRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocationRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.ParticipationRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.ParticipationCheckingDto;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MogakkoParticipationTest {

    private static final Logger log = LoggerFactory.getLogger(MogakkoParticipationTest.class);
    @Autowired
    MogakkoService mogakkoService;
    @Autowired
    MogakkoParticipationService mogakkoParticipationService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MogakkoRepository mogakkoRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ChatParticipantRepository chatParticipantRepository;
    @Autowired
    MogakkoLocationRepository locationRepository;

    User testCreator;
    Mogakko testMogakko;

    @BeforeEach
    void setUp() {
        testCreator = createUser();
        testMogakko = createMogakko(testCreator);

        imageRepository.save(testCreator.getProfileImage());
        userRepository.save(testCreator);

        Long savedMogakkoId = mogakkoService.save(
            new MogakkoCreateRequestDto(testCreator.getId(), testMogakko.getTitle(),
                new LocationInfoDto("", 127.52d, 27.8621d, null), testMogakko.getStartTime(),
                testMogakko.getEndTime(), testMogakko.getDeadline(),
                testMogakko.getMaxParticipants(),
                testMogakko.getContent(), Collections.EMPTY_LIST)).id();

        testMogakko = mogakkoRepository.findById(savedMogakkoId)
            .orElseThrow(() -> new RuntimeException("mogakko not found"));
    }

    @AfterEach
    void tearDown() {
        log.info("청소 시작");
        chatParticipantRepository.deleteAll();
        chatRoomRepository.deleteAll();
        participantRepository.deleteAll();
        locationRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @DisplayName("참여 중인 모각코에 중복 참여 신청은 불가능하다.")
    void fail_participate_in_mogakko_already_belong_to() {
        // given
        Long mogakkoId = testMogakko.getId();
        ParticipationRequestDto requestDto = new ParticipationRequestDto(testCreator.getId());

        // when then
        assertThatThrownBy(() -> mogakkoParticipationService.participate(mogakkoId, requestDto))
            .isInstanceOf(RuntimeException.class).hasMessageContaining("이미 참여한 유저입니다."); // TODO: 참여 예외 반환
    }

    @Test
    @DisplayName("모각코 작성자가 참여를 취소할 수는 없다.")
    void fail_cancel_to_participate() {
        // given
        Long mogakkoId = testMogakko.getId();
        Long creatorId = testCreator.getId();

        ThrowingCallable cancelFunc = () -> mogakkoParticipationService.cancel(
            mogakkoId, creatorId);

        // when then
        assertThatThrownBy(cancelFunc).isInstanceOf(RuntimeException.class)
            .hasMessageContaining("모각코 생성자가 참여를 취소할 수 없습니다.");
    }

    @Test
    @DisplayName("(동시성) 참여 인원이 다 차면 더 이상 참여할 수 없다.")
    void fail_participate_in_mogakko_when_it_is_full() throws Exception {
        // given
        Long mogakkoId = testMogakko.getId();
        int usersNum = testMogakko.getMaxParticipants() + 10;

        ThreadPoolExecutor threadPoolExecutor = ExecutorServiceUtil.newThreadPoolExecutor();
        threadPoolExecutor.setMaximumPoolSize(usersNum);

        List<User> users = IntStream.range(0, usersNum).mapToObj(i -> createUser()).toList();
        users.forEach(u -> imageRepository.save(u.getProfileImage()));
        userRepository.saveAll(users);
        List<ParticipationRequestDto> participationRequestDtos = users.stream()
            .map(u -> new ParticipationRequestDto(u.getId(), null, null)).toList();

        CountDownLatch countDownLatch = new CountDownLatch(usersNum);

        // when
        AtomicInteger overCount = new AtomicInteger();

        participationRequestDtos.forEach(dto ->
            threadPoolExecutor.execute(
                () -> {
                    try {
                        mogakkoParticipationService.participate(mogakkoId, dto);
                    } catch (Exception e) {
                        log.info("예외 발생");
                        overCount.getAndIncrement();
                    } finally {
                        countDownLatch.countDown();
                    }
                })
        );
        countDownLatch.await(5000, TimeUnit.MILLISECONDS);

        // then
        assertThat(overCount.get()).isEqualTo(usersNum - testMogakko.getMaxParticipants() + 1);
    }

    @Test
    @DisplayName("참여자의 참여 정보를 업데이트할 수 있다.")
    void success_update_info() {
        // given
        double latitude = 27.12345678999999d;
        double longitude = 127.12423d;
        ParticipationRequestDto requestDto = new ParticipationRequestDto(testCreator.getId(),
            longitude, latitude);

        // when
        mogakkoParticipationService.update(testMogakko.getId(), requestDto);

        // then
        Participant participant = participantRepository.findByMogakkoIdAndUserId(
            testMogakko.getId(), testCreator.getId()).orElseThrow(RuntimeException::new);

        double columnPointLimit = 0.0000000001d;
        assertThat(participant.getLongitude()).isEqualTo(longitude, within(columnPointLimit));
        assertThat(participant.getLatitude()).isEqualTo(latitude, within(columnPointLimit));
    }

    @Test
    @DisplayName("모각코 참여를 취소할 수 있다.")
    void success_cancel_to_participate_in_mogakko() throws Exception {
        // given
        User user1 = createUser();
        User user2 = createUser();
        List<User> users = List.of(user1, user2);

        users.stream().map(User::getProfileImage).forEach(imageRepository::save);
        userRepository.saveAll(users);

        users.forEach(user -> mogakkoParticipationService.participate(testMogakko.getId(), new ParticipationRequestDto(user.getId())));

        // when
        CountDownLatch countDownLatch = new CountDownLatch(users.size());

        users.forEach(user -> CompletableFuture.runAsync(() -> {
            mogakkoParticipationService.cancel(testMogakko.getId(), user.getId());
            countDownLatch.countDown();
        }));

        if (!countDownLatch.await(1000, TimeUnit.MILLISECONDS)) {
            Assertions.fail();
        }

        // then
        List<Participant> participants = participantRepository.findAllByMogakkoId(
            testMogakko.getId());

        assertThat(participants).hasSize(1);
    }

    @Test
    @DisplayName("특정 유저가 특정 모각코에 참여하고 있는지 아닌지 확인할 수 있다.")
    void success_check_whether_user_participate_in_mogakko() {
        // when
        ParticipationCheckingDto checkedTrueDto = mogakkoParticipationService.check(testMogakko.getId(),
            testCreator.getId());
        ParticipationCheckingDto checkedFalseDto = mogakkoParticipationService.check(testMogakko.getId(),
            testCreator.getId() + 1L);

        // then
        assertThat(checkedTrueDto.isParticipated()).isTrue();
        assertThat(checkedFalseDto.isParticipated()).isFalse();
    }
}
