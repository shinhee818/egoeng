package sh.egoeng.api.word.service.command;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sh.egoeng.api.exception.UserNotFoundException;
import sh.egoeng.api.word.controller.dto.response.CsvImportResponse;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.security.SecurityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
@Transactional
@Service
@RequiredArgsConstructor
public class UserWordCsvImportService {

    private final RegisterUserWordService registerUserWordService;
    private final UserService userService;

    public CsvImportResponse importUserWordsFromCsv(MultipartFile file) {
        validateUser();

        String filename = getFilename(file);

        if (filename.endsWith(".xlsx")) {
            return importFromXlsx(file);
        }
        if (filename.endsWith(".csv")) {
            return importFromCsv(file);
        }

        throw new IllegalArgumentException("CSV 또는 XLSX 파일만 업로드할 수 있습니다.");
    }

    private void validateUser() {
        Long userId = SecurityUtils.currentId();
        userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private String getFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("파일명을 확인할 수 없습니다.");
        }
        return filename.toLowerCase();
    }

    /* =========================
       CSV 처리
    ========================= */

    private CsvImportResponse importFromCsv(MultipartFile file) {
        ImportContext ctx = new ImportContext();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean header = true;
            int rowNumber = 1;

            while ((line = reader.readLine()) != null) {
                rowNumber++;

                if (header) {
                    header = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                ctx.totalRows++;
                String[] columns = parseCsvLine(line);

                if (columns.length < 2) {
                    ctx.fail(rowNumber, columns.length > 0 ? columns[0] : "",
                            "CSV 형식이 올바르지 않습니다. (단어,뜻)");
                    continue;
                }

                processRow(ctx, rowNumber, columns[0], columns[1]);
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV 파일 처리 중 오류 발생", e);
        }

        return ctx.toResponse();
    }

    /* =========================
       XLSX 처리
    ========================= */

    private CsvImportResponse importFromXlsx(MultipartFile file) {
        ImportContext ctx = new ImportContext();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Cell cell0 = row.getCell(0);
                Cell cell1 = row.getCell(1);
                if (cell0 == null && cell1 == null) {
                    continue;
                }

                ctx.totalRows++;
                int rowNumber = i + 1;

                String word = getCellValueAsString(cell0);
                String meaning = getCellValueAsString(cell1);

                processRow(ctx, rowNumber, word, meaning);
            }

        } catch (Exception e) {
            throw new RuntimeException("XLSX 파일 처리 중 오류 발생", e);
        }

        return ctx.toResponse();
    }

    /* =========================
       공통 처리
    ========================= */

    private void processRow(
            ImportContext ctx,
            int rowNumber,
            String word,
            String meaningKo
    ) {
        if (word == null || word.trim().isEmpty()) {
            ctx.fail(rowNumber, "", "단어가 비어있습니다.");
            return;
        }

        if (meaningKo == null || meaningKo.trim().isEmpty()) {
            ctx.fail(rowNumber, word.trim(), "뜻이 비어있습니다.");
            return;
        }

        try {
            Long userWordId =
                    registerUserWordService.registerCustomUserWord(
                            word.trim(), meaningKo.trim(), null);

            ctx.success(rowNumber, word.trim(), userWordId);
        } catch (Exception e) {
            ctx.fail(rowNumber, word.trim(),
                    e.getMessage() != null ? e.getMessage() : "단어 등록 실패");
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                double v = cell.getNumericCellValue();
                yield v == Math.floor(v)
                        ? String.valueOf((long) v)
                        : String.valueOf(v);
            }
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private String[] parseCsvLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                columns.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        columns.add(sb.toString());
        return columns.toArray(new String[0]);
    }

    /* =========================
       내부 컨텍스트
    ========================= */

    private static class ImportContext {
        int totalRows;
        int successCount;
        int failureCount;
        List<CsvImportResponse.ImportResult> results = new ArrayList<>();

        void success(int row, String word, Long id) {
            successCount++;
            results.add(new CsvImportResponse.ImportResult(
                    row, word, "SUCCESS", id, "단어가 성공적으로 등록되었습니다."
            ));
        }

        void fail(int row, String word, String reason) {
            failureCount++;
            results.add(new CsvImportResponse.ImportResult(
                    row, word, "FAILURE", null, reason
            ));
        }

        CsvImportResponse toResponse() {
            return new CsvImportResponse(
                    failureCount == 0,
                    totalRows,
                    successCount,
                    failureCount,
                    results
            );
        }
    }
}
