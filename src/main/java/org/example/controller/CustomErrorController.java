package org.example.controller;

import org.example.model.LoginDto;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "An error occurred: " + ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ModelAndView handleNotFoundException(ChangeSetPersister.NotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("loginError", "Invalid credentials");
        modelAndView.addObject("login", new LoginDto());
        return modelAndView;
    }
}