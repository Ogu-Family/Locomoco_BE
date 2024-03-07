package org.prgms.locomocoserver.report.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReportUpdateRequest(
        @NotBlank
        String content
) {
}
