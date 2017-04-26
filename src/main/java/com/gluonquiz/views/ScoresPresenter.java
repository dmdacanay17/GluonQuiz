package com.gluonquiz.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ScoresPresenter {

    @FXML
    private View scores;

    @FXML
    private Button backBTN;

    @FXML
    private TextFlow scoreFlow;

    @FXML
    private Accordion scoresAccord;

    Connection con;
    PreparedStatement ps;
    String user;

    ArrayList<TitledPane> titledPanes = new ArrayList<>();

    int total = 0;
    int totalAns = 0;
    int totalAttempt = 0;
    int firstAttempt = 0;
    int numCorrect = 0;

    private Text helloText = new Text();

    public void initialize() throws ClassNotFoundException, SQLException {
        try {
            con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
            ps = con.prepareStatement("SELECT * FROM currentuser");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user = rs.getString("username");
            }
            ps = con.prepareStatement("select * FROM entries WHERE username = ?");
            ps.setString(1, user);
            rs = ps.executeQuery();
            while (rs.next()) {
                setSummaryData(rs.getBoolean("correct"), rs.getInt("attempt"));
            }

            titledPanes.add(new TitledPane("All Chapters",
                    new Text(totalAns + "/" + total + " Attempted out of Total\n"
                            + numCorrect + "/" + totalAns + " Correct out of Attempted\n"
                            + (numCorrect - firstAttempt) + "/" + totalAns + " Correct on First Try\n"
                            + (double) totalAttempt / (double) totalAns + " Average Number of Attempts"
                    )));

            for (int i = 1; i < 45; i++) {
                totalAns = total = numCorrect = firstAttempt = totalAttempt = 0;
                ps = con.prepareStatement("select * FROM entries WHERE "
                        + "(username = ?) "
                        + "and (chapter = ?)");
                ps.setString(1, user);
                ps.setString(2, Integer.toString(i));
                rs = ps.executeQuery();
                while (rs.next()) {
                    setSummaryData(rs.getBoolean("correct"), rs.getInt("attempt"));
                }

                titledPanes.add(new TitledPane("Chapter " + Integer.toString(i),
                        new Text(totalAns + "/" + total + " Attempted out of Total\n"
                                + numCorrect + "/" + totalAns + " Correct out of Attempted\n"
                                + (numCorrect - firstAttempt) + "/" + totalAns + " Correct on First Try\n"
                                + ((double) totalAttempt / (double) totalAns) + " Average Number of Attempts"
                        )));
            }
        } catch (SQLException ex) {
            Logger.getLogger(QuestionPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }

        scoresAccord.getPanes().addAll(titledPanes);
    }

    @FXML
    void goHome() throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/home.fxml"));
        scores.getScene().setRoot(pane);
    }

    void setSummaryData(boolean correct, int attempt) {
        total++;
        if (attempt > 0) {
            totalAns++;
            totalAttempt = totalAttempt + attempt;
            if (attempt == 1) {
                firstAttempt++;
            }
        }
        if (correct) {
            numCorrect++;
        }
    }

}
