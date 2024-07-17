package org.prgms.locomocoserver.report.dto.request;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public record ReplyReportUpdateRequest(
        @Nonnull
        Long reporterId,
        @NotBlank
        String content
) {
}
