
package com.example.taskapp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TaskServiceTest {

    @Test
    void createAddsATaskNotDone() {
        TaskService service = new TaskService();
        Task task = service.create("Apprendre Jenkins");
        assertEquals("Apprendre Jenkins", task.title());
        assertFalse(task.done());
        assertEquals(1, service.findAll().size());
    }

    @Test
    void createRejectsBlankTitle() {
        TaskService service = new TaskService();
        assertThrows(IllegalArgumentException.class, () -> service.create("  "));
    }

    @Test
    void markDoneUpdatesTheTask() {
        TaskService service = new TaskService();
        Task task = service.create("Ecrire le rapport");
        assertTrue(service.markDone(task.id()).orElseThrow().done());
    }

    @Test
    void markDoneOnUnknownIdReturnsEmpty() {
        assertTrue(new TaskService().markDone(99).isEmpty());
    }

    @Test
    void deleteRemovesTheTask() {
        TaskService service = new TaskService();
        Task task = service.create("A supprimer");
        assertTrue(service.delete(task.id()));
        assertTrue(service.findAll().isEmpty());
    }
}
