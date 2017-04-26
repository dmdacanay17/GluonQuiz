package com.gluonquiz.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class HomePresenter {

    @FXML
    private View home;

    @FXML
    private Label hello;

    @FXML
    private TextFlow helloFlow;

    private String user;
    private String first;
    private String last;

    private Text helloText = new Text();

    public void initialize() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HomePresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
        PreparedStatement ps = con.prepareStatement("SELECT * FROM currentuser");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            user = rs.getString("username");
        }
        ps = con.prepareStatement("select * from users where username =?");
        ps.setString(1, user);
        rs = ps.executeQuery();
        while (rs.next()) {
            first = rs.getString("firstname");
            last = rs.getString("lastname");
        }
        helloText.setText("Hello, " + first + " " + last + "!");
        helloFlow.getChildren().clear();
        helloFlow.getChildren().add(helloText);
    }

    @FXML
    void goSelection() throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/selection.fxml"));
        home.getScene().setRoot(pane);
    }

    @FXML
    void goScores() throws IOException, SQLException, ClassNotFoundException {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/scores.fxml"));
        home.getScene().setRoot(pane);
    }

    @FXML
    void goLogout() throws IOException, SQLException, ClassNotFoundException {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/login.fxml"));
        home.getScene().setRoot(pane);
    }

    @FXML
    void goAccount() throws IOException, SQLException, ClassNotFoundException {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/account.fxml"));
        home.getScene().setRoot(pane);
    }

}
