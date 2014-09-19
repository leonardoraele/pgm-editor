package aac.impl.filter;

import aac.BorderTreatmentStrategy;
import aac.PGM;

/**
 * Este filtro, quando aplicado às bordas da imagem, irá ignorar os pixels que o
 * filtro não casar na imagem. (a parte do filtro que cai fora da imagem)
 */
public class IgnoreBorderFilter implements BorderTreatmentStrategy {

	public void apply(PGM image, int[][] filter, int pivotX, int pivotY)
	{
		int[][] newBitmap = new int[image.getWidth()][image.getHeight()];
		
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int value = 0;
				int div = 0;
				
				for (int filterY = 0; filterY < filter.length; filterY++)
				{
					for (int filterX = 0; filterX < filter[filterY].length; filterX++)
					{
						int posX = x + (filterX - pivotX);
						int posY = y + (filterY - pivotY);
						if (posX >= 0 && posX < image.getWidth() &&
							posY >= 0 && posY < image.getHeight())
						{
							value += image.getPixel(posX, posY) * filter[filterX][filterY];
							div++;
						}
					}
				}
				
				newBitmap[x][y] = value / div;
			}
		}

		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				image.setPixel(x, y, newBitmap[x][y]);
			}
		}
	}

}
