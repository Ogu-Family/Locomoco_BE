package org.prgms.locomocoserver.inquiries.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;
import org.prgms.locomocoserver.inquiries.domain.InquiryRepository;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryCreateRequestDto;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryUpdateRequestDto;
import org.prgms.locomocoserver.inquiries.dto.response.InquiryUpdateResponseDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {

    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private MogakkoRepository mogakkoRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private InquiryService inquiryService;

    @Test
    @DisplayName("새 문의 생성이 제대로 되는지 확인한다")
    void success_save_new_inquiry() {
        // given
        long userid = 1L;
        long mogakkoId = 3L;
        String content = "tempContent";
        InquiryCreateRequestDto requestDto = new InquiryCreateRequestDto(userid, mogakkoId,
            content);

        User user = User.builder().nickname("생성자").email("cho@gmail.com").birth(LocalDate.EPOCH).gender(Gender.MALE).temperature(36.5).provider("github")
            .build();
        Mogakko mogakko = Mogakko.builder().title("title").content("제곧내").views(20).likeCount(10)
            .startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(2))
            .deadline(LocalDateTime.now().plusHours(1)).creator(user)
            .build();
        Inquiry inquiry = Inquiry.builder().user(user).mogakko(mogakko).content(content)
            .build();

        when(userRepository.findById(userid)).thenReturn(Optional.of(user));
        when(mogakkoRepository.findById(mogakkoId)).thenReturn(Optional.of(mogakko));
        when(inquiryRepository.save(any(Inquiry.class))).thenReturn(inquiry);

        // when
        inquiryService.save(requestDto);

        // then
        verify(inquiryRepository).save(any(Inquiry.class));
    }

    @Test
    @DisplayName("이미 존재하는 문의를 수정할 수 있다")
    void success_update_inquiry() {
        // given
        long inquiryId = 3L;
        long userId = 1L;
        String updateContent = "updateContent";
        InquiryUpdateRequestDto requestDto = new InquiryUpdateRequestDto(userId, updateContent);

        User user = User.builder().nickname("생성자").email("cho@gmail.com")
            .birth(LocalDate.EPOCH).gender(Gender.MALE).temperature(36.5).provider("github")
            .build();
        Inquiry preInquiry = Inquiry.builder().user(user).mogakko(null).content("content").build();
        Inquiry updatedInquiry = Inquiry.builder().user(user).mogakko(null).content(updateContent).build();

        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(updatedInquiry, "id", inquiryId);

        when(inquiryRepository.findByIdAndDeletedAtIsNull(inquiryId)).thenReturn(Optional.of(preInquiry));
        when(inquiryRepository.save(preInquiry)).thenReturn(updatedInquiry);

        // when
        InquiryUpdateResponseDto responseDto = inquiryService.update(inquiryId, requestDto);

        // then
        assertThat(responseDto.id()).isEqualTo(inquiryId);

        verify(inquiryRepository).save(preInquiry);
    }
}
