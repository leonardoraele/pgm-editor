package aac.impl.grayscale;

import aac.GrayscaleConverterStrategy;

public class LuminosityStrategy implements GrayscaleConverterStrategy {

	@Override
	public int toGrayscale(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;
		
		double result = red * 0.21d + green * 0.72d + blue * 0.07d;
		
		return (int) result;
	}

}
