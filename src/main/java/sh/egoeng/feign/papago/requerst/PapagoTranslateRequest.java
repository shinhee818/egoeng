package sh.egoeng.feign.papago.requerst;

public record PapagoTranslateRequest(
        String text,
        String source,
        String target
) {
}
