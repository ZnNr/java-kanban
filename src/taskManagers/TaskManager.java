package taskManagers;

import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    List<Task> getHistory();

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Epic epic, Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteAllSubtasksByEpic(Epic epic);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getAllSubtasksByEpicId(Epic epic);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    Set<Task> getTasksPriorityTree();

}