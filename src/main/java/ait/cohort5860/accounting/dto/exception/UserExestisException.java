package ait.cohort5860.accounting.dto.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@NoArgsConstructor
public class UserExestisException extends RuntimeException {
    public UserExestisException(String message) {
        super(message);
    }
}
