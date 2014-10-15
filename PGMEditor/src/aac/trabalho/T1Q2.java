package aac.trabalho;

import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import aac.BorderTreatmentStrategy;
import aac.PGM;
import aac.gui.FilterDialog;
import aac.gui.PGMGUI;
import aac.impl.filter.IgnoreBorderFilter;

public class T1Q2 {
	
	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException {
		// Carrega a imagem
		PGM image = new PGM("lena.pgm");
		
		// Monta o filtro e escolhe a estratégia de tratamento de borda a ser aplicada
		int[][] filter = FilterDialog.getFilter(3);
		BorderTreatmentStrategy strategy = selectBorderTratment();
		
		// Aplica o efeito de filtro
		image.applyFilter(filter, strategy);
		
		// Exibe o resultado do efeito de filtro
		new PGMGUI(image);
	}

	private static BorderTreatmentStrategy selectBorderTratment() throws InstantiationException, IllegalAccessException {
		Object strategy = JOptionPane.showInputDialog(
				null,
				"Algorítmo de tratamento de bordas",
				"Tratamento de bordas",
				JOptionPane.QUESTION_MESSAGE,
				null,
				new Object[] {
						// Classes que apareceção para escolha:
						IgnoreBorderFilter.class
				},
				null
			);
		
		@SuppressWarnings("unchecked")
		Class<? extends BorderTreatmentStrategy> treatment =
				(Class<? extends BorderTreatmentStrategy>) strategy;
		
		return treatment.newInstance();
	}

}
