package sh.egoeng.api.word.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sh.egoeng.api.word.controller.dto.response.CsvImportResponse;
import sh.egoeng.api.word.service.command.UserWordCsvImportService;


@RestController
@RequestMapping("/api/user-words")
@RequiredArgsConstructor
public class UserWordCsvImportController {

    private final UserWordCsvImportService csvImportService;

    @PostMapping("/import")
    public ResponseEntity<CsvImportResponse> importUserWords(
            @RequestParam("file") MultipartFile file
    ) {
        CsvImportResponse response =
                csvImportService.importUserWordsFromCsv(file);

        return ResponseEntity.ok(response);
    }
}

