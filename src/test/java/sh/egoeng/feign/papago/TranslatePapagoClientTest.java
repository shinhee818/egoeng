package sh.egoeng.feign.papago;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sh.egoeng.feign.papago.response.PapagoTranslationResponse;

@SpringBootTest
@ActiveProfiles("test")
class TranslatePapagoClientTest {
    @Autowired
    private TranslatePapagoClient translatePapagoClient;

    @Test
    @DisplayName("papago translate 호출")
    void callAPI() {
        PapagoTranslationResponse translate = translatePapagoClient.translate(
                PapagoTargetLanguage.KOREAN.getCode(),
                PapagoTargetLanguage.ENGLISH.getCode(),
                "바다"
        );
        Assertions.assertNotNull(translate);

        System.out.println(translate);
    }
}