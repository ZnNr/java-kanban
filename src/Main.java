
import taskManager.*;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;






/*
Помимо метода сохранения создайте статический метод static FileBackedTasksManager
loadFromFile(File file), который будет восстанавливать данные менеджера из файла
 при запуске программы. Не забудьте убедиться, что новый менеджер задач работает так же,
 как предыдущий. И проверьте работу сохранения и восстановления менеджера из файла (сериализацию).
Для этого
1. создайте метод static void main(String[] args) в классе FileBackedTasksManager и реализуйте небольшой сценарий:

2. Заведите несколько разных задач, эпиков и подзадач.
3. Запросите некоторые из них, чтобы заполнилась история просмотра.
4. Создайте новый FileBackedTasksManager менеджер из этого же файла.
5. Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи, которые были в старом, есть в новом менеджере.

 Доброго времени суток! Не уверен что всё работает корректно, скорее всего беда с InMemoryHistoryManager, но я очень надеюсь на конструктивные комментарии!
 Прошу прощения, что результат не финальный...
 заранее огромное спасибо!

 C:\Users\light\.jdks\openjdk-19.0.1\bin\java.exe "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.2.1\lib\idea_rt.jar=4481:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.2.1\bin" -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath C:\Users\light\Documents\dev\java-kanban\out\production\java-kanban Main
Лист Эпиков пустой
Лист подзадачь пуст
Exception in thread "main" java.lang.NullPointerException: Cannot read field "next" because "newHead" is null
	at taskManager.InMemoryHistoryManager.getTasks(InMemoryHistoryManager.java:35)
	at taskManager.InMemoryHistoryManager.getHistory(InMemoryHistoryManager.java:104)
	at taskManager.InMemoryTaskManager.getHistory(InMemoryTaskManager.java:37)
	at taskManager.FileBackedTasksManager.save(FileBackedTasksManager.java:158)
	at taskManager.FileBackedTasksManager.createTask(FileBackedTasksManager.java:255)
	at Main.main(Main.java:42)

Process finished with exit code 1

 Не смог решить за выходные..


 */


public class Main {
    private static final File PATH_FILE = new File("resources/data.csv");
    public static void main(String[] args) {
        // Спринт 6


            //---------------------------Создаем менеджер---------------------------------










            TaskManager fileBackedTasksManager = Managers.getManagerDefault();
        Task task1 = new Task("Выгулить Бубу", "Буба-прогулка", Status.NEW);
        Task task2 = new Task("Выгулить Грэма", "Грэм-прогулка", Status.NEW);
        Task task3 = new Task("Task3", "Description task3", Status.NEW);
        Epic epic1 = new Epic("Сдать модуль Java до жесткого дедлайна 26 декабря", "Дедлайн 26 декабря", Status.NEW);
        Subtask subtask11 = new Subtask("Сдать Спринт 6", "Спринт 6", Status.NEW, epic1.getId());
        Subtask subtask12 = new Subtask("Сдать Спринт 7", "Спринт 7", Status.NEW, epic1.getId());
        Subtask subtask13 = new Subtask("Сдать Спринт 8", "Спринт 8", Status.NEW, epic1.getId());
        Epic epic2 = new Epic("Празднование новгого года", "Новый Год", Status.NEW);
        Subtask subtask21 = new Subtask("Купить подарки", "подарки", Status.NEW, epic2.getId());
        Subtask subtask22 = new Subtask("Купить продукты на стол", "Праздничный стол", Status.NEW, epic2.getId());
        Subtask subtask23 = new Subtask("Купить костюм", "Праздничный костюм", Status.NEW, epic2.getId());
            //---------------------------Создаем задачи------------------------------------
        fileBackedTasksManager.createTask(task1);
        fileBackedTasksManager.createTask(task2);
        fileBackedTasksManager.createTask(task3);
        fileBackedTasksManager.createSubtask(subtask11);
        fileBackedTasksManager.createSubtask(subtask12);
        fileBackedTasksManager.createSubtask(subtask13);
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createEpic(epic2);
        fileBackedTasksManager.createSubtask(subtask21);
        fileBackedTasksManager.createSubtask(subtask22);
        fileBackedTasksManager.createSubtask(subtask23);


            //---------------------------Просматриваем задачи------------------------------
        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.getEpicById(epic2.getId());
        fileBackedTasksManager.getAllSubtasks();
        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.getTaskById(task2.getId());
            //---------------------------Создаем новый менеджер----------------------------
            FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(PATH_FILE);
            //------------------Выводим на экран задачи, созданные первым менеджером-------
            System.out.println("Загружаем задачи...\n");
            for (Task task : newManager.getAllTasks()) {
                System.out.println(task);
            }
            for (Epic epic : newManager.getAllEpics()) {
                System.out.println(epic);
            }
            for (Subtask subtask : newManager.getAllSubtasks()) {
                System.out.println(subtask);
            }
            System.out.println();
            System.out.println("Задачи загружены.\n");
            //------------------Выводим на экран ID просмотренных задач---------------------
            System.out.println();
            System.out.println("Загружаем историю просмотров...\n");
            for (Task task : newManager.getHistory()) {
                System.out.println(task);
            }
            System.out.println();
            System.out.println("История просмотров загружена.");
        }
    }


















