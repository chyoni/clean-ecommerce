package cwchoiit.cleanecommerce.adapter.web.common;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ProblemDetail handleIllegalException(RuntimeException e) {
        log.warn("[handleIllegalException] message: {}", e.getMessage());
        return invokeProblemDetail(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({Exception.class})
    public ProblemDetail handleException(Exception e) {
        log.error("[handleException]-[INTERNAL_SERVER_ERROR] Root cause: ", e);
        return invokeProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    private static @NonNull ProblemDetail invokeProblemDetail(HttpStatus status, Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("exception", e.getClass().getSimpleName());

        return problemDetail;
    }
}
