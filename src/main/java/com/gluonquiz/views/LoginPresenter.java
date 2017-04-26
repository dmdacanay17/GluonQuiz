package com.gluonquiz.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LoginPresenter {

    @FXML
    private View login;

    @FXML
    private TextFlow flagFlow;

    @FXML
    private TextField userTF, passTF;

    private Text flagText = new Text();

    private String password = "";

    public void initialize() {
        flagFlow.setMaxWidth(flagFlow.getPrefWidth());
    }

    @FXML
    void createAction() throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/create.fxml"));
        flagFlow.getScene().setRoot(pane);
    }

    @FXML
    void loginAction() throws IOException, SQLException, ClassNotFoundException {
        boolean go = true;

        flagText.setText("");
        if (userTF.getText().trim().equals("")) {
            flagText.setText(flagText.getText() + "**Username Cannot be Empty\n");
            go = false;
        } else if (!containsDB(userTF.getText())) {
            flagText.setText(flagText.getText() + "**Username Doesn't Exist\n");
            go = false;
        }
        if (passTF.getText().trim().equals("")) {
            flagText.setText(flagText.getText() + "**Password Cannot be Empty\n");
            go = false;
        }
flagText.setFont(Font.font("Times", FontWeight.BOLD, 14));
        flagFlow.getChildren().clear();
        flagFlow.getChildren().add(flagText);
        if (go) {
            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("org.sqlite.JDBC");
             Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username = ? ");
            try {
                ps.setString(1, userTF.getText());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    go = rs.getString("password").equals(passTF.getText());
                }

                Statement statement = con.createStatement();
                statement.executeUpdate("DELETE FROM currentuser");
                ps = con.prepareStatement("insert into currentuser values(?)");
                ps.setString(1, userTF.getText());
                int i = ps.executeUpdate();
            } catch (SQLException ex) {
                go = false;
            }
        }
        if (go) {
            Parent pane = FXMLLoader.load(getClass().getResource("../views/home.fxml"));
            flagFlow.getScene().setRoot(pane);
        }
    }

    public boolean containsDB(String user) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Class.forName("org.sqlite.JDBC");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
        PreparedStatement ps = con.prepareStatement("SELECT username FROM users WHERE username = ? ");
        try {
            ps.setString(1, userTF.getText().toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception ex) {
        }
        return false;
    }

}
