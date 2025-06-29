package aivlemini.domain;

import aivlemini.AiserviceApplication;
// import aivlemini.domain.PublishPrepared;
import aivlemini.service.AIService;
import aivlemini.service.PDFService;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import java.time.LocalDate;
// import java.util.Collections;
// import java.util.Date;
// import java.util.List;
// import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.client.RestTemplate;

@Entity
@Table(name = "Publishing_table")
@Data
//<<< DDD / Aggregate Root
public class Publishing {
    private static final Logger logger = LoggerFactory.getLogger(Publishing.class);
    private static final AtomicBoolean isProcessing = new AtomicBoolean(false); // 동시 출간 요청 방지를 위한 지역변수 상태값

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String coverImagePath;

    @Lob
    private String summaryContent;

    private String bookName;

    @Lob
    private String pdfPath;

    private String authorId;

    private String authorName;

    private String category;

    private Boolean notifyStatus;

    // ManuscriptId 속성을 찾을 수 없는 오류 -> 주석 이유
    //@Embedded
    //private ManuscriptId manuscriptId;

    public static PublishingRepository repository() {
        PublishingRepository publishingRepository = AiserviceApplication.applicationContext.getBean(
            PublishingRepository.class
        );
        return publishingRepository;
    }

    //<<< Clean Arch / Port Method
    public static void publish(PublicationRequested publicationRequested) {
        //implement business logic here:

        // 동시 여러 요청 처리 방지
        if (!isProcessing.compareAndSet(false, true)) {
            logger.warn("이미 출판 처리가 진행 중입니다. 요청이 무시됩니다.");
            return;
        }

        try {
            logger.info("\n===== AI 출판 처리 시작 =====");

            // 출판 정보 객체 생성
            Publishing publishing = new Publishing();
            publishing.setBookName(publicationRequested.getTitle()); // 책제목
            publishing.setAuthorId(publicationRequested.getAuthorId().toString()); // 작가id
            publishing.setNotifyStatus(false); // notifyStatus 기본값
            logger.info("책 제목: {}", publicationRequested.getTitle());
            logger.info("작가id: {}", publicationRequested.getAuthorId().toString());
            
            // 서비스 인스턴스 가져오기
            AIService aiService = AiserviceApplication.applicationContext.getBean(AIService.class);
            PDFService pdfService = AiserviceApplication.applicationContext.getBean(PDFService.class);

            // 책 내용 가져오기
            String content = publicationRequested.getContents();
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("책 내용이 비어 있습니다.");
            }
            logger.info("책 내용 길이: {}자", content.length());
            
            // 1. 표지 이미지 생성을 위한 프롬프트 생성
            logger.info("1단계: 이미지 생성 프롬프트 생성 시작");
            String coverImagePrompt = aiService.generateCoverImagePrompt(content);
            logger.info("1단계 완료: 이미지 생성 프롬프트 - {}", coverImagePrompt);
            
            // 2. DALL-E API를 사용하여 실제 이미지 생성 및 URL 저장
            logger.info("2단계: 이미지 생성 API 호출 시작");
            String imageUrl = null;
            try {
                imageUrl = aiService.generateImage(coverImagePrompt);
                publishing.setCoverImagePath(imageUrl);
                logger.info("2단계 완료: 이미지 URL 생성됨 - {}", imageUrl);
            } catch (Exception e) {
                // API 호출 실패 시 기본 이미지 사용
                logger.error("이미지 생성 API 호출 실패: {}", e.getMessage());
                publishing.setCoverImagePath("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSczdFZZc2BhiuLpRhm8qQ4ZTev3tBafeHg-Q&s");
            }

            // 3. 장르 분류 및 저장
            logger.info("3단계: 카테고리 분류 시작");
            String category = aiService.categorizeContent(content);
            publishing.setCategory(category);
            logger.info("3단계 완료: 분류된 카테고리 - {}", category);
            
            // 4. 줄거리 요약 및 저장
            logger.info("4단계: 내용 요약 시작");
            String summary = aiService.summarizeContent(content);
            publishing.setSummaryContent(summary);
            logger.info("4단계 완료: 요약 완료 ({}자)", summary.length());        

            // 5. 저자 정보 처리
            logger.info("5단계: 저자 정보 처리 시작");
            try {
                publishing.setAuthorName(publicationRequested.getAuthorName());
                logger.info("5단계 완료: 저자 이름 - {}", publishing.getAuthorName());
            } catch (Exception e) {
                logger.error("저자 정보 조회 실패: {}", e.getMessage());
                publishing.setAuthorName("알 수 없는 저자");
            }

            // 6. 모든 정보가 준비된 후 PDF 생성 (PDFService 직접 호출)
            logger.info("6단계: PDF 생성 시작");
            String fileName = pdfService.generatePdf(
                content, 
                publishing.getCoverImagePath(), 
                publishing.getSummaryContent(), 
                publishing.getBookName());
            publishing.setPdfPath(fileName);
            logger.info("6단계 완료: PDF 생성됨 - {}", fileName);

            // 7. PDF 파일명을 웹에서 접근 가능한 URL로 변환
            logger.info("7단계: 웹 URL 생성 시작");
            try {
                String webUrl = pdfService.generateWebUrl(fileName);
                publishing.setPdfPath(webUrl);
                logger.info("7단계 완료: 웹 URL 생성됨 - {}", webUrl);
            } catch (Exception e) {
                logger.error("웹 URL 생성 실패: {}", e.getMessage(), e);
                // 오류 발생 시에도 동적으로 URL 생성 시도
                try {
                    String fallbackUrl = "http://localhost:8084/pdfs/" + fileName;
                    publishing.setPdfPath(fallbackUrl);
                    logger.warn("폴백 URL 사용: {}", fallbackUrl);
                } catch (Exception fallbackError) {
                    logger.error("폴백 URL 생성도 실패: {}", fallbackError.getMessage());
                    publishing.setPdfPath("/pdfs/" + fileName); // 상대 경로로 설정
                }
            }

            // 8. 오류없이 여기까지 잘 진행되었을 경우, notifyStatus 갱신
            publishing.setNotifyStatus(true);
            logger.info("8단계 : notifyStatus : {}", publishing.getNotifyStatus());

            // 9. 출판 정보 저장
            logger.info("9단계: 출판 정보 저장 시작");
            repository().save(publishing);
            logger.info("9단계 완료: 출판 정보 저장됨");
            
            // 10. 이벤트 발행
            logger.info("10단계: 출판 이벤트 발행 시작");
            PublishPrepared published = new PublishPrepared(publishing);
            published.publishAfterCommit();
            logger.info("10단계 완료: 출판 이벤트 발행됨");            

        } catch(Exception e) {
            logger.error("출판 처리 중 오류 발생 : {}", e.getMessage(), e);
        } finally {
            // 처리 상태 초기화 : 다음 출간 요청 작업 이루어질 수 있도록
            isProcessing.set(false);
        }

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
