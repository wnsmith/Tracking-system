package entities;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Iterator;
import java.util.List;

@Entity
@DiscriminatorValue("0")
public class RegularUser extends User {

    // data manipulation
    private static final String readQuery = "select * from tasks where (projectConcerned = (select userId from users where username = :usr) or projectConcerned is null) and active = 1";

    @Override
    public void readTasks(TableView data, ObservableList<Task> listOfTasks) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            SQLQuery readingData = session.createSQLQuery(readQuery);
            readingData.setParameter("usr", this.getUsername());
            readingData.addEntity(Task.class);


            List<Task> tasks = readingData.list();
            Iterator<Task> iterator = tasks.iterator();


            while (iterator.hasNext()) {
                Task task = iterator.next();
                task.setUsernameFromId();
                listOfTasks.add(task);


            }

            data.setItems(listOfTasks);

            transaction.commit();
            System.out.println("COMMIT");
        } catch (Exception e) {
            System.out.println("Reading tasks failed");
            transaction.rollback();
        } finally {
            session.close();
        }
    }




    /* regular user can't delete or create a task */

    @Override
    public void deleteTask(int id) {
        System.out.println("You do not have permission");
    }

    @Override
    public void createTask(String ID, String shortname, String date, String TO) {
        System.out.println("You do not have permission");
    }


}

