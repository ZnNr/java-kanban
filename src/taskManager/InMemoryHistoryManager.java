package taskManager;


import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task value;
        Node prev;
        Node next;

        private Node(Task task, Node next, Node prev) {
            this.value = task;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString() {
            return "Node:{" + value.toString() + "}";
        }
    }

    private Node head;
    private Node last;
    private int size;
    private final Map<Integer, Node> nodeMap = new HashMap<>();

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node newHead = head;
        while (newHead.next != null) {
            taskList.add(newHead.value);
            newHead = newHead.next;
        }
        taskList.add(newHead.value);
        return taskList;
    }

    public void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(task, null, l);
        last = newNode;
        if (l == null)
            head = newNode;
        else
            l.next = newNode;
        size += 1;
    }

    public void removeFirst() {
        if (getSize() == 0) {
            throw new NoSuchElementException("История пока что пуста");
        }
        if (head == last) {
            last = null;
        } else {
            head.next.prev = null;
        }
        head = head.next;
        size -= 1;
    }

    public void removeLast() {
        if (getSize() == 0) {
            throw new NoSuchElementException("История пока что пуста");
        }
        if (head == last) {
            head = null;
            return;
        } else {
            last.prev.next = null;
        }
        last = last.prev;
        size -= 1;
    }

    @Override
    public void remove(int id) {
        final Node node = nodeMap.remove(id);
        if (node.prev == null) {
            removeFirst();
        } else if (node.next == null) {
            removeLast();
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
            size -= 1;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        if (checkSize()) {
            removeFirst();
        }
        linkLast(task);
        nodeMap.put(task.getId(), last);
    }


    public void removeAll() {
        nodeMap.clear();
        Node newNode = head;
        while (newNode.next != null) {
            head = null;
            newNode = newNode.next;
        }
    }

    private boolean checkSize() {
        return size == 10;
    }

    public int getSize() {
        return size;
    }

}