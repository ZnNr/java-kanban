package taskManagers;

import constant.Status;
import constant.TaskType;
import taskManagers.historyManaghers.HistoryManager;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileStringFormatter {

    // Метод для сохранения истории в CSV
    static String historyToString(HistoryManager manager) {
        StringBuilder historyIds = new StringBuilder();

        if (manager.getHistory().isEmpty()) {
            return historyIds.append("0").toString();

        } else

            for (Task task : manager.getHistory()) {
                historyIds.append(task.getId()).append(',');
            }

        return historyIds.toString();
    }

    // Метод восстановления менеджера истории из CSV
    public static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] taskArray = value.split(",");

        for (String element : taskArray) {
            historyIds.add(Integer.valueOf(element));
        }

        return historyIds;
    }

    // Метод сохранения задачи в строку
    public static String toString(Task task) {
        String id = String.valueOf(task.getId());
        String type;
        String title = task.getTitle();
        String status = String.valueOf(task.getStatus());
        String epic;
        String description = task.getDescription();
        String duration = task.getDuration().toString();
        String startTime;
        String endTime;

        if (task instanceof Epic) {
            type = TaskType.EPIC.name();
            epic = "";
            if (task.getStartTime() != null && task.getEndTime() != null) {
                startTime = task.getStartTime().toString();
                endTime = task.getEndTime().toString();

            } else {
                startTime = "null";
                endTime = "null";
            }
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK.name();
            epic = String.valueOf(((Subtask) task).getEpicId());
            if (task.getStartTime() != null && task.getEndTime() != null) {
                startTime = task.getStartTime().toString();
                endTime = task.getEndTime().toString();

            } else {
                startTime = "null";
                endTime = "null";
            }
        } else {
            type = TaskType.TASK.name();
            epic = "";
            if (task.getStartTime() != null && task.getEndTime() != null) {
                startTime = task.getStartTime().toString();
                endTime = task.getEndTime().toString();

            } else {
                startTime = "null";
                endTime = "null";
            }
        }
        return String.join(",", id, type, title, status, description, epic, duration, startTime, endTime) + System.lineSeparator();
    }

    //восстановление задачи из строки
    static Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        Duration duration = Duration.parse(parts[6]);
        LocalDateTime taskStartTime;
        LocalDateTime taskEndTime;
        if (parts[7].equals("null") && parts[8].equals("null")) {
            taskStartTime = null;
            taskEndTime = null;
        } else {
            taskStartTime = LocalDateTime.parse(parts[7]);
            taskEndTime = LocalDateTime.parse(parts[8]);
        }

        switch (TaskType.valueOf(type)) {
            case TASK:
                Task task = new Task(title, description, duration, taskStartTime);
                task.setId(id);
                task.setStatus(status);
                task.setEndTime(taskStartTime);

                return task;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(title, description, duration, taskStartTime);
                subtask.setEpicId(epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                subtask.setEndTime(taskEndTime);
                return subtask;
            case EPIC:
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setEndTime(taskEndTime);
                return epic;
        }
        return null;
    }

}