package taskType;

import enumconstants.Status;

import java.util.*;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String description, String title, Status status, int epicId) {
        super(description, title, status);
        this.epicId = epicId;
    }

    // получить id эпика
    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + getEpicId() +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}