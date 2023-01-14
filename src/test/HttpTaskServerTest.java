package test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.http.endPoints;
import taskType.Epic;
import taskType.Subtask;
import taskType.Task;
import manager.task.TaskManager;
import manager.http.HttpTaskServer;
import manager.http.KVServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;

    private static Gson gson;
    private static Task task1;
    private static Epic epic1;
    private static Subtask subtask1;
    private static Subtask subtask2;
    private static HttpClient client;
    private static TaskManager manager;

    private static final URI url = URI.create("http://localhost:8080");
    private static final String FAKE_URL = "/fake_url";
    private static final int FAKE_ID = -999;

    @BeforeAll
    public static void beforeAll() {
        gson = new Gson();
        client = HttpClient.newBuilder().build();

        task1 = new Task("Task1", "Description Task1");
        epic1 = new Epic("Epic1", "Description Epic1");
        subtask1 = new Subtask("Subtask1", "Description Subtask1",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 10, 0));
        subtask1.setEpicId(2);
        subtask2 = new Subtask("Subtask2", "Description Subtask2",
                Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 2, 8, 0));
        subtask2.setEpicId(2);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        manager = httpTaskServer.getManager();
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void shouldCheckPostMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.epicKey))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskKey))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
        assertEquals(2, manager.getTasksPriorityTree().size());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + FAKE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCheckGetMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.tasksKey))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertTrue(manager.getTasksPriorityTree().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.epicKey))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskKey))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubtasks().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + FAKE_URL))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCheckGetMethodById() throws IOException, InterruptedException {
        createAllTask();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey + "?id=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskForCheck = manager.getTaskById(1);
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertEquals(taskForCheck, taskFromResponse);

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.epicKey + "?id=2"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic epicForCheck = manager.getEpicById(2);
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertEquals(epicForCheck, epicFromResponse);

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskKey + "?id=3"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtaskForCheck = manager.getSubtaskById(3);
        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtaskForCheck, subtaskFromResponse);

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskEpicKey + "?id=2"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type typeToken = new TypeToken<List<Subtask>>() {
        }.getType();

        List<Subtask> allSubtaskForCheck = manager.getAllSubtasksByEpicId(manager.getEpicById(2));
        List<Subtask> allSubtaskFromResponse = gson.fromJson(response.body(), typeToken);
        assertEquals(allSubtaskForCheck.size(), allSubtaskFromResponse.size());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey + "?id=" + FAKE_ID))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldCheckGetMethodFromHistory() throws IOException, InterruptedException {
        createHistory();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.historyKey))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertFalse(manager.getHistory().isEmpty());

    }

    @Test
    void shouldCheckDeleteMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.tasksKey))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertTrue(manager.getTasksPriorityTree().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.epicKey))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpics().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskKey))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubtasks().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + FAKE_URL))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCheckDeleteMethodById() throws IOException, InterruptedException {
        createAllTask();
        assertNotNull(manager.getTaskById(1));
        assertNotNull(manager.getSubtaskById(3));
        assertNotNull(manager.getSubtaskById(4));
        assertNotNull(manager.getEpicById(2));
        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(2, manager.getAllSubtasks().size());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey + "?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllTasks().size());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskKey + "?id=3"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskKey + "?id=4"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllSubtasks().size());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.epicKey + "?id=2"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllEpics().size());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey + "?id=" + FAKE_ID))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    private void createAllTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.epicKey))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskKey))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.subtaskKey))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void createHistory() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + endPoints.taskKey + "?id=1"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}