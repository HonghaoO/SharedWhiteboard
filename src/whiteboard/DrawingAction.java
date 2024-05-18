package whiteboard;

import java.io.Serializable;

public class DrawingAction implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum ActionType {
        BRUSH, CHANGE_COLOR, ERASE, STRAIGHT_LINE, RECT_EMPTY, RECT_FILL, CIRCLE, TRIANGLE, OVAL, 
        OVAL_FILL, CLI_RECT, TEXT, MESSAGE, USER_LIST, CLEAR, KICK, DISCONNECT;
    }


    private ActionType actionType;
    private int x1, y1, x2, y2;
    private int color, backColor, foreColor;
    private String text;
    private float strokeSize;

    // Handle drawings
    public DrawingAction(ActionType actionType, int x1, int y1, int x2, int y2, int color, float strokeSize) {

        this.actionType = actionType;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.strokeSize = strokeSize;
//        System.out.println("DrawingAction = " + actionType);
    }
    
    // Handle Textbox
    public DrawingAction(ActionType actionType, int x1, int y1, String text, int color) {
		this.actionType = actionType;
		this.x1 = x1;
		this.y1 = y1;
		this.text = text;
		this.color = color;
	}
    
    // Handle Chat/Kick
    public DrawingAction(ActionType actionType, String text) {
		this.actionType = actionType;
		this.text = text;

	}
    
    // Handle Clear
    public DrawingAction(ActionType actionType, int backColor, int foreColor) {
		this.actionType = actionType;
		this.backColor = backColor;
		this.foreColor = foreColor;

	}


    // Getters and setters
    public ActionType getActionType() {
        return actionType;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getBackColor() {
        return backColor;
    }
    
    public int getForeColor() {
        return foreColor;
    }
    
    public int getColor() {
        return color;
    }
    
    public String getText() {
		return text;
	}
    
    public float getStrokeSize() {
        return strokeSize;
    }

}
