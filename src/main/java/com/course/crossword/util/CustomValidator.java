package com.course.crossword.util;

import com.course.crossword.model.Constants;
import com.course.crossword.model.crossword.Cell;
import com.course.crossword.model.crossword.Crossword;
import com.course.crossword.model.dictionary.Dictionary;
import com.course.crossword.model.dictionary.Word;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CustomValidator {

    public static String isValidWord(Word word, Dictionary dictionary) {

        String russianLanguageRegex = "[а-яА-Я]+";
        if (!word.getValue().matches(russianLanguageRegex)) {
            return "В понятии присутствуют символы, отличные от букв русского языка";
        }

        Optional<Word> duplicate = dictionary.getWords().stream()
                .filter(w -> w.getValue().equals(word.getValue()))
                .findAny();
        if (duplicate.isPresent()) {
            return String.format("В словаре уже есть такое понятие: %s - %s",
                    duplicate.get().getValue(), duplicate.get().getDefinition());
        }

        return Constants.SUCCESS;
    }

    private static boolean[][] visited;     // массив посещенных ячеек для прохода в глубину
    private static int activeCellsCount;    // кол-во активных ячеек, считается при проходе в глубину

    public static String isValidCrossword(Crossword crossword) {

        Cell[][] cells = crossword.getCells();
        int rows = cells.length;
        int columns = cells[0].length;

        int notActiveCellsCount = 0;
        int activeCellRow = -1;
        int activeCellColumn = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j] == null) {
                    return "В кроссворде есть неопределенные (null) ячейки";
                } else if (cells[i][j].isActive()) {
                    activeCellRow = i;
                    activeCellColumn = j;
                } else {
                    notActiveCellsCount++;
                }
            }
        }

        // Пустой кроссворд является валидным
        int totalCells = rows * columns;
        if (activeCellRow == -1) {
            if (notActiveCellsCount == totalCells) {
                return Constants.SUCCESS;
            } else {
                return "В кроссворде нет активных ячеек, но при этом количество неактивных не совпадает с общим количеством";
            }
        }

        // проверка, чтобы кроссворд не распадался на несколько "под-кроссвордов"
        visited = new boolean[rows][columns];
        activeCellsCount = 0;
        dfs(activeCellRow, activeCellColumn, cells);
        if (activeCellsCount + notActiveCellsCount != totalCells) {
            return "На сетке присутствует более чем один цельный кроссворд";
        }

        // проверка, что в кроссворде более чем одно слово
        Set<String> definitions = new HashSet<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isActive()) {
                    definitions.addAll(cells[i][j].getDefinitions());
                }
            }
        }
        if (definitions.size() < 2) {
            return "Минимальное количество слов в кроссворде - 2";
        }

        return Constants.SUCCESS;
    }

    /* Depth-first search */
    private static void dfs(int i, int j, Cell[][] cells) {
        visited[i][j] = true;
        activeCellsCount++;
        if (shouldVisit(i - 1, j, cells)) {
            dfs(i - 1, j, cells);
        }
        if (shouldVisit(i + 1, j, cells)) {
            dfs(i + 1, j, cells);
        }
        if (shouldVisit(i, j - 1, cells)) {
            dfs(i, j - 1, cells);
        }
        if (shouldVisit(i, j + 1, cells)) {
            dfs(i, j + 1, cells);
        }
    }

    private static boolean shouldVisit(int i, int j, Cell[][] cells) {
        int rows = cells.length;
        int columns = cells[0].length;
        if (i < 0 || i >= rows || j < 0 || j >= columns) {
            return false;
        }

        if (!cells[i][j].isActive()) {
            return false;
        }

        if (visited[i][j]) {
            return  false;
        }

        return true;
    }
}
