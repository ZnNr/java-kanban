package taskManagers;

import http.HttpTaskManager;
import taskManagers.historyManaghers.HistoryManager;
import taskManagers.historyManaghers.InMemoryHistoryManager;

public class Managers {
    public static TaskManager getManagerDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getHTTPManager() {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBacked() { //исправлено!
        return new FileBackedTasksManager();
    }

}