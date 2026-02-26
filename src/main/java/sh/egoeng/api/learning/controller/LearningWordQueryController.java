package sh.egoeng.api.learning.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.learning.service.LearningWordQueryService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/learning/user-words")
public class LearningWordQueryController {
    private final LearningWordQueryService learningWordQueryService;
}
