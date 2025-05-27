package fr.mcoolive.crud_png.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidImage extends RuntimeException {
    public InvalidImage(String message) {
        super(message);
    }
}
