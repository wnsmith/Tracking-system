package entities;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;

// mapping table tasks

@Entity
@Table(name = "tasks")
public class Task {

    private static final String query = "select username from users where userId = :id";
    private static final String querySet = "select userId from users where username = :usr";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId", updatable = false, nullable = false)
    private int taskId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "description")
    private String description;

    @Column(name = "projectConcerned", insertable = false, updatable = false)
    private Integer projectConcerned;

    /*
    instead of deleting task from database set active = 0
    default: active = 1
    */
    @Column(name = "active")
    private int active;

    @Column(name = "shortname")
    private String shortname;

    @Transient
    private String username;


    public String getUsername() {
        return username;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProjectConcerned() {
        return projectConcerned;
    }

    public void setProjectConcerned(Integer projectConcerned) {
        this.projectConcerned = projectConcerned;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    /*
    mapping relation between username and userId for projectConcerned column in table tasks

     */


    public void setUsernameFromId() {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        String username = null;
        try {
            SQLQuery findUsername = session.createSQLQuery(query);
            findUsername.setParameter("id", this.projectConcerned);
            username = (String) findUsername.uniqueResult();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            session.close();
        }

        this.username = username;

    }

    public void setProjectConcernedFromUsername() {
        if (this.username == null) {
            this.projectConcerned = null;
            return;
        }

        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Integer projectConcerned = 0;
        try {
            SQLQuery findUsername = session.createSQLQuery(querySet);
            findUsername.setParameter("usr", this.username);
            projectConcerned = (Integer) findUsername.uniqueResult();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            session.close();
        }

        this.projectConcerned = projectConcerned;
    }

}
