package com.course.crossword.restapi.controllers;

import com.course.crossword.dto.DictionaryDTO;
import com.course.crossword.exceptions.ValidationException;
import com.course.crossword.model.dictionary.Dictionary;
import com.course.crossword.model.dictionary.Word;
import com.course.crossword.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/dictionaries", produces = APPLICATION_JSON_VALUE)
public class DictionaryController {

    private DictionaryService dictionaryService;

    @Autowired
    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping(value = "/list")
    public List<String> getAllDictionaryNames() {
        return dictionaryService.getAllDictionaryNames();
    }

    @GetMapping(value = "/dictionary")
    public DictionaryDTO getWords(@RequestParam(value = "name") String dictionaryName,
                                  @RequestParam int page,
                                  @RequestParam(required = false) String filter,
                                  @RequestParam(required = false) String sort,
                                  @RequestParam(required = false) String sortDirection) {
        return dictionaryService.getWords(dictionaryName, page, filter, sort, sortDirection);
    }

    @PostMapping(value = "/dictionary")
    public ResponseEntity<String> addWordToDictionary(@RequestParam(value = "name") String dictionaryName, @RequestBody Word word) {
        try {
            dictionaryService.addWord(word, dictionaryName);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Throwable t) {
            return new ResponseEntity<>(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/dictionary")
    public ResponseEntity deleteWordFromDictionary(@RequestParam(value = "name") String dictionaryName,
                                                   @RequestParam(value = "value") String wordValue) throws IOException {
        dictionaryService.removeWord(wordValue, dictionaryName);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/dictionary")
    public ResponseEntity<String> updateWord(@RequestParam(value = "name") String dictionaryName,
                                             @RequestBody Word word) throws IOException {
        dictionaryService.updateWord(word, dictionaryName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/save")
    public ResponseEntity saveChangesInDictionary(@RequestParam(value = "name") String dictionaryName) {
        // Данный эндпоинт имеет смысл реализовывать, если работа со словарем будет производиться в кэше.
        // Сейчас же все изменения в словаре и так сразу сохраняются в файл.
        return ResponseEntity.ok().build();
    }

}
