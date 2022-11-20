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
        TaskManager taskManager = Managers.getManagerDefault();



        System.out.println("Проверка Истории");
        System.out.println("-> Создадим задачи");
        taskManager.createTask(new Task("Описание обычной задачи-1 Купить Корм для Бубы", "Task-1 Буба", Status.NEW)); //1
        taskManager.createTask(new Task("Описание обычной задачи-2 Купить Корм для Пупы", "Task-2 Пупа", Status.NEW)); //2
        taskManager.printTasks();

        System.out.println("-> Создадим Epic");
        taskManager.createEpic(new Epic("Описание Эпического Таска-1 Позаботитбся о Бубе", "Epic-1 - Буба", Status.NEW)); //3
        taskManager.createEpic(new Epic("Описание Эпического Таска-2 Позапотиться о Пупе", "Epic-2 - Пупа", Status.NEW)); //4
        taskManager.printEpics();


        System.out.println("Создадим Subtask");
        taskManager.createSubtask(new Subtask("Описание подзадачи-1 Бубы", "Subtask-1 Покормить Бубу", Status.NEW, 3)); //4
        taskManager.createSubtask(new Subtask("Описание подзадачи-2 Бубы", "Subtask-2 Выгулить Бубу", Status.NEW, 3)); //5
        taskManager.createSubtask(new Subtask("Описание подзадачи-3 Пупы", "Subtask-3 Покормить Пупу", Status.NEW, 4)); //6
        taskManager.createSubtask(new Subtask("Описание подзадачи-4 Пупы", "Subtask-4 Выгулить Пупу", Status.NEW, 4)); //7
        taskManager.printSubtasks();



        System.out.println("-> Проверка корректности работы истории просмотров ");
        System.out.println("-> Получение через id");
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getSubtaskById(7);
        taskManager.getEpicById(4);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);
        taskManager.getTaskById(1);


        System.out.println("-----! Вызов метода getHistory !---");
        List<Task> history = taskManager.getHistory();
        System.out.println(history);

        System.out.println("--- Удаление из истории ---");
        taskManager.remove(2);
        taskManager.deleteEpicById(3);
        System.out.println("-----! просмотр после подчистки истории !---");
        List<Task> historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);

    }
}