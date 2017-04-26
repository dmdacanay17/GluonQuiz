package com.gluonquiz.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class SelectionPresenter {

    public String chapterString = "";
    public String sectionString = "";
    public String questionString = "";
    String start = "";
    ObservableList<String> questionArray = FXCollections.observableArrayList();
    ObservableList<String> questionArrayFinal = FXCollections.observableArrayList();

    @FXML
    private View selection;

    @FXML
    private Label label;

    @FXML
    private ComboBox chapCB, secCB, questCB;

    public void initialize() throws FileNotFoundException, IOException {
        ObservableList<String> chapters = FXCollections.observableArrayList();
        for (int i = 1; i <= 44; i++) {
            String chapterFile = "mcquestions/chapter" + i + ".txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(chapterFile))) {
                String line;
                line = reader.readLine();
                chapters.add(line);
            }
        }
        chapCB.setItems(chapters);
    }

    @FXML
    void dropChapter() {
        chapterString = (String) chapCB.getSelectionModel().getSelectedItem();
        chapterString = chapterString.substring(8, 10).trim();
        secCB.setItems(findSections(chapterString));
        questCB.setItems(FXCollections.observableArrayList());
        questionArrayFinal = findQuestions("Chapter " + chapterString);
        questionArray = (findQuestions("Chapter " + chapterString));
        sectionString = "";
        questionString = "";
        start = "1";
    }

    @FXML
    void dropSection() {
        sectionString = (String) secCB.getSelectionModel().getSelectedItem();
        if ("No Sections".equals(sectionString)) {
            sectionString = "-1";
            questCB.setItems(findQuestions("Chapter " + chapterString));
        } else {
            questCB.setItems(findQuestions(sectionString));
            start = (findQuestions(sectionString).get(0));
        }
        questionString = "";
    }

    @FXML
    void dropQuestion() {
        questionString = (String) questCB.getValue();
        start = questionString;
    }

    @FXML
    void go() throws Exception {
        String questString = "";
        for (int i = 0; i < questionArrayFinal.size(); i++) {
            questString = questString + questionArrayFinal.get(i) + ",";
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("temp.txt"))) {
            bw.write(chapterString + "," + start + "," + questString);
        } catch (IOException e) {
        }
        Parent pane = FXMLLoader.load(getClass().getResource("../views/question.fxml"));
        chapCB.getScene().setRoot(pane);
    }

    @FXML
    void goHome() throws Exception {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/home.fxml"));
        selection.getScene().setRoot(pane);
    }

    public ObservableList<String> findSections(String chapter) {
        ObservableList<String> sections = FXCollections.observableArrayList();
        try {
            String chapterFile = "mcquestions/chapter" + chapter + ".txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(chapterFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().length() > 0) {
                        line = line + "  ";
                        if (line.substring(0, line.indexOf(" ")).toLowerCase().equals("section")) {
                            sections.add(line.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        if (sections.isEmpty()) {
            sections.add("No Sections");
        }
        return sections;
    }

    public ObservableList<String> findQuestions(String section) {
        ObservableList<String> questions = FXCollections.observableArrayList();
        if (section.contains("Chapter " + chapterString)) {
            try {
                questionArray.clear();
                String chapterFile = "mcquestions/chapter" + chapterString + ".txt";
                try (BufferedReader reader = new BufferedReader(new FileReader(chapterFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(section)) {
                            while ((line = reader.readLine()) != null) {

                                if (line.trim().equals("#")) {
                                    line = reader.readLine();
                                    if (!line.substring(0, line.indexOf(" ")).toLowerCase().contains("section")) {
                                        line = line.substring(0, line.indexOf('.'));
                                        questions.add(line);
                                        questionArray.add(line);
                                    }
                                }
                            }
                        } else {
                        }
                    }
                }
            } catch (IOException e) {
            }
        } else {
            try {
                questionArray.clear();
                String chapterFile = "mcquestions/chapter" + chapterString + ".txt";
                try (BufferedReader reader = new BufferedReader(new FileReader(chapterFile))) {
                    String line;
                    boolean go = true;
                    while ((line = reader.readLine()) != null && go) {
                        if (line.contains(section)) {
                            while ((line = reader.readLine()) != null && go) {

                                if (line.trim().equals("#") && go) {
                                    line = reader.readLine();
                                    if (!line.substring(0, line.indexOf(" ")).toLowerCase().contains("section")) {
                                        line = line.substring(0, line.indexOf('.'));
                                        questions.add(line);
                                        questionArray.add(line);
                                    } else {
                                        go = false;
                                    }
                                }
                            }
                        } else {
                        }
                    }
                }
            } catch (IOException e) {
            }
        }
        return questions;
    }
}
