package com.gluonquiz.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import com.gluonquiz.Question;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class QuestionPresenter extends SelectionPresenter {

    public ArrayList<String> questionNum = new ArrayList<>();
    ArrayList<Question> questionArray = new ArrayList<>();

    public String chapterString;

    ArrayList<RadioButton> radioButtonArray = new ArrayList<>();
    ArrayList<CheckBox> checkBoxArray = new ArrayList<>();
    ToggleGroup group = new ToggleGroup();

    Image check = new Image("file:check.png");
    Image xmark = new Image("file:xmark.gif");

    int current = 0;
    double changeHeight;
    Connection con;
    PreparedStatement ps;

    @FXML
    private View create;

    @FXML
    private Button hintBTN;

    @FXML
    private VBox vBoxQuest;

    @FXML
    ImageView feedback;

    @FXML
    private TextFlow questionTF, hintTF;

    private String user, key;
    private int attempt;
    private boolean isCorrect;
    private boolean isAnswered;
    private String answerString;

    ToggleGroup questionTog = new ToggleGroup();
    public Question t = new Question();

    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
            ps = con.prepareStatement("SELECT * FROM currentuser");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = rs.getString("username");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HomePresenter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(QuestionPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        questionsToArray();
        QuestionToScene(current);
    }

    public void questionsToArray() {
        try {
            String line;
            try (BufferedReader reader = new BufferedReader(new FileReader("temp.txt"))) {
                line = reader.readLine();
            }
            String[] parts = line.split(",");
            chapterString = parts[0];
            current = Integer.parseInt(parts[1]) - 1;
            System.out.println(current);
            for (int i = 2; i < parts.length; i++) {
                questionNum.add(parts[i]);
            }
        } catch (IOException e) {
        }
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader("mcquestions/chapter" + chapterString + ".txt"))) {
                String line;
                int i = 0;
                boolean go = true;
                while ((line = reader.readLine()) != null && go) {
                    line = line + "     ";
                    if (line.substring(0, 3).contains(questionNum.get(i) + ".")) {
                        questionArray.add(new Question());
                        questionArray.get(i).addLineToQuestion(line);
                        line = reader.readLine();
                        while (!line.contains("KEY:")) {
                            line = line + "    ";
                            if ((('a' <= line.charAt(0)) && (line.charAt(0) <= 'z')) && line.charAt(1) == '.') {
                                questionArray.get(i).addOption(line);
                            } else {
                                questionArray.get(i).addLineToQuestion(line);
                            }
                            line = reader.readLine();
                        }
                        for (int j = 4; j < line.length(); j++) {
                            questionArray.get(i).addKey(line.charAt(j) + "");
                            if (line.charAt(j) == ' ') {
                                questionArray.get(i).addHint(line.substring(j + 1));
                                j = line.length() + 1;
                            }
                        }
                        i++;
                        if (questionNum.size() == i) {
                            go = false;
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    public void QuestionToScene(int index) {
        feedback.setVisible(false);
        try {
            //con = DriverManager.getConnection("jdbc:derby://localhost/CSCI5520TeamProject", "dustin", "dustin");
            con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
            ps = con.prepareStatement("select * FROM entries WHERE "
                    + "(username = ?) "
                    + "and (chapter = ?) "
                    + "and (question = ?)");
            ps.setString(1, user);
            ps.setString(2, chapterString);
            ps.setString(3, questionNum.get(current));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                isCorrect = rs.getBoolean("correct");
                attempt = rs.getInt("attempt");
                answerString = rs.getString("entry");
            }
            System.out.println(isCorrect);
        } catch (SQLException ex) {
            Logger.getLogger(QuestionPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (attempt > 0) {
            if (isCorrect) {
                feedback.setImage(check);
                feedback.setVisible(true);
                hintTF.getChildren().clear();
                hintTF.getChildren().add(new Text("already answered correctly"));
            } else {
                feedback.setImage(xmark);
                feedback.setVisible(true);
                hintTF.getChildren().clear();
                hintTF.getChildren().add(new Text("already answered incorrectly"));
            }
        }
        hintTF.getChildren().clear();
        hintBTN.setVisible(false);
        hintBTN.setDisable(true);
        radioButtonArray.clear();
        checkBoxArray.clear();
        vBoxQuest.getChildren().clear();
        questionTF.getChildren().clear();
        double total = 0;
        for (int j = 0; j < questionArray.get(index).getQuestionStringArray().size(); j++) {
            String[] words = questionArray.get(index).getQuestionStringArray().get(j).split("(?<!^|[a-zA-Z'])|(?![a-zA-Z'])");
            ArrayList<Text> texts = new ArrayList<>();
            for (int i = 0; i < words.length; i++) {
                if (words[i].contains("\"")) {
                    if (words[i].matches("\".*\"")) {
                        texts.add(new Text(words[i]));
                        continue;
                    }

                    String string = words[i];
                    i++;
                    while (!words[i].equals("\"")) {
                        string += words[i++];
                    }
                    string += words[i];
                    texts.add(new Text(string));
                } else {
                    texts.add(new Text(words[i]));
                }
            }

            for (int i = 0; i < texts.size(); i++) {
                if (containsKeyword(texts.get(i).getText())) {
                    texts.get(i).setFill(Color.DARKGREEN);
                }

                if (texts.get(i).getText().matches("\".*\"")) {
                    texts.get(i).setFill(Color.BLUE);
                }

                questionTF.getChildren().add(texts.get(i));
                total += texts.size();
            }
            if(questionArray.get(index).getQuestionStringArray().size() - 1 == j){
            double origHeight = 600;
            total = total/ 6;
            changeHeight = origHeight;
            System.out.println(total);
            if (total > 500) {
                changeHeight = (changeHeight + (total/7));
                System.out.println(changeHeight);
            }
            create.setPrefHeight(changeHeight);}
            questionTF.getChildren().add(new Text("\n"));
        }
        for (int j = 0; j < questionArray.get(index).getOptions().size(); j++) {
            System.out.println(questionArray.get(index).getKeyArray());
            if (questionArray.get(index).getKeyArray().size() == 1) {

                radioButtonArray.add(new RadioButton(questionArray.get(index).getOptions().get(j)));
                if (vBoxQuest.getHeight() > 300) {
                    changeHeight = (changeHeight + (vBoxQuest.getHeight() / 4));
                    create.setPrefHeight(changeHeight);
                }
               
                radioButtonArray.get(j).setToggleGroup(group);
                vBoxQuest.getChildren().add(radioButtonArray.get(j));
                radioButtonArray.get(j).setToggleGroup(group);
                radioButtonArray.get(j).setWrapText(true);
                radioButtonArray.get(j).setMaxWidth(300);
                if (answerString != null) {
                    if (radioButtonArray.get(j).getText().charAt(0) == (answerString.charAt(0))) {
                        radioButtonArray.get(j).setSelected(true);
                    }
                }
            } else {
               checkBoxArray.add(new CheckBox(questionArray.get(index).getOptions().get(j)));
                checkBoxArray.get(j).setWrapText(true);
                checkBoxArray.get(j).setMaxWidth(300);
                
                if (vBoxQuest.getHeight() > 300) {
                    changeHeight = (changeHeight + (vBoxQuest.getHeight() / 4));
                    create.setPrefHeight(changeHeight);
                }
                vBoxQuest.getChildren().add(checkBoxArray.get(j));

                if (answerString != null) {

                    for (int i = 0; i < answerString.length(); i++) {
                        if (checkBoxArray.get(j).getText().charAt(0) == (answerString.charAt(i))) {
                            checkBoxArray.get(j).setSelected(true);
                        }
                    }
                }
            }
        }
    }

    @FXML
    void goBack() throws Exception {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/selection.fxml"));
        feedback.getScene().setRoot(pane);
    }

    @FXML
    void submit() throws ClassNotFoundException {

        try {
            con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
            ps = con.prepareStatement("select * FROM entries WHERE "
                    + "(username = ?) "
                    + "and (chapter = ?) "
                    + "and (question = ?)");
            ps.setString(1, user);
            ps.setString(2, chapterString);
            ps.setString(3, questionNum.get(current));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                isCorrect = rs.getBoolean("correct");
                attempt = rs.getInt("attempt");
            }
        } catch (SQLException ex) {
            Logger.getLogger(QuestionPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!isCorrect) {
            attempt++;
            boolean correct = false;
            if (questionArray.get(current).getHintArray().size() > 0) {
                for (int j = 0; j < questionArray.get(current).getHintArray().size(); j++) {
                    hintBTN.setVisible(true);
                    hintBTN.setDisable(false);
                }
            }
            if (questionArray.get(current).getKeyArray().size() == 1) {
                String selected = group.getSelectedToggle().toString();
                selected = selected.charAt(selected.indexOf(']') + 2) + "";
                for (int j = 0; j < questionArray.get(current).getKeyArray().size(); j++) {

                    if (questionArray.get(current).getKeyArray().get(j).contains(selected)) {
                        feedback.setImage(check);
                        correct = true;
                    } else {
                        feedback.setImage(xmark);
                    }
                    key = selected;
                }
            } else {
                String selected = "";
                String finSelect = "";
                String keyString = "";
                for (int j = 0; j < questionArray.get(current).getKeyArray().size(); j++) {
                    keyString = keyString + questionArray.get(current).getKeyArray().get(j);
                }

                for (int j = 0; j < checkBoxArray.size(); j++) {
                    if (checkBoxArray.get(j).isSelected()) {
                        selected = checkBoxArray.get(j).toString();
                        finSelect = finSelect + selected.charAt(selected.indexOf(']') + 2);
                    }
                }
                keyString = keyString.trim();
                if (keyString.equals(finSelect.trim())) {
                    feedback.setImage(check);
                    correct = true;
                } else {
                    feedback.setImage(xmark);
                }
                key = finSelect.trim();
            }
            feedback.setVisible(true);
            PreparedStatement ps;
            try {
                //con = DriverManager.getConnection("jdbc:derby://localhost/CSCI5520TeamProject", "dustin", "dustin");
                con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
                delete(user, chapterString, questionNum.get(current));
                ps = con.prepareStatement("insert into entries values(?,?,?,?,?,?,?,?)");
                ps.setString(1, user);
                ps.setString(2, chapterString);
                ps.setString(3, questionNum.get(current));
                ps.setBoolean(4, correct);
                ps.setInt(5, attempt);
                ps.setNull(6, Types.TIME);
                ps.setString(7, key);
                ps.setNull(8, Types.DATE);

                int k = ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(QuestionPresenter.class.getName()).log(Level.SEVERE, null, ex);
            }

            //insert sql insert here
        } else {
            hintTF.getChildren().clear();
            hintTF.getChildren().add(new Text("Can't submit question that is already answered correctly"));
        }
        System.out.println(attempt);
    }

    @FXML
    void showHint() {
        Text hint = new Text();
        for (int j = 0; j < questionArray.get(current).getHintArray().size(); j++) {
            hint.setText(hint.getText().toString() + " " + questionArray.get(current).getHintArray().get(j));
        }
        hintTF.getChildren().clear();
        hintTF.getChildren().add(hint);
    }

    @FXML
    private void forwardQuest() {
        current++;
        if (current >= questionNum.size()) {
            current = 0;
        }
        QuestionToScene(current);
    }

    @FXML
    private void backQuest() {
        current--;
        if (current < 0) {
            current = questionNum.size() - 1;
        }

        QuestionToScene(current);
    }

    public void delete(String user, String chapter, String question) throws ClassNotFoundException, SQLException {
        con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
        ps = con.prepareStatement("DELETE FROM entries WHERE "
                + "(username = ?) "
                + "and (chapter = ?) "
                + "and (question = ?)");
        ps.setString(1, user);
        ps.setString(2, chapter);
        ps.setString(3, question);
        ps.executeUpdate();
    }

    boolean containsKeyword(String line) {
        String KEYWORDS[] = {"abstract", "assert", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while"};

        for (int i = 0; i < KEYWORDS.length; i++) {
            if (line.contains(KEYWORDS[i])) {
                return true;
            }
        }
        return false;
    }
}
