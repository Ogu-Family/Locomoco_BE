package org.prgms.locomocoserver.inquires.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.inquires.domain.InquireRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquireService {
    private final InquireRepository inquireRepository;


}
