package org.prgms.locomocoserver.global.common.dto;

public record SlackErrorAlertDto(String requestUrl,
                                 String httpMethod,
                                 String errorMessage) {

}
