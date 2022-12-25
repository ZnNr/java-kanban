package test;

import http.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskManagers.*;
import taskType.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest {

    private Task task1;
    private Task task2;
    private Epic epic1;
    private Subtask subtask1;
    private Subtask subtask2;
    private HttpTaskManager manager;
    private static KVServer kvServer;

    @BeforeAll
    public static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    public void beforeEach() {
        manager = (HttpTaskManager) Managers.getHTTPManager();

        task1 = new Task("Task1", "Description Task1");
        task2 = new Task("Task2", "Description Task2");
        epic1 = new Epic("Epic1", "Description Epic1");
        subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0, 0));
        subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0,0));
    }

    @Test
    public void shouldSaveAndRestoreAnEmptyTaskList() {
        manager.createTask(task1);

        assertEquals(1, manager.getAllTasks().size());

        manager.deleteTaskById(task1.getId());

        manager = (HttpTaskManager) Managers.getHTTPManager();

        assertEquals(List.of(), manager.getAllTasks());
    }

    @Test
    public void shouldSaveAndRestoreEpicWithoutSubtasks() {
        List<Task> epicList = new ArrayList<>(List.of(epic1));

        manager.createEpic(epic1);

        manager = (HttpTaskManager) Managers.getHTTPManager();
        manager.loadFromServer();

        assertEquals(epicList.size(), manager.getAllEpics().size());
        assertEquals(List.of(), manager.getAllSubtasks());
    }

    @Test
    public void shouldSaveAndRestoreAnEmptyHistoryList() {

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        assertEquals(2, manager.getAllTasks().size());
        assertEquals(2, manager.getHistory().size());

        manager.deleteAllTasks();
        manager = (HttpTaskManager) Managers.getHTTPManager();

        assertEquals(0, manager.getAllTasks().size());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void shouldSaveAndLoadTasks() {
        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        assertFalse(manager.getAllTasks().isEmpty());
        assertFalse(manager.getAllEpics().isEmpty());
        assertFalse(manager.getAllSubtasks().isEmpty());

        manager = (HttpTaskManager) Managers.getHTTPManager();

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());

        manager.loadFromServer();

        assertFalse(manager.getAllTasks().isEmpty());
        assertFalse(manager.getAllEpics().isEmpty());
        assertFalse(manager.getAllSubtasks().isEmpty());
    }

    @AfterAll
    public static void afterAll() {
        kvServer.stop();
    }
}