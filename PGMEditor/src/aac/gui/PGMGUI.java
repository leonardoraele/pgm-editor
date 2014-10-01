package aac.gui;

import java.awt.BorderLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import aac.BinarityAlgorithm;
import aac.BorderTreatmentStrategy;
import aac.PGM;
import aac.util.Pair;
import aac.util.SwingUtil;

public class PGMGUI {
	
	public static void main(String[] args) throws IOException {
		SwingUtil.LoadingWindow loading = new SwingUtil.LoadingWindow(TITLE); // Displays a "loading" window.
		
		// Load image and open GUI.
		try {
			PGMGUI gui;
			if (args.length > 0)
			{
				PGM image = new PGM(args[0]);
				gui = new PGMGUI(image);
			}
			else
			{
				gui = new PGMGUI();
			}
			SwingUtil.centralizeWindow(gui.getFrame());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
			main(new String[0]);
		} finally {
			loading.dispose();
		}
	}
	
	private static final String TITLE = "Aboshop";
	private static final String LABEL_ORIGINAL_IMAGE = "Imagem original";
	private static final String LABEL_GENERATED_IMAGE = "Nova imagem";
	
	private JFrame frame;
	private JPanel originalContainer;
	private JPanel generatedContainer;
	private JPanel bothContainer;
	private JMenu fileMenu;
	private JMenu effectMenu;
	private JMenuBar menuBar;
	private JMenu editMenu;
	private PGMDrawer originalImageDrawer;
	private PGMDrawer generatedImageDrawer;
	private LinkedList<PGM> history;
	private LinkedList<PGM> undoneChanges;
	private JPanel generatedHistogramContainer;
	private HistogramDrawer histogramDrawer;
	
	public PGMGUI(PGM image)
	{
		this(image, true);
	}
	
	public PGMGUI(PGM image, boolean initiallyVisible)
	{
		this(false);
		this.load(image);
		this.frame.setVisible(initiallyVisible);
	}
	
	public PGMGUI()
	{
		this(true);
	}
	
	public PGMGUI(boolean initiallyVisible)
	{
		// Setup inner attributes
		this.history = new LinkedList<PGM>();
		this.undoneChanges = new LinkedList<PGM>();
		
		// Setup "File" menu
		this.fileMenu = new JMenu("Arquivo");
		this.fileMenu.add(new MenuItemBindToMethod(this, "commandLoad", "Carregar nova imagem..."));;
		this.fileMenu.add(new MenuItemBindToMethod(this, "commandSave", "Salvar imagem gerada como..."));
		this.fileMenu.add(new MenuItemBindToMethod(this, "commandReset", "Restaurar imagem original"));
		this.fileMenu.add(new MenuItemBindToMethod(this, "commandExit", "Sair"));
		
		// Setup "Edit" menu
		this.editMenu = new JMenu("Editar");
		this.editMenu.add(new MenuItemBindToMethod(this, "commandUndo", "Desfazer"));
		this.editMenu.add(new MenuItemBindToMethod(this, "commandRedo", "Refazer"));
		
		// Setup "Apply Effect" menu
		this.effectMenu = new JMenu("Aplicar Efeito");
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyBrightness", "Brilho..."));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyContrast", "Contraste..."));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyNegative", "Negativar"));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyExpansionFixed", "Expansão do Histograma (completar histograma)"));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyExpansionScale", "Expansão do Histograma... (especificar escala)"));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyEqualization", "Equalização do Histograma"));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplySpecification", "Especificação do Histograma..."));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyFilter", "Filtro..."));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyBinarization", "Binarização..."));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyDilatation", "Dilatação..."));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyErosion", "Erosão..."));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyOpenning", "Abertura..."));
		this.effectMenu.add(new MenuItemBindToMethod(this, "commandApplyClosing", "Fechamento..."));
		
		// Setup Menu bar
		this.menuBar = new JMenuBar();
		this.menuBar.add(this.fileMenu);
		this.menuBar.add(this.editMenu);
		this.menuBar.add(this.effectMenu);
		
		// Setup original image's container
		this.originalContainer = new JPanel();
		this.originalContainer.setLayout(new BorderLayout());
		
		// Setup new image's container
		this.generatedContainer = new JPanel();
		this.generatedContainer.setLayout(new BorderLayout());
		
		// Setup new image histogram's container
		this.generatedHistogramContainer = new JPanel();
		
		// Setup a container with both images
		this.bothContainer = new JPanel();
		this.bothContainer.setLayout(new BoxLayout(this.bothContainer, BoxLayout.X_AXIS));
		this.bothContainer.add(this.originalContainer);
		this.bothContainer.add(this.generatedContainer);
		
		// Setup frame
		this.frame = new JFrame(TITLE);
		this.frame.setLayout(new BorderLayout());
		this.frame.addWindowListener(new SwingUtil.WindowCloser());
		this.frame.setSize(320, 240);
		this.frame.add(this.bothContainer, BorderLayout.CENTER);
		this.frame.setJMenuBar(this.menuBar);
		this.frame.setVisible(initiallyVisible);
	}
	
	public void load(PGM image)
	{
		this.generatedImageDrawer = new PGMDrawer(image);
		this.generatedContainer.removeAll();
		this.generatedContainer.add(this.generatedImageDrawer, BorderLayout.CENTER);
		this.generatedContainer.add(new JLabel(LABEL_GENERATED_IMAGE), BorderLayout.SOUTH);
		
		this.originalImageDrawer = new PGMDrawer(new PGM(image));
		this.originalContainer.removeAll();
		this.originalContainer.add(this.histogramDrawer = new HistogramDrawer(this.generatedImageDrawer.getImage()));
		this.originalContainer.add(new JLabel("Histograma"), BorderLayout.SOUTH);
		
		this.history.clear();
		this.undoneChanges.clear();
		this.frame.setSize(image.getWidth() * 2 + 4, image.getHeight() + 24);
		this.frame.invalidate();
		SwingUtil.centralizeWindow(this.frame);
	}
	
	public JFrame getFrame()
	{
		return this.frame;
	}
	
	public void commandNotSupported()
	{
		JOptionPane.showMessageDialog(null, "This feature is not supported by PGMGUI yet.", "Information", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void registerHistory()
	{
		PGM generatedImage = PGMGUI.this.generatedImageDrawer.getImage();
		this.history.push(new PGM(generatedImage));
		this.undoneChanges.clear();
	}
	
	/////////////////////////// MENUBAR:FILE ///////////////////////////////////
	
	public void commandLoad()
	{
		String input = JOptionPane.showInputDialog("Arquivo");
		
		if (input != null)
		{
			try {
				this.load(new PGM(input));
				
				this.frame.setTitle(TITLE + " - " + input);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void commandSave()
	{
		if (this.generatedImageDrawer != null && this.originalImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Arquivo para salvar");
			
			if (input != null)
			{
				try {
					PGM generatedImage = this.generatedImageDrawer.getImage();
					generatedImage.export(input);
					
					this.originalImageDrawer.setImage(generatedImage);
					this.generatedImageDrawer.setImage(new PGM(generatedImage));
					
					this.frame.setTitle(TITLE + " - " + input);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	public void commandReset()
	{
		if (this.histogramDrawer != null && this.generatedImageDrawer != null)
		{
			PGM originalImage = this.originalImageDrawer.getImage();
			PGM newGeneratedImage = new PGM(originalImage);
			
			this.generatedImageDrawer.setImage(newGeneratedImage);
			this.histogramDrawer.setImage(newGeneratedImage);
		}
	}
	
	public void commandExit()
	{
		this.frame.dispose();
	}
	
	///////////////////////// MENUBAR:EDIT /////////////////////////////////////
	
	public void commandUndo()
	{
		if (this.generatedImageDrawer != null)
		{
			try {
				PGM lastChange = this.history.pop();
				PGM currentImage = this.generatedImageDrawer.getImage();

				this.generatedImageDrawer.setImage(new PGM(lastChange));
				this.undoneChanges.push(currentImage);
			} catch (NoSuchElementException e) {}
		}
	}
	
	public void commandRedo()
	{
		if (this.generatedImageDrawer != null)
		{
			try {
				PGM lastUndoneChange = this.undoneChanges.pop();
				PGM currentImage = this.generatedImageDrawer.getImage();
				
				this.generatedImageDrawer.setImage(new PGM(lastUndoneChange));
				this.history.push(currentImage);
			} catch (NoSuchElementException e) {}
		}
	}
	
	////////////////////// MENUBAR:APPLYEFFECT /////////////////////////////////
	
	public void commandApplyBrightness()
	{
		if (this.generatedImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Quantidade de brilho (-250, 250)");
			int value = Integer.valueOf(input);
			
			this.generatedImageDrawer.getImage().applyBrightness(value);
			this.frame.repaint();
			
			this.registerHistory();
		}
	}
	
	public void commandApplyContrast()
	{
		if (this.generatedImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Valor de contraste (0, 2)");
			double value = Double.valueOf(input);
			
			this.generatedImageDrawer.getImage().applyContrast(value);
			this.frame.repaint();
		}
	}
	
	public void commandApplyNegative()
	{
		if (this.generatedImageDrawer != null)
		{
			this.generatedImageDrawer.getImage().applyNegative();
			this.frame.repaint();
		}
	}
	
	public void commandApplyExpansionFixed()
	{
		if (this.generatedImageDrawer != null)
		{
			this.generatedImageDrawer.getImage().applyHistogramExpansion();
			this.frame.repaint();
		}
	}
	
	public void commandApplyExpansionScale()
	{
		if (this.generatedImageDrawer != null)
		{
			int pixelLimit = this.generatedImageDrawer.getImage().getPixelLimit();
			
			int from = Integer.valueOf(JOptionPane.showInputDialog("Valor do menor pixel (de)", 0));
			int to = Integer.valueOf(JOptionPane.showInputDialog("Valor do maior pixel (até)", pixelLimit));
			
			Pair<Integer, Integer> newScale = new Pair<Integer, Integer>(from, to);
			
			this.generatedImageDrawer.getImage().applyHistogramExpansion(newScale);
			this.frame.repaint();
		}
	}
	
	public void commandApplyEqualization()
	{
		if (this.generatedImageDrawer != null)
		{
			PGM image = this.generatedImageDrawer.getImage();
			image.applyHistogramEqualization();
			this.frame.repaint();
		}
	}
	
	public void commandApplyBinarization()
	{
		if (this.generatedImageDrawer != null)
		{
			Object strategy = JOptionPane.showInputDialog(null, "Algorítmo de binarização", "Binarização", JOptionPane.QUESTION_MESSAGE, null, getClassList("binarityAlgorithms.conf"), null);
			
			if (strategy != null)
			{
				try {
					@SuppressWarnings("unchecked")
					BinarityAlgorithm algorithm = ((Class<? extends BinarityAlgorithm>) strategy).newInstance();
					
					this.generatedImageDrawer.getImage().applyBinarity(algorithm);
					this.frame.repaint();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		}
	}
	
	private Class<?>[] getClassList(String filename)
	{
		LinkedList<Class<?>> classes = new LinkedList<Class<?>>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(filename));
			
			while (scanner.hasNext())
			{
				String className = scanner.nextLine();
				try {
					Class<?> clazz = Class.forName(className);
					classes.add(clazz);
				} catch (ClassNotFoundException e) {
					System.err.println(e);
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
		} finally {
			if (scanner != null)
			{
				scanner.close();
			}
		}
		
		return classes.toArray(new Class<?>[classes.size()]);
	}

	public void commandApplyDilatation()
	{
		if (this.generatedImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Quantidade de dilatação (>= 1)");
			int value = Integer.valueOf(input);
			
			this.generatedImageDrawer.getImage().applyDilatation(value);
			this.frame.repaint();
		}
	}
	
	public void commandApplyErosion()
	{
		if (this.generatedImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Quantidade de erosão (>= 1)");
			int value = Integer.valueOf(input);
			
			this.generatedImageDrawer.getImage().applyErosion(value);
			this.frame.repaint();
		}
	}
	
	public void commandApplyOpenning()
	{
		if (this.generatedImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Quantidade de abertura (>= 1)");
			int value = Integer.valueOf(input);
			
			this.generatedImageDrawer.getImage().applyOpenning(value);
			this.frame.repaint();
		}
	}
	
	public void commandApplyClosing()
	{
		if (this.generatedImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Quantidade de fechamento (>= 1)");
			int value = Integer.valueOf(input);
			
			this.generatedImageDrawer.getImage().applyClosing(value);
			this.frame.repaint();
		}
	}
	
	public void commandApplySpecification()
	{
		if (this.generatedImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Imagem do histograma especificado.");
			
			if (input != null)
			{
				try {
					int[] histogram = new PGM(input).getHistogram();
					this.generatedImageDrawer.getImage().applyHistogramSpecification(histogram);
					this.frame.repaint();
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Arquivo não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	public void commandApplyFilter()
	{
		if (this.generatedImageDrawer != null)
		{
			String input = JOptionPane.showInputDialog("Tamanho do filtro?");
			Integer filterSize = Integer.valueOf(input);
			
			int[][] filter = FilterEditor.getFilter(this.frame, "Filtro", filterSize);
			
			if (filter != null)
			{
				Object strategy = JOptionPane.showInputDialog(null, "Algorítmo de tratamento de bordas", "Tratamento de bordas", JOptionPane.QUESTION_MESSAGE, null, getClassList("filterAlgorithms.conf"), null);
				
				if (strategy != null)
				{
					try {
						@SuppressWarnings("unchecked")
						BorderTreatmentStrategy algorithm = ((Class<? extends BorderTreatmentStrategy>) strategy).newInstance();
						
						this.generatedImageDrawer.getImage().applyFilter(filter, algorithm);
						this.frame.repaint();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, e.getMessage());
					}
				}
			}
		}
	}

}
