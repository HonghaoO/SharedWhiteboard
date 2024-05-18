package whiteboard;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;

public class DrawCanvas extends Canvas {
	private Image image=null;
	private Image tempImage = null;

	public void setTempImage(Image tempImage) {
	    this.tempImage = tempImage;
	}


	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public void paint(Graphics g) {
	    g.drawImage(image, 0, 0, null);
	    if (tempImage != null) {
	        g.drawImage(tempImage, 0, 0, null);
	    }
	}

	@Override
	public void update(Graphics g) {
		// TODO Auto-generated method stub
		paint(g);
	}
	
	

}
