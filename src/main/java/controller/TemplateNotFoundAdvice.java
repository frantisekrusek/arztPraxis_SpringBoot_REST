package controller;

import exceptions.AppointmentNotFoundException;
import exceptions.TemplateNotFoundException;
import model.appointment.Template;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TemplateNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(TemplateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String templateNotFoundHandler(TemplateNotFoundException ex) {
        return ex.getMessage();
    }
}
