package org.prgms.locomocoserver.inquiries.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;
import org.prgms.locomocoserver.inquiries.domain.InquiryRepository;
import org.prgms.locomocoserver.inquiries.dto.request.InquiryCreateRequestDto;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.stereotype.Service;

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
}
