package org.prgms.locomocoserver.report.application;

import org.junit.jupiter.api.*;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.global.TestFactory;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.prgms.locomocoserver.inquiries.domain.Inquiry;
import org.prgms.locomocoserver.inquiries.domain.InquiryRepository;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.MogakkoRepository;
import org.prgms.locomocoserver.replies.domain.Reply;
import org.prgms.locomocoserver.replies.domain.ReplyRepository;
import org.prgms.locomocoserver.report.domain.ReplyReportRepository;
import org.prgms.locomocoserver.report.dto.ReplyReportDto;
import org.prgms.locomocoserver.report.dto.request.ReplyReportCreateRequest;
import org.prgms.locomocoserver.report.dto.request.ReplyReportUpdateRequest;
import org.prgms.locomocoserver.report.exception.ReportException;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReplyReportServiceTest {

    @Autowired
    private ReplyReportService replyReportService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private MogakkoRepository mogakkoRepository;
    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private ReplyReportRepository replyReportRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private User user;
    private Reply reply;
    private Inquiry inquiry;
    private Mogakko mogakko;

    @BeforeEach
    void setUp() {
        User newUser = TestFactory.createUser();
        imageRepository.save(newUser.getProfileImage());
        user = userRepository.save(newUser);

        mogakko = mogakkoRepository.save(TestFactory.createMogakko(user));
        inquiry = inquiryRepository.save(TestFactory.createInquiry(user, mogakko));
        reply = replyRepository.save(TestFactory.createReply(user, inquiry));
    }

    @AfterEach
    void tearDown() {
        replyReportRepository.deleteAll();
        replyRepository.deleteAll();
        inquiryRepository.deleteAll();
        chatRoomRepository.deleteAll();
        mogakkoRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글을 신고할 수 있습니다.")
    void testCreateReplyReport() {
        ReplyReportCreateRequest request = new ReplyReportCreateRequest(reply.getId(), user.getId(), "Test content");
        ReplyReportDto replyReportDto = replyReportService.create(request);

        assertNotNull(replyReportDto);
        assertEquals("Test content", replyReportDto.content());
    }

    @Test
    @DisplayName("댓글을 신고한 내용을 수정할 수 있습니다.")
    void testUpdateReplyReport() {
        ReplyReportCreateRequest createRequest = new ReplyReportCreateRequest(reply.getId(), user.getId(), "Initial content");
        ReplyReportDto createdReport = replyReportService.create(createRequest);

        ReplyReportUpdateRequest updateRequest = new ReplyReportUpdateRequest(user.getId(), "Updated content");
        ReplyReportDto updatedReport = replyReportService.update(createdReport.replyId(), updateRequest);

        assertNotNull(updatedReport);
        assertEquals("Updated content", updatedReport.content());
    }

    @Test
    @DisplayName("댓글 신고 목록을 조회할 수 있습니다.")
    void testGetReplyReport() {
        ReplyReportCreateRequest request = new ReplyReportCreateRequest(reply.getId(), user.getId(), "Test content");
        replyReportService.create(request);

        List<ReplyReportDto> replyReports = replyReportService.getAllReplyReports(0L, 10);

        assertNotNull(replyReports);
        assertEquals(1, replyReports.size());
    }

    @Test
    @DisplayName("댓글 신고한 내역을 삭제할 수 있습니다.")
    void testDeleteReplyReport() {
        ReplyReportCreateRequest request = new ReplyReportCreateRequest(reply.getId(), user.getId(), "Test content");
        ReplyReportDto createdReport = replyReportService.create(request);

        replyReportService.delete(createdReport.id());

        assertThrows(ReportException.class, () -> replyReportService.getById(createdReport.id()));
    }
}
