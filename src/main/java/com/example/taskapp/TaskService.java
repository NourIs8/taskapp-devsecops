
package com.example.taskapp;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<Task> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparing(Task::id))
                .toList();
    }

    public Task create(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        long id = sequence.incrementAndGet();
        Task task = new Task(id, title.trim(), false);
        tasks.put(id, task);
        return task;
    }

    public Optional<Task> markDone(long id) {
        return Optional.ofNullable(
                tasks.computeIfPresent(id, (k, t) -> new Task(t.id(), t.title(), true)));
    }

    public boolean delete(long id) {
        return tasks.remove(id) != null;
    }
}
