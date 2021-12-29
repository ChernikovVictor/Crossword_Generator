package com.course.crossword.service;

import com.course.crossword.dto.CrosswordNameResponse;
import com.course.crossword.dto.CrosswordParametersDTO;
import com.course.crossword.exceptions.ValidationException;
import com.course.crossword.model.crossword.Crossword;

import java.util.List;

public interface CrosswordService {

    List<CrosswordNameResponse> getCrosswordNamesList(String login);

    Crossword getById(String id, String login);

    void save(Crossword crossword, String crosswordName, String login, String id) throws ValidationException;
    void save(Crossword crossword, String login) throws ValidationException;

    String createCrossword(CrosswordParametersDTO crosswordParametersDTO);

    List<String> extractAllWordsFromCrossword(Crossword crossword);

}
