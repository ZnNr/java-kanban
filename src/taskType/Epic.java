package taskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    protected List<Integer> subtaskIds;

    public Epic(String title, String description) {
        super(title, description);
        subtaskIds = new ArrayList<>();
    }

    /*
        public Epic(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
            super(title, description, status, duration, startTime);
            subtaskIds = new ArrayList<>();
        }
    */
    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public Integer getSubtask(Integer id) {
        return getSubtaskIds().get(id);
    }

    /*
        public void setSubtaskIds(int id) {
            subtaskIds.add(id);}

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }
        @Override
        public LocalDateTime getEndTime() {
            return endTime;
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
    */

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", subTaskIds=" + subtaskIds +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
