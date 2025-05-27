package fr.mcoolive.crud_png.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageNotFound extends RuntimeException {
    public ImageNotFound() {
        super("Image not found");
    }
}
