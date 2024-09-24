package org.prgms.locomocoserver.mogakkos.dto.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record SearchParameterDto(String titleAndContent,
                                 String location,
                                 String nickname,
                                 List<Long> tagIds) {

    public SearchParameterDto(String titleAndContent, String location,
        String nickname, List<Long> tagIds) {
        this.titleAndContent = isNullOrBlank(titleAndContent) ? null : titleAndContent.strip();
        this.location = isNullOrBlank(location) ? null : location.strip();
        this.nickname = isNullOrBlank(nickname) ? null : nickname.strip();
        this.tagIds = tagIds;
    }

    private static boolean isNullOrBlank(String str) {
        return Objects.isNull(str) || str.isBlank();
    }

    public boolean isInvalidInput(int size) {
        boolean ret = false;

        List<String> validationFields = new ArrayList<>();
        validationFields.add(titleAndContent);
        validationFields.add(location);
        validationFields.add(nickname);

        for (String field : validationFields) {
            ret = ret || (Objects.nonNull(field) && field.length() < size);
        }

        return ret;
    }
}
