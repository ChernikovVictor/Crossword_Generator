package com.course.crossword.model.crossword.builder;

import com.course.crossword.dto.DictionaryDTO;
import com.course.crossword.model.crossword.Cell;
import com.course.crossword.model.crossword.Crossword;
import com.course.crossword.model.dictionary.Word;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class CrosswordBuilder
{
    private Crossword crossword;
    private DictionaryDTO dict;
    private List<Word> wordsDict;
    private int height;
    private int width;

    public CrosswordBuilder(Crossword crossword, DictionaryDTO dict){
        this.crossword = crossword;
        this.dict = dict;
        wordsDict = dict.getData();
        height = getCells().length;
        width = getCells()[0].length;
    }

    public Cell[][] getCells(){
        return crossword.getCells();
    }

    public Crossword buildCrossword(){
        int i = 0;

        while(wordsDict.get(i).getValue().length() > width){
            i++;
        }
        int h = (int) Math.floor(height/2.0);
        int w = (int) Math.floor((width - wordsDict.get(i).getValue().length())/2.0);

        placeWordHorizontal(wordsDict.get(i),h,w);

        i++;
        while (i < wordsDict.size())
        {
            Word curr = wordsDict.get(i);
            List<Point> points = getCrosses(curr);
            for(Point point : points){
                if(canPlaceWordHorizontal(curr,point.getX(),point.getY()-point.index)){
                    placeWordHorizontal(curr,point.getX(),point.getY()-point.index);
                    break;
                }else if(canPlaceWordVertical(curr,point.getX()-point.index,point.getY())){
                    placeWordVertical(curr,point.getX()-point.index,point.getY());
                    break;
                }
            }
            i++;
        }


        return crossword;
    }

    private List<Point> getCrosses(Word word){
        List<Point> points = new ArrayList<>();
        for(int i = 0;i<getCells().length;i++){
            for(int j = 0;j<getCells()[0].length;j++){
                if(getCells()[i][j].isActive() && word.getValue().contains(getCells()[i][j].getOriginalValue())){
                    points.add(new Point(i,j,word.getValue().indexOf(getCells()[i][j].getOriginalValue())));
                }
            }
        }
        return points;
    }

    private void placeWordHorizontal(Word word, int h,int w){
        for(int i = w;i < w+word.getValue().length();i++){
            getCells()[h][i].setActive(true);
            getCells()[h][i].getDefinitions().add(word.getDefinition());
            getCells()[h][i].setOriginalValue(String.valueOf(word.getValue().charAt(i-w)));
        }
    }

    private boolean canPlaceWordHorizontal(Word word, int h,int w){
        if(h<0 || w<0 || w+word.getValue().length() > getCells()[0].length){
            return false;
        }
        if(w>0 && getCells()[h][w-1].isActive()
        || w+word.getValue().length()<getCells()[0].length
                && getCells()[h][w+word.getValue().length()].isActive()){
            return false;
        }
        int rep1 = 0;
        int rep2 = 0;
        for(int i = w;i < w+word.getValue().length();i++){
            if(getCells()[h][i].isActive()
                    && !getCells()[h][i].getOriginalValue().equals(String.valueOf(word.getValue().charAt(i-w)))){
                return false;
            }else if(!getCells()[h][i].isActive()){
                if(h>0 && getCells()[h-1][i].isActive() ||
                        h+1<getCells().length && getCells()[h+1][i].isActive()){
                    return false;
                }
            }
            if(h>0 && getCells()[h-1][i].isActive()){
                rep1++;
            }else{
                rep1 = 0;
            }

            if(h<getCells().length-1 && getCells()[h+1][i].isActive()){
                rep2++;
            }else{
                rep2 = 0;
            }
            if(rep1>1 || rep2>1){
                return false;
            }
        }
        return true;
    }

    private void placeWordVertical(Word word, int h,int w){
        for(int i = h;i < h+word.getValue().length();i++){
            getCells()[i][w].setActive(true);
            getCells()[i][w].getDefinitions().add(word.getDefinition());
            getCells()[i][w].setOriginalValue(String.valueOf(word.getValue().charAt(i-h)));
        }
    }

    private boolean canPlaceWordVertical(Word word, int h,int w){
        if(h<0 || w<0 || h+word.getValue().length()>getCells().length){
            return false;
        }
        if(h>0 && getCells()[h-1][w].isActive()
                || h+word.getValue().length()<getCells().length && getCells()[h+word.getValue().length()][w].isActive()){
            return false;
        }
        int rep1 = 0;
        int rep2 = 0;
        for(int i = h;i < h+word.getValue().length();i++){
            if(getCells()[i][w].isActive()
                    && !getCells()[i][w].getOriginalValue().equals(String.valueOf(word.getValue().charAt(i-h)))){
                return false;
            }else if(!getCells()[i][w].isActive()){
                if(w>0 && getCells()[i][w-1].isActive() ||
                        w+1<getCells()[0].length && getCells()[i][w+1].isActive()){
                    return false;
                }
            }

            if(w>0 && getCells()[i][w-1].isActive()){
                rep1++;
            }else{
                rep1 = 0;
            }

            if(w<getCells()[0].length-1 && getCells()[i][w+1].isActive()){
                rep2++;
            }else{
                rep2 = 0;
            }
            if(rep1>1 || rep2>1){
                return false;
            }
        }
        return true;
    }

    class Point{
        int x = 0;
        int y = 0;
        int index = 0;

        public Point(final int x, final int y,final int index)
        {
            this.x = x;
            this.y = y;
            this.index = index;
        }

        public int getX()
        {
            return x;
        }

        public void setX(final int x)
        {
            this.x = x;
        }

        public int getY()
        {
            return y;
        }

        public void setY(final int y)
        {
            this.y = y;
        }

        @Override
        public String toString()
        {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", index=" + index +
                    '}';
        }
    }
}

