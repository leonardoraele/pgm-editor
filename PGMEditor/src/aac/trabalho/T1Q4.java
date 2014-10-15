package aac.trabalho;

import java.io.FileNotFoundException;

import aac.BinarityComparison;
import aac.PGM;

public class T1Q4 {
	
	private static String BASE_DIRECTORY = "../tests";
	private static String RESULT_DIRECTORY = "result";
	private static String EXPECTED_DIRECTORY = "GT";
	
	public static void main(String[] args) throws FileNotFoundException
	{
		compare("H01.pgm");
		compare("H02.pgm");
		compare("H03.pgm");
		compare("H04.pgm");
		compare("H05.pgm");
		compare("H06.pgm");
		compare("H07.pgm");
		compare("H08.pgm");
		compare("H09.pgm");
		compare("H10.pgm");
		compare("H11.pgm");
		compare("H12.pgm");
		compare("H13.pgm");
		compare("H14.pgm");
		compare("H15.pgm");
		compare("HW1.pgm");
		compare("HW2.pgm");
		compare("HW3.pgm");
		compare("HW4.pgm");
		compare("HW5.pgm");
		compare("HW6.pgm");
		compare("HW7.pgm");
		compare("HW8.pgm");
		compare("P01.pgm");
		compare("P02.pgm");
		compare("P03.pgm");
		compare("P04.pgm");
		compare("P05.pgm");
	}

	private static void compare(String filename) throws FileNotFoundException {
//		System.out.println("Loading " + filename + "...");
		PGM obtained = new PGM(BASE_DIRECTORY + "/" + RESULT_DIRECTORY + "/" + filename);
		
//		System.out.println("Loading expected counterpart...");
		PGM expected = new PGM(BASE_DIRECTORY + "/" + EXPECTED_DIRECTORY + "/" + filename);
		
		try {
//			System.out.println("Comparing...");
			BinarityComparison comparison = PGM.compare(obtained, expected);
			
			System.out.print(filename + "\t");
			System.out.print(comparison.getAccuracy() + "\t");
			System.out.print(comparison.getPrecision() + "\t");
			System.out.print(comparison.getRecall() + "\t");
			System.out.print(comparison.getSpecificity() + "\t");
			System.out.print(comparison.getFMeasure() + "\t");
			System.out.print(comparison.getNegativeRateMetric() + "\t");
			System.out.println();
		} catch (RuntimeException e) {
			System.err.println(e.toString());
		}
	}

}
