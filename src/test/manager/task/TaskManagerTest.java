package test.manager.task;

import taskType.Epic;
import taskType.Subtask;
import taskType.Task;
import constant.Status;
import manager.task.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected static final int FAKE_ID = -999;
    protected T manager;

    @Test
    void shouldReturnTaskList() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> taskList1 = new ArrayList<>(List.of(task1, task2));
        List<Task> taskList2 = new ArrayList<>(List.of(task2, task1, task2));

        assertEquals(taskList1.size(), manager.getAllTasks().size());
        assertNotEquals(taskList2.size(), manager.getAllTasks().size());

        assertEquals(taskList1, manager.getAllTasks());
        assertNotEquals(taskList2, manager.getAllTasks());
    }

    @Test
    void shouldReturnEpicList() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1");

        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(epic2, subtask1);

        List<Task> epicList1 = new ArrayList<>(List.of(epic1, epic2));
        List<Task> epicList2 = new ArrayList<>(List.of(epic2, epic1, epic2));

        List<Task> subtaskList1 = new ArrayList<>(List.of(subtask1));

        assertEquals(epicList1.size(), manager.getAllEpics().size());
        assertNotEquals(epicList2.size(), manager.getAllEpics().size());

        assertEquals(epicList1, manager.getAllEpics());
        assertNotEquals(epicList2, manager.getAllEpics());

        assertEquals(subtaskList1, manager.getAllSubtasks());

        List<Task> emptyEpic = new ArrayList<>();
        List<Task> emptySubtaskByEpic = new ArrayList<>();
        manager.deleteAllEpics();

        assertEquals(emptyEpic, manager.getAllEpics());
        assertEquals(emptySubtaskByEpic, manager.getAllSubtasks());
    }

    @Test
    void shouldReturnSubTaskList() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        List<Task> subtaskList = new ArrayList<>(List.of(subtask1, subtask2));

        assertEquals(subtaskList.size(), manager.getAllSubtasks().size());
        assertEquals(subtaskList, manager.getAllSubtasks());
        assertFalse(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldRemoveAllTask() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());

        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getTasksPriorityTree().isEmpty());
    }

    @Test
    void shouldRemoveAllEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1");

        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(epic2, subtask1);

        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());

        manager.deleteAllEpics();
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getTasksPriorityTree().isEmpty());
    }

    @Test
    void shouldRemoveAllSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        manager.deleteAllSubtasks();
        assertFalse(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getTasksPriorityTree().isEmpty());
    }

    @Test
    void shouldReturnTask() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        int taskId = manager.createTask(task1);

        assertEquals(task1, manager.getTaskById(taskId));
        assertNotEquals(task2, manager.getTaskById(taskId));
    }

    @Test
    void shouldReturnGetEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");

        manager.createEpic(epic1);

        assertEquals(epic1, manager.getEpicById(epic1.getId()));
        assertNotEquals(epic2, manager.getEpicById(epic1.getId()));
    }

    @Test
    void shouldReturnSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        assertEquals(subtask1, manager.getSubtaskById(subtask1.getId()));
        assertNotEquals(subtask1, manager.getSubtaskById(subtask2.getId()));
    }

    @Test
    void shouldCreateTask() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        assertEquals(task1, manager.getTaskById(task1.getId()));
        assertNotEquals(FAKE_ID, task2.getId());
        assertFalse(manager.getAllTasks().isEmpty());
        assertEquals(2, manager.getAllTasks().size());
    }

    @Test
    void shouldCreateEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        assertEquals(epic1, manager.getEpicById(epic1.getId()));
        assertNotEquals(FAKE_ID, epic2.getId());
        assertFalse(manager.getAllEpics().isEmpty());
        assertEquals(2, manager.getAllEpics().size());
    }

    @Test
    void shouldCreateSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        assertEquals(subtask1, manager.getSubtaskById(subtask1.getId()));
        assertNotEquals(FAKE_ID, subtask2.getId());
        assertFalse(manager.getAllSubtasks().isEmpty());
        assertEquals(2, manager.getAllSubtasks().size());
    }

    @Test
    void shouldUpdateTask() {
        Task task1 = new Task("Task1", "Description Task1");

        int taskId = manager.createTask(task1);

        task1 = new Task("New Task", "Description Task1");
        task1.setId(taskId);
        task1.setStatus(Status.IN_PROGRESS);

        manager.updateTask(task1);

        assertEquals("New Task", manager.getTaskById(1).getName());
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(1).getStatus());
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");

        int epicId = manager.createEpic(epic1);

        epic1 = new Epic("New Epic", "Description Epic1");
        epic1.setId(epicId);
        epic1.setStatus(Status.IN_PROGRESS);

        manager.updateEpic(epic1);

        assertEquals("New Epic", manager.getEpicById(1).getName());
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(1).getStatus());
    }

    @Test
    void shouldUpdateSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        int epicId = manager.createEpic(epic1);
        int subtask1Id = manager.createSubtask(epic1, subtask1);
        int subtask2Id = manager.createSubtask(epic1, subtask2);

        assertEquals(Status.NEW, epic1.getStatus());
        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(Status.NEW, subtask2.getStatus());

        subtask1 = new Subtask("New Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        subtask1.setId(subtask1Id);
        subtask1.setEpicId(epicId);
        subtask1.setStatus(Status.DONE);

        manager.updateSubtask(subtask1);

        subtask2 = new Subtask("New SubTask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));
        subtask2.setId(subtask2Id);
        subtask2.setEpicId(epicId);
        subtask2.setStatus(Status.IN_PROGRESS);

        manager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
        assertEquals(Status.DONE, subtask1.getStatus());
        assertEquals(Status.IN_PROGRESS, subtask2.getStatus());
    }

    @Test
    void shouldRemoveTask() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());

        manager.deleteTaskById(task1.getId());

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.deleteTaskById(FAKE_ID));

        assertNull(exception.getMessage());

        manager.deleteTaskById(task2.getId());

        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
        assertTrue(manager.getTasksPriorityTree().isEmpty());
    }

    @Test
    void shouldRemoveEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        manager.getEpicById(epic1.getId());

        manager.deleteEpicById(epic1.getId());

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.deleteEpicById(FAKE_ID));

        assertNull(exception.getMessage());

        manager.deleteEpicById(epic2.getId());

        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveSubTask() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        manager.deleteSubtaskById(subtask1.getId());

        final NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> manager.deleteSubtaskById(FAKE_ID));

        assertNull(exception.getMessage());

        manager.deleteSubtaskById(subtask2.getId());

        assertFalse(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldReturnAllSubTaskOfEpic() {
        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createEpic(epic1);

        assertTrue(manager.getAllSubtasksByEpicId(epic1).isEmpty());

        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        List<Task> subTaskList = new ArrayList<>(List.of(subtask1, subtask2));

        assertEquals(subTaskList.size(), manager.getAllSubtasksByEpicId(epic1).size());
        assertEquals(subTaskList, manager.getAllSubtasksByEpicId(epic1));
    }

    @Test
    void shouldReturnHistory() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        List<Task> taskList1 = new ArrayList<>(List.of(task1, task2));
        List<Task> taskList2 = new ArrayList<>(List.of(task2, task1, task2));

        assertEquals(taskList1.size(), manager.getHistory().size());
        assertNotEquals(taskList2.size(), manager.getHistory().size());

        assertEquals(taskList1, manager.getHistory());
        assertNotEquals(taskList2, manager.getHistory());
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        List<Task> prioritizedTasks = new ArrayList<>();

        prioritizedTasks.add(task2);
        prioritizedTasks.add(subtask2);
        prioritizedTasks.add(subtask1);
        prioritizedTasks.add(task1);

        int size = prioritizedTasks.size();

        assertEquals(size, manager.getTasksPriorityTree().size());

        int indexCounter = 0;
        for (Task task : manager.getTasksPriorityTree()) {
            assertEquals(task, prioritizedTasks.get(indexCounter++));
        }
    }

    @Test
    void shouldUpdateTaskAndReturnPrioritizedTasks() {

        Task task1 = new Task("Task1", "Description Task1");
        Task task2 = new Task("Task2", "Description Task2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 1, 8, 0));

        Epic epic1 = new Epic("Epic1", "Description Epic1");
        Subtask subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        Subtask subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));

        int task1Id = manager.createTask(task1);
        manager.createTask(task2);

        manager.createEpic(epic1);
        manager.createSubtask(epic1, subtask1);
        manager.createSubtask(epic1, subtask2);

        List<Task> expectedOrderList = new ArrayList<>();

        task1 = new Task("Updated task1", "Task1 was update",
                Duration.ofMinutes(120), LocalDateTime.of(2021, 1, 1, 0, 0));
        task1.setId(task1Id);

        expectedOrderList.add(task1);
        expectedOrderList.add(task2);
        expectedOrderList.add(subtask2);
        expectedOrderList.add(subtask1);

        manager.updateTask(task1);

        int indexCounter = 0;

        for (Task task : manager.getTasksPriorityTree()) {
            assertEquals(task, expectedOrderList.get(indexCounter++));
        }

        assertEquals(LocalDateTime.of(2021, 1, 1, 2, 0), task1.getEndTime());
    }
}