package org.prgms.locomocoserver.mogakkos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.prgms.locomocoserver.global.TestFactory.createMogakko;
import static org.prgms.locomocoserver.global.TestFactory.createUser;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prgms.locomocoserver.mogakkos.application.recommend.MidpointRecommendService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.Midpoint;
import org.prgms.locomocoserver.mogakkos.domain.midpoint.MidpointRepository;
import org.prgms.locomocoserver.mogakkos.domain.participants.Participant;
import org.prgms.locomocoserver.mogakkos.domain.participants.ParticipantRepository;
import org.prgms.locomocoserver.mogakkos.dto.response.MidpointDto;
import org.prgms.locomocoserver.user.domain.User;

@ExtendWith(MockitoExtension.class)
class MidpointServiceTest {
    @InjectMocks
    private MidpointService midpointService;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private MidpointRecommendService midpointRecommendService;
    @Mock
    private MidpointRepository midpointRepository;
    @Mock
    private MogakkoRepository mogakkoRepository;

    @Test
    @DisplayName("모각코 id를 받고 해당 모각코 참여자들의 중간 위치를 추천해줄 수 있다.")
    void success_recommend_midpoint() {
        // given
        long mogakkoId = 1L;
        double creatorLat = 28.123142d;
        double creatorLon = 127.25342d;
        double userLat = 27.89123412d;
        double userLon = 127.452423d;

        User creator = createUser();
        User user = createUser();
        Mogakko mogakko = createMogakko(creator);

        int participantSize = 2;
        String placeName = "카페";
        String address = "서울특별시 강동구 양재대로 1369";
        String city = "서울특별시 강동구 둔촌동";

        Midpoint midpoint = Midpoint.builder().latitude((creatorLat + userLat) / participantSize)
            .longitude((creatorLon + userLon) / participantSize).mogakko(mogakko).placeName(placeName)
            .city(city).address(address).build();

        Participant participantCreator = Participant.builder().latitude(creatorLat).longitude(creatorLon)
            .mogakko(mogakko).user(creator).build();
        Participant participantUser = Participant.builder().latitude(userLat).longitude(userLon)
            .mogakko(mogakko).user(user).build();

        when(midpointRepository.findByMogakkoId(mogakkoId)).thenReturn(Optional.empty());
        when(participantRepository.findAllByMogakkoId(mogakkoId))
            .thenReturn(List.of(participantCreator, participantUser));
        when(midpointRecommendService.recommend(anyList())).thenReturn(midpoint);
        when(mogakkoRepository.findById(mogakkoId)).thenReturn(Optional.of(mogakko));

        // when
        MidpointDto midpointDto = midpointService.recommend(mogakkoId);

        // then
        assertThat(midpointDto.longitude()).isEqualTo((creatorLon + userLon) / participantSize);
        assertThat(midpointDto.latitude()).isEqualTo((creatorLat + userLat) / participantSize);
        assertThat(midpointDto.name()).isEqualTo(placeName);
        assertThat(midpointDto.address()).isEqualTo(address);

        verify(midpointRepository).findByMogakkoId(mogakkoId);
        verify(participantRepository).findAllByMogakkoId(mogakkoId);
        verify(midpointRecommendService).recommend(anyList());
        verify(mogakkoRepository).findById(mogakkoId);
    }

    @Test
    @DisplayName("참여자 중 한 명이라도 출발 위치가 없으면 중간 지점을 얻을 수 없다.")
    void fail_recommend_midpoint_given_no_location_info_exists() {
        // given
        long mogakkoId = 1L;
        double creatorLat = 28.123142d;
        double creatorLon = 127.25342d;

        User creator = createUser();
        User user = createUser();
        Mogakko mogakko = createMogakko(creator);

        Participant participantCreator = Participant.builder().latitude(creatorLat).longitude(creatorLon)
            .mogakko(mogakko).user(creator).build();
        Participant participantUser = Participant.builder().mogakko(mogakko).user(user).build();

        when(midpointRepository.findByMogakkoId(mogakkoId)).thenReturn(Optional.empty());
        when(participantRepository.findAllByMogakkoId(mogakkoId))
            .thenReturn(List.of(participantCreator, participantUser));

        // when then
        assertThatThrownBy(() -> midpointService.recommend(mogakkoId))
            .isInstanceOf(RuntimeException.class); // TODO: 중간지점 예외 반환

        verify(midpointRepository).findByMogakkoId(mogakkoId);
        verify(participantRepository).findAllByMogakkoId(mogakkoId);
    }
}
