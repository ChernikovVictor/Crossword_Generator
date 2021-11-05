package com.course.crossword.restapi.controllers;

import com.course.crossword.dto.CrosswordNameResponse;
import com.course.crossword.exceptions.ValidationException;
import com.course.crossword.model.crossword.Crossword;
import com.course.crossword.service.CrosswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/crosswords", produces = APPLICATION_JSON_VALUE)
public class CrosswordController {

    private CrosswordService crosswordService;

    @Autowired
    public CrosswordController(CrosswordService crosswordService) {
        this.crosswordService = crosswordService;
    }

    @GetMapping(value = "/list")
    public List<CrosswordNameResponse> list(@RequestParam String login) {
        return crosswordService.getCrosswordNamesList(login);
    }

    @GetMapping(value = "/crossword")
    public Crossword getCrosswordById(@RequestParam String id, @RequestParam String login) {
        return crosswordService.getById(id, login);
    }

    @PostMapping(value = "/crossword")
    public ResponseEntity saveCrossword(@RequestParam String name,
                                        @RequestParam String login,
                                        @RequestParam(required = false) String id,
                                        @RequestBody Crossword crossword) {
        try {
            crosswordService.save(crossword, name, login, id);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok().build();
    }
}
