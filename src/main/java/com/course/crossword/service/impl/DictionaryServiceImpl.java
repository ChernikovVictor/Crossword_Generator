package com.course.crossword.service.impl;

import com.course.crossword.dao.DictionaryDao;
import com.course.crossword.dto.DictionaryDTO;
import com.course.crossword.exceptions.ValidationException;
import com.course.crossword.model.dictionary.Dictionary;
import com.course.crossword.model.dictionary.Word;
import com.course.crossword.service.DictionaryService;
import com.course.crossword.util.CustomValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class DictionaryServiceImpl implements DictionaryService {

    private static final long WORDS_PER_PAGE = 20;

    private DictionaryDao dictionaryDao;

    @Autowired
    public DictionaryServiceImpl(DictionaryDao dictionaryDao) {
        this.dictionaryDao = dictionaryDao;
    }

    @Override
    public List<String> getAllDictionaryNames() {
        return dictionaryDao.getAllDictionaries().stream()
                .map(Dictionary::getName).collect(Collectors.toList());
    }

    @Override
    public void addWord(Word word, String dictionaryName) throws ValidationException, IOException {
        Dictionary dictionary = dictionaryDao.findByName(dictionaryName)
                .orElseThrow(() -> new RuntimeException("Словаря с таким именем не существует"));

        String validationResult = CustomValidator.isValidWord(word, dictionary);
        if (!validationResult.equals("success")) {
            throw new ValidationException(validationResult);
        }

        dictionary.addWord(word);
        dictionaryDao.save(dictionary);
    }

    @Override
    public DictionaryDTO getWords(String dictionaryName, int page, String filter, String sort, String sortDirection ) {
        Dictionary dictionary = dictionaryDao.findByName(dictionaryName)
                .orElseThrow(() -> new RuntimeException("Словаря с таким именем не существует"));

        List<Word> filteredWords = applyFilter(dictionary.getWords(), filter);
        List<Word> filteredAndSortedWords = applySort(filteredWords, sort, sortDirection);

        List<Word> resultWords = filteredAndSortedWords.stream()
                .skip((page - 1) * WORDS_PER_PAGE)
                .limit(WORDS_PER_PAGE)
                .collect(Collectors.toList());

        return new DictionaryDTO(resultWords, filteredWords.size());
    }

    @Override
    public void removeWord(String wordValue, String dictionaryName) throws IOException {
        Dictionary dictionary = dictionaryDao.findByName(dictionaryName)
                .orElseThrow(() -> new RuntimeException("Словаря с таким именем не существует"));
        Word word = dictionary.getWords().stream()
                .filter(w -> w.getValue().equals(wordValue))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("В словаре нет такого понятия"));
        dictionary.getWords().remove(word);
        dictionaryDao.save(dictionary);
    }

    @Override
    public void updateWord(Word word, String dictionaryName) throws IOException {
        Dictionary dictionary = dictionaryDao.findByName(dictionaryName)
                .orElseThrow(() -> new RuntimeException("Словаря с таким именем не существует"));
        Word wordToUpdate = dictionary.getWords().stream()
                .filter(w -> w.getValue().equals(word.getValue()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("В словаре нет такого понятия"));
        wordToUpdate.setDefinition(word.getDefinition());
        dictionaryDao.save(dictionary);
    }

    private List<Word> applyFilter(List<Word> words, String filter) {
        if (words == null || words.isEmpty()) {
            return Collections.emptyList();
        }
        if (filter == null || filter.isEmpty()) {
            return words;
        }

        final String regex = filter.replaceAll("1", ".")
                .replaceAll("\\*", ".+")
                .toLowerCase(Locale.ROOT);
        words = words.stream().filter(word -> word.getValue().toLowerCase(Locale.ROOT).matches(regex))
                .collect(Collectors.toList());

        return words;
    }

    private List<Word> applySort(List<Word> words, String sort, String sortDirection) {
        if (sort == null) {
            return words;
        }
        if (sortDirection == null) {
            return words;
        }

        Comparator<Word> lengthComparator = Comparator.comparingInt(w -> w.getValue().length());
        Comparator<Word> alphabetComparator = Comparator.comparing(Word::getValue);
        if (sort.equals("length")) {
            if (sortDirection.equals("ASC")) {
                words.sort(lengthComparator);
            } else {
                words.sort(lengthComparator.reversed());
            }
        } else {
            if (sortDirection.equals("ASC")) {
                words.sort(alphabetComparator);
            } else {
                words.sort(alphabetComparator.reversed());
            }
        }

        return words;
    }
}
