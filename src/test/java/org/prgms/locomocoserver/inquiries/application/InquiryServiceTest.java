package org.prgms.locomocoserver.inquiries.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.prgms.locomocoserver.global.TestFactory.createMogakko;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prgms.locomocoserver.global.TestFactory;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;
import org.prgms.locomocoserver.inquiries.domain.InquiryRepository;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryCreateRequestDto;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryUpdateRequestDto;
import org.prgms.locomocoserver.inquiries.dto.response.InquiryResponseDto;
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

        User user = User.builder().nickname("생성자").email("cho@gmail.com").birth(LocalDate.EPOCH)
            .gender(Gender.MALE).temperature(36.5).provider("github")
            .build();
        Mogakko mogakko = createMogakko(user);
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
        Inquiry updatedInquiry = Inquiry.builder().user(user).mogakko(null).content(updateContent)
            .build();

        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(updatedInquiry, "id", inquiryId);

        when(inquiryRepository.findByIdAndDeletedAtIsNull(inquiryId)).thenReturn(
            Optional.of(preInquiry));
        when(inquiryRepository.save(preInquiry)).thenReturn(updatedInquiry);

        // when
        InquiryUpdateResponseDto responseDto = inquiryService.update(inquiryId, requestDto);

        // then
        assertThat(responseDto.id()).isEqualTo(inquiryId);

        verify(inquiryRepository).save(preInquiry);
    }

    @Test
    @DisplayName("작성한 유저가 자신의 문의를 삭제할 수 있다.")
    void success_delete_inquiry_given_valid_user() {
        // given
        long inquiryId = 1L;
        long writerId = 2L;
        User writer = TestFactory.createUser();
        Mogakko mogakko = createMogakko(writer);
        Inquiry inquiry = Inquiry.builder().user(writer).mogakko(mogakko).content("content")
            .build();

        setId(writer, writerId);

        when(inquiryRepository.findByIdAndDeletedAtIsNull(inquiryId)).thenReturn(
            Optional.of(inquiry));

        // when
        inquiryService.delete(inquiryId, writerId);

        // then
        assertThat(inquiry.getDeletedAt()).isNotNull();

        verify(inquiryRepository, times(1)).findByIdAndDeletedAtIsNull(inquiryId);
    }

    @Test
    @DisplayName("작성하지 않은 유저가 다른 작성자 문의를 삭제할 수 없다.")
    void fail_delete_inquiry_given_invalid_user() {
        // given
        long inquiryId = 1L;
        long writerId = 2L;
        User writer = TestFactory.createUser();
        Mogakko mogakko = createMogakko(writer);
        Inquiry inquiry = Inquiry.builder().user(writer).mogakko(mogakko).content("content")
            .build();

        setId(writer, writerId);

        when(inquiryRepository.findByIdAndDeletedAtIsNull(inquiryId)).thenReturn(
            Optional.of(inquiry));

        // when, then
        long invalidWriterId = 253L;

        assertThatThrownBy(() -> inquiryService.delete(inquiryId, invalidWriterId))
            .isInstanceOf(RuntimeException.class)
            .hasFieldOrPropertyWithValue("message", "문의를 작성한 유저가 아닙니다.");

        verify(inquiryRepository, times(1)).findByIdAndDeletedAtIsNull(inquiryId);
    }

    @Test
    @DisplayName("모각코와 연관된 모든 삭제되지 않은 문의를 불러올 수 있다.")
    void success_find_all_inquiries_except_deleted() {
        // given
        long inquiryWriterId = 1L;
        long mogakkoId = 2L;
        long mogakkoWriterId = 3L;
        int pageSize = 20;
        String notDeletedContent = "작성된 문의";
        String deletedContent = "삭제된 문의";
        User mogakkoWriter = TestFactory.createUser();
        User inquiryWriter = TestFactory.createUser();
        Mogakko mogakko = createMogakko(mogakkoWriter);

        setId(inquiryWriter, inquiryWriterId);
        setId(mogakko, mogakkoId);
        setId(mogakkoWriter, mogakkoWriterId);

        Inquiry inquiryNotDeleted = Inquiry.builder().user(inquiryWriter).mogakko(mogakko)
            .content(notDeletedContent)
            .build();
        Inquiry inquiryDeleted = Inquiry.builder().user(inquiryWriter).mogakko(mogakko).content(
                deletedContent)
            .build();
        inquiryDeleted.delete();

        when(inquiryRepository.findAllByMogakko(Long.MAX_VALUE, mogakkoId, pageSize)).thenReturn(
            List.of(inquiryNotDeleted));

        // when
        List<InquiryResponseDto> inquiries = inquiryService.findAll(Long.MAX_VALUE, mogakkoId, null);

        // then
        assertThat(inquiries).hasSize(1);
        assertThat(inquiries.get(0).content()).isEqualTo(notDeletedContent);

        verify(inquiryRepository, times(1)).findAllByMogakko(Long.MAX_VALUE, mogakkoId, pageSize);
    }

    @Test
    @DisplayName("모든 문의를 불러올 수 있다.")
    void success_find_all_inquiries() {
        // given
        long inquiryWriterId = 1L;
        long mogakkoId = 2L;
        long mogakkoWriterId = 3L;
        int pageSize = 20;
        String notDeletedContent = "작성된 문의";
        String deletedContent = "삭제된 문의";
        User mogakkoWriter = TestFactory.createUser();
        User inquiryWriter = TestFactory.createUser();
        Mogakko mogakko = createMogakko(mogakkoWriter);

        setId(inquiryWriter, inquiryWriterId);
        setId(mogakko, mogakkoId);
        setId(mogakkoWriter, mogakkoWriterId);

        Inquiry inquiryNotDeleted = Inquiry.builder().user(inquiryWriter).mogakko(mogakko)
            .content(notDeletedContent)
            .build();
        Inquiry inquiryDeleted = Inquiry.builder().user(inquiryWriter).mogakko(mogakko).content(
                deletedContent)
            .build();
        inquiryDeleted.delete();

        when(inquiryRepository.findAll(Long.MAX_VALUE, pageSize)).thenReturn(
            List.of(inquiryNotDeleted, inquiryDeleted));

        // when
        List<InquiryResponseDto> inquiries = inquiryService.findAll(Long.MAX_VALUE, null, null);

        // then
        assertThat(inquiries).hasSize(2);

        verify(inquiryRepository, times(1)).findAll(Long.MAX_VALUE, pageSize);
    }

    @Test
    @DisplayName("해당 유저와 연관된 모든 문의를 불러올 수 있다.")
    void success_find_all_inquiries_related_to_a_user() {
        // given
        long inquiryWriterId = 1L;
        long mogakkoId = 2L;
        long mogakkoWriterId = 3L;
        int pageSize = 20;
        String notDeletedContent = "작성된 문의";
        String deletedContent = "삭제된 문의";
        User mogakkoWriter = TestFactory.createUser();
        User inquiryWriter = TestFactory.createUser();
        Mogakko mogakko = createMogakko(mogakkoWriter);

        setId(inquiryWriter, inquiryWriterId);
        setId(mogakko, mogakkoId);
        setId(mogakkoWriter, mogakkoWriterId);

        Inquiry inquiryNotDeleted = Inquiry.builder().user(inquiryWriter).mogakko(mogakko)
            .content(notDeletedContent)
            .build();
        Inquiry inquiryDeleted = Inquiry.builder().user(inquiryWriter).mogakko(mogakko).content(
                deletedContent)
            .build();
        inquiryDeleted.delete();

        when(inquiryRepository.findAllByUser(Long.MAX_VALUE, inquiryWriterId, pageSize)).thenReturn(
            List.of(inquiryNotDeleted, inquiryDeleted));

        // when
        List<InquiryResponseDto> inquiries = inquiryService.findAll(Long.MAX_VALUE, null, inquiryWriterId);

        // then
        assertThat(inquiries).hasSize(2);

        verify(inquiryRepository, times(1)).findAllByUser(Long.MAX_VALUE, inquiryWriterId, pageSize);
    }

    @Test
    @DisplayName("특정 모각코, 유저와 관련된 모든 문의를 불러올 수 있다.")
    void success_find_all_inquiries_related_to_mogakko_and_user() {
        // given
        long inquiryWriterId = 1L;
        long mogakko1Id = 2L;
        long mogakko2Id = 4L;
        long mogakkoWriterId = 3L;
        int pageSize = 20;
        String content = "작성된 문의";
        User mogakkoWriter = TestFactory.createUser();
        User inquiryWriter = TestFactory.createUser();
        Mogakko mogakko1 = createMogakko(mogakkoWriter);
        Mogakko mogakko2 = createMogakko(mogakkoWriter);

        setId(inquiryWriter, inquiryWriterId);
        setId(mogakko1, mogakko1Id);
        setId(mogakko2, mogakko2Id);
        setId(mogakkoWriter, mogakkoWriterId);

        Inquiry inquiry = Inquiry.builder().user(inquiryWriter).mogakko(mogakko1)
            .content(content)
            .build();

        when(inquiryRepository.findAllByMogakkoAndUser(Long.MAX_VALUE, mogakko1Id, inquiryWriterId, pageSize)).thenReturn(
            List.of(inquiry));

        // when
        List<InquiryResponseDto> inquiries = inquiryService.findAll(Long.MAX_VALUE, mogakko1Id, inquiryWriterId);

        // then
        assertThat(inquiries).hasSize(1);

        verify(inquiryRepository, times(1)).findAllByMogakkoAndUser(Long.MAX_VALUE, mogakko1Id, inquiryWriterId, pageSize);
    }

    static void setId(Object o, long id) {
        ReflectionTestUtils.setField(o, "id", id);
    }
}
