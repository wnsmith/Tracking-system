package entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HomeController extends LoginController implements Initializable {

    private final String regex = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";


    // data manipulation
    private static final String usersId = "select * from users";
    private static final String tasksId = "select * from tasks where active = 1";

    ObservableList<Task> listOfTasks = FXCollections.observableArrayList();


    // javafx controls

    // save section
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtShortname;
    @FXML
    private DatePicker txtDOB;
    @FXML
    private Button btnSave;
    @FXML
    private ComboBox<String> txtUser;
    @FXML
    private Label lblStatus;


    @FXML
    TableView tblData;

    // table columns
    @FXML
    private TableColumn id;

    @FXML
    private TableColumn status;

    @FXML
    private TableColumn description;

    @FXML
    private TableColumn concerned;

    @FXML
    private TableColumn deadline;

    @FXML
    private TableColumn shortname;

    // delete section
    @FXML
    private ComboBox<String> txtDelete;

    @FXML
    private Button deleteButton;


    @FXML
    private Label lblError;


    private void errorHandler(String errorMessage) {
        lblError.setText(errorMessage);
    }


    private void specifieTableColumns() {
        status.setCellFactory(TextFieldTableCell.forTableColumn());
        description.setCellFactory(TextFieldTableCell.forTableColumn());
        shortname.setCellFactory(TextFieldTableCell.forTableColumn());
        deadline.setCellFactory(TextFieldTableCell.forTableColumn());

        id.setCellValueFactory(new PropertyValueFactory<Task, Integer>("taskId"));
        status.setCellValueFactory(new PropertyValueFactory<Task, String>("status"));
        description.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        shortname.setCellValueFactory(new PropertyValueFactory<Task, String>("shortname"));
        deadline.setCellValueFactory(new PropertyValueFactory<Task, String>("deadline"));
        concerned.setCellValueFactory(new PropertyValueFactory<Task, String>("username"));
    }

    private void setColumnEditable(boolean edit) {
        tblData.setEditable(edit);
        status.setEditable(edit);
        description.setEditable(edit);
        shortname.setEditable(edit);
        deadline.setEditable(edit);
    }

    private void setFieldVisible(boolean visible) {
        txtUser.setVisible(visible);
        txtShortname.setVisible(visible);
        txtDelete.setVisible(visible);
        txtDOB.setVisible(visible);
        txtDescription.setVisible(visible);
        btnSave.setVisible(visible);
        deleteButton.setVisible(visible);
    }


    private boolean checkIfValidDate(String date) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    private boolean checkIfValidStatus(String status) {
        return status.equalsIgnoreCase("new") || status.equalsIgnoreCase("in progress") || status.equalsIgnoreCase("finished");
    }


    /*
    set valid values for /delete task/ and /select user/ combo box
     */
    private void setComboValues(boolean ifUsers, boolean ifTasks) {


        if (ifUsers) {
            txtUser.getItems().clear();
            Session session = HibernateUtil.getSession();
            Transaction transaction = session.beginTransaction();
            try {
                SQLQuery query = session.createSQLQuery(usersId);
                query.addEntity(User.class);
                List<User> users = query.list();
                Iterator<User> iterator = users.iterator();


                ObservableList<String> options = FXCollections.observableArrayList();
                while (iterator.hasNext()) {
                    User u = iterator.next();
                    options.add(u.getUsername());
                }

                transaction.commit();
                txtUser.getItems().addAll(options);
                txtUser.getItems().add("---");
            } catch (Exception e) {
                transaction.rollback();
            } finally {
                session.close();
            }
        }

        if (ifTasks) {
            txtDelete.getItems().clear();
            Session session = HibernateUtil.getSession();
            Transaction transaction = session.beginTransaction();
            try {
                SQLQuery query = session.createSQLQuery(tasksId);
                query.addEntity(Task.class);
                List<Task> tasks = query.list();
                Iterator<Task> iterator = tasks.iterator();


                ObservableList<String> options = FXCollections.observableArrayList();
                while (iterator.hasNext()) {
                    Task t = iterator.next();
                    options.add(String.valueOf(t.getTaskId()));
                }

                transaction.commit();
                txtDelete.getItems().addAll(options);

            } catch (Exception e) {
                transaction.rollback();
            } finally {
                session.close();
            }
        }


    }

    // initial setup for regular user
    // regular user can't delete or create task
    private void setUp() {
        setColumnEditable(true);
        specifieTableColumns();
    }

    // admin can change concerned column
    private void setUpForAdmin() {

        setFieldVisible(true);

        concerned.setCellFactory(TextFieldTableCell.forTableColumn());
        concerned.setEditable(true);

        setComboValues(true, true);

    }

    private void setUpForManager() {
        txtDelete.setVisible(false);
        deleteButton.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFieldVisible(false);

        setUp();
        if (Main.currentUser instanceof Admin || Main.currentUser instanceof Manager)
            setUpForAdmin();


        if(Main.currentUser instanceof Manager)
            setUpForManager();


    }

    public void displayData(ActionEvent event) {
        tblData.getItems().clear();
        Main.currentUser.readTasks(tblData, listOfTasks);
    }


    // save new task
    public void saveTask(ActionEvent event) {
        if (txtUser.getSelectionModel().isEmpty())
            return;
        ;

        // set fields
        String username = txtUser.getValue();
        String shortname = txtShortname.getText();
        String description = txtDescription.getText();
        String date = txtDOB.getValue() == null ? "" : txtDOB.getValue().toString();


        Main.currentUser.createTask(shortname, description, date, username);

        //reset combo values
        setComboValues(false, true);
        displayData(event);

    }

    // update button handler
    public void update(ActionEvent event) {
        Main.currentUser.modifyTask(tblData);
        displayData(event);
        errorHandler("");
    }



    /* HANDLING CHANGES IN TABLE */

    public void onEditStatus(TableColumn.CellEditEvent cellEditEvent) {
        Task task = (Task) tblData.getSelectionModel().getSelectedItem();
        String status = cellEditEvent.getNewValue().toString();
        if (!checkIfValidStatus(status)) {
            errorHandler("Invalid status. Required new/in progress/finished");
            return;
        }
        task.setStatus(status);

    }

    public void onEditDesc(TableColumn.CellEditEvent cellEditEvent) {
        Task task = (Task) tblData.getSelectionModel().getSelectedItem();
        String description = cellEditEvent.getNewValue().toString();
        task.setDescription(description.equals("") ? null : description);
    }

    public void onEditDeadline(TableColumn.CellEditEvent cellEditEvent) {
        Task task = (Task) tblData.getSelectionModel().getSelectedItem();
        String date = cellEditEvent.getNewValue().toString();
        if (date.trim().equals("")) {
            task.setDeadline(null);
            return;
        }
        if (!checkIfValidDate(date)) {
            errorHandler("Incorrect date. Please enter date in format yyyy-mm-dd");
            return;
        }
        task.setDeadline(date.equals("") ? null : date);
    }

    public void onEditName(TableColumn.CellEditEvent cellEditEvent) {
        Task task = (Task) tblData.getSelectionModel().getSelectedItem();
        String name = cellEditEvent.getNewValue().toString();
        task.setShortname(name.equals("") ? null : name);

    }

    public void onEditConcerned(TableColumn.CellEditEvent cellEditEvent) {
        Task task = (Task) tblData.getSelectionModel().getSelectedItem();
        ObservableList<String> list = txtUser.getItems();
        String username = cellEditEvent.getNewValue().toString();
        if (!list.contains(username) && !username.trim().equals("")) {
            errorHandler("Invalid username.");
            return;
        }

        //set new value for username and userId
        task.setUsername(username);
        task.setProjectConcernedFromUsername();
    }


    // delete button handler
    public void deleteTask(ActionEvent actionEvent) {
        if (txtDelete.getSelectionModel().isEmpty())
            return;
        Main.currentUser.deleteTask(Integer.valueOf(Integer.valueOf(txtDelete.getValue())));
        // remove value from combobox
        txtDelete.getItems().remove(txtDelete.getValue());
        displayData(actionEvent);

    }

    // signOut button handler
    // back to login screen
    public void signOut(ActionEvent actionEvent) {
        Main.currentUser = null;
        try {
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            //stage.setMaximized(true);
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../FXML/Login.fxml")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}