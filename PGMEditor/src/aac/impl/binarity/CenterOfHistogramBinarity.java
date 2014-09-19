package aac.impl.binarity;

import aac.BinarityAlgorithm;
import aac.PGM;
import aac.util.Pair;

public class CenterOfHistogramBinarity implements BinarityAlgorithm {
	
	@Override
	public int calculeThreshold(PGM image)
	{
		Pair<Integer, Integer> scale = image.getScale();
		
		int min = scale.getFirst();
		int max = scale.getSecond();
		
		int result = min + (max - min) / 2;
		
		return result;
	}
	

}
