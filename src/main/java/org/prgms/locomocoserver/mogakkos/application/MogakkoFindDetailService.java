package org.prgms.locomocoserver.mogakkos.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MogakkoFindDetailService {

    private final MogakkoRepository mogakkoRepository;

    @Transactional
    public void increaseViews(Mogakko foundMogakko) {
        mogakkoRepository.increaseViews(foundMogakko);
    }
}
