package aac.trabalho;

import java.io.FileNotFoundException;

import aac.PGM;
import aac.gui.PGMGUI;

public class T1Q3 {
	
	public static void main(String[] args) throws FileNotFoundException {
		// Carrega a imagem
		PGM ruido = new PGM("ruido.pgm");

		// Aplica efeitos de abertura e dilatação para elimitar os ruídos
		ruido.applyOpenning(1);
		ruido.applyDilatation(1);
		
		// Exibe o resultado
		new PGMGUI(ruido);
	}

}
