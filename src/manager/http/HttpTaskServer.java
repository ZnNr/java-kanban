package manager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import taskType.Epic;
import taskType.Subtask;
import taskType.Task;
import manager.Managers;
import manager.task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;

    private final TaskManager manager;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        gson = new Gson().newBuilder().setPrettyPrinting().create();
        manager = Managers.getDefault();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext(endPoints.tasksKey, new TasksHandler());
    }

    public TaskManager getManager() {
        return manager;
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    handlesGetProcessing(exchange);
                    break;

                case "POST":
                    handlesPostProcessing(exchange);
                    break;

                case "DELETE":
                    handlesDeleteProcessing(exchange);
                    break;
            }
        }

        private void handlesGetProcessing(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode = 404;

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if (path.endsWith(endPoints.tasksKey) && query == null) {
                statusCode = 200;
                response = gson.toJson(manager.getTasksPriorityTree());
            }

            if (path.endsWith(endPoints.subtaskEpicKey) && query != null) {
                statusCode = 200;
                int taskId = getIdFromQuery(query);
                response = gson.toJson(manager.getAllSubtasksByEpicId(manager.getEpicById(taskId)));
            }

            if (path.endsWith(endPoints.taskKey)) {
                if (query != null) {
                    Task taskForReturn = null;
                    int taskId = getIdFromQuery(query);

                    for (Task task : manager.getAllTasks()) {
                        if (taskId == task.getId()) {
                            taskForReturn = manager.getTaskById(taskId);
                            statusCode = 200;
                            response = gson.toJson(taskForReturn);
                        }
                    }

                    if (taskForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, task c " + taskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(manager.getAllTasks());
                }
            }

            if (path.endsWith(endPoints.historyKey) && query == null) {
                statusCode = 200;
                response = gson.toJson(manager.getHistory());
            }

            if (path.endsWith(endPoints.epicKey)) {
                if (query != null) {
                    Epic epicForReturn = null;
                    int epicId = getIdFromQuery(query);

                    for (Epic epic : manager.getAllEpics()) {
                        if (epicId == epic.getId()) {
                            epicForReturn = manager.getEpicById(epicId);
                            statusCode = 200;
                            response = gson.toJson(epicForReturn);
                        }
                    }

                    if (epicForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, epic c " + epicId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(manager.getAllEpics());
                }
            }

            if (path.endsWith(endPoints.subtaskKey)) {
                if (query != null) {
                    Subtask subtaskForReturn = null;
                    int subTaskId = getIdFromQuery(query);

                    for (Subtask subTask : manager.getAllSubtasks()) {
                        if (subTaskId == subTask.getId()) {
                            subtaskForReturn = manager.getSubtaskById(subTaskId);
                            statusCode = 200;
                            response = gson.toJson(subtaskForReturn);
                        }
                    }

                    if (subtaskForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, SubTask c " + subTaskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(manager.getAllSubtasks());
                }
            }

            exchange.sendResponseHeaders(statusCode, response.getBytes(CHARSET).length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(CHARSET));
            }
        }

        private void handlesPostProcessing(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode = 404;

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            InputStream inputStream = exchange.getRequestBody();
            if (inputStream != null) {
                String body = new String(inputStream.readAllBytes(), CHARSET);
                if (query == null) {
                    if (path.endsWith(endPoints.taskKey)) {
                        statusCode = 201;
                        Task newTask = gson.fromJson(body, Task.class);
                        manager.createTask(newTask);
                        response = "Задача типа Task создана" + newTask;

                    } else if (path.endsWith(endPoints.epicKey)) {
                        statusCode = 201;
                        Epic newEpic = gson.fromJson(body, Epic.class);
                        manager.createEpic(newEpic);
                        response = "Задача типа Epic создана" + newEpic;

                    } else if (path.endsWith(endPoints.subtaskKey)) {
                        Subtask newSubtask = gson.fromJson(body, Subtask.class);
                        Epic epicForSubTask = null;

                        for (Epic epic : manager.getAllEpics()) {
                            if (epic.getId() == newSubtask.getEpicId()) {
                                epicForSubTask = epic;
                                statusCode = 201;
                                manager.createSubtask(epic, newSubtask);
                                manager.updateSubtask(newSubtask);
                                response = "Задача типа Subtask создана" + newSubtask;
                            }
                        }

                        if (epicForSubTask == null) {
                            statusCode = 400;
                            response = "Необходимо указать корректный id Epic в теле Subtask";
                        }
                    }
                }
            }

            exchange.sendResponseHeaders(statusCode, response.getBytes(CHARSET).length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(CHARSET));
            }
        }

        private void handlesDeleteProcessing(HttpExchange exchange) throws IOException {

            String response = "";
            int statusCode = 404;

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if (path.endsWith("/tasks") && query == null) {
                statusCode = 200;
                manager.deleteAllTasks();
                manager.deleteAllEpics();
                manager.getTasksPriorityTree().clear();
                response = "Все задачи удалены";
            }

            if (path.endsWith(endPoints.taskKey)) {
                if (query != null) {
                    Task taskForRemove = null;
                    int taskId = getIdFromQuery(query);

                    for (Task task : manager.getAllTasks()) {
                        if (taskId == task.getId()) {
                            taskForRemove = task;
                            statusCode = 200;
                            manager.deleteTaskById(taskId);
                            response = "Задача типа TASK с ID " + taskId + " удалена";
                        }
                    }

                    if (taskForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, task c " + taskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    manager.deleteAllTasks();
                    response = "удалены все задачи типа TASK";
                }
            }

            if (path.endsWith(endPoints.epicKey)) {
                if (query != null) {
                    Epic epicForRemove = null;
                    int epicId = getIdFromQuery(query);

                    for (Epic epic : manager.getAllEpics()) {
                        if (epicId == epic.getId()) {
                            epicForRemove = epic;
                            statusCode = 200;
                            manager.getAllSubtasksByEpicId(epicForRemove).clear();
                            manager.deleteEpicById(epicId);
                            response = "Задача типа EPIC с ID " + epicId + " удалена";
                        }
                    }

                    if (epicForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, epic c " + epicId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    manager.deleteAllEpics();
                    response = "удалены все задачи типа EPIC";
                }
            }

            if (path.endsWith(endPoints.subtaskKey)) {
                if (query != null) {
                    Subtask subtaskForRemove = null;
                    int subtaskId = getIdFromQuery(query);

                    for (Subtask subTask : manager.getAllSubtasks()) {
                        if (subtaskId == subTask.getId()) {
                            subtaskForRemove = subTask;
                            statusCode = 200;
                            manager.deleteSubtaskById(subtaskId);
                            response = "Задача типа Subtask с ID " + subtaskId + " удалена";
                        }
                    }

                    if (subtaskForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, Subtask c " + subtaskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    manager.deleteAllSubtasks();
                    response = "удалены все задачи типа SUBTASK";
                }
            }

            exchange.sendResponseHeaders(statusCode, response.getBytes(CHARSET).length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(CHARSET));
            }
        }

        private int getIdFromQuery(String query) {
            String[] splitQuery = query.split("=");
            return Integer.parseInt(splitQuery[1]);
        }

    }
}





