package taskManager;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int LIMIT_HISTORY_TASKS = 10;
    private final List<Task> historyTasks = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            historyTasks.add(task);
            if (historyTasks.size() >= LIMIT_HISTORY_TASKS) {
                historyTasks.remove(0);

            }

        }
    }

    @Override
    public List<Task> getHistory() {
        return historyTasks;
    }
}
