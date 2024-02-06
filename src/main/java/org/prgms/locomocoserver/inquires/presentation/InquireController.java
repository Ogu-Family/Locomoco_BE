package org.prgms.locomocoserver.inquires.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.inquires.application.InquireService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InquireController {
    private final InquireService inquireService;


}
