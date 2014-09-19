package aac.util;

import java.awt.image.BufferedImage;

import aac.GrayscaleConverterStrategy;
import aac.PGM;

public class BufferedImageToPGM {
	
	public static PGM create(BufferedImage image, GrayscaleConverterStrategy strategy) 
	{
		int[][] bitmap = new int[image.getWidth()][image.getHeight()];
		
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int rgb = image.getRGB(x, y);
				int grayscale = strategy.toGrayscale(rgb);
				bitmap[x][y] = grayscale;
			}
		}
		
		return new PGM(bitmap, 255);
	}

}
