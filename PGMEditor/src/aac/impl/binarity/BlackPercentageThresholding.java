package aac.impl.binarity;

import aac.BinarityAlgorithm;
import aac.PGM;

public class BlackPercentageThresholding implements BinarityAlgorithm {
	
	private static final double DEFAULT_PERCENTAGE = 0.2125d;
	private Double percentage;
	
	public BlackPercentageThresholding()
	{
		this(DEFAULT_PERCENTAGE);
	}

	public BlackPercentageThresholding(double percentage)
	{
		this.percentage = Math.max(0.0d, Math.min(1.0d, percentage));
	}
	
	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	@Override
	public int calculeThreshold(PGM image) {
		int[] histogram = image.getHistogram();
		int total = image.getHeight() * image.getWidth();
		int target = (int) (this.percentage * total);
		
		int i = 0;
		for (int sum = 0; sum < target; i++)
		{
			sum += histogram[i];
		}
		
		return i;
	}

}
