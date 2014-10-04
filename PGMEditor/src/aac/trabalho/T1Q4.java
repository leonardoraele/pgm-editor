package aac.trabalho;

import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import aac.BinarityAlgorithm;
import aac.PGM;
import aac.gui.PGMGUI;
import aac.impl.binarity.BalancedHistogramThresholding;
import aac.impl.binarity.CenterOfHistogramThresholding;

public class T1Q4 {
	
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		// Carrega a imagem
		PGM HW1 = new PGM("HW1.pgm");
		
		// Seleciona e aplica o algorítmo de binarização
		BinarityAlgorithm strategy = selectBinarityAlgorithm();
		HW1.applyBinarity(strategy);
		
		// Exibe a imagem
		new PGMGUI(HW1);
	}
	
	private static BinarityAlgorithm selectBinarityAlgorithm() throws InstantiationException, IllegalAccessException {
		Object strategy = JOptionPane.showInputDialog(
				null,
				"Seleção de Estratégia",
				"Algorítmo de binarização",
				JOptionPane.QUESTION_MESSAGE,
				null,
				new Object[] {
						// Classes que apareceção para escolha:
						BalancedHistogramThresholding.class,
						CenterOfHistogramThresholding.class
				},
				null
			);
		
		@SuppressWarnings("unchecked")
		Class<? extends BinarityAlgorithm> treatment =
				(Class<? extends BinarityAlgorithm>) strategy;
		
		return treatment.newInstance();
	}

}
