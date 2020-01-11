package entities;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpController {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSurname;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblError;

    private static String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

    private void errorHandler(String errorMessage) {
        lblError.setText(errorMessage);
    }

    private boolean checkIfValidEmail(String email) {
        Pattern pattern = java.util.regex.Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean checkIfValidFields(String name, String surname, String email, String username, String password) {
        if(name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorHandler("Please insert all required fields.");
            return false;
        }

        if(!checkIfValidEmail(email)) {
            errorHandler("Invalid email adress.");
            return false;
        }


        return true;
    }


    private static String insertUsers = "insert into users values (default, :username, sha1(:password), :email, :name, :surname, 0, 1)";


    public void insertUser(ActionEvent actionEvent) {
        String name = txtName.getText().trim();
        String surname = txtSurname.getText().trim();
        String email = txtEmail.getText().trim();
        String username = txtUser.getText().trim();
        String password = txtPassword.getText().trim();

        if(!checkIfValidFields(name, surname, email, username, password))
            return;

        errorHandler("");

        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();


        try {
            SQLQuery query = session.createSQLQuery(insertUsers);
            query.setParameter("username", username);
            query.setParameter("password", password);
            query.setParameter("email", email);
            query.setParameter("name", name);
            query.setParameter("surname", surname);

            query.executeUpdate();

            System.out.println("COMMIT");

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        showNewScene(actionEvent, "../FXML/Login.fxml");


    }

    private void showNewScene(ActionEvent event, String path) {
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            //stage.setMaximized(true);
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(path)));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}