package sh.egoeng.feign.papago;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sh.egoeng.feign.papago.response.PapagoTranslationResponse;

@FeignClient(
        name = "papagoClient",
        url = "${papago.base-url}",
        configuration = PapagoFeignConfig.class
)
public interface TranslatePapagoClient {

    @PostMapping(
            value = "/nmt/v1/translation",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    PapagoTranslationResponse translate(
            @RequestParam("source") String sourceLang,
            @RequestParam("target") String targetLang,
            @RequestParam("text") String text
    );
}