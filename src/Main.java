
// проверка с помощью консоли
import taskManager.Managers;
import taskManager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
        System.out.println("Проверка");


        System.out.println("-> Создадим задачи");
        taskManager.createTask(new Task("Описание обычной задачи-1 Купить Корм для Бубы", "Task-1 Буба", Status.NEW));
        taskManager.createTask(new Task("Описание обычной задачи-2 Купить Корм для Пупы", "Task-2 Пупа", Status.NEW));
        taskManager.printTasks();

        System.out.println("-> Создадим Epic");
        taskManager.createEpic(new Epic("Описание Эпического Таска-1 Позаботитбся о Бубе", "Epic-1 - Буба", Status.NEW));
        taskManager.createEpic(new Epic("Описание Эпического Таска-2 Позапотиться о Пупе", "Epic-2 - Пупа", Status.NEW));
        taskManager.printEpics();


        System.out.println("Создадим Subtask");
        taskManager.createSubtask(new Subtask("Описание подзадачи-1 Бубы", "Subtask-1 Покормить Бубу", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Описание подзадачи-2 Бубы", "Subtask-2 Погладить Бубу", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Описание подзадачи-3 Пупы", "Subtask-3 Покормить Пупу", Status.NEW, 4));
        taskManager.createSubtask(new Subtask("Описание подзадачи-4 Пупы", "Subtask-4 Погладить Пупу", Status.NEW, 4));
        taskManager.printSubtasks();

        //вызовите разные методы интерфейса TaskManager и напечатайте историю
        // просмотров после каждого вызова. Если код рабочий, то история
        // просмотров задач будет отображаться корректно.


        System.out.println("-> Проверка корректности работы истории просмотров ");
        System.out.println("-> Получение через id");
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(7);
        taskManager.getEpicById(4);
        taskManager.getSubtaskById(8);
        taskManager.getSubtaskById(7);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);



        System.out.println("-----! Вызов метода getHistory !---");
        List<Task> history = taskManager.getHistory();
        System.out.println(history);



        /* далее остатки кода от предыдущего спринта

        System.out.println("-> Получим все Task");
        List<Task> taskList = taskManager.getAllTasks();
        System.out.println(taskList);

        System.out.println("-> Обновим Таск");
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        System.out.println(task);
        System.out.println();



        System.out.println("-> Получим все Эпики");
        List<Epic> epics = taskManager.getAllEpics();
        System.out.println(epics);
        System.out.println("-> Получим Эпик через id");
        Epic epic = taskManager.getEpicById(3);
        System.out.println(epic);
        System.out.println("-> Обновим Эпик");
        epic.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic);
        Epic epic3 = taskManager.getEpicById(3);
        System.out.println(epic3);
        System.out.println();

        System.out.println("-> Сабтаск <-");
        System.out.println("Создадим Subtask");
        taskManager.createSubtask(new Subtask("Описание подзадачи-1", "Subtask-1", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Описание подзадачи-2", "Subtask-2", Status.NEW, 3));
        taskManager.createSubtask(new Subtask("Описание подзадачи-3", "Subtask-3", Status.NEW, 4));
        taskManager.createSubtask(new Subtask("Описание подзадачи-4", "Subtask-4", Status.NEW, 4));
        taskManager.printSubtasks();
        System.out.println("-> Получим все Сабтаски через epic id");
        List<Subtask> subtasksByEpicId = taskManager.getAllSubtasksByEpicId(3);
        System.out.println(subtasksByEpicId);
        System.out.println("-> Получим все Сабтаски");
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        System.out.println(subtasks);
        System.out.println("-> Получим Сабтаск через id");
        Subtask subtask = taskManager.getSubtaskById(5);
        System.out.println(subtask);
        System.out.println("-> Обновим Сабтаск");
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        System.out.println(subtask);
        System.out.println();

        System.out.println("-> Удаление <-");
        System.out.println("-> Удаление Таска через id");
        taskManager.deleteTaskById(1);
        System.out.println(taskList);
        System.out.println("-> Удалим все Таски");
        taskManager.deleteAllTasks();
        taskManager.printTasks();
        System.out.println("-> Удаление Сабтаска через id");
        taskManager.deleteSubtaskById(5);
        taskManager.printSubtasks();
        System.out.println("-> Удалим все Сабтаски");
        taskManager.deleteAllSubtasks();
        taskManager.printSubtasks();
        System.out.println("-> Удаление Эпика через id");
        taskManager.deleteEpicById(4);
        taskManager.printEpics();
        System.out.println("-> Удалим все Эпики");
        taskManager.deleteAllEpics();
        taskManager.printEpics();
        */


    }
}