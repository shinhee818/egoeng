package sh.egoeng.api.word.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.word.controller.dto.request.RegisterOcrWordsRequest;
import sh.egoeng.api.word.controller.dto.response.RegisterOcrWordsResponse;
import sh.egoeng.api.word.service.command.RegisterUserWordService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-words")
@RequiredArgsConstructor
public class RegisterOcrWordsController {
    private final RegisterUserWordService registerUserWordService;

    /**
     * OCR 텍스트 일괄 등록 API
     * OCR 결과에서 선택한 텍스트들을 한번에 커스텀 단어로 등록합니다.
     * 
     * @param request 등록할 단어 리스트
     * @return 등록된 단어 ID 목록
     */
    @PostMapping("/ocr/bulk-register")
    public ResponseEntity<RegisterOcrWordsResponse> registerOcrWords(
            @RequestBody RegisterOcrWordsRequest request) {
        
        if (request.words() == null || request.words().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new RegisterOcrWordsResponse(0, 0, 0, List.of())
            );
        }

        // DTO 변환
        List<RegisterUserWordService.OcrWordText> wordTexts = request.words().stream()
                .map(w -> new RegisterUserWordService.OcrWordText(w.text(), w.meaningKo()))
                .collect(Collectors.toList());

        // 등록
        RegisterUserWordService.OcrWordRegisterResult result = registerUserWordService.registerOcrWords(wordTexts, null);

        RegisterOcrWordsResponse response = new RegisterOcrWordsResponse(
                result.totalCount(),
                result.registeredCount(),
                result.skippedCount(),
                result.registeredIds()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
