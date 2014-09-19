package aac.trabalho;

import java.io.FileNotFoundException;

import aac.PGM;
import aac.gui.PGMGUI;

public class T1Q7 {
	
	private static int MASK_SIZE = 3;
	
	public static void main(String[] args) throws FileNotFoundException {
		// Carrega a imagem
		PGM image = new PGM("lena.pgm");
		
		// Monta a máscara para erosão
		boolean[][] mask = new boolean[MASK_SIZE][MASK_SIZE];
		for (int y = 0; y < MASK_SIZE; y++) {
			for (int x = 0; x < MASK_SIZE; x++) {
				mask[x][y] = true;
			}
		}
		
		// Aplica efeito de erosão
		image.applyErosion(mask);
		
		// Exibe o resultado
		new PGMGUI(image);
	}

}
