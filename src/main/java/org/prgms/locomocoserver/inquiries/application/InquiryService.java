package org.prgms.locomocoserver.inquiries.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;
import org.prgms.locomocoserver.inquiries.domain.InquiryRepository;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryCreateRequestDto;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryDeleteRequestDto;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryUpdateRequestDto;
import org.prgms.locomocoserver.inquiries.dto.response.InquiryUpdateResponseDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final MogakkoRepository mogakkoRepository;
    private final UserRepository userRepository;

    public void save(InquiryCreateRequestDto requestDto) {
        User user = userRepository.findById(requestDto.userid())
            .orElseThrow(RuntimeException::new); // TODO: 유저 예외 반환
        Mogakko mogakko = mogakkoRepository.findById(requestDto.mogakkoId())
            .orElseThrow(RuntimeException::new);// TODO: 모각코 예외 반환

        Inquiry inquiry = Inquiry.builder().mogakko(mogakko).user(user).content(requestDto.content())
            .build();

        inquiryRepository.save(inquiry);
    }

    public InquiryUpdateResponseDto update(Long id, InquiryUpdateRequestDto requestDto) {
        Inquiry foundInquiry = inquiryRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(RuntimeException::new);// TODO: 문의 예외 반환

        validateUser(requestDto.userId(), foundInquiry);

        foundInquiry.updateInfo(requestDto.content());
        Inquiry updatedInquiry = inquiryRepository.save(foundInquiry);

        return InquiryUpdateResponseDto.create(updatedInquiry);
    }

    @Transactional
    public void delete(Long id, InquiryDeleteRequestDto requestDto) {
        Inquiry inquiry = inquiryRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(RuntimeException::new); // TODO: 문의 예외 반환

        validateUser(requestDto.userId(), inquiry);

        inquiry.delete();
    }

    private static void validateUser(Long inquiryUserId, Inquiry foundInquiry) {
        boolean isSameUser = foundInquiry.getUser().getId().equals(inquiryUserId);

        if (!isSameUser) {
            throw new RuntimeException(); // TODO: 유저 예외 반환
        }
    }
}
