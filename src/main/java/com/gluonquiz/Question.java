
package com.gluonquiz;

import java.util.ArrayList;

public class Question {

    int questionNumber;
    //Each index is a line of the question
    private ArrayList<String> questionStringArray = new ArrayList<String>();
    //Each index is a option
    private ArrayList<String> abcdArray = new ArrayList<String>();
    //Each index is a letter of the key
    private ArrayList<String> keyArray = new ArrayList<String>();
    //Each index is a line of the hint
    private ArrayList<String> hintArray = new ArrayList<String>();

    public Question(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Question() {
    }

    public void addLineToQuestion(String line) {
        questionStringArray.add(line);
    }

    public ArrayList<String> getQuestionStringArray() {
        return this.questionStringArray;
    }

    public void addOption(String line) {
        abcdArray.add(line);
    }

    public ArrayList<String> getOptions() {
        return this.abcdArray;
    }

    public void addKey(String line) {
        if (!line.trim().equals("")){
            keyArray.add(line);
        }
    }

    public ArrayList<String> getKeyArray() {
        return this.keyArray;
    }

    public void addHint(String line) {
        hintArray.add(line);
    }

    public ArrayList<String> getHintArray() {
        return this.hintArray;
    }
}
