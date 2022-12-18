package taskManagers;

import enumconstants.Status;
import enumconstants.TaskType;
import taskManagers.historyManaghers.HistoryManager;
import taskType.*;

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

    private String getParentEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicId());
        }
        return "";
    }

    private TaskType getType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    // Метод сохранения задачи в строку
    public static String toString(Task task) {
        String id = String.valueOf(task.getId());
        String type;
        String name = task.getTitle();
        String status = String.valueOf(task.getStatus());
        String description = task.getDescription();
        String epic;

        if (task instanceof Epic) {
            type = TaskType.EPIC.name();
            epic = "";
        } else if (task instanceof Subtask) {
            type = TaskType.SUBTASK.name();
            epic = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = TaskType.TASK.name();
            epic = "";
        }

        return String.join(",", id, type, name, status, description, epic) + System.lineSeparator();
    }

    //восстановление задачи из строки
    static Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (TaskType.valueOf(type)) {
            case TASK: // стояло openjdk 19, исправил на amazon correto version 11.0.16, так же исправил swich
                Task task = new Task(title, description, status);
                task.setId(id);
                task.setStatus(status);
                return task;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(title, description, status, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            case EPIC:
                Epic epic = new Epic(title, description, status);
                epic.setId(id);
                return epic;
        }
        return null;
    }

}




