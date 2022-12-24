package taskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    protected int epicId;

    public Subtask(String title, String description) {
        super(title, description);

    }

    public Subtask(String title, String description, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", epicId=" + epicId +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}