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
@DiscriminatorValue("1")
public class Admin extends User {

    // data manipulation
    private static final String readQuery = "select * from tasks where active = 1";
    private static final String deleteQuery = "update tasks set active = 0 where taskId = :taskId";
    private static final String insertQuery = "insert into tasks values(default, :st, :dat, :des, :sn, :pc, 1)";


    // read all tasks from database
    @Override
    public void readTasks(TableView data, ObservableList<Task> listOfTasks) {

        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            SQLQuery readingData = session.createSQLQuery(readQuery);
            readingData.addEntity(Task.class);


            List<Task> tasks = readingData.list();
            Iterator<Task> iterator = tasks.iterator();


            while (iterator.hasNext()) {
                Task task = iterator.next();
                task.setUsernameFromId();
                listOfTasks.add(task);


            }
            // put data in dable
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

    // update tasks, set active = 0
    @Override
    public void deleteTask(int taskId) {


        Session session = HibernateUtil.getSession();
        SQLQuery delete = session.createSQLQuery(deleteQuery);
        Transaction transaction = session.beginTransaction();

        try {
            delete.setParameter("taskId", taskId);
            synchronized (this) {
                try {
                    delete.executeUpdate();
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Success!");
        } catch (Exception e) {
            System.out.println("Deleting failed");
            transaction.rollback();
        } finally {
            session.close();
            System.out.println("COMMIT");

        }


    }

    @Override
    public void createTask(String description, String shortname, String date, String TO) {

        Session session = HibernateUtil.getSession();
        Task newTask = new Task();
        Transaction transaction = session.beginTransaction();
        SQLQuery query = session.createSQLQuery(insertQuery);


        try {

            /*
            set values for new task

             */

            newTask.setDescription(description.isEmpty() ? null : description);
            newTask.setDeadline(date.isEmpty() ? null : date);
            newTask.setActive(1);
            newTask.setStatus("new");
            newTask.setShortname(shortname.isEmpty() ? null : shortname);

            // set project concerned column from given username
            if (TO.trim().equals(" ")) {
                newTask.setProjectConcerned(null);
            } else {
                newTask.setUsername(TO);
                newTask.setProjectConcernedFromUsername();
            }


            query.setParameter("dat", newTask.getDeadline());
            query.setParameter("pc", newTask.getProjectConcerned());
            query.setParameter("des", newTask.getDescription());
            query.setParameter("sn", newTask.getShortname());
            query.setParameter("st", "new");


            synchronized (this) {
                try {
                    query.executeUpdate();
                    transaction.commit();
                    System.out.println("Success!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            System.out.println("Inserting failed");
            transaction.rollback();
        } finally {
            session.close();
            System.out.println("COMMIT");
        }
    }
}
