package com.lamolienda.lamolienda.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalRequestAttributes {

    @ModelAttribute("httpServletRequest")
    public HttpServletRequest exposeRequest(HttpServletRequest request) {
        return request;
    }
}
