package taskType;

import constant.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id = 1;// id задачи
    protected String title; // краткое название задачи
    protected String description; // описание задачи
    protected Status status; // статус - "NEW", "IN_PROGRESS", "DONE"
    protected LocalDateTime startTime; // время начала
    protected Duration duration; // продолжительность
    protected LocalDateTime endTime;

    public Task(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(0);
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = Duration.ofMinutes(0);
    }

    public Task(String title, String description, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    // получить id задачи
    public int getId() {
        return id;
    }

    // установить id задачи
    public void setId(int id) {
        this.id = id;
    }

    // задать название задачи
    public void setTitle(String title) {
        this.title = title;
    }

    // получить название задачи
    public String getTitle() {
        return title;
    }

    // задать описание задачи
    public void setDescription(String description) {
        this.description = description;
    }

    // получить описание задачи
    public String getDescription() {
        return description;
    }

    // задать статус задачи
    public void setStatus(Status status) {
        this.status = status;
    }

    // получить статус задачи
    public Status getStatus() {
        return status;
    }

    //установить время начала
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    //установить время завершения
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    //получить время завершения
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // получить время начала
    public LocalDateTime getStartTime() {
        return startTime;
    }

    //установить продолжительность
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    //получить продолжительность
    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id
                && Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}