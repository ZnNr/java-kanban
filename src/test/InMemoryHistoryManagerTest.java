package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskManagers.historyManaghers.HistoryManager;
import taskManagers.historyManaghers.InMemoryHistoryManager;
import taskType.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryManagerTest extends InMemoryHistoryManager {

    HistoryManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTaskInHistory() {
        Task task1 = new Task("Task1", "Description Task1");
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subTask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));

        task1.setId(1);
        epic1.setId(2);
        subTask1.setId(3);

        manager.add(task1);
        manager.add(epic1);
        manager.add(subTask1);

        assertEquals(3, manager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskByIdFromHistory() {
        Task task1 = new Task("Task1", "Description Task1");
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("SubTask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));

        task1.setId(1);
        epic1.setId(2);
        subtask1.setId(3);

        manager.add(task1);
        manager.add(epic1);
        manager.add(subtask1);

        manager.remove(1);

        assertEquals(2, manager.getHistory().size());

        manager.remove(2);
        manager.remove(3);

        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnHistoryList() {
        Task task1 = new Task("Task1", "Description Task1");
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));

        task1.setId(1);
        epic1.setId(2);
        subtask1.setId(3);

        assertTrue(manager.getHistory().isEmpty());

        manager.add(task1);
        manager.add(epic1);
        manager.add(subtask1);

        assertEquals(3, manager.getHistory().size());
    }

    @Test
    void ShouldReturnEmptyHistoryList() {
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    void ShouldCheckAndReturnDuplicateHistoryList() {
        Task task1 = new Task("Task1", "Description Task1");
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("SubTask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));

        task1.setId(1);
        epic1.setId(1);
        subtask1.setId(1);

        manager.add(task1);
        manager.add(epic1);
        manager.add(subtask1);

        assertEquals(1, manager.getHistory().size());
    }
}
