package org.prgms.locomocoserver.tags.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.tags.domain.TagRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
}
