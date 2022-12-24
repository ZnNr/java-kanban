package test;

import constant.*;
import taskManagers.*;
import taskType.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static constant.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    /*
Спасибо за ревью! Пожалуйста примите работу чтобы мне успеть сдать 8 спринт
    */

    private static TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getManagerDefault();
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

        Subtask testSubTask1 = new Subtask("Test SubTask1", "Description Test SubTask1");
        Subtask testSubTask2 = new Subtask("Test SubTask2", "Description Test SubTask2");

        manager.createSubtask(testEpic, testSubTask1);
        manager.createSubtask(testEpic, testSubTask2);

        Status epicStatus = manager.getEpicById(testEpicId).getStatus();

        assertEquals(NEW, epicStatus);
    }

    @Test
    public void shouldCheckEpicStatusWithDoneSubTasksStatus() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Subtask testSubTask1 = new Subtask("Test SubTask1", "Description Test SubTask1");
        Subtask testSubTask2 = new Subtask("Test SubTask2", "Description Test SubTask2");

        manager.createSubtask(testEpic, testSubTask1);
        manager.createSubtask(testEpic, testSubTask2);

        testSubTask1.setStatus(DONE);
        testSubTask2.setStatus(DONE);

        manager.updateSubtask(testSubTask2);
        manager.updateSubtask(testSubTask2);

        Status epicStatus = manager.getEpicById(testEpicId).getStatus();

        assertEquals(DONE, epicStatus);
    }

    @Test
    public void shouldCheckEpicInProgressStatus() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Subtask testSubTask1 = new Subtask("Test SubTask1", "Description Test SubTask1");
        Subtask testSubTask2 = new Subtask("Test SubTask2", "Description Test SubTask2");

        manager.createSubtask(testEpic, testSubTask1);
        manager.createSubtask(testEpic, testSubTask2);

        testSubTask2.setStatus(DONE);

        manager.updateSubtask(testSubTask2);

        Status epicStatus = manager.getEpicById(testEpicId).getStatus();

        assertEquals(IN_PROGRESS, epicStatus);
    }

    @Test
    public void shouldCheckEpicInProgressStatus2() {
        Epic testEpic = new Epic("Test Epic", "Description Test Epic");
        int testEpicId = manager.createEpic(testEpic);

        Subtask testSubtask1 = new Subtask("Test SubTask1", "Description Test SubTask1");
        Subtask testSubtask2 = new Subtask("Test SubTask1", "Description Test SubTask1");

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