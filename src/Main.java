
// проверка с помощью консоли
        import manager.TaskManager;
        import status.Status;
        import tasks.Epic;
        import tasks.Subtask;
        import tasks.Task;
        import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        System.out.println("Проверка");
        System.out.println("-> Task <-");
        System.out.println("-> Создадим Таск");
        manager.createTask(new Task("Описание-1", "Task-1", Status.NEW));
        manager.createTask(new Task("Описание-2", "Task-2", Status.NEW));
        manager.printTasks();
        System.out.println("-> Получим все Task");
        List<Task> taskList = manager.getAllTasks();
        System.out.println(taskList);
        System.out.println("-> Получение Таска через id");
        Task task = manager.getTaskById(1);
        System.out.println(task);
        System.out.println("-> Обновим Таск");
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        System.out.println(task);
        System.out.println();

        System.out.println("-> Эпик <-");
        System.out.println("-> Создадим Epic");
        manager.createEpic(new Epic("Описание-1", "Epic-1", Status.NEW));
        manager.createEpic(new Epic("Описание-2", "Epic-2", Status.NEW));
        manager.printEpics();
        System.out.println("-> Получим все Эпики");
        List<Epic> epics = manager.getAllEpics();
        System.out.println(epics);
        System.out.println("-> Получим Эпик через id");
        Epic epic = manager.getEpicById(3);
        System.out.println(epic);
        System.out.println("-> Обновим Эпик");
        epic.setStatus(Status.IN_PROGRESS);
        manager.updateEpic(epic);
        Epic epic3 = manager.getEpicById(3);
        System.out.println(epic3);
        System.out.println();

        System.out.println("-> Сабтаск <-");
        System.out.println("Создадим Subtask");
        manager.createSubtask(new Subtask("Описание-1", "Subtask-1", Status.NEW, 3));
        manager.createSubtask(new Subtask("Описание-2", "Subtask-2", Status.NEW, 3));
        manager.createSubtask(new Subtask("Описание-3", "Subtask-3", Status.NEW, 4));
        manager.createSubtask(new Subtask("Описание-4", "Subtask-4", Status.NEW, 4));
        manager.printSubtasks();
        System.out.println("-> Получим все Сабтаски через epic id");
        List<Subtask> subtasksByEpicId = manager.getAllSubtasksByEpicId(3);
        System.out.println(subtasksByEpicId);
        System.out.println("-> Получим все Сабтаски");
        List<Subtask> subtasks = manager.getAllSubtasks();
        System.out.println(subtasks);
        System.out.println("-> Получим Сабтаск через id");
        Subtask subtask = manager.getSubtaskById(5);
        System.out.println(subtask);
        System.out.println("-> Обновим Сабтаск");
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        System.out.println(subtask);
        System.out.println();

        System.out.println("-> Удаление <-");
        System.out.println("-> Удаление Таска через id");
        manager.deleteTaskById(1);
        System.out.println(taskList);
        System.out.println("-> Удалим все Таски");
        manager.deleteAllTasks();
        manager.printTasks();
        System.out.println("-> Удаление Сабтаска через id");
        manager.deleteSubtaskById(5);
        manager.printSubtasks();
        System.out.println("-> Удалим все Сабтаски");
        manager.deleteAllSubtasks();
        manager.printSubtasks();
        System.out.println("-> Удаление Эпика через id");
        manager.deleteEpicById(4);
        manager.printEpics();
        System.out.println("-> Удалим все Эпики");
        manager.deleteAllEpics();
        manager.printEpics();



    }
}