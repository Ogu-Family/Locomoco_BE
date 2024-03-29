package org.prgms.locomocoserver.tags.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.tags.application.TagService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TagController {

    private final TagService tagService;
}
