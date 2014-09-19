package aac.impl.binarity;

import aac.BinarityAlgorithm;
import aac.PGM;

public class BlackPercentageThresholding implements BinarityAlgorithm {

	public int calculeThreshold(PGM image)
	{
		int[] histogram = image.getHistogram();
		int total = 0;
		double target = (image.getWidth() * image.getHeight()) / 2;
		target += target % 2;
		int i;
		for (i = 0; total < target && i < histogram.length; i++)
		{
			total += histogram[i];
		}
		return i;
	}
	
}
