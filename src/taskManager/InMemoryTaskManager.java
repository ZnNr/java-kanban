package taskManager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 0;
    // таблицы для хранения данных
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();



    public int generateId() {
        return ++id;
    }

    //получение истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    /* ------ Методы для создания ------ */
    // создание задачи
    @Override
    public int createTask(Task task) {
        int newTaskId = generateId();
        task.setId(newTaskId);
        tasks.put(newTaskId, task);
        return newTaskId;
    }

    // создание эпической задачи
    @Override
    public int createEpic(Epic epic) {
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);
        return newEpicId;
    }

    // создание подзадачи в эпической задаче
    @Override
    public int createSubtask(Subtask subtask) {
        int newSubtaskId = generateId();
        subtask.setId(newSubtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(newSubtaskId, subtask);
            epic.setSubtaskIds(newSubtaskId);
            updateStatusEpic(epic);
            return newSubtaskId;
        } else {
            System.out.println("Эпик не найден");
            return -1;
        }
    }

    /* ------ Методы для удаления  по ID------ */
    // удаление задачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
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
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        } else {
            System.out.println("Эпик не найден");
        }
    }

    // удаление ПОДзадачи по идентификатору
    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            updateStatusEpic(epic);
            subtasks.remove(id);
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    /* ------ Методы для удаления  ALL----- */
    // удаление всех задачь
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    // удаление всех эпиков
    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    // удаление всех ПОДзадачь
    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
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
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    /* ------ Методы для списков---- */
    // получение списка всех задач
    @Override
    public List<Task> getAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("Лист Тасков пустой");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    // получение списка всех ЭПИКОВ
    @Override
    public List<Epic> getAllEpics() {
        if (epics.size() == 0) {
            System.out.println("Лист Эпиков пустой");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    // получение списка всех ПОДзадач
    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Лист подзадачь пуст");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
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

    /* ------ Методы для обновления---- */
    // обновление задачи
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    // обновление эпика
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatusEpic(epic);
        } else {
            System.out.println("Эпик не найден");
        }
    }

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

    //обновление сабтаска
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Список подзадачь пуст");
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
                    "description='" + task.getDescription() + '\'' +
                    ", id=" + task.getId() +
                    ", title='" + task.getTitle() + '\'' +
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
                    "subtasksIds=" + epic.getSubtaskIds() +
                    ", description='" + epic.getDescription() + '\'' +
                    ", id=" + epic.getId() +
                    ", title='" + epic.getTitle() + '\'' +
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
                    ", description='" + subtask.getDescription() + '\'' +
                    ", id=" + subtask.getId() +
                    ", title='" + subtask.getTitle() + '\'' +
                    ", status=" + subtask.getStatus() +
                    '}');
        }
    }


}