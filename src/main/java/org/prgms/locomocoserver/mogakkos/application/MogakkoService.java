package org.prgms.locomocoserver.mogakkos.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MogakkoService {

    private final MogakkoRepository mogakkoRepository;


}
