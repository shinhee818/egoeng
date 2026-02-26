package sh.egoeng.api.word.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.word.controller.dto.request.RegisterUserWordRequest;
import sh.egoeng.api.word.service.command.RegisterUserWordService;

@RestController
@RequestMapping("/api/user-words")
@RequiredArgsConstructor
public class RegisterUserWordController {
    private final RegisterUserWordService registerUserWordService;

    @PostMapping
    public Long saveMyWord(@RequestBody RegisterUserWordRequest request) {
        request.validate();
        
        if (request.wordId() != null) {
            // 기존 Word 참조
            return registerUserWordService.registerUserWord(request.wordId(), request.tagNames());
        } else {
            // 커스텀 단어
            return registerUserWordService.registerCustomUserWord(
                    request.text(),
                    request.meaningKo(),
                    request.tagNames()
            );
        }
    }
}
