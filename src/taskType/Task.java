package taskType;

import constant.Status;

import java.util.Objects;

public class Task {

    private String title; // краткое название задачи
    private String description; // описание задачи
    private int id = 0; // id задачи
    private Status status; // статус - "NEW", "IN_PROGRESS", "DONE"

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    protected Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    protected Task(String title, Status status, int id, String description) {
        this.title = title;
        this.status = status;
        this.id = id;
        this.description = description;
    }

    protected Task(String title, int id, String description) {
        this.title = title;
        this.id = id;
        this.description = description;
    }

    // получить название задачи
    public String getTitle() {
        return title;
    }

    // задать название задачи
    public void setTitle(String title) {
        this.title = title;
    }

    // получить описание задачи
    public String getDescription() {
        return description;
    }

    // задать описание задачи
    public void setDescription(String description) {
        this.description = description;
    }

    // получить id задачи
    public int getId() {
        return id;
    }

    // установить id задачи
    public void setId(int id) {
        this.id = id;
    }

    // получить статус задачи
    public Status getStatus() {
        return status;
    }

    // задать статус задачи
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(title, task.title) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, id, title, status);
    }

    @Override
    public String toString() {
        return "Task{" + "description='" + description + '\'' + ", id=" + id + ", title='" + title + '\'' + ", status=" + status + '}';
    }

}