package taskManagers;

import taskManagers.historyManaghers.HistoryManager;
import taskManagers.historyManaghers.InMemoryHistoryManager;


import java.io.File;

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


}