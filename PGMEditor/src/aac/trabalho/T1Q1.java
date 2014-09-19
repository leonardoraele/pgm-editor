package aac.trabalho;

import java.io.FileNotFoundException;

import aac.PGM;
import aac.gui.PGMGUI;

public class T1Q1 {
	
	public static void main(String[] args) throws FileNotFoundException {
		// Lê a imagem
		PGM image = new PGM("lena.pgm");
		
		// Aplica efeitos
		image.applyContrast(1.2);					// Contraste
		image.applyBrightness(30);					// Brilho
		image.applyNegative();						// Negativação
		image.applyHistogramExpansion(64, 192);		// Expansão
		image.applyHistogramEqualization();			// Equalização
		
		// Cria uma GUI para exibir a imagem
		new PGMGUI(image);
	}
	
}
