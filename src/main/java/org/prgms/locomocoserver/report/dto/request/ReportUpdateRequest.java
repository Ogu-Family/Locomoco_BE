package org.prgms.locomocoserver.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record ReportUpdateRequest(
        @NonNull
        Long reportId,
        @NotBlank
        String content
) {
}
