// проверка с помощью консоли

import taskManager.Managers;
import taskManager.TaskManager;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
/* Zinnur, привет. Как успехи ? Как настрой?
Программа работает и улучшается!
Нужно подправить архитектурно класс InMemoryHistoryManager и пару вопросов задал.
 */
/* Доброго времени суток! Настрой нормальный, успехи так себе...,
ближе к новому году на основной работе завал, поэтому полноценно посидеть в Idea в будни возможности нет..\
но стараюсь хотя бы теорию проходить, сложность конечно растет, и не всегда без подсказки выходит решить самостоятельные работы
Спасибо за ревью и интересные комментарии!
 */
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
        //вопрос:  почему ты используешь цифру 3, а не метод somEpic.getId() ?
        //  не могу понять вопрос..удаляем конкретный эпик с конктретным id
        //поэтому использую конкретный айди конкретного эпика, метода  somEpic.getId() у меня нет...
        // имеется ввиду при создании эпика писать new Epiс1,new Epiс2 ? и удалять его deleteEpicById(Epic3.getId())
        System.out.println("-----! просмотр после подчистки истории !---");
        List<Task> historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);


        //сейчас выводится в строчку, можем сделать вывод в столбик для большей наглядности?  типа :
        //"History:
        // форматирование строк и вывод в форматированном виде рассматриваентся в 6 спринте, мне бы пока что сдать 5...


    }
}


