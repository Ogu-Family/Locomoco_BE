package org.prgms.locomocoserver.inquiries.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.locomocoserver.global.TestFactory;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

class InquiryTest {

    @Test
    @DisplayName("문의를 업데이트 할 수 있다.")
    void success_update_content() {
        // given
        final String preContent = "업데이트 전 내용";
        final String postContent = "업데이트 후 내용";

        User user = TestFactory.createUser();
        Mogakko mogakko = TestFactory.createMogakko(user);
        Inquiry inquiry = Inquiry.builder().user(user).mogakko(mogakko).content(preContent)
            .build();

        // when
        inquiry.updateInfo(postContent);

        // then
        assertThat(inquiry.getContent()).isEqualTo(postContent);
        assertThat(inquiry.getUser()).isSameAs(user);
        assertThat(inquiry.getMogakko()).isSameAs(mogakko);
    }

    @Test
    @DisplayName("200자 초과된 내용은 업데이트 될 수 없다.")
    void fail_update_content_given_content_more_than_200_letter() throws Exception {
        // given
        Field maximumContentLengthField = Inquiry.class.getDeclaredField("MAXIMUM_CONTENT_LENGTH");
        maximumContentLengthField.setAccessible(true);
        int maximumContentLength = (int) maximumContentLengthField.get(Inquiry.class);

        String originalContent = "temp";
        String updateContent = "a".repeat(maximumContentLength + 1);

        User user = TestFactory.createUser();
        Mogakko mogakko = TestFactory.createMogakko(user);
        Inquiry inquiry = Inquiry.builder().user(user).mogakko(mogakko).content(originalContent).build();

        // when, then
        assertThatThrownBy(
            () -> inquiry.updateInfo(updateContent)).isInstanceOf(
            RuntimeException.class).hasFieldOrPropertyWithValue("message",
            "문의 내용은 " + maximumContentLength + "자를 초과할 수 없습니다.");
    }
}
