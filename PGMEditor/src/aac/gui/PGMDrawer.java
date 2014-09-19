package aac.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import aac.PGM;

public class PGMDrawer extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private PGM image;

	public PGMDrawer(PGM image)
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
		if (image != null)
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					int tone = image.getPixel(x, y);
					g.setColor(new Color(tone, tone, tone));
					g.drawRect(x, y, 1, 1);
				}
			}
		}
	}
	
}
