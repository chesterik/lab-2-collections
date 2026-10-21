package ua.kpi.comsys.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class ActionHistoryManagerTest {

    @Test
    void constructorRejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ActionHistoryManager(0));
        assertThrows(IllegalArgumentException.class, () -> new ActionHistoryManager(-5));
    }

    @Test
    void performActionRejectsNullAndBlank() {
        ActionHistoryManager manager = new ActionHistoryManager(3);
        assertThrows(IllegalArgumentException.class, () -> manager.performAction(null));
        assertThrows(IllegalArgumentException.class, () -> manager.performAction(""));
        assertThrows(IllegalArgumentException.class, () -> manager.performAction("   "));
        assertEquals(0, manager.getHistorySize());
    }

    @Test
    void historyIsOrderedFromNewestToOldest() {
        ActionHistoryManager manager = new ActionHistoryManager(5);
        manager.performAction("open");
        manager.performAction("edit");
        manager.performAction("save");

        assertEquals(List.of("save", "edit", "open"), manager.getHistory());
        assertEquals(3, manager.getHistorySize());
    }

    @Test
    void oldestActionIsRemovedWhenCapacityIsExceeded() {
        ActionHistoryManager manager = new ActionHistoryManager(2);
        manager.performAction("a");
        manager.performAction("b");
        manager.performAction("c");

        assertEquals(List.of("c", "b"), manager.getHistory());
        assertEquals(2, manager.getHistorySize());
    }

    @Test
    void undoReturnsNullWhenEmpty() {
        assertNull(new ActionHistoryManager(3).undo());
    }

    @Test
    void undoRemovesNewestAction() {
        ActionHistoryManager manager = new ActionHistoryManager(3);
        manager.performAction("a");
        manager.performAction("b");

        assertEquals("b", manager.undo());
        assertEquals(List.of("a"), manager.getHistory());
    }

    @Test
    void peekReturnsNullWhenEmpty() {
        assertNull(new ActionHistoryManager(3).peekRecentAction());
    }

    @Test
    void peekReturnsNewestActionWithoutRemoving() {
        ActionHistoryManager manager = new ActionHistoryManager(3);
        manager.performAction("a");
        manager.performAction("b");

        assertEquals("b", manager.peekRecentAction());
        assertEquals(2, manager.getHistorySize());
    }

    @Test
    void getHistoryReturnsDefensiveCopy() {
        ActionHistoryManager manager = new ActionHistoryManager(3);
        manager.performAction("a");

        manager.getHistory().clear();

        assertEquals(List.of("a"), manager.getHistory());
    }

    @Test
    void clearRemovesAllActions() {
        ActionHistoryManager manager = new ActionHistoryManager(3);
        manager.performAction("a");
        manager.performAction("b");

        manager.clear();

        assertEquals(0, manager.getHistorySize());
        assertNull(manager.peekRecentAction());
    }
}