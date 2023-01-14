package manager.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;
import exception.ManagerSaveException;
import manager.task.FileBackedTasksManager;

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
            kvTaskClient.put(endPoints.taskKey, gson.toJson(tasks));
            kvTaskClient.put(endPoints.epicKey, gson.toJson(epics));
            kvTaskClient.put(endPoints.subtaskKey, gson.toJson(subtasks));
            kvTaskClient.put(endPoints.historyKey, gson.toJson(getHistory()));
        } catch (Exception e) {
            throw new ManagerSaveException("Не удалось сохранить задачи");
        }
    }

    public void loadFromServer() {
        String tasksFromJson = kvTaskClient.load(endPoints.taskKey);
        if (tasksFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, Task>>() {
            }.getType();

            tasks = gson.fromJson(tasksFromJson, typeToken);
            tasksPriorityTree.addAll(tasks.values());
        }

        String epicsFromJson = kvTaskClient.load(endPoints.epicKey);
        if (epicsFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, Epic>>() {
            }.getType();

            epics = gson.fromJson(epicsFromJson, typeToken);

        }

        String subtasksFromJson = kvTaskClient.load(endPoints.subtaskKey);
        if (subtasksFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, Subtask>>() {
            }.getType();

            subtasks = gson.fromJson(subtasksFromJson, typeToken);
            tasksPriorityTree.addAll(subtasks.values());

        }

        String historyFromJson = kvTaskClient.load(endPoints.historyKey);
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

