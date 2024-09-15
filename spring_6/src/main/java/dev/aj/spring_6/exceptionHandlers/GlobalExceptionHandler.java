package dev.aj.spring_6.exceptionHandlers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage().concat(" - Global."), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> {
            Map<String, String> map = new HashMap<>();
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
            return map;
        }).toList();

        return ResponseEntity.badRequest().body(fieldErrors);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<List<String>> handleJPAValidationExceptions(DataIntegrityViolationException exception) {

        String regex = "\\[([^\\[^\\]]+)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(exception.getLocalizedMessage());

        List<String> matchResults = matcher.results().map(Object::toString).toList();

        return new ResponseEntity<>(matchResults, HttpStatus.BAD_REQUEST);
    }
}
