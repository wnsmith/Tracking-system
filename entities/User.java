package entities;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;


/*
mapping table users

user can be admin or regular user
creating one table per hierarchy with discriminator column ---> role
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.INTEGER)
@Table(name = "users")
public abstract class User {

    private static final String updateQuery = "update tasks set projectConcerned = :pc, description = :des, shortname = :sn, deadline = :dat, status = :st where taskId = :id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private int userId;

    @Column(name = "username", nullable = false)
    private String username;

    // sha1 encrypted password string
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "email")
    private String email;

    // role for existing user - 1 for admin, 0 for regular user
    @Column(name = "role", updatable = false, insertable = false)
    private int role;

    /*
    instead of deleting user from database set active = 0
    default: active = 1
     */
    @Column(name = "active")
    private int active;

    public int getRole() {
        return role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void modifyTask(TableView data) {


        Task task = new Task();


        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        SQLQuery query = session.createSQLQuery(updateQuery);
        // reading data from table

        try {
            for (int i = 0; i < data.getItems().size(); i++) {
                task = (Task) data.getItems().get(i);

                // regular user cannot modify
               if(this instanceof RegularUser && task.getUsername() == null)
                      continue;
               


                query.setParameter("pc", task.getProjectConcerned());
                query.setParameter("id", task.getTaskId());
                query.setParameter("des", task.getDescription());
                query.setParameter("sn", task.getShortname());
                query.setParameter("dat", task.getDeadline());
                query.setParameter("st", task.getStatus());
                synchronized (this) {
                    try {
                        query.executeUpdate();
                        task.setUsernameFromId();
                        System.out.println("Success!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            transaction.commit();
            System.out.println("COMMIT");

        } catch (Exception e) {
            System.out.println("Update failed!");
            transaction.rollback();
        } finally {
            session.close();
        }


    }


    public abstract void deleteTask(int id);

    public abstract void readTasks(TableView data, ObservableList<Task> listOfTasks);

    public abstract void createTask(String ID, String shortname, String date, String TO);


}
