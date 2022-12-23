package test;

import org.junit.jupiter.api.BeforeEach;
import taskManagers.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }
}