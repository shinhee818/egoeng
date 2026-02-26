package sh.egoeng.api.exception;

public class ApiWordNotFoundException extends ApiException {
    public ApiWordNotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}

















