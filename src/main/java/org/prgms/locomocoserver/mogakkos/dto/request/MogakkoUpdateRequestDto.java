package org.prgms.locomocoserver.mogakkos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record MogakkoUpdateRequestDto(@Schema(description = "수정하려는 유저 id", example = "1") Long creatorId,
                                      @Schema(description = "모각코 글 제목", example = "이게 무슨 일이야 이렇게 좋은 날에") String title,
                                      @Schema(description = "모각코 장소", example = "서울 서초구 강남대로 327") String location,
                                      @Schema(description = "모각코 시작 시간") LocalDateTime startTime,
                                      @Schema(description = "모각코 종료 시간") LocalDateTime endTime,
                                      @Schema(description = "모각코 모집 데드라인 시간") LocalDateTime deadline,
                                      @Schema(description = "최대 참여자 수", example = "2") int maxParticipants,
                                      @Schema(description = "모각코 글 내용", example = "난... ㄱ ㅏ끔... 눈물을 흘린 ㄷ ㅏ ...") String content,
                                      @Schema(description = "선택된 태그 id 모음") List<Long> tags) {

}
