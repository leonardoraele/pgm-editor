package aac;

public interface BorderTreatmentStrategy {

	public void apply(PGM image, int[][] filter, int pivotX, int pivotY);

}
