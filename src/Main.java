public class Main {      //Спринт 7 маин не нужен
   /* private static final File PATH_FILE = new File("resources/data.csv");


    protected void addTasks() {
        Task task1 = new Task("Task1", "Description Task1",
                Duration.ofMinutes(30), LocalDateTime.of(2022, 11, 15, 14, 0));

        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2021, 1, 1, 8, 0));
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2020, 1, 2, 8, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2019, 1, 2, 8, 0));
        Subtask subtask21 = new Subtask("Subtask21", "Description Subtask22",
                Duration.ofMinutes(60), LocalDateTime.of(2020, 1, 2, 8, 0));
        Subtask subtask22 = new Subtask("Subtask22", "Description Subtask22",
                Duration.ofMinutes(60), LocalDateTime.of(2019, 1, 2, 8, 0));
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);
        manager.createSubtask(epic2, subtask21);
        manager.createSubtask(epic2, subtask22);
    }


    public static void main(String[] args) {

        // Спринт 6
        //---------------------------Создаем менеджер---------------------------------
        TaskManager fileBackedTasksManager = Managers.getFileBacked(); // исправлено!
        //---------------------------Создаем задачи------------------------------------
        Task task1 = new Task("Выгулить Бубу", "Буба-прогулка", Status.NEW);
        Task task2 = new Task("Выгулить Грэма", "Грэм-прогулка", Status.NEW);
        Task task3 = new Task("Task3", "Description task3", Status.NEW);
        fileBackedTasksManager.createTask(task1);
        fileBackedTasksManager.createTask(task2);
        fileBackedTasksManager.createTask(task3);
        //---------------------------Создаем epic and subtasks------------------------------------
        Epic epic1 = new Epic("Сдать модуль Java до жесткого дедлайна 26 декабря", "Дедлайн 26 декабря", Status.NEW);
        fileBackedTasksManager.createEpic(epic1);
        Subtask subtask11 = new Subtask("Сдать Спринт 6", "Спринт 6", Status.NEW, epic1.getId());
        Subtask subtask12 = new Subtask("Сдать Спринт 7", "Спринт 7", Status.NEW, epic1.getId());
        Subtask subtask13 = new Subtask("Сдать Спринт 8", "Спринт 8", Status.NEW, epic1.getId());
        fileBackedTasksManager.createSubtask(subtask11);
        fileBackedTasksManager.createSubtask(subtask12);
        fileBackedTasksManager.createSubtask(subtask13);
        Epic epic2 = new Epic("Празднование новгого года", "Новый Год", Status.NEW);
        fileBackedTasksManager.createEpic(epic2);
        Subtask subtask21 = new Subtask("Купить подарки", "подарки", Status.NEW, epic2.getId());
        Subtask subtask22 = new Subtask("Купить продукты на стол", "Праздничный стол", Status.NEW, epic2.getId());
        Subtask subtask23 = new Subtask("Купить костюм", "Праздничный костюм", Status.NEW, epic2.getId());
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
        //------------------Выводим на экран задачи, созданные  менеджером -------
        System.out.println();
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
    }*/
}