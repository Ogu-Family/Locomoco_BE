package org.prgms.locomocoserver.global.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record Results<T>(@Schema(description = "값 리스트") List<T> data) {

}
