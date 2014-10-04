package aac.impl.binarity;

import aac.BinarityAlgorithm;
import aac.PGM;
import aac.util.Pair;

public class CenterOfHistogramThresholding implements BinarityAlgorithm {
	
	private static final double DEFAULT_PERCENTAGE = 0.5d;
	private double percentage;

	public CenterOfHistogramThresholding() {
		this(DEFAULT_PERCENTAGE);
	}
	
	public CenterOfHistogramThresholding(double percentage) {
		this.percentage = percentage;
	}

	@Override
	public int calculeThreshold(PGM image)
	{
		Pair<Integer, Integer> scale = image.getScale();
		
		int min = scale.getFirst();
		int max = scale.getSecond();
		
		max -= 10;
		
		double result = min + (max - min) * percentage;
		
		return (int) result;
	}
	

}
