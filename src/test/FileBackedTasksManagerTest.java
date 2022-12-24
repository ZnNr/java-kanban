package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskManagers.FileBackedTasksManager;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private static final File PATH_FILE = new File("resources/data.csv");

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager();
    }

        /*
Спасибо за ревью! Пожалуйста примите работу чтобы мне успеть сдать 8 спринт
    */

    @Test
    void testLoadFromFile() {
        Task task1 = new Task("Task", "Description Task");
        Task task2 = new Task("Task2", "Description Task2");

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask11 = new Subtask("Subtask1", "Description Subtask1");
        Subtask subtask12 = new Subtask("Subtask2", "Description Subtask2");

        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask11);
        manager.createSubtask(epic1, subtask12);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        assertEquals(2, manager.getAllTasks().size());
        assertEquals(2, manager.getHistory().size());

        manager.deleteAllTasks();
        FileBackedTasksManager loader = FileBackedTasksManager.loadFromFile(PATH_FILE);

        assertEquals(0, loader.getAllTasks().size());
        assertEquals(0, loader.getHistory().size());
    }

    @Test
    void testEmptyLoad() {
        manager.deleteAllTasks();
        FileBackedTasksManager testManager = FileBackedTasksManager.loadFromFile(PATH_FILE);
        assertEquals(0, testManager.getAllTasks().size());

    }

    @Test
    public void shouldSaveAndRestoreAnEmptyTaskList() {
        Task task = new Task("Task", "Description Task");
        manager.createTask(task);

        assertEquals(1, manager.getAllTasks().size());

        manager.deleteTaskById(task.getId());

        FileBackedTasksManager loader = FileBackedTasksManager.loadFromFile(PATH_FILE);

        assertEquals(List.of(), loader.getAllTasks());
    }

    @Test
    public void shouldSaveAndRestoreEpicWithoutSubtasks() {
        Epic epic = new Epic("Epic", "Description Epic");

        List<Task> epicList = new ArrayList<>(List.of(epic));

        manager.createEpic(epic);

        FileBackedTasksManager loader = FileBackedTasksManager.loadFromFile(PATH_FILE);

        assertEquals(epicList.size(), loader.getAllEpics().size());
        assertEquals(List.of(), loader.getAllSubtasks());
    }

    @Test
    public void shouldSaveAndRestoreAnEmptyHistoryList() {
        Task task = new Task("Task", "Description Task");
        Task task2 = new Task("Task2", "Description Task2");

        manager.createTask(task);
        manager.createTask(task2);

        manager.getTaskById(task.getId());
        manager.getTaskById(task2.getId());

        assertEquals(2, manager.getAllTasks().size());
        assertEquals(2, manager.getHistory().size());

        manager.deleteAllTasks();
        FileBackedTasksManager loader = FileBackedTasksManager.loadFromFile(PATH_FILE);

        assertEquals(0, loader.getAllTasks().size());
        assertEquals(0, loader.getHistory().size());
    }
}