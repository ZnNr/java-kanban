//спринт6
package taskManagers;

import taskType.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final File PATH_FILE = new File("resources/data.csv");

    /*    public FileBackedTasksManager(File file) {
            super();
        }
    */
    //сохранение
    private void save() {
        final String header = "id,type,name,status,description,epic";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_FILE, StandardCharsets.UTF_8))) {

            bw.write(header);
            bw.write(System.lineSeparator());

            for (Task tasks : getAllTasks()) {
                bw.write(FileStringFormatter.toString(tasks));
            }
            for (Epic epics : getAllEpics()) {
                bw.write(FileStringFormatter.toString(epics));
            }
            for (Subtask subtasks : getAllSubtasks()) {
                bw.write(FileStringFormatter.toString(subtasks));
            }

            bw.write(System.lineSeparator());
            bw.write(FileStringFormatter.historyToString(historyManager));

        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось записать файл");
        }
    }

    //загрузка Менеджера из файла
    public static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {

        if (!Files.exists(file.toPath())) {
            System.err.println("Файла в папочке небыло, создан пустой FileBackedTasksManager");
            return new FileBackedTasksManager();
        } //проверка наличи файла

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

                    Epic epicToConnectSubTasks = fileBackedTasksManager.epics.get(((Subtask) task).getEpicId());
                    epicToConnectSubTasks.getSubtaskIds().add(task.getId());

                } else {
                    fileBackedTasksManager.tasks.put(task.getId(), task);

                    if (task.getId() > fileBackedTasksManager.nextId) {
                        fileBackedTasksManager.nextId = task.getId();
                    }
                    allTasks.put(task.getId(), task);
                }
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла:" + exception.getMessage());
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
    public int createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
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
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasksByEpic(Epic epic) {
        super.deleteAllSubtasksByEpic(epic);
        save();
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
    public Subtask getSubtaskById() {
        Subtask subtask = super.getSubtaskById();
        save();
        return subtask;
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

}




















