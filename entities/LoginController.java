package entities;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    // user validation queries
    private static final String checkForUser = "select * from users where username = :usr";
    private static final String encryptPassword = "select sha1(:pass)";


    // javafx controls

    @FXML
    private Label lblErrors;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignin;


    private void errorHandler(String errorMessage) {
        lblErrors.setText(errorMessage);
    }

    private String encryptPassword(String password) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        String encryptedPassword = null;

        try {
            SQLQuery query = session.createSQLQuery(encryptPassword);
            query.setParameter("pass", password);

            encryptedPassword = (String) query.uniqueResult();

            transaction.commit();
            session.close();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            session.close();
        }

        return encryptedPassword;

    }


    // return 1 if current user is admin, 0 otherwise
    private int determineRole() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            SQLQuery resolveRole = session.createSQLQuery(checkForUser);
            resolveRole.addEntity(User.class);
            resolveRole.setParameter("usr", txtUsername.getText());
            List<User> users = resolveRole.list();
            Iterator<User> iterator = users.iterator();
            int role = 0;
            while (iterator.hasNext()) {
                role = iterator.next().getRole();
                break;
            }

            transaction.commit();
            return role;
        } catch (Exception e) {
            System.out.println("Failed");
            transaction.rollback();
        } finally {
            session.close();
        }

        return 0;

    }

    private boolean checkEmptyFields() {
        return txtPassword.getText().isEmpty() || txtUsername.getText().isEmpty();
    }

    // set new scene after login in
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    // signIn button handler
    @FXML
    public void onButtonSignIn(ActionEvent event) {
        if (checkEmptyFields()) {
            errorHandler("Insert username and password");
            return;
        }

        String username = txtUsername.getText();
        String password = txtPassword.getText();


        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();



        try {
            SQLQuery checkUser = session.createSQLQuery(checkForUser);
            checkUser.addEntity(User.class);
            checkUser.setParameter("usr", username);

            User checking = (User) checkUser.uniqueResult();

            if (checking == null) {
                transaction.commit();
                session.close();
                errorHandler("No such user. Sign up to continue.");
                return;
            } else if (checking != null) {
                String encryptedPassword = encryptPassword(password);
                if (!checking.getPassword().equals(encryptedPassword)) {
                    transaction.commit();
                    session.close();
                    errorHandler("Invalid password");
                    return;
                } else {


                    int x = determineRole();
                    if(x == 1) {
                        Main.currentUser = new Admin();
                    }
                    else if(x == 2) {
                        Main.currentUser = new Manager();
                    }
                    else {
                        Main.currentUser = new RegularUser();
                    }
                    //Main.currentUser = determineRole() == 1 ? new Admin() : new RegularUser();
                    Main.currentUser.setUsername(username);


                    System.out.println("COMMIT");
                    showNewScene(event, "../FXML/Board.fxml");
                    transaction.commit();


                }

            }


        } catch (Exception e) {
            transaction.rollback();
        } finally {
            session.close();
        }


    }


    public void onButtonSignup(ActionEvent actionEvent) {
        showNewScene(actionEvent, "../FXML/SignUp.fxml");

    }
}

