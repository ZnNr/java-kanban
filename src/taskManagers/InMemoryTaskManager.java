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
    private static int id = 0;
    // таблицы для хранения данных
    final Map<Integer, Task> tasks = new HashMap<>();
    final Map<Integer, Subtask> subtasks = new HashMap<>();
    final Map<Integer, Epic> epics = new HashMap<>();
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
    /*  private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
        protected final Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);*/
    protected int nextId = 1;

    //получение истории
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

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

    private void updateSubTaskStatus(int id) {
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

    /*
    public void updateTimeEpic(Epic epic) {
        List<Subtask> subtasks = getAllSubtasksByEpicId(epic.getId());
        Instant startTime = Instant.from(subtasks.get(0).getStartTime());
        Instant endTime = Instant.from(subtasks.get(0).getEndTime());

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(ChronoLocalDateTime.from(startTime)))
                startTime = Instant.from(subtask.getStartTime());
            if (subtask.getEndTime().isAfter(ChronoLocalDateTime.from(endTime)))
                endTime = Instant.from(subtask.getEndTime());
        }

        epic.setStartTime(LocalDateTime.from(startTime));
        epic.setEndTime(LocalDateTime.from(endTime));
        long duration = (endTime.toEpochMilli() - startTime.toEpochMilli());
        epic.setDuration(Duration.ofDays(duration));
    }
*/
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            tasksPriorityTree.add(subtask);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
            setEpicEndTimeAndDuration(epic);
        } else {
            System.out.println("Список подзадачь пуст");
        }
    }

    /* ------ Методы для обновления---- */
    // обновление задачи
    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            setTaskEndTime(task);
            tasksPriorityTree.remove(tasks.get(task.getId()));
            tasksPriorityTree.add(task);
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    // обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatusEpic(epic);
        } else {
            System.out.println("Эпик не найден");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //удаление из истории по айди
    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    public void addToHistory(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        } else if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
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
    public int createSubtask(Epic epic, Subtask subTask) {
        subTask.setId(nextId++);

        if (subTask.getDuration() == null) {
            subTask.setDuration(Duration.ZERO);
        }

        if (subTask.getStatus() == null) {
            subTask.setStatus(Status.NEW);
        }

        setTaskEndTime(subTask);
        epic.getSubtaskIds().add(subTask.getId());
        subTask.setEpicId(epic.getId());

        if (checkIntersection(subTask)) {
            tasksPriorityTree.add(subTask);
            subtasks.put(subTask.getId(), subTask);

            if (subTask.getStartTime() == null) {
                return subTask.getId();
            }

            setEpicEndTimeAndDuration(epic);

        } else {
            throw new ManagerSaveException("Подзадача не сохранена, задачи не должны пересекаться по времени исполнения");
        }
        return subTask.getId();
    }

    /* ------ Методы для удаления  по ID------ */
    // удаление задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasksPriorityTree.removeIf(task -> task.getId() == id);
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Таск не найден");
        }
    }

    // удаление эпика по идентификатору
    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                tasksPriorityTree.removeIf(task -> Objects.equals(task.getId(), subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Эпик не найден");
        }
    }

    // удаление ПОДзадачи по идентификатору
    @Override
    public void deleteSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        if (epicId != 0) {
            tasksPriorityTree.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
            List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
            subtaskIds.removeIf(subtaskId -> subtaskId == id);
            updateSubTaskStatus(epicId);
            setEpicEndTimeAndDuration(epics.get(epicId));

        } else {
            System.out.println("Подзадача не найдена");
        }

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
        for (Epic epic : epics.values()) {
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
        historyManager.add(subtasks.get(InMemoryTaskManager.id));
        return subtasks.get(InMemoryTaskManager.id);
    }

    // получение списка всех ПОДзадач относящихся к ID эпической задачи
    @Override
    public List<Subtask> getAllSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtasksNew = new ArrayList<>();
            Epic epic = epics.get(id);
            for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                subtasksNew.add(subtasks.get(epic.getSubtaskIds().get(i)));
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }

    //обновление сабтаска
 /*   @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && subtasks.containsKey(subtask.getId())) {
            setTaskEndTime(subtask);
            tasksPriorityTree.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateSubtaskStatus(epic.getId());
            setEpicEndTimeAndDuration(epic);
            tasksPriorityTree.add(subtask);
        } else {
            System.out.println("Список подзадачь пуст");
        }
    }

  */

    // обновление статуса эпика
    @Override
    public void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtaskIds().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                List<Subtask> subtasksNew = new ArrayList<>();
                int countDone = 0;
                int countNew = 0;

                for (int i = 0; i < epic.getSubtaskIds().size(); i++) {
                    subtasksNew.add(subtasks.get(epic.getSubtaskIds().get(i)));
                }

                for (Subtask subtask : subtasksNew) {
                    if (subtask.getStatus() == Status.DONE) {
                        countDone++;
                    }
                    if (subtask.getStatus() == Status.NEW) {
                        countNew++;
                    }
                    if (subtask.getStatus() == Status.IN_PROGRESS) {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }

                if (countDone == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.DONE);
                } else if (countNew == epic.getSubtaskIds().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Эпик не найден");
        }
    }

    /* ------ Методы для печати---- */
    // печать эпиков
    @Override
    public void printTasks() {
        if (tasks.size() == 0) {
            System.out.println("Список задачь пуст");
            return;
        }
        for (Task task : tasks.values()) {
            System.out.println("Task{" +
                    "id=" + task.getId() +
                    ", title='" + task.getTitle() + '\'' +
                    ", description='" + task.getDescription() + '\'' +
                    ", status=" + task.getStatus() +
                    '}');
        }
    }

    //печать эпиков
    @Override
    public void printEpics() {
        if (epics.size() == 0) {
            System.out.println("Лист эпиков пуст");
            return;
        }
        for (Epic epic : epics.values()) {
            System.out.println("Epic{" +
                    "subtaskIds=" + epic.getSubtaskIds() +
                    ", id=" + epic.getId() +
                    ", title='" + epic.getTitle() + '\'' +
                    ", description='" + epic.getDescription() + '\'' +
                    ", status=" + epic.getStatus() +
                    '}');
        }
    }

    //печать сабтасков
    @Override
    public void printSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Лист подзадачь пуст");
            return;
        }
        for (Subtask subtask : subtasks.values()) {
            System.out.println("Subtask{" +
                    "epicId=" + subtask.getEpicId() +
                    ", id=" + subtask.getId() +
                    ", title='" + subtask.getTitle() + '\'' +
                    ", description='" + subtask.getDescription() + '\'' +
                    ", status=" + subtask.getStatus() +
                    '}');
        }
    }

}

