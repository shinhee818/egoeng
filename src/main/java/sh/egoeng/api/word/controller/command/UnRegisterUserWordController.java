package sh.egoeng.api.word.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.word.service.command.UnRegisterUserWordService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-words")
public class UnRegisterUserWordController {
    private final UnRegisterUserWordService unRegisterUserWordService;

    @DeleteMapping("/{userWordId}")
    public void deleteWord(@PathVariable Long userWordId) {
        unRegisterUserWordService.unRegister(userWordId);
    }
}