package com.course.crossword.service.impl;

import com.course.crossword.dao.CrosswordDao;
import com.course.crossword.dto.CrosswordNameResponse;
import com.course.crossword.dto.CrosswordParametersDTO;
import com.course.crossword.dto.DictionaryDTO;
import com.course.crossword.exceptions.ValidationException;
import com.course.crossword.model.Constants;
import com.course.crossword.model.crossword.Cell;
import com.course.crossword.model.crossword.Crossword;
import com.course.crossword.model.crossword.builder.CrosswordBuilder;
import com.course.crossword.service.CrosswordService;
import com.course.crossword.service.DictionaryService;
import com.course.crossword.util.CustomValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrosswordServiceImpl implements CrosswordService {

    private CrosswordDao crosswordDao;
    private DictionaryService dictionaryService;

    @Autowired
    public CrosswordServiceImpl(CrosswordDao crosswordDao, DictionaryService dictionaryService) {
        this.crosswordDao = crosswordDao;
        this.dictionaryService = dictionaryService;
    }

    @Override
    public List<CrosswordNameResponse> getCrosswordNamesList(String login) {
        Stream<CrosswordNameResponse> s1 = crosswordDao.getCrosswordsForUser(Constants.ADMIN_ROLE)
                .stream().map(c -> new CrosswordNameResponse(c.getId(), c.getName(), true));
        if (login.equals(Constants.ADMIN_ROLE)) {
            return s1.collect(Collectors.toList());
        }
        Stream<CrosswordNameResponse> s2 = crosswordDao.getCrosswordsForUser(login)
                .stream().map(c -> new CrosswordNameResponse(c.getId(), c.getName(), false));
        return Stream.concat(s1, s2).collect(Collectors.toList());
    }

    @Override
    public Crossword getById(String id, String login) {
        log.info("id = {} login {}", id, login);
        return crosswordDao.getById(id, login).orElseThrow(() -> new RuntimeException("Кроссворд не найден"));
    }

    @Override
    public void save(Crossword crossword, String crosswordName, String login, String id) throws ValidationException {
        crossword.setId((id == null) ? UUID.randomUUID().toString() : id);
        crossword.setName(crosswordName);

        String validationResult = CustomValidator.isValidCrossword(crossword);
        if (!validationResult.equals(Constants.SUCCESS)) {
            throw new ValidationException(validationResult);
        }

        try {
            crosswordDao.save(crossword, login);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Не удалось сохранить файл: " + e.getMessage());
        }
    }

    @Override
    public void save(Crossword crossword, String login) throws ValidationException {
        this.save(crossword, crossword.getName(), login, crossword.getId());
    }

    @Override
    public String createCrossword(CrosswordParametersDTO crossParamsDTO) {
        Crossword crossword;
        if (crossParamsDTO.isAuto()) {
            crossword = generateCrossword(crossParamsDTO);
        } else {
            crossword = createEmptyCrossword(crossParamsDTO);
        }
        crossword.setName(crossParamsDTO.getName());
        crossword.setDictionaryName(crossParamsDTO.getDictionary());
        crossword.setHints(Constants.HINT_COUNT);
        crossword.setId(UUID.randomUUID().toString());
        try {
            this.save(crossword, Constants.ADMIN_ROLE);
            return crossword.getId();
        } catch (ValidationException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Crossword createEmptyCrossword(CrosswordParametersDTO crossParamsDTO) {
        int height = crossParamsDTO.getHeight();
        int width = crossParamsDTO.getWidth();
        return createBaseCrossword(height, width);
    }

    private Crossword createBaseCrossword(int height, int width) {
        Cell[][] cells = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cells[i][j] = new Cell();
                cells[i][j].setId(String.format("%d,%d", i, j));
                cells[i][j].setActive(false);
            }
        }
        Crossword crossword = new Crossword();
        crossword.setCells(cells);
        return crossword;
    }

    private Crossword generateCrossword(CrosswordParametersDTO crossParamsDTO) {
        DictionaryDTO dict = dictionaryService.getWords(crossParamsDTO.getDictionary(), 0, "*", "length", "DESC");
        int width = crossParamsDTO.getWidth();
        int height = crossParamsDTO.getHeight();
        Crossword crossword = createBaseCrossword(height, width);
        CrosswordBuilder crosswordBuilder = new CrosswordBuilder(crossword, dict);
        crossword = crosswordBuilder.buildCrossword();
        log.info(crossword.toString());
        return crossword;
    }
}
