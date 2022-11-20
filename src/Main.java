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



       /* Тестирование работы программы
        После написания менеджера истории проверьте его работу:
        создайте две задачи, эпик с тремя подзадачами и эпик без подзадач;
        запросите созданные задачи несколько раз в разном порядке;
        после каждого запроса выведите историю и убедитесь, что в ней нет повторов;
        удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться;
        удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        Интересного вам программирования! */

        System.out.println("Проверка Истории");
        System.out.println("-> Создадим задачи");
        taskManager.createTask(new Task("задача-1 Купить Корм", "Task-1 Буба", Status.NEW)); //1
        taskManager.createTask(new Task("задача-2 Купить Поводок", "Task-2 Пупа", Status.NEW)); //2
        taskManager.printTasks();

        System.out.println("-> Создадим Epic");
        taskManager.createEpic(new Epic("Эп. Таска-1 Позаботитбся о Бубе", "Epic-1 - Буба", Status.NEW)); //3
        taskManager.createEpic(new Epic("Эп. Таска-2 Позапотиться о Пупе", "Epic-2 - Пупа", Status.NEW)); //4
        taskManager.printEpics();


        System.out.println("Создадим Subtask");
        taskManager.createSubtask(new Subtask("Описание под/зд-1 Бубы", "Subtask-1 Покормить Бубу", Status.NEW, 3)); //5
        taskManager.createSubtask(new Subtask("Описание под/зд-2 Бубы", "Subtask-2 Выгулить Бубу", Status.NEW, 3)); //6
        taskManager.createSubtask(new Subtask("Описание под/зд-3 Бубы", "Subtask-3 Вычесать Бубу", Status.NEW, 3)); //7
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
        taskManager.remove(1);
        taskManager.deleteEpicById(3);
        System.out.println("-----! просмотр после подчистки истории !---");
        List<Task> historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);

    }
}


