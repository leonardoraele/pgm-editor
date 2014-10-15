package aac.trabalho;

import java.io.FileNotFoundException;

import aac.BinarityAlgorithm;
import aac.PGM;
import aac.gui.PGMGUI;
import aac.impl.filter.IgnoreBorderFilter;

public class T2Q1 {
	
	public static void main(String[] args) throws FileNotFoundException {
		// Carrega a imagem
		PGM chessboard = new PGM("chessboard.pgm");
		
		// Monta a máscara do filtro
		int[][] filter = new int[3][3];
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				filter[x][y] = -1;
			}
		}
		filter[1][1] = 8;
		
		// Aplica efeitos
		chessboard.applyFilter(filter, new IgnoreBorderFilter());	// Filtro laplace
		chessboard.applyBinarity(new BinarityAlgorithm()			// Binarização
				{public int calculeThreshold(PGM image){return 6;}});
		chessboard.applyDilatation(1);								// Dilatação
		
		// Exibe o resultado
		new PGMGUI(chessboard);
	}

}
