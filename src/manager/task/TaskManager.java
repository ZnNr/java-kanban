package manager.task;

import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Epic epic, Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<Subtask> getAllSubtasksByEpicId(Epic epic);

    List<Task> getHistory();

    Set<Task> getTasksPriorityTree();

}


