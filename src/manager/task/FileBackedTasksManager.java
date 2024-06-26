package manager.task;

import taskType.Epic;
import taskType.Subtask;
import taskType.Task;
import exception.ManagerSaveException;
import manager.FileStringFormatter;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final File PATH_FILE = new File("resources/data.csv");

    protected void save() {

        final String header = "id,type,title,status,description,epic,duration,startTime,endTime";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_FILE, StandardCharsets.UTF_8))) {

            bw.write(header);
            bw.write(System.lineSeparator());

            for (Task task : getAllTasks()) {
                bw.write(FileStringFormatter.toString(task));
            }
            for (Epic epic : getAllEpics()) {
                bw.write(FileStringFormatter.toString(epic));
            }
            for (Subtask subTask : getAllSubtasks()) {
                bw.write(FileStringFormatter.toString(subTask));
            }

            bw.write(System.lineSeparator());
            bw.write(FileStringFormatter.historyToString(historyManager));

        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось записать файл");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        Map<Integer, Task> allTasks = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> lines = new ArrayList<>();

            while (br.ready()) {
                lines.add(br.readLine());
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.isEmpty()) {
                    line = lines.get(i + 1);
                    List<Integer> history = FileStringFormatter.historyFromString(line);

                    for (Integer id : history) {
                        if (id == 0) {
                            break;
                        }
                        fileBackedTasksManager.historyManager.add(allTasks.get(id));
                    }
                    break;
                }

                Task task = FileStringFormatter.fromString(lines.get(i));
                assert task != null;

                if (task instanceof Epic) {
                    fileBackedTasksManager.epics.put(task.getId(), (Epic) task);

                    if (task.getId() > fileBackedTasksManager.nextId) {
                        fileBackedTasksManager.nextId = task.getId();
                    }
                    allTasks.put(task.getId(), task);

                } else if (task instanceof Subtask) {
                    fileBackedTasksManager.subtasks.put(task.getId(), (Subtask) task);

                    if (task.getId() > fileBackedTasksManager.nextId) {
                        fileBackedTasksManager.nextId = task.getId();
                    }
                    allTasks.put(task.getId(), task);

                    Epic epicToConnectSubtasks = fileBackedTasksManager.epics.get(((Subtask) task).getEpicId());
                    epicToConnectSubtasks.getSubtaskIds().add(task.getId());

                } else {
                    fileBackedTasksManager.tasks.put(task.getId(), task);

                    if (task.getId() > fileBackedTasksManager.nextId) {
                        fileBackedTasksManager.nextId = task.getId();
                    }
                    allTasks.put(task.getId(), task);
                }
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return fileBackedTasksManager;
    }

    @Override
    public int createTask(Task task) {
        super.createTask(task);
        save();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subTask = super.getSubtaskById(id);
        save();
        return subTask;
    }

    @Override
    public int createSubtask(Epic epic, Subtask subtask) {
        super.createSubtask(epic, subtask);
        save();
        return subtask.getId();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();

    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();

    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();

    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}


