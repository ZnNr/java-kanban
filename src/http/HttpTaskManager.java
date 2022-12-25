package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import taskManagers.FileBackedTasksManager;
import taskManagers.ManagerSaveException;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HttpTaskManager(String url) {
        this.kvTaskClient = new KVTaskClient(url);
    }

    @Override
    protected void save() {
        try {
            kvTaskClient.put("tasks/task", gson.toJson(tasks));
            kvTaskClient.put("tasks/epic", gson.toJson(epics));
            kvTaskClient.put("tasks/subtask", gson.toJson(subtasks));
            kvTaskClient.put("tasks/history", gson.toJson(getHistory()));
        } catch (Exception e) {
            throw new ManagerSaveException("Не удалось сохранить задачи");
        }
    }

    public void loadFromServer() {
        String tasksFromJson = kvTaskClient.load("tasks/task");
        if (tasksFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, Task>>() {
            }.getType();

            tasks = gson.fromJson(tasksFromJson, typeToken);
            tasksPriorityTree.addAll(tasks.values());
        }

        String epicsFromJson = kvTaskClient.load("tasks/epic");
        if (epicsFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, Epic>>() {
            }.getType();

            epics = gson.fromJson(epicsFromJson, typeToken);
            tasksPriorityTree.addAll(epics.values());
        }

        String subtasksFromJson = kvTaskClient.load("tasks/subtask");
        if (subtasksFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, Subtask>>() {
            }.getType();

            subtasks = gson.fromJson(subtasksFromJson, typeToken);
            tasksPriorityTree.addAll(subtasks.values());
        }

        String historyFromJson = kvTaskClient.load("tasks/history");
        if (historyFromJson != null) {
            Type typeToken = new TypeToken<List<Task>>() {
            }.getType();

            List<Task> historyList = gson.fromJson(historyFromJson, typeToken);
            for (Task task : historyList) {
                historyManager.add(task);
            }
        }
    }
}
