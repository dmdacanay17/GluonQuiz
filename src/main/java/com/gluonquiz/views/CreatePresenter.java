package com.gluonquiz.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class CreatePresenter {

    @FXML
    private View create;

    @FXML
    private TextField codeTF, firstNameTF, lastNameTF, userTF, passTF, confirmTF;

    @FXML
    private TextFlow flagFlow;

   // @FXML
    //private ComboBox typeCB;

    private Text flagText = new Text();
    private String type = "S";

    public void initialize() {
       // typeCB.getItems().addAll("Student", "Teacher");
    }
//
//    @FXML
//    void dropType() {
//        type = (String) typeCB.getSelectionModel().getSelectedItem();
//        if (type.equals("Teacher")) {
//            type = "T";
//        } else {
//            type = "S";
//        }
//    }

    @FXML
    void backAction() throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("../views/login.fxml"));
        flagFlow.getScene().setRoot(pane);
    }

    @FXML
    void createAction() throws IOException, ClassNotFoundException, SQLException {
        boolean go = true;

        flagText.setText("");
        if (userTF.getText().toString().trim().equals("")) {
            flagText.setText(flagText.getText().toString() + "Username Cannot be Empty\n");
            go = false;
        }
        if (containsDB(userTF.getText().toString())) {
            flagText.setText(flagText.getText().toString() + "Username Already Taken\n");
            go = false;
        }
        if (passTF.getText().toString().trim().equals("")) {
            flagText.setText(flagText.getText().toString() + "Password Cannot be Empty\n");
            go = false;
        }
        if (firstNameTF.getText().toString().trim().equals("")) {
            flagText.setText(flagText.getText().toString() + "Fisrt Name Cannot be Empty\n");
            go = false;
        }
        if (lastNameTF.getText().toString().trim().equals("")) {
            flagText.setText(flagText.getText().toString() + "Last Name Cannot be Empty\n");
            go = false;
        }
        if (type.trim().equals("")) {
            flagText.setText(flagText.getText().toString() + "You must select a type\n");
            go = false;
        }
        //if (type.trim().equals("S") && !containsDBClass(codeTF.getText().toString())) {
          //  flagText.setText(flagText.getText().toString() + "Code Doesn't Exist\n");
        //    go = false;
        //}
        if (type.trim().equals("T") && !codeTF.getText().toString().trim().equals("")) {
            flagText.setText(flagText.getText().toString() + "Teachers Can't Have a Code\n");
            go = false;
        }
        if (!passTF.getText().toString().trim().equals(confirmTF.getText().toString().trim())) {
            flagText.setText(flagText.getText().toString() + "Passwords do not match\n");
            go = false;
        }
        if (go) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Class.forName("org.sqlite.JDBC");
                System.out.println("Driver loaded");
                Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
                System.out.println("Database connected");
                PreparedStatement ps = con.prepareStatement("insert into users values(?,?,?,?,?,?)");
                ps.setString(1, userTF.getText().toString());
                ps.setString(2, passTF.getText().toString());
                ps.setString(3, firstNameTF.getText().toString());
                ps.setString(4, lastNameTF.getText().toString());
                ps.setString(5, "S");
                ps.setString(6, "");

                int i = ps.executeUpdate();
                if (i > 0) {
                    if (type.equals("T")) {
                        flagText.setText("Sucessfully Registered\nHit Back to Login");
                    } else if (type.equals("S") && createAccoutDB(userTF.getText().toString())) {
                        flagText.setText("Sucessfully Registered\nHit Back to Login");
                    } else {
                        flagText.setText("Unsucessfully Registered");
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        flagFlow.getChildren().clear();
        flagFlow.getChildren().add(flagText);

    }

    public boolean containsDB(String user) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Class.forName("org.sqlite.JDBC");
        System.out.println("Driver loaded");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
        System.out.println("Database connected");
        PreparedStatement ps = con.prepareStatement("SELECT username FROM users WHERE username = ? ");
        try {
            ps.setString(1, userTF.getText().toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception ex) {
        }
        return false;
    }

    public boolean containsDBClass(String code) throws ClassNotFoundException, SQLException {
        if (code.trim().equals("")) {
            return true;
        }
        Class.forName("com.mysql.jdbc.Driver");
        Class.forName("org.sqlite.JDBC");
        System.out.println("Driver loaded");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
        System.out.println("Database connected");
        PreparedStatement ps = con.prepareStatement("SELECT class FROM classes WHERE class = ? ");
        try {
            ps.setString(1, userTF.getText().toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception ex) {
        }
        return false;
    }

    private boolean createAccoutDB(String username) {
        try {
        Class.forName("com.mysql.jdbc.Driver");
        Class.forName("org.sqlite.JDBC");
        System.out.println("Driver loaded");
        Connection con = DriverManager.getConnection("jdbc:mysql://liang.armstrong.edu:3306/team3", "team3", "tiger");
        System.out.println("Database connected");
        for (int i = 1; i < 45; i++) {
            for (int j = 1; j < findQuestionAmount(Integer.toString(i)) + 1; j++) {
                    PreparedStatement ps = con.prepareStatement("insert into entries values(?,?,?,?,?,?,?,?)");
                    ps.setString(1, userTF.getText().toString());
                    ps.setString(2, Integer.toString(i));
                    ps.setString(3, Integer.toString(j));
                    ps.setBoolean(4, false);
                    ps.setInt(5, 0);
                    ps.setNull(6, Types.TIME);
                    ps.setNull(7, Types.VARCHAR);
                    ps.setNull(8, Types.DATE);

                    int k = ps.executeUpdate();
                    if (k == 0) {
                        return false;
                    }
            }
        }
        } catch (Exception ex) {
                    System.out.println(ex);
                }
        return true;
    }

    private int findQuestionAmount(String chapNum) {
        int i = 0;
        try {
            String chapterFile = "mcquestions/chapter" + chapNum + ".txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(chapterFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals("#")) {
                        line = reader.readLine();
                        line = line + "    ";
                        if (!line.substring(0, line.indexOf(" ")).toLowerCase().contains("section")) {
                            i++;
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        return i;
    }

}
