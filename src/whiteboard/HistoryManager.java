package whiteboard;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private List<DrawingAction> history = new ArrayList<>();

    public synchronized void addAction(DrawingAction action) {
        history.add(action);
    }

    public synchronized List<DrawingAction> getHistory() {
        return new ArrayList<>(history);
    }
}
