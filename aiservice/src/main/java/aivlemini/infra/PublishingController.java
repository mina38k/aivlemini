package aivlemini.infra;

import aivlemini.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/publishings")
@Transactional
public class PublishingController {

    @Value("${app.storage.path:./storage}")
    private String storagePath;

    @Autowired
    PublishingRepository publishingRepository;

    /**
     * 생성된 PDF 파일을 브라우저에서 바로 열거나 다운로드할 수 있도록 제공합니다.
     */
    @GetMapping(value = "/books/{id}")
    public ResponseEntity<Resource> getPdfFile(@PathVariable String id) {
        try {
            // 먼저 PDF 파일을 찾아보고, 없으면 텍스트 파일 찾기
            Path pdfPath = Paths.get(storagePath, "pdfs", id + ".pdf");
            String contentType = MediaType.APPLICATION_PDF_VALUE;
            String fileName = id + ".pdf";
            
            if (!Files.exists(pdfPath)) {
                // PDF 파일이 없으면 텍스트 파일을 찾기.
                pdfPath = Paths.get(storagePath, "pdfs", id + ".txt");
                contentType = MediaType.TEXT_PLAIN_VALUE;
                fileName = id + ".txt";
                
                if (!Files.exists(pdfPath)) {
                    return ResponseEntity.notFound().build();
                }
            }
            
            Resource resource = new FileSystemResource(pdfPath.toFile());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    
}
//>>> Clean Arch / Inbound Adaptor
