package com.course.crossword.restapi.controllers;

import com.course.crossword.dto.CrosswordParametersDTO;
import com.course.crossword.exceptions.ImportFailedException;
import com.course.crossword.service.CrosswordService;
import com.course.crossword.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/admin", produces = APPLICATION_JSON_VALUE)
public class AdminController {

    private DictionaryService dictionaryService;
    private CrosswordService crosswordService;

    @Autowired
    public AdminController(DictionaryService dictionaryService, CrosswordService crosswordService) {
        this.dictionaryService = dictionaryService;
        this.crosswordService = crosswordService;
    }

    @PutMapping(value = "/dictionary")
    public ResponseEntity<String> createDictionary(@RequestParam String name) {
        try {
            dictionaryService.createDictionary(name);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/dictionary")
    public ResponseEntity<String> importDictionary(@RequestParam MultipartFile file) {
        try {
            dictionaryService.importDictionary(file);
            return ResponseEntity.ok().build();
        } catch (ImportFailedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (IOException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/crossword")
    public ResponseEntity<String> createCrossword(@RequestBody CrosswordParametersDTO crosswordParametersDTO) {
        String crosswordId = crosswordService.createCrossword(crosswordParametersDTO);
        return new ResponseEntity<>(crosswordId, HttpStatus.OK);
    }

}
