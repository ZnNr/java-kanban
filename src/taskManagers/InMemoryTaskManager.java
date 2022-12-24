package taskManagers;

import constant.Status;
import taskManagers.historyManaghers.HistoryManager;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    // таблицы для хранения данных
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

    private void setTaskEndTime(Task task) {
        if (task.getDuration() != null && task.getStartTime() != null) {
            LocalDateTime endTime = task.getStartTime().plus(task.getDuration());
            task.setEndTime(endTime);
        }
    }

    private void setEpicEndTimeAndDuration(Epic epic) {
        if (!epic.getSubtaskIds().isEmpty()) {
            LocalDateTime start = LocalDateTime.MAX;
            LocalDateTime end = LocalDateTime.MIN;
            for (Integer id : epic.getSubtaskIds()) {
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

    private void updateSubtaskStatus(int id) {
        int numberOfSubTask = epics.get(id).getSubtaskIds().size();
        int counterStatusNew = 0;
        int counterStatusDone = 0;

        List<Integer> subTaskKeys = epics.get(id).getSubtaskIds();
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

    /* ------ Методы для обновления---- */
    // обновление задачи
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            setTaskEndTime(task);
            tasksPriorityTree.remove(tasks.get(task.getId()));
            tasksPriorityTree.add(task);
            tasks.put(task.getId(), task);
        }
    }

    // обновление эпика
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
            updateSubtaskStatus(epicId);
            setEpicEndTimeAndDuration(epics.get(epicId));
            tasksPriorityTree.add(subtask);
        }
    }

    /* ------ Методы для создания ------ */
    // создание задачи
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
            throw new ManagerSaveException("task - ошибка сохранения! задачи не могут выполняться одновременно");
        }
        return task.getId();
    }

    // создание эпической задачи
    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId++);
        if (epic.getSubtaskIds() == null) {
            epic.setSubtaskIds(new ArrayList<>());
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

    // создание подзадачи в эпической задаче
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
        epic.getSubtaskIds().add(subtask.getId());
        subtask.setEpicId(epic.getId());

        if (checkIntersection(subtask)) {
            tasksPriorityTree.add(subtask);
            subtasks.put(subtask.getId(), subtask);

            if (subtask.getStartTime() == null) {
                return subtask.getId();
            }

            setEpicEndTimeAndDuration(epic);

        } else {
            throw new ManagerSaveException("Subtask - ошибка сохранения! задачи не могут выполняться одновременно");
        }
        return subtask.getId();
    }

    /* ------ Методы для удаления  по ID------ */
    // удаление задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        tasksPriorityTree.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    // удаление эпика по идентификатору
    @Override
    public void deleteEpicById(int id) {
        List<Integer> subtaskIds = epics.get(id).getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            tasksPriorityTree.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    // удаление ПОДзадачи по идентификатору
    @Override
    public void deleteSubtaskById(int id) {

        int epicId = subtasks.get(id).getEpicId();
        tasksPriorityTree.remove(subtasks.get(id));
        subtasks.remove(id);
        historyManager.remove(id);

        List<Integer> subTaskIds = epics.get(epicId).getSubtaskIds();
        subTaskIds.removeIf(subTaskId -> subTaskId == id);

        updateSubtaskStatus(epicId);
        setEpicEndTimeAndDuration(epics.get(epicId));
    }

    /* ------ Методы для удаления  ALL----- */
    // удаление всех задачь
    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
            tasksPriorityTree.remove(tasks.get(taskId));
        }
        tasks.clear();
    }

    // удаление всех эпиков
    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().stream().map(subtasks::get).forEach(tasksPriorityTree::remove);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.clear();

    }

    // удаление всех ПОДзадачь
    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().stream().map(subtasks::get).forEach(tasksPriorityTree::remove);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        for (Integer epicKey : epics.keySet()) {
            epics.get(epicKey).getSubtaskIds().clear();
            epics.get(epicKey).setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic epic) {
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                tasksPriorityTree.remove(subtask);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getSubtaskIds().clear();
        }
    }

    @Override
    public Set<Task> getTasksPriorityTree() {
        return tasksPriorityTree;
    }

    /* ------ Методы для списков---- */
    // получение списка всех задач
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

    // получение списка всех ПОДзадач относящихся к ID эпической задачи
    @Override
    public List<Subtask> getAllSubtasksByEpicId(Epic epic) {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer subtaskKey : subtasks.keySet()) {
            if (subtasks.get(subtaskKey).getEpicId() == epic.getId()) {
                subtaskList.add(subtasks.get(subtaskKey));
            }
        }
        return subtaskList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /* ------ Методы для получения по ID---- */
    // получение задачи по id
    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    // получение эпика по id
    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    // получение ПОДзадачи по id
    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

}