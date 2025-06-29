package aivlemini.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
// slf4j 해결 필요시 라이브러리 import 필요

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@Service
// @Slf4j
public class AIService {
    @Value("${openai.api.key:}")
    private String apiKEY;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiURL;

    @Value("${openai.api.image-url:https://api.openai.com/v1/images/generations}")
    private String imageApiUrl;

    @Value("${app.storage.path:./storage}")
    private String storagePath;

    @Value("${app.base.url:http://localhost:8084}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public AIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        // PostConstruct를 통해, 모든 프로퍼티가 주입된 후 실행
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            if (storagePath == null || storagePath.trim().isEmpty()) {
                storagePath = "./storage"; // 기본값 설정
                System.out.println("[AIService] 스토리지 경로가 설정되지 않아 기본값으로 설정: " + storagePath);
            }
            
            // 루트 스토리지 디렉토리 생성
            Path rootDir = Paths.get(storagePath);
            if (!Files.exists(rootDir)) {
                Files.createDirectories(rootDir);
                System.out.println("[AIService] 루트 스토리지 디렉토리 생성: " + rootDir.toAbsolutePath());
            }
            
            // PDF 저장소 디렉토리 생성
            Path pdfDir = Paths.get(storagePath, "pdfs");
            if (!Files.exists(pdfDir)) {
                Files.createDirectories(pdfDir);
                System.out.println("[AIService] PDF 디렉토리 생성: " + pdfDir.toAbsolutePath());
            }
            
            // 웹 컨텐츠 디렉토리 생성
            Path webDir = Paths.get(storagePath, "web");
            if (!Files.exists(webDir)) {
                Files.createDirectories(webDir);
                System.out.println("[AIService] 웹 컨텐츠 디렉토리 생성: " + webDir.toAbsolutePath());
            }
            
            // 디렉토리 권한 확인 (디버그용)
            System.out.println("[AIService] 스토리지 경로: " + rootDir.toAbsolutePath());
            System.out.println("[AIService] 스토리지 디렉토리 존재 여부: " + Files.exists(rootDir));
            System.out.println("[AIService] 스토리지 디렉토리 쓰기 권한: " + Files.isWritable(rootDir));
            System.out.println("[AIService] 웹 디렉토리 존재 여부: " + Files.exists(webDir));
            System.out.println("[AIService] 웹 디렉토리 쓰기 권한: " + Files.isWritable(webDir));
        } catch (Exception e) {
            System.err.println("[AIService] 스토리지 디렉토리 생성 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 원고 내용을 기반으로 표지 이미지 생성에 사용할 prompt 생성
     * @param content 원고 내용
     * @return 이미지 생성을 위한 프롬프트
     */
    public String generateCoverImagePrompt(String content) {
        System.out.println("[AIService] 표지 이미지 프롬프트 생성 시작");
        String prompt = "책 내용을 기반으로 표지 이미지를 생성해주세요: " + content.substring(0, Math.min(500, content.length()));
        
        Map<String, Object> requestBody = createChatCompletionRequest(prompt);
        System.out.println("[AIService] OpenAI API 호출 중 (프롬프트 생성)");
        String response = callOpenAI(requestBody);
        
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String result = (String) message.get("content");
            System.out.println("[AIService] 생성된 프롬프트: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("[AIService] AI 응답 처리 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("AI 응답 처리 중 오류 발생", e);
        }
    }

    /**
     * DALL-E-3 API : 이미지 생성 및 URL 반환
     * @param prompt 프롬프트
     * @return URL
     */
    public String generateImage(String prompt) {
        System.out.println("[AIService] 이미지 생성 시작");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKEY);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");
        requestBody.put("model", "dall-e-3");
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            System.out.println("[AIService] DALL-E API 호출 중... (URL: " + imageApiUrl + ")");
            ResponseEntity<String> response = restTemplate.postForEntity(imageApiUrl, entity, String.class);
            System.out.println("[AIService] DALL-E API 응답 코드: " + response.getStatusCode());
            
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            List<Map<String, Object>> data = (List<Map<String, Object>>) responseMap.get("data");
            String imageUrl = (String) data.get(0).get("url");
            System.out.println("[AIService] 생성된 이미지 URL 길이: " + imageUrl.length());
            return imageUrl;
        } catch (Exception e) {
            System.err.println("[AIService] OpenAI 이미지 API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("OpenAI 이미지 API 호출 중 오류 발생", e);
        }
    }    

    /**
     * 카테고리 분류
     * @param content 책 내용
     * @return 분류된 카테고리
     */
    public String categorizeContent(String content) {
        System.out.println("[AIService] 카테고리 분류 시작");
        String prompt = "다음 책 내용의 장르를 분류해주세요. 소설, 시, 에세이, 자기계발, 역사, 과학, 경제, 철학 중 하나만 골라 정확히 한 단어로만 답변해주세요. 다른 설명은 하지 말고 카테고리 단어만 답변해주세요: " 
            + content.substring(0, Math.min(1000, content.length()));
        
        Map<String, Object> requestBody = createChatCompletionRequest(prompt);
        System.out.println("[AIService] OpenAI API 호출 중 (카테고리 분류)");
        String response = callOpenAI(requestBody);
        
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String result = (String) message.get("content");
            
            // 결과를 정리: 단일 단어만 추출
            result = result.trim();
            // 특수문자, 마침표 등 제거
            result = result.replaceAll("[,.\"':]", "");
            // 여러 줄이면 첫 줄만 사용
            if (result.contains("\n")) {
                result = result.substring(0, result.indexOf("\n"));
            }
            // 여러 단어라면 첫 단어만 사용
            if (result.contains(" ")) {
                result = result.substring(0, result.indexOf(" "));
            }
            
            System.out.println("[AIService] 분류된 카테고리: " + result);
            
            // 허용된 카테고리 목록과 비교하여 정확한 카테고리만 반환
            String[] validCategories = {"소설", "시", "에세이", "자기계발", "역사", "과학", "경제", "철학"};
            boolean isValid = false;
            
            for (String category : validCategories) {
                if (result.equalsIgnoreCase(category)) {
                    result = category; // 정확한 형식으로 변환
                    isValid = true;
                    break;
                }
            }
            
            // 유효하지 않은 카테고리일 경우 기본값 반환
            if (!isValid) {
                System.out.println("[AIService] 유효하지 않은 카테고리, 기본값으로 '소설' 설정");
                result = "소설";
            }
            
            return result;
        } catch (Exception e) {
            System.err.println("[AIService] AI 응답 처리 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("AI 응답 처리 중 오류 발생", e);
        }
    }

    /**
     * 책 내용 요약
     * @param content 책 내용
     * @return 요약된 내용
     */
    public String summarizeContent(String content) {
        System.out.println("[AIService] 내용 요약 시작");
        String prompt = "다음 책 내용을 350자 이내로 요약해주세요: " + content.substring(0, Math.min(2000, content.length()));
        
        Map<String, Object> requestBody = createChatCompletionRequest(prompt);
        System.out.println("[AIService] OpenAI API 호출 중 (내용 요약)");
        String response = callOpenAI(requestBody);
        
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String result = (String) message.get("content");
            System.out.println("[AIService] 요약 결과 길이: " + result.length() + "자");
            return result;
        } catch (Exception e) {
            System.err.println("[AIService] AI 응답 처리 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("AI 응답 처리 중 오류 발생", e);
        }
    }

    /**
     * 책 내용을 HTML로 변환하고 웹에서 접근 가능한 URL 생성
     * @param content 책 내용
     * @return 생성된 웹 URL
     */
    public String convertToPdfAndGenerateWebUrl(String content) {
        System.out.println("[AIService] 웹 URL 생성 시작");
        
        try {
            // 경로 확인 및 재설정
            if (storagePath == null || storagePath.trim().isEmpty()) {
                storagePath = "./storage";
                System.out.println("[AIService] 스토리지 경로가 설정되지 않아 기본값으로 설정: " + storagePath);
            }
            
            // 고유 ID 생성
            String uniqueId = String.valueOf(System.currentTimeMillis());
            
            // HTML 파일 저장 경로
            Path webDir = Paths.get(storagePath, "web");
            
            // 웹 디렉토리가 없으면 다시 생성 시도
            if (!Files.exists(webDir)) {
                Files.createDirectories(webDir);
                System.out.println("[AIService] 웹 디렉토리 다시 생성: " + webDir.toAbsolutePath());
            }
            
            Path htmlFilePath = webDir.resolve(uniqueId + ".html");
            
            // 간단한 HTML 템플릿 생성
            String htmlContent = "<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "<head>\n"
                    + "    <meta charset=\"UTF-8\">\n"
                    + "    <title>책 내용</title>\n"
                    + "    <style>\n"
                    + "        body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }\n"
                    + "        h1 { color: #333; }\n"
                    + "        .content { white-space: pre-wrap; }\n"
                    + "    </style>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "    <h1>책 내용</h1>\n"
                    + "    <div class=\"content\">" + content.replaceAll("<", "&lt;").replaceAll(">", "&gt;") + "</div>\n"
                    + "</body>\n"
                    + "</html>";
            
            // HTML 파일 저장 전 경로 확인
            System.out.println("[AIService] HTML 파일 저장 경로: " + htmlFilePath.toAbsolutePath());
            System.out.println("[AIService] 상위 디렉토리 존재 여부: " + Files.exists(htmlFilePath.getParent()));
            
            // HTML 파일 저장
            Files.writeString(htmlFilePath, htmlContent, StandardCharsets.UTF_8);
            System.out.println("[AIService] HTML 파일 생성 완료: " + htmlFilePath.toAbsolutePath());
            
            // URL 생성 (실제 환경에서는 웹 서버 설정에 맞게 조정 필요)
            String url = baseUrl + "/books/" + uniqueId;
            System.out.println("[AIService] 생성된 웹 URL: " + url);
            
            return url;
        } catch (IOException e) {
            System.err.println("[AIService] 웹 컨텐츠 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return baseUrl + "/books/error";
        }
    }

    /**
     * 프롬프트에 대한 System, Human Message 설정 
     * @param prompt : 프롬프트 내용
     * @return header의 요청 바디
     */
    private Map<String, Object> createChatCompletionRequest(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "당신은 도서 출판을 돕는 AI 비서입니다.");
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        return requestBody;
    }

    /**
     * RestTemplate 형식으로 openai http 통신
     * @param requestBody : header의 요청 바디
     * @return : api 응답 바디
     */
    private String callOpenAI(Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKEY);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiURL, entity, String.class);
            System.out.println("[AIService] OpenAI API 응답 코드: " + response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("[AIService] OpenAI API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("OpenAI API 호출 중 오류 발생", e);
        }
    }
    
}
