package aac.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import aac.PGM;

public class HistogramDrawer extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 256;
	
	private PGM image;

	public HistogramDrawer(PGM image)
	{
		this.image = image;
	}
	
	public PGM getImage() {
		return image;
	}
	
	public void setImage(PGM newImage)
	{
		this.image = newImage;
		this.repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		if (image != null) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, image.getPixelLimit(), HEIGHT);
			g.setColor(Color.BLUE);
			
			int[] histogram = image.getHistogram();
			int maxValue = 0;
	
			for (int i = 0; i < histogram.length; i++)
			{
				maxValue = Math.max(maxValue, histogram[i]);
			}
			
			for (int i = 0; i < histogram.length; i++)
			{
				int height = (int) (HEIGHT * (((double) histogram[i]) / maxValue));
				g.drawRect(i, HEIGHT - height, 1, height);
			}
		}
	}

}
