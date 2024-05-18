package whiteboard;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class DrawingActionHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<DrawingAction> history;

    public DrawingActionHistory() {
        history = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized void addAction(DrawingAction action) {
        history.add(action);
    }

    public synchronized List<DrawingAction> getHistory() {
        return new ArrayList<>(history);
    }
}
