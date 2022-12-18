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
}
