package whiteboard;

import java.awt.Graphics2D;

public class Shapes {

	
	public void rectShape(Graphics2D g,int x,int y,int width,int heigth) {
		g.drawRect(x, y, width, heigth);
	}
	public void lineShape(Graphics2D g,int x1,int y1,int x2,int y2) {
		g.drawLine(x1, y1, x2, y2);
	}
	
    public void circleShape(Graphics2D g, int x, int y, int diameter) {
        g.drawOval(x, y, diameter, diameter);
    }
	
	public void ovalShape(Graphics2D g,int x,int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}
	public void trangleShape(Graphics2D g,int []x,int []y, int pointNum) {
		g.drawPolygon(x, y, pointNum);
	}

}
