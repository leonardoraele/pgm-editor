package aac.impl.grayscale;

import aac.GrayscaleConverterStrategy;

public class AverageStrategy implements GrayscaleConverterStrategy {

	@Override
	public int toGrayscale(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		
		int result = (red + green + blue) / 3;
		
		return result;
	}

}
