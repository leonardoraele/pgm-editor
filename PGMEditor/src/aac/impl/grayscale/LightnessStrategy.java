package aac.impl.grayscale;

import aac.GrayscaleConverterStrategy;

public class LightnessStrategy implements GrayscaleConverterStrategy {

	@Override
	public int toGrayscale(int rgb) {
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;

		int max = Math.max(Math.max(red, green), blue);
		int min = Math.min(Math.max(red, green), blue);
		
		int result = (max + min) / 2;
		
		return result;
	}

}
