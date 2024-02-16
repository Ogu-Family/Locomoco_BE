package org.prgms.locomocoserver.inquiries.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.inquiries.domain.InquiryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;


}
