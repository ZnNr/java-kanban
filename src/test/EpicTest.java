package test;

import constant.Status;
import manager.Managers;
import manager.task.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskType.Epic;
import taskType.Subtask;

import static constant.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private static TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void shouldCheckEpicStatusWithoutSubTasks() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Status epicStatus = manager.getEpicById(testEpicId).getStatus();

        assertEquals(NEW, epicStatus);
    }

    @Test
    public void shouldCheckEpicStatusWithNewSubTasks() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Subtask testSubtask1 = new Subtask("Test Subtask1", "Description Test Subtask1");
        Subtask testSubtask2 = new Subtask("Test Subtask2", "Description Test Subtask2");

        manager.createSubtask(testEpic, testSubtask1);
        manager.createSubtask(testEpic, testSubtask2);

        Status epicStatus = manager.getEpicById(testEpicId).getStatus();

        assertEquals(NEW, epicStatus);
    }

    @Test
    public void shouldCheckEpicStatusWithDoneSubTasksStatus() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Subtask testSubtask1 = new Subtask("Test Subtask1", "Description Test Subtask1");
        Subtask testSubtask2 = new Subtask("Test Subtask2", "Description Test Subtask2");

        manager.createSubtask(testEpic, testSubtask1);
        manager.createSubtask(testEpic, testSubtask2);

        testSubtask1.setStatus(DONE);
        testSubtask2.setStatus(DONE);

        manager.updateSubtask(testSubtask1);
        manager.updateSubtask(testSubtask2);

        Status epicStatus = manager.getEpicById(testEpicId).getStatus();

        assertEquals(DONE, epicStatus);
    }

    @Test
    public void shouldCheckEpicInProgressStatus() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Subtask testSubtask1 = new Subtask("Test Subtask1", "Description Test Subtask1");
        Subtask testSubtask2 = new Subtask("Test Subtask2", "Description Test Subtask2");

        manager.createSubtask(testEpic, testSubtask1);
        manager.createSubtask(testEpic, testSubtask2);

        testSubtask2.setStatus(DONE);

        manager.updateSubtask(testSubtask2);

        Status epicStatus = manager.getEpicById(testEpicId).getStatus();

        assertEquals(IN_PROGRESS, epicStatus);
    }

    @Test
    public void shouldCheckEpicInProgressStatus2() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Subtask testSubtask1 = new Subtask("Test Subtask1", "Description Test Subtask1");
        Subtask testSubtask2 = new Subtask("Test Subtask1", "Description Test Subtask1");

        manager.createSubtask(testEpic, testSubtask1);
        manager.createSubtask(testEpic, testSubtask2);

        testSubtask1.setStatus(IN_PROGRESS);
        testSubtask2.setStatus(IN_PROGRESS);

        manager.updateSubtask(testSubtask1);
        manager.updateSubtask(testSubtask2);

        Status epicStatus = manager.getEpicById(testEpicId).getStatus();

        assertEquals(IN_PROGRESS, epicStatus);
    }
}