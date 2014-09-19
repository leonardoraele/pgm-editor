package aac.trabalho;

import java.io.IOException;

import javax.swing.JOptionPane;

import aac.PGM;
import aac.PGM.SegmentationType;

public class T1Q5 {
	
	public static void main(String[] args) throws IOException {
		// Carregar imagem
		PGM H01 = new PGM("H01.pgm");
		
		// Log
		System.out.println("File H01.pgm loaded.");
		
		// Segmentação da imagem
		PGM[] segments = H01.applySegmentation(12, SegmentationType.HORIZONTAL);
		
		// Log
		System.out.println("Generated " + segments.length + " images.");
		int response = JOptionPane.showConfirmDialog(null, "Salvar " + segments.length + " imagens geradas?", "Pergunta", JOptionPane.YES_NO_OPTION);
		
		if (response == 0) // Sim
		{
			// Salva cada imagem gerada em um arquivo
			for (int i = 0; i < segments.length; i++)
			{
				// Gera o nome do arquivo
				String filename = "H01_" + i + ".pgm";
				
				// Salva a imagem em arquivo
				segments[i].export(filename);
				
				// Log
				System.out.println("File created: " + filename);
			}
		}
	}

}
