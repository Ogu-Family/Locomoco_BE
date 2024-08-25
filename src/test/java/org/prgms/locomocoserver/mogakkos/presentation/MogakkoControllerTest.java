package org.prgms.locomocoserver.mogakkos.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.global.exception.GlobalExceptionHandler;
import org.prgms.locomocoserver.global.filter.AuthenticationFilter;
import org.prgms.locomocoserver.global.filter.CorsFilter;
import org.prgms.locomocoserver.global.filter.ExceptionHandlerFilter;
import org.prgms.locomocoserver.image.dto.ImageDto;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.application.SearchType;
import org.prgms.locomocoserver.mogakkos.dto.CursorDto;
import org.prgms.locomocoserver.mogakkos.dto.LocationInfoDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.request.MogakkoUpdateRequestDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoCreateResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoDetailResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoInfoDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoParticipantDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoUpdateResponseDto;
import org.prgms.locomocoserver.user.dto.response.UserBriefInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(controllers = MogakkoController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { CorsFilter.class, AuthenticationFilter.class, ExceptionHandlerFilter.class, GlobalExceptionHandler.class }))
class MogakkoControllerTest {
    private static final long OFFSET = 0L;
    private static final int PAGE_SIZE = 20;
    private static final String API_VERSION = "/api/v1";

    @MockBean
    private MogakkoService mogakkoService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("검색 정보를 담아 검색 요청을 한 뒤, 검색된 모각코 목록을 받아올 수 있다.")
    void success_find_all_mogakkos() throws Exception {
        // given
        String search = "ISD";
        SearchType searchType = SearchType.TITLE_CONTENT;
        List<Long> tags = List.of(1L, 2L);

        MogakkoSimpleInfoResponseDto responseDto1 = new MogakkoSimpleInfoResponseDto(1L, "임시1",
            200L, 2, LocalDateTime.now(), LocalDateTime.now(), 5, 3,
            new LocationInfoDto("주소1", 26.2442123d, 128.3422352d, "도시1", "행정동1"), List.copyOf(tags));
        MogakkoSimpleInfoResponseDto responseDto2 = new MogakkoSimpleInfoResponseDto(2L, "임시2",
            1200L, 22, LocalDateTime.now(), LocalDateTime.now(), 10, 4,
            new LocationInfoDto("주소2", 26.2642123d, 128.3622352d, "도시1", "행정동2"), List.copyOf(tags));

        when(mogakkoService.findAllByFilter(tags, search, searchType, PAGE_SIZE, OFFSET))
            .thenReturn(List.of(responseDto1, responseDto2));

        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("offset", String.valueOf(OFFSET));
        paramMap.add("search", search);
        paramMap.add("searchType", searchType.name());
        paramMap.add("pageSize", String.valueOf(PAGE_SIZE));
        tags.forEach(tId -> paramMap.add("tags", tId.toString()));

        // when, then

        mockMvc.perform(get(API_VERSION + "/mogakko/map")
                .params(paramMap)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].id").isNumber())
            .andExpect(jsonPath("$.data[0].title").value("임시1"))
            .andDo(print());
    }

    @Test
    @DisplayName("모각코 생성 정보를 담아 생성을 요청한 후, 생성된 모각코 정보를 받아올 수 있다.")
    void success_create_mogakko() throws Exception {
        // given
        MogakkoCreateRequestDto requestDto = new MogakkoCreateRequestDto(1L, "타이틀",
            new LocationInfoDto("주소1", 27.252453d, 127.5453234d, "도시1", "행정동1"), LocalDateTime.now(),
            LocalDateTime.now(), LocalDateTime.now(), 10, "내용", List.of(1L, 2L));

        when(mogakkoService.save(requestDto)).thenReturn(new MogakkoCreateResponseDto(10L));

        // when then
        mockMvc.perform(post(API_VERSION + "/mogakko/map")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andDo(print());
    }

    @Test
    @DisplayName("자세한 정보를 보고싶은 모각코 정보를 담아 요청하고 해당 모각코의 자세한 정보를 받아올 수 있다.")
    void success_find_a_mogakko_in_details() throws Exception {
        // given
        long mogakkoId = 25L;
        long creatorId = 1L;

        UserBriefInfoDto creatorInfo = new UserBriefInfoDto(creatorId, "이름1", new ImageDto(2L, "path"));
        List<MogakkoParticipantDto> participants = List.of(new MogakkoParticipantDto(10L, "이름2", null));
        LocationInfoDto locationInfo = new LocationInfoDto("주소1", 27.2342342d, 126.142332d, "도시1", "행정동1");
        MogakkoInfoDto mogakkoInfo = new MogakkoInfoDto(mogakkoId, "제목1", "내용1", 10, LocalDateTime.now(),
            LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
            locationInfo, 10, 10, List.of(1L, 2L));

        MogakkoDetailResponseDto responseDto = new MogakkoDetailResponseDto(creatorInfo, participants, mogakkoInfo);

        when(mogakkoService.findDetail(mogakkoId)).thenReturn(responseDto);

        // when then
        mockMvc.perform(get(API_VERSION + "/mogakko/map/{id}", mogakkoId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.creatorInfo.userId").value(creatorId))
            .andExpect(jsonPath("$.participants.length()").value(1))
            .andExpect(jsonPath("$.mogakkoInfo.mogakkoId").value(mogakkoId))
            .andDo(print());
    }

    @Test
    @DisplayName("특정 모각코 수정 정보를 보내 수정을 요청하고 수정된 모각코 정보를 받아올 수 있다.")
    void success_update_mogakko() throws Exception {
        // given
        MogakkoUpdateRequestDto requestDto = new MogakkoUpdateRequestDto(1L, "수정 모각코", null, null, null,
            null, 3, null, List.of(1L, 2L));

        long updateMogakkoId = 2L;
        when(mogakkoService.update(requestDto, updateMogakkoId))
            .thenReturn(new MogakkoUpdateResponseDto(updateMogakkoId));

        // when then
        mockMvc.perform(patch(API_VERSION + "/mogakko/map/{id}", updateMogakkoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(updateMogakkoId))
            .andDo(print());
    }

    @Test
    @DisplayName("특정 모각코 삭제를 요청하고 응답을 받을 수 있다.")
    void success_delete_mogakko() throws Exception {
        // given
        long deleteMogakkoId = 1L;

        // when then
        mockMvc.perform(delete(API_VERSION + "/mogakko/map/{id}", deleteMogakkoId))
            .andExpect(status().isNoContent())
            .andDo(print());
    }
}
