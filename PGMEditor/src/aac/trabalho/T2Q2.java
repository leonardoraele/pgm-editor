package aac.trabalho;

import java.awt.image.BufferedImage;
import java.util.Scanner;

import aac.GrayscaleConverterStrategy;
import aac.PGM;
import aac.gui.PGMGUI;
import aac.impl.grayscale.LightnessStrategy;
import aac.util.BufferedImageToPGM;
import aac.util.Pair;
import aac.util.VideoSource;

public class T2Q2 {
	
	public static void main(String[] args) {
		System.out.println("Loading video...");
		VideoSource video = new VideoSource("file://home/leonardo/workspace/PGMEditor/video1.avi", true);
		GrayscaleConverterStrategy strategy = new LightnessStrategy();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Video loaded.");
		
		while (true)
		{
			System.out.println("Frame: ");
			int frame = scanner.nextInt();
			
			if (frame == -1)
			{
				break;
			}
			
			BufferedImage image = video.getFrame(frame);
			PGM pgm = BufferedImageToPGM.create(image, strategy);
			new PGMGUI(pgm);
		}
		
		/*
		PGM a = BufferedImageToPGM.create(video.getFrame(0), strategy);
		PGM b;
		for (int i = 1; i < video.getFrameCount(); i++)
		{
			b = BufferedImageToPGM.create(video.getFrame(0), strategy);
		}
		 */
		
		scanner.close();
	}
	
	public Pair<Integer, Integer>[] labelization(PGM image) {
		int[][] label_map = new int[image.getWidth()][image.getHeight()];
		//int[] label_tree = new int[image.getWidth() * image.getHeight() / 3];
		int size = 0;
		
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int pixel = image.getPixel(x, y);
				if (pixel > 0 && label_map[x][y] == 0)
				{
					int label = label_map[x - 1][y - 1];
					if (label == 0) label = label_map[x][y - 1];
					if (label == 0) label = label_map[x + 1][y - 1];
					if (label == 0) label = label_map[x - 1][y];
					if (label == 0) label = ++size;
					
					label_map[x][y] = label;
				}
			}
		}
		
		return null;
	}

}
