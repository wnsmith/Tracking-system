package entities;


import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("2")
public class Manager extends User {

    public void deleteTask(int id) {

    }

    public void readTasks(TableView data, ObservableList<Task> listOfTasks) {
        Admin admin = new Admin();
        admin.setUserId(super.getUserId());
        admin.readTasks(data, listOfTasks);
    }

    public  void createTask(String ID, String shortname, String date, String TO) {
        Admin admin = new Admin();
        admin.setUserId(super.getUserId());
        admin.createTask(ID, shortname, date, TO);
    }

}
