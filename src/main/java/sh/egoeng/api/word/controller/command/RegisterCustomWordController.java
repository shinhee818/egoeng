package sh.egoeng.api.word.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.word.controller.dto.request.RegisterCustomWordRequest;
import sh.egoeng.api.word.controller.dto.response.RegisterCustomWordResponse;
import sh.egoeng.api.word.service.command.RegisterUserWordService;

@RestController
@RequestMapping("/api/user-words")
@RequiredArgsConstructor
public class RegisterCustomWordController {
    private final RegisterUserWordService registerUserWordService;

    /**
     * 커스텀 단어 등록 API
     * 유저가 직접 입력한 단어를 한 건 등록합니다.
     * 
     * @param request 단어, 뜻, 태그 정보
     * @return 등록된 단어 정보
     */
    @PostMapping("/custom")
    public ResponseEntity<RegisterCustomWordResponse> registerCustomWord(
            @RequestBody RegisterCustomWordRequest request) {
        request.validate();
        
        Long userWordId = registerUserWordService.registerCustomUserWord(
                request.word(),
                request.meaningKo(),
                request.tags()
        );
        
        RegisterCustomWordResponse response = RegisterCustomWordResponse.success(
                userWordId,
                request.word(),
                request.meaningKo()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}











