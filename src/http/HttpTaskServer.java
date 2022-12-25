package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import taskManagers.Managers;
import taskManagers.TaskManager;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;

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

    private final TaskManager httpManager;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        gson = new Gson().newBuilder().setPrettyPrinting().create();
        httpManager = Managers.getHTTPManager();
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
    }

    public TaskManager getHttpManager() {
        return httpManager;
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
                case "GET" -> handlesGetProcessing(exchange);
                case "POST" -> handlesPostProcessing(exchange);
                case "DELETE" -> handlesDeleteProcessing(exchange);
            }
        }

        private void handlesGetProcessing(HttpExchange exchange) throws IOException {
            String response = "";
            int statusCode = 404;

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            if (path.endsWith("/tasks") && query == null) {
                statusCode = 200;
                response = gson.toJson(httpManager.getTasksPriorityTree());
            }

            if (path.endsWith("tasks/subtask/epic") && query != null) {
                statusCode = 200;
                int taskId = getIdFromQuery(query);
                response = gson.toJson(httpManager.getAllSubtasksByEpicId(httpManager.getEpicById(taskId)));
            }

            if (path.endsWith("tasks/task")) {
                if (query != null) {
                    Task taskForReturn = null;
                    int taskId = getIdFromQuery(query);

                    for (Task task : httpManager.getAllTasks()) {
                        if (taskId == task.getId()) {
                            taskForReturn = httpManager.getTaskById(taskId);
                            statusCode = 200;
                            response = gson.toJson(taskForReturn);
                        }
                    }

                    if (taskForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, TASK c " + taskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(httpManager.getAllTasks());
                }
            }

            if (path.endsWith("tasks/history") && query == null) {
                statusCode = 200;
                response = gson.toJson(httpManager.getHistory());
            }

            if (path.endsWith("tasks/epic")) {
                if (query != null) {
                    Epic epicForReturn = null;
                    int epicId = getIdFromQuery(query);

                    for (Epic epic : httpManager.getAllEpics()) {
                        if (epicId == epic.getId()) {
                            epicForReturn = httpManager.getEpicById(epicId);
                            statusCode = 200;
                            response = gson.toJson(epicForReturn);
                        }
                    }

                    if (epicForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, EPIC c " + epicId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(httpManager.getAllEpics());
                }
            }

            if (path.endsWith("tasks/subtask")) {
                if (query != null) {
                    Subtask subtaskForReturn = null;
                    int subtaskId = getIdFromQuery(query);

                    for (Subtask subtask : httpManager.getAllSubtasks()) {
                        if (subtaskId == subtask.getId()) {
                            subtaskForReturn = httpManager.getSubtaskById(subtaskId);
                            statusCode = 200;
                            response = gson.toJson(subtaskForReturn);
                        }
                    }

                    if (subtaskForReturn == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, SUBTASK c " + subtaskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    response = gson.toJson(httpManager.getAllSubtasks());
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
                    if (path.endsWith("tasks/task")) {
                        statusCode = 201;
                        Task newTask = gson.fromJson(body, Task.class);
                        httpManager.createTask(newTask);
                        response = "Задача типа TASK создана";

                    } else if (path.endsWith("tasks/epic")) {
                        statusCode = 201;
                        Epic newEpic = gson.fromJson(body, Epic.class);
                        httpManager.createEpic(newEpic);
                        response = "Задача типа EPIC создана";

                    } else if (path.endsWith("tasks/subtask")) {
                        Subtask newSubtask = gson.fromJson(body, Subtask.class);
                        Epic epicForSubtask = null;

                        for (Epic epic : httpManager.getAllEpics()) {
                            if (epic.getId() == newSubtask.getEpicId()) {
                                epicForSubtask = epic;
                                statusCode = 201;
                                httpManager.createSubtask(epic, newSubtask);
                                httpManager.updateSubtask(newSubtask);
                                response = "Задача типа SUBTASK создана";
                            }
                        }

                        if (epicForSubtask == null) {
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
                httpManager.deleteAllTasks();
                httpManager.deleteAllEpics();
                httpManager.getTasksPriorityTree().clear();
                response = "Все задачи удалены";
            }

            if (path.endsWith("tasks/task")) {
                if (query != null) {
                    Task taskForRemove = null;
                    int taskId = getIdFromQuery(query);

                    for (Task task : httpManager.getAllTasks()) {
                        if (taskId == task.getId()) {
                            taskForRemove = task;
                            statusCode = 200;
                            httpManager.deleteTaskById(taskId);
                            response = "Задача типа TASK с ID " + taskId + " удалена";
                        }
                    }

                    if (taskForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, TASK c " + taskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    httpManager.deleteAllTasks();
                    response = "удалены все задачи типа TASK";
                }
            }

            if (path.endsWith("tasks/epic")) {
                if (query != null) {
                    Epic epicForRemove = null;
                    int epicId = getIdFromQuery(query);

                    for (Epic epic : httpManager.getAllEpics()) {
                        if (epicId == epic.getId()) {
                            epicForRemove = epic;
                            statusCode = 200;
                            httpManager.getAllSubtasksByEpicId(epicForRemove).clear();
                            httpManager.deleteEpicById(epicId);
                            response = "Задача типа EPIC с ID " + epicId + " удалена";
                        }
                    }

                    if (epicForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, EPIC c " + epicId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    httpManager.deleteAllEpics();
                    response = "удалены все задачи типа EPIC";
                }
            }

            if (path.endsWith("tasks/subtask")) {
                if (query != null) {
                    Subtask subtaskForRemove = null;
                    int subtaskId = getIdFromQuery(query);

                    for (Subtask subtask : httpManager.getAllSubtasks()) {
                        if (subtaskId == subtask.getId()) {
                            subtaskForRemove = subtask;
                            statusCode = 200;
                            httpManager.deleteTaskById(subtaskId);
                            response = "Задача типа SUBTASK с ID " + subtaskId + " удалена";
                        }
                    }

                    if (subtaskForRemove == null) {
                        statusCode = 400;
                        response = "Необходимо указать корректный id задачи, SUBTASK c " + subtaskId + " не существует";
                    }

                } else {
                    statusCode = 200;
                    httpManager.deleteAllSubtasks();
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
