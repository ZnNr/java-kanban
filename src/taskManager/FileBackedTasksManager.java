//спринт6
package taskManager;


import status.Status;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    private boolean needToSave = true;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;

        if (!file.isFile()) {
            try {
                Path path = Files.createFile(Paths.get(file.getName()));
            } catch (IOException e) {
                System.out.println("Ошибка создания файла.");
            }
        }
    }


    //загрузка Менеджера из файла
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        fileBackedTasksManager.needToSave = false;

        try {
            FileInputStream input = new FileInputStream(file);
            byte[] bytes = new byte[input.available()];
            input.read(bytes);
            String data = new String(bytes, "UTF-8");
            String[] lines = data.split("\r\n");
            List<String> epics = new ArrayList<>();
            List<String> subtasks = new ArrayList<>();
            List<String> tasks = new ArrayList<>();
            String lineOfHistory = "";
            boolean isTitle = true;
            boolean isTask = true;
            int maxId = 0;
            int id;
            for (String line : lines) {
                if (isTitle) {
                    isTitle = false;
                    continue;
                }
                if (line.isEmpty()) {
                    isTask = false;
                    continue;
                }
                if (isTask) {
                    TaskType taskType = TaskType.valueOf(line.split(",")[1]);
                    switch (taskType) {
                        case EPIC:
                            Epic epic = (Epic) fromString(line);
                            id = epic.getId();
                            if (id > maxId) {
                                maxId = id;
                            }
                            fileBackedTasksManager.createEpic(epic);
                            //epics.add(line);
                            break;

                        case SUBTASK:
                            Subtask subtask = (Subtask) fromString(line);
                            id = subtask.getId();
                            if (id > maxId) {
                                maxId = id;
                            }
                            fileBackedTasksManager.createSubtask(subtask);
                            //subtasks.add(line);
                            break;

                        case TASK:
                            Task task = fromString(line);

                            id = task.getId();
                            if (id > maxId) {
                                maxId = id;
                            }
                            fileBackedTasksManager.createTask(task);
                            //tasks.add(line);
                            break;
                    }
                } else {
                    lineOfHistory = line;
                }
            }

            fileBackedTasksManager.setId(maxId);
            List<Integer> ids = historyFromString(lineOfHistory);
            for (Integer taskId : ids) {
                fileBackedTasksManager.getHistoryManager().add(fileBackedTasksManager.getTaskAllKind(taskId));
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Не удалось прочитать файл!");
        }

        fileBackedTasksManager.needToSave = true;
        return fileBackedTasksManager;
    }

    private Task getTaskAllKind(int id) {
        Task task = getTaskById(id);
        if (task != null) {
            return task;
        }

        Task epic = getEpicById(id);
        if (epic != null) {
            return epic;
        }

        Task subtask = getSubtaskById();
        if (subtask != null) {
            return subtask;
        }

        return null;
    }

    //сохранение
    private void save() {
        if (needToSave) {
            try {
                PrintWriter printWriter = new PrintWriter(file);
                try {
                    printWriter = new PrintWriter(file, "UTF-8");
                } catch (UnsupportedEncodingException exx) {
                    printWriter = new PrintWriter(file);
                }

                printWriter.println("id,type,name,status,description,epic");

                for (Task task : getAllTasks()) {
                    printWriter.println(toString(task));
                }
                for (Epic epic : getAllEpics()) {
                    printWriter.println(toString(epic));
                }
                for (Subtask subtask : getAllSubtasks()) {
                    printWriter.println(toString(subtask));
                }

                List<Task> history = getHistory();
                if (!history.isEmpty()) {
                    printWriter.println();
                    printWriter.println(historyToString(getHistoryManager()));
                }
                printWriter.close();
            } catch (FileNotFoundException ex) {
                throw new ManagerSaveException("Ошибка! Не удалось сохранить в файл.");
            }
        }
    }

    private String getParentEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicId());
        }
        return "";
    }

    private TaskType getType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    // Метод сохранения задачи в строку
    private String toString(Task task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getTitle(),
                task.getStatus().toString(), task.getDescription(), getParentEpicId(task)};
        return String.join(",", toJoin);
    }

    //восстановление задачи из строки
    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (TaskType.valueOf(type)) {
            case TASK -> {
                Task task = new Task(title, description, status);
                task.setId(id);
                return task;

            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(title, description, status, epicId);
                subtask.setId(id);
                return subtask;
            }
            case EPIC -> {
                Epic epic = new Epic(title, description, status);
                epic.setId(id);
                return epic;
            }
        }
        return null;
    }


    // Метод для сохранения истории в CSV
    private static String historyToString(HistoryManager manager) {
        if (manager == null) {
            return "история пуста";
        }
        List<Task> historyList = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        for (Task task : historyList) {
            if (task == null) continue;
            sb.append(task.getId()).append(",");
        }
        return sb.toString();
    }

    // Метод восстановления менеджера истории из CSV
    private static List<Integer> historyFromString(String value) {
        List<Integer> historyList = new ArrayList<>();
        if (value != null) {
            String[] list = value.split(",");
            for (String id : list) {
                historyList.add(Integer.parseInt(id));
            }
        }
        return historyList;
    }


    @Override
    public int createTask(Task task) {
        int newTaskId = super.createTask(task);
        save();
        return newTaskId;
    }


    @Override
    public int createEpic(Epic epic) {
        int newEpicId = super.createEpic(epic);
        save();
        return newEpicId;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int newSubtaskId = super.createSubtask(subtask);
        save();
        return newSubtaskId;
    }


    public int addTask(Task task) {
        return super.createTask(task);
    }

    public int addEpic(Epic epic) {
        return super.createEpic(epic);
    }

    public int addSubtask(Subtask subtask) {
        return super.createSubtask(subtask);
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