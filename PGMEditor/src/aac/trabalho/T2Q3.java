package aac.trabalho;

import java.io.FileNotFoundException;

import aac.PGM;
import aac.gui.PGMGUI;
import aac.impl.binarity.BalancedHistogramThresholding;

public class T2Q3 {
	
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		// Carrega a imagem
		PGM HW1 = new PGM("HW1.pgm");
		
		// Seleciona e aplica o algorítmo de binarização
		/*////
		BinarityAlgorithm strategy = selectBinarityAlgorithm();
		HW1.applyBinarity(strategy);
		/*/////
		HW1.applyBrightness(-60);
		HW1.applyHistogramExpansion();
		HW1.applyBinarity(new BalancedHistogramThresholding());
		//*////
		
		// Exibe a imagem
		new PGMGUI(HW1);
	}

}
