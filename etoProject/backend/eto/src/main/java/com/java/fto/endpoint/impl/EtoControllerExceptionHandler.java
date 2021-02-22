package com.java.fto.endpoint.impl;

import com.java.fto.exception.IncorrectTypeException;
import org.apache.http.conn.HttpHostConnectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class EtoControllerExceptionHandler {

    @ExceptionHandler(IncorrectTypeException.class)
    public ResponseEntity<Object> handleIncorrectTypeException(IncorrectTypeException ex, WebRequest request) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("solution", "you might want to use custom converter and exclude the row that are mentioned above");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpHostConnectException.class)
    public ResponseEntity<Object> handleHttpHostConnectException(HttpHostConnectException ex, WebRequest request) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "cannot get any connection with graph db");
        body.put("solution", "please first start graph db to use this functions");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<Object> connectException(ConnectException ex, WebRequest request) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "cannot get any connection with graph db");
        body.put("solution", "please first start graph db to use this functions");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex, WebRequest request) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
