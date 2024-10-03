package org.prgms.locomocoserver.mogakkos.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocation;
import org.prgms.locomocoserver.mogakkos.domain.location.MogakkoLocationRepository;
import org.prgms.locomocoserver.mogakkos.domain.vo.AddressInfo;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchConditionDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchParameterDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.prgms.locomocoserver.global.TestFactory.*;

@SpringBootTest
class MogakkoServiceFindAllTest {
    @Autowired
    private MogakkoService mogakkoService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private MogakkoLocationRepository mogakkoLocationRepository;
    @Autowired
    private ImageRepository imageRepository;

    private User creator;
    private List<MogakkoLocation> tempLocations;

    @BeforeEach
    void setUp() {
        creator = createUser();
        Image image = creator.getProfileImage();
        imageRepository.save(image);
        userRepository.save(creator);

        AddressInfo addressInfo1 = AddressInfo.builder().address("주소").city("법정동").hCity("행정동").build();
        MogakkoLocation tempLocation1 = MogakkoLocation.builder().longitude(12.12314d).latitude(127.23522d).addressInfo(addressInfo1).build();
        AddressInfo addressInfo2 = AddressInfo.builder().address("소주").city("동정법").hCity("동정행").build();
        MogakkoLocation tempLocation2 = MogakkoLocation.builder().longitude(12.12314d).latitude(127.23522d).addressInfo(addressInfo2).build();

        tempLocations = mogakkoLocationRepository.saveAll(List.of(tempLocation1, tempLocation2));
    }

    @AfterEach
    void tearDown() {
        mogakkoLocationRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @DisplayName("제목, 내용에 대한 검색을 제대로 수행할 수 있다.")
    void success_find_mogakko_fillterd_by_title_and_content() throws Exception {
        // given
        Mogakko mogakko1 = createMogakko(creator);
        Mogakko mogakko2 = createMogakko(creator);

        changeMogakkoInfo(mogakko1, "모코", "");
        changeMogakkoInfo(mogakko2, "조금 더 긴 제목", "모코");

        tempLocations.get(0).updateMogakko(mogakko1);
        tempLocations.get(1).updateMogakko(mogakko2);

        mogakkoRepository.saveAll(List.of(mogakko1, mogakko2));
        mogakkoLocationRepository.saveAll(tempLocations);

        int pageSize = 10;
        int offset = 0;
        LocalDateTime searchTime = LocalDateTime.now();
        List<String> search = List.of("제목", "모코");

        // when
        List<List<MogakkoSimpleInfoResponseDto>> dtos = search.stream().map(s ->
                mogakkoService.findAll(
                        new SearchParameterDto(s, null, null, null),
                        new SearchConditionDto(searchTime, offset, pageSize))
                )
                .toList();

        // then
        List<MogakkoSimpleInfoResponseDto> dto1 = dtos.get(0);
        List<MogakkoSimpleInfoResponseDto> dto2 = dtos.get(1);

        assertThat(dto1).hasSize(1);
        assertThat(dto2).hasSize(2);
    }

    void changeMogakkoInfo(Mogakko mogakko, String title, String content) throws Exception {
        Field titleField = mogakko.getClass().getDeclaredField("title");
        Field contentField = mogakko.getClass().getDeclaredField("content");
        titleField.setAccessible(true);
        contentField.setAccessible(true);

        if (title != null && !title.isBlank())
            titleField.set(mogakko, title);

        if (content != null)
            contentField.set(mogakko, content);
    }
}