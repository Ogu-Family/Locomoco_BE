package org.prgms.locomocoserver.report.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReplyReportUpdateRequest(
        @NotBlank
        String content
) {
}
