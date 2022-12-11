package tasks;

import status.Status;

import java.util.*;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String description, String title, Status status) {

        super(description, title, status);

    }

    public Epic(String title, Status status, int id, String description) {
        super(title, status, id, description);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", status=" + getStatus() +
                '}';
    }


}