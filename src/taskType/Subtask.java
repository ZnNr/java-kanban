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

    // получить id эпика
    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    /*
        public Subtask(String title, String description, Status status, int epicId) {
            super(title, description, status);
            this.epicId = epicId;
        }
    /*
        public Subtask(String title, String description) {
            super(title, description);
        }

        public Subtask(String title, String description, Duration duration, LocalDateTime startTime) {
            super(title, description, duration, startTime);
        }


        public Subtask(String title, String description, Status status, Duration duration, LocalDateTime startTime, int epicId) {
            super(title, description, status, duration, startTime);
            this.epicId = epicId;
        }
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    /*
        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), epicId);
        }
    */
    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + getEpicId() +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration='" + getDuration() +
                '}';
    }
}