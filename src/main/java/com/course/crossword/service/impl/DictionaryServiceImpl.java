package com.course.crossword.service.impl;

import com.course.crossword.dao.DictionaryDao;
import com.course.crossword.dto.CrosswordParametersDTO;
import com.course.crossword.dto.DictionaryDTO;
import com.course.crossword.exceptions.ImportFailedException;
import com.course.crossword.exceptions.ValidationException;
import com.course.crossword.model.Constants;
import com.course.crossword.model.dictionary.Dictionary;
import com.course.crossword.model.dictionary.Word;
import com.course.crossword.service.DictionaryService;
import com.course.crossword.util.CustomValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

        word.setValue(word.getValue().toUpperCase(Locale.ROOT));
        String validationResult = CustomValidator.isValidWord(word, dictionary);
        if (!validationResult.equals(Constants.SUCCESS)) {
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
        List<Word> resultWords = applyPagination(filteredAndSortedWords, page);
        return new DictionaryDTO(resultWords, filteredWords.size());
    }

    @Override
    public void removeWord(String wordValue, String dictionaryName) throws IOException {
        Dictionary dictionary = dictionaryDao.findByName(dictionaryName)
                .orElseThrow(() -> new RuntimeException("Словаря с таким именем не существует"));
        String wordValueUpperCase = wordValue.toUpperCase(Locale.ROOT);
        Word word = dictionary.getWords().stream()
                .filter(w -> w.getValue().equals(wordValueUpperCase))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("В словаре нет такого понятия"));
        dictionary.getWords().remove(word);
        dictionaryDao.save(dictionary);
    }

    @Override
    public void updateWord(String oldValue, Word word, String dictionaryName) throws IOException {
        Dictionary dictionary = dictionaryDao.findByName(dictionaryName)
                .orElseThrow(() -> new RuntimeException("Словаря с таким именем не существует"));
        String oldValueUpperCase = oldValue.toUpperCase(Locale.ROOT);
        Word wordToUpdate = dictionary.getWords().stream()
                .filter(w -> w.getValue().equals(oldValueUpperCase))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("В словаре нет такого понятия"));
        dictionary.removeWord(wordToUpdate);
        word.setValue(word.getValue().toUpperCase(Locale.ROOT));
        String validationResult = CustomValidator.isValidWord(word, dictionary);
        if (!validationResult.equals(Constants.SUCCESS)) {
            throw new RuntimeException(validationResult);
        }
        dictionary.addWord(word);
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
                .toUpperCase(Locale.ROOT);
        words = words.stream().filter(word -> word.getValue().matches(regex))
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

    private List<Word> applyPagination(List<Word> words, int page) {
        if (page == 0) {
            return words;
        }
        return words.stream()
                .skip((page - 1) * WORDS_PER_PAGE)
                .limit(WORDS_PER_PAGE)
                .collect(Collectors.toList());
    }

    @Override
    public void createDictionary(String name) throws IOException {
        Dictionary dictionary = new Dictionary(name, Collections.emptyList());
        dictionaryDao.save(dictionary);
    }

    @Override
    public void importDictionary(MultipartFile file) throws ImportFailedException, IOException {
        String filename = file.getOriginalFilename();
        int indexOfLastDot = filename.lastIndexOf(".");
        String extension = filename.substring(indexOfLastDot);
        if (!extension.equals(Constants.DICTIONARY_EXTENSION)) {
            throw new ImportFailedException("Недопустимое расширение у файла: " + filename);
        }
        importDictionary(filename.substring(0, indexOfLastDot), file.getInputStream());
    }

    @Override
    public void importDictionary(String name, InputStream inputStream) throws ImportFailedException, IOException {
        List<String> dictionaryNames = this.getAllDictionaryNames();
        for (String dictionaryName : dictionaryNames) {
            if (dictionaryName.equals(name)) {
                throw new ImportFailedException("Уже существует словарь с таким именем: " + name);
            }
        }

        Dictionary dictionary = new Dictionary(name, new ArrayList<>());
        Scanner scanner = new Scanner(inputStream);
        log.info("Если в словаре есть некорректные строчки, они будут представлены ниже:");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int indexOfFirstSpace = line.indexOf(" ");
            if (indexOfFirstSpace < 1) {
                log.info("{} (Отсутствует определение)", line);
                continue;
            }
            String value = line.substring(0, indexOfFirstSpace).trim();
            String definition = line.substring(indexOfFirstSpace + 1).trim();
            Word word = new Word(value.toUpperCase(Locale.ROOT), definition);
            String validationResult = CustomValidator.isValidWord(word, dictionary);
            if (!validationResult.equals(Constants.SUCCESS)) {
                log.info("{} ({})", line, validationResult);
                continue;
            }
            dictionary.addWord(word);
        }

        dictionaryDao.save(dictionary);
    }

}
