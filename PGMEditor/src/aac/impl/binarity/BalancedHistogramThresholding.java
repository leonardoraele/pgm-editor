package aac.impl.binarity;

import aac.BinarityAlgorithm;
import aac.PGM;

public class BalancedHistogramThresholding implements BinarityAlgorithm {

	public int calculeThreshold(PGM image)
	{
		int[] histogram = image.getHistogram();
		int min = 0;
		int max = histogram.length - 1;
		
		while (min < max)
		{
			int leftFrom = min;
			int leftTo = (int) Math.floor((max - min) / 2) - 1;
			int leftSum = sum(histogram, leftFrom, leftTo);
			
			int rightFrom = (int) Math.ceil((max - min) / 2 + 1);
			int rightTo = max;
			int rightSum = sum(histogram, rightFrom, rightTo);
			
			if (leftSum > rightSum)
			{
				min++;
			}
			else
			{
				max--;
			}
		}
		
		return min;
	}

	private int sum(int[] histogram, int from, int to)
	{
		int result = 0;
		
		for (int i = from; i <= to; i++)
		{
			result += histogram[i];
		}
		
		return result;
	}
	
}
