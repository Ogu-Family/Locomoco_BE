package org.prgms.locomocoserver.mogakkos.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MogakkoController {

    private final MogakkoService mogakkoService;
}
