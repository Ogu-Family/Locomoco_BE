package org.prgms.locomocoserver.mogakkos.dto.request;

import java.util.List;
import java.util.Objects;

public record SearchParameterDto(String totalSearch,
                                 String titleAndContent,
                                 String location,
                                 String nickname,
                                 List<Long> tagIds) {

    public SearchParameterDto(String totalSearch, String titleAndContent, String location,
        String nickname, List<Long> tagIds) {
        this.totalSearch = Objects.isNull(totalSearch) ? null : totalSearch.strip();
        this.titleAndContent = Objects.isNull(titleAndContent) ? null : titleAndContent.strip();
        this.location = Objects.isNull(location) ? null : location.strip();
        this.nickname = Objects.isNull(nickname) ? null : nickname.strip();
        this.tagIds = tagIds;
    }
}
