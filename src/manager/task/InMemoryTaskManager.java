package manager.task;

import taskType.*;
import constant.Status;
import exception.ManagerSaveException;
import manager.Managers;
import manager.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> tasksPriorityTree = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null) return o1.getId() - o2.getId();
        if (o1.getStartTime() == null) return 1;
        if (o2.getStartTime() == null) return -1;
        if (o1.getStartTime().isAfter(o2.getStartTime())) return 1;
        if (o1.getStartTime().isBefore(o2.getStartTime())) return -1;
        if (o1.getStartTime().isEqual(o2.getStartTime())) return o1.getId() - o2.getId();
        return 0;
    });

    protected int nextId = 1;


    private void setTaskEndTime(Task task) {
        if (task.getDuration() != null && task.getStartTime() != null) {
            LocalDateTime endTime = task.getStartTime().plus(task.getDuration());
            task.setEndTime(endTime);
        }
    }

    private void setEpicEndTimeAndDuration(Epic epic) {

        if (!epic.getSubTaskIds().isEmpty()) {
            LocalDateTime start = LocalDateTime.MAX;
            LocalDateTime end = LocalDateTime.MIN;
            for (Integer id : epic.getSubTaskIds()) {
                if (subtasks.get(id).getStartTime() != null && subtasks.get(id).getStartTime().isBefore(start)) {
                    start = subtasks.get(id).getStartTime();
                }
                if (subtasks.get(id).getStartTime() != null && subtasks.get(id).getEndTime().isAfter(end)) {
                    end = subtasks.get(id).getEndTime();
                }
            }

            epic.setStartTime(start);
            epic.setEndTime(end);
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));

        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
        }
    }

    private boolean checkIntersection(Task task) {
        boolean isValid = true;

        if (!tasksPriorityTree.isEmpty()) {
            for (Task taskForCheck : tasksPriorityTree) {

                if ((task.getStartTime() != null && task.getEndTime() != null) &&
                        (taskForCheck.getStartTime() != null && taskForCheck.getEndTime() != null)) {

                    if ((task.getStartTime().isEqual(taskForCheck.getStartTime())) ||
                            (task.getEndTime().isEqual(taskForCheck.getEndTime())) ||
                            ((task.getStartTime().isBefore(taskForCheck.getEndTime())) &&
                                    (task.getStartTime().isAfter(taskForCheck.getStartTime()))) ||
                            ((task.getEndTime().isBefore(taskForCheck.getEndTime())) &&
                                    (task.getEndTime().isAfter(taskForCheck.getStartTime()))) ||
                            ((task.getStartTime().isBefore(taskForCheck.getStartTime())) &&
                                    (task.getEndTime().isAfter(taskForCheck.getEndTime()))) ||
                            ((task.getStartTime().isAfter(taskForCheck.getStartTime())) &&
                                    (task.getEndTime().isBefore(taskForCheck.getEndTime())))) {
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    private void updateSubTaskStatus(int id) {
        int numberOfSubTask = epics.get(id).getSubTaskIds().size();
        int counterStatusNew = 0;
        int counterStatusDone = 0;

        List<Integer> subTaskKeys = epics.get(id).getSubTaskIds();
        for (Integer subTaskKey : subTaskKeys) {
            if (subtasks.get(subTaskKey).getStatus().equals(Status.NEW)) {
                counterStatusNew++;
            } else if (subtasks.get(subTaskKey).getStatus().equals(Status.DONE)) {
                counterStatusDone++;
            }
        }
        if (counterStatusNew == numberOfSubTask) {
            epics.get(id).setStatus(Status.NEW);
        } else if (counterStatusDone == numberOfSubTask) {
            epics.get(id).setStatus(Status.DONE);
        } else {
            epics.get(id).setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
            tasksPriorityTree.remove(tasks.get(taskId));
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().stream().map(subtasks::get).forEach(tasksPriorityTree::remove);
        subtasks.keySet().forEach(historyManager::remove);

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().stream().map(subtasks::get).forEach(tasksPriorityTree::remove);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();

        for (Integer epicKey : epics.keySet()) {
            epics.get(epicKey).getSubTaskIds().clear();
            epics.get(epicKey).setStatus(Status.NEW);
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public int createTask(Task task) {
        task.setId(nextId++);

        if (task.getDuration() == null) {
            task.setDuration(Duration.ZERO);
        }

        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }

        setTaskEndTime(task);

        if (checkIntersection(task)) {
            tasksPriorityTree.add(task);
            tasks.put(task.getId(), task);
        } else {
            throw new ManagerSaveException("Задача не сохранена, задачи не должны пересекаться по времени исполнения");
        }
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId++);

        if (epic.getSubTaskIds() == null) {
            epic.setSubTaskIds(new ArrayList<>());
        }

        if (epic.getDuration() == null) {
            epic.setDuration(Duration.ZERO);
        }

        if (epic.getStatus() == null) {
            epic.setStatus(Status.NEW);
        }

        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createSubtask(Epic epic, Subtask subtask) {
        subtask.setId(nextId++);

        if (subtask.getDuration() == null) {
            subtask.setDuration(Duration.ZERO);
        }

        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }

        setTaskEndTime(subtask);
        epic.getSubTaskIds().add(subtask.getId());
        subtask.setEpicId(epic.getId());

        if (checkIntersection(subtask)) {
            tasksPriorityTree.add(subtask);
            subtasks.put(subtask.getId(), subtask);

            if (subtask.getStartTime() == null) {
                return subtask.getId();
            }

            setEpicEndTimeAndDuration(epic);

        } else {
            throw new ManagerSaveException("Подзадача не сохранена, задачи не должны пересекаться по времени исполнения");
        }
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            setTaskEndTime(task);
            tasksPriorityTree.remove(tasks.get(task.getId()));
            tasksPriorityTree.add(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            setTaskEndTime(subtask);
            tasksPriorityTree.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            int epicId = subtask.getEpicId();

            updateSubTaskStatus(epicId);
            setEpicEndTimeAndDuration(epics.get(epicId));
            tasksPriorityTree.add(subtask);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasksPriorityTree.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        List<Integer> subTaskIds = epics.get(id).getSubTaskIds();
        for (Integer subTaskId : subTaskIds) {
            tasksPriorityTree.remove(subtasks.get(subTaskId));
            subtasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        tasksPriorityTree.remove(subtasks.get(id));
        subtasks.remove(id);
        historyManager.remove(id);

        List<Integer> subTaskIds = epics.get(epicId).getSubTaskIds();
        subTaskIds.removeIf(subTaskId -> subTaskId == id);

        updateSubTaskStatus(epicId);
        setEpicEndTimeAndDuration(epics.get(epicId));
    }

    @Override
    public List<Subtask> getAllSubtasksByEpicId(Epic epic) {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer subTaskKey : subtasks.keySet()) {
            if (subtasks.get(subTaskKey).getEpicId() == epic.getId()) {
                subtaskList.add(subtasks.get(subTaskKey));
            }
        }
        return subtaskList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getTasksPriorityTree() {
        return tasksPriorityTree;
    }
}
