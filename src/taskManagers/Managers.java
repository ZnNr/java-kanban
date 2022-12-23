package taskManagers;

import taskManagers.historyManaghers.HistoryManager;
import taskManagers.historyManaghers.InMemoryHistoryManager;

public class Managers {
    public static TaskManager getManagerDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBacked() { //исправлено!
        return new FileBackedTasksManager();
    }
/* подсказка ревьюера
    public static TaskManager getManager(TypeOfManager typeOfManager, String pathToFile) {
        if (typeOfManager == TypeOfManager.FILE && pathToFile != null) {
            return new FileBackedTasksManager(pathToFile);
        }
        return new InMemoryTaskManager();
    } */
/*public static TaskManager getManagerDefault(FileBackedTasksManager FileBackedTasksManager, String pathToFile) {
    if (FileBackedTasksManager == FileBackedTasksManager.FILE && pathToFile != null) {
        return new FileBackedTasksManager(pathToFile);
    }
    return new InMemoryTaskManager();
}*/
}
