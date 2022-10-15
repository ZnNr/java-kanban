package manager;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
public class TaskManager {

    private int generateId = 1;
    private static int id = 0;
    // таблицы для хранения данных
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();



    public int generateId() {
        return ++id;
    }

    /* ------ Методы для задач типа Task ------ */

    // создание задачи
    public int createTask(Task task) {
        int newTaskId = generateId();
        task.setId(newTaskId);
        tasks.put(newTaskId, task);
        return newTaskId;
    }



    // получение списка всех задач
    public List<Task> getAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("Лист Тасков пустой");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    // удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // получение задачи по id
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // обновление задачи
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Таск не найден");
        }
    }

    // удаление задачи по идентификатору
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Таск не найден");
        }
    }



    public void printTasks() {
        if (tasks.size() == 0) {
            System.out.println("Лист Тасков пустой");
            return;
        }
        for (Task task : tasks.values()) {
            System.out.println("Task{" +
                    "description='" + task.getDescription() + '\'' +
                    ", id=" + task.getId() +
                    ", tittle='" + task.getTitle() + '\'' +
                    ", status=" + task.getStatus() +
                    '}');
        }
    }


    /* ------ Методы для задач типа Epic ------ */

    // создание эпика
    public int createEpic(Epic epic) {
        int newEpicId = generateId();
        epic.setId(newEpicId);
        epics.put(newEpicId, epic);
        return newEpicId;
    }


    // обновление статуса эпика
    private void updateStatusEpic(Epic epic) {
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

    // получение списка эпиков!!!
    public List<Epic> getAllEpics() {
        if (epics.size() == 0) {
            System.out.println("Лист Эпиков пустой");
            return Collections.emptyList();
        }
        return new ArrayList<>(epics.values());
    }

    // удаление всех эпиков
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    // получение эпика по id
    public Epic getEpicById(int id) {
        return epics.get(id);
    }


    // обновление эпика
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatusEpic(epic);
        } else {
            System.out.println("Эпик не найден");
        }
    }

    // удаление эпика по идентификатору
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


// печать эпиков
public void printEpics() {
    if (epics.size() == 0) {
        System.out.println("Лист Эпиков пустой");
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

    /* ------ Методы для подзадач типа Subtask ------ */

    // создание подзадачи
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



    // Получение списка всех подзадач определенного эпика
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

    // получение списка всех подзадач
    public List<Subtask> getAllSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Лист Сабтасков пустой");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtasks.values());
    }

    // удаление всех подзадач
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatusEpic(epic);
        }
    }

    // получение подзадачи по id
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }


    // обновление подзадачи
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusEpic(epic);
        } else {
            System.out.println("Сабтаск не найден");
        }
    }


    // удаление подзадачи по идентификатору
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIds().remove((Integer) subtask.getId());
            updateStatusEpic(epic);
            subtasks.remove(id);
        } else {
            System.out.println("Сабтаск не найден");
        }
    }


//печать сабтасков
    public void printSubtasks() {
        if (subtasks.size() == 0) {
            System.out.println("Лист Сабтасков пустой");
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
