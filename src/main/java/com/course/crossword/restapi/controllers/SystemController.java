package com.course.crossword.restapi.controllers;

import com.course.crossword.dto.CredentialsBody;
import com.course.crossword.dto.CredentialsResponse;
import com.course.crossword.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping(value = "/system", produces = APPLICATION_JSON_VALUE)
public class SystemController {

    private SystemService systemService;

    @Autowired
    public SystemController(SystemService service) {
        this.systemService = service;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<CredentialsResponse> register(@RequestBody CredentialsBody body) {
        CredentialsResponse response = systemService.register(body.getLogin(), body.getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/authorize")
    public ResponseEntity<CredentialsResponse> authorize(@RequestBody CredentialsBody body) {
        CredentialsResponse response = systemService.authorize(body.getLogin(), body.getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
