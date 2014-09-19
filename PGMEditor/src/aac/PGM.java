package aac;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import aac.util.Pair;

public class PGM {
	
	public enum SegmentationType {
		HORIZONTAL,
		VERTICAL
	}
	
	private Pair<Integer, Integer> scale; /** Valores do menor e do maior pixel da imagem, respectivamente. */
	private int width;
	private int height;
	private int pixelLimit; /** Limite máximo de valor de cada pixel da imagem. O limite mínimo é 0(zero). */
	private int[] histogram; /** Representação do histograma da imagem. (Contagem do número de pixels de cada tom de cinza) */
	private int[][] bitmap; /** Mapa com o valor de cada pixel.*/
	
	public PGM(String filename) throws FileNotFoundException
	{
		this(new FileInputStream(filename));
	}

	public PGM(InputStream input)
	{
		setup(input);
	}
	
	public PGM(int[][] image, int pixelLimit)
	{
		// Define as dimensões da imagem
		this.pixelLimit = pixelLimit;
		this.width = image.length;
		if (image.length > 0)
		{
			this.height = image[0].length;
		}
		else
		{
			this.height = 0; 
		}
		
		// Inicializa o array do histograma
		this.histogram = new int[this.pixelLimit + 1];
		this.histogram[0] = this.width * this.height; // Inicialmente todos os pixels são 0.
		
		// Inicializa a escala do histograma para ser setada corretamente
		// durante a leitura da imagem.
		this.scale = new Pair<Integer, Integer>(this.pixelLimit, 0);
		
		// Cria o mapa de pixels
		this.bitmap = new int[this.width][this.height];
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.setPixel(x, y, image[x][y]);
			}
		}
	}
	
	public PGM(PGM image)
	{
		this.scale = image.getScale();
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.pixelLimit = image.getPixelLimit();
		this.histogram = image.getHistogram();
		this.bitmap = new int[this.width][this.height];
		
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.bitmap[x][y] = image.getPixel(x, y);
			}
		}
	}

	private void setup(InputStream input)
	{
		Scanner scanner = new Scanner(input);
		
		// Ignores first line's magic number P2
		ignoreCommentsIfAny(scanner);
		scanner.nextLine();
		
		// Read dimensions
		ignoreCommentsIfAny(scanner); this.width = scanner.nextInt();
		ignoreCommentsIfAny(scanner); this.height = scanner.nextInt();
		this.bitmap = new int[this.width][this.height];
		
		// Read pixel limit
		ignoreCommentsIfAny(scanner);
		this.pixelLimit = scanner.nextInt();
		this.histogram = new int[this.pixelLimit + 1];
		this.histogram[0] = this.width * this.height; // Inicialmente todos os pixels são 0.
		
		// Inicializa a escala do histograma para ser setada corretamente
		// durante a leitura da imagem.
		this.scale = new Pair<Integer, Integer>(this.pixelLimit, 0);
		
		// Read pixel map
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				ignoreCommentsIfAny(scanner);
				this.setPixel(x, y, scanner.nextInt());
			}
		}
		
		scanner.close();
	}

	/**
	 * Verifica se a linha atual é um comentário. Se for, avança com o scanner
	 * até a próxima linha que não for comentário. Caso contrário, não faz
	 * nenhuma modificação.
	 */
	private void ignoreCommentsIfAny(Scanner scanner) {
		while (scanner.findInLine("#") != null) // Verifica se a linha possui #
		{
			scanner.nextLine(); // Pula para a próxima linha
		}
	}
	
	public Pair<Integer, Integer> getScale() {
		return new Pair<Integer, Integer>(this.scale.getFirst(), this.scale.getSecond());
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getPixelLimit() {
		return pixelLimit;
	}
	
	public int getPixel(int x, int y) {
		return this.bitmap[x][y];
	}
	
	public int getPixelCount(int value) {
		return this.histogram[value];
	}
	
	public int[] getHistogram()
	{
		return Arrays.copyOf(this.histogram, this.histogram.length);
	}
	
	/**
	 * Escreve esta imagem no formato PGM a um fluxo de saída especificado.
	 */
	public void export(OutputStream stream)
	{
		PrintStream output = new PrintStream(stream);
		
		// Magic number P2
		output.println("P2");
		
		// Width and height
		output.println("" + this.width + " " + this.height);
		
		// Max gray byte
		output.println(this.pixelLimit);
		
		// Pixels
		for (int y = 0; y < this.height; y++)
		{
			output.print(this.bitmap[0][y]);
			for (int x = 1; x < this.width; x++)
			{
				output.print(" " + this.bitmap[x][y]);
			}
			output.println();
		}
	}
	
	/**
	 * Escreve esta imagem no formato PGM em uma string que pode ser lida,
	 * armazenada em arquivo, etc.
	 */
	public String export()
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		this.export(stream);
		byte[] binary = stream.toByteArray();
		String result = new String(binary);
		return result;
	}
	
	/**
	 * Salva esta imagem no sistema de arquivos, no diretório/arquivo
	 * especificado.
	 * @param filename Nome do arquivo e diretório para salvar.
	 * @throws IOException 
	 */
	public void export(String filename) throws IOException
	{
		this.export(new FileOutputStream(filename));
	}
	
	/**
	 * Aplica brilho baseaddo no parâmetro value.
	 * 		Valores cima de zero aumentam o brilho.
	 * 		Valores abaixo de zero diminuem o brilho.
	 */
	public void applyBrightness(int value)
	{
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.setPixel(x, y, this.getPixel(x, y) + value);
			}
		}
	}

	/**
	 * Aplica contraste baseado no parâmetro value.
	 * 		Valores acima de 1 aumentam o contraste.
	 * 		Valores abaixo de 1 diminuem o contraste.
	 * @param value 
	 */
	public void applyContrast(double value)
	{
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.setPixel(x, y, (int) Math.round(this.bitmap[x][y] * value));
			}
		}
	}
	
	/**
	 * Aplica efeito de negativo, invertendo as cores da imagem.
	 */
	public void applyNegative()
	{
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.setPixel(x, y, this.pixelLimit - this.bitmap[x][y]);
			}
		}
	}
	
	/**
	 * Aplica o efeito de mudança de escala de forma a preencher completamente o
	 * histograma.
	 * Equivalente a: this.applyExpansion(new Pair<Integer, Integer>(0, this.getPixelLimit()));
	 */
	public void applyHistogramExpansion()
	{
		this.applyHistogramExpansion(new Pair<Integer, Integer>(0, this.pixelLimit));
	}

	/**
	 * Aplica o efeito de mudança de escala do histograma para uma nova escala.
	 */
	public void applyHistogramExpansion(int min, int max)
	{
		this.applyHistogramExpansion(new Pair<Integer, Integer>(min, max));
	}
	
	/**
	 * Aplica o efeito de mudança de escala do histograma para uma nova escala.
	 */
	public void applyHistogramExpansion(Pair<Integer, Integer> newScale)
	{
		int[][] newBitmap = new int[this.width][this.height];
		
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				int delta_f = this.scale.getSecond() - this.scale.getFirst();
				double delta_g = newScale.getSecond() - newScale.getFirst();
				int f_min = this.scale.getFirst();
				int g_min = newScale.getFirst();
				int f = this.bitmap[x][y];
				double g = (delta_g / delta_f) * (f - f_min) + g_min;
				
				newBitmap[x][y] = (int) g;
			}
		}
		

		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.setPixel(x, y, newBitmap[x][y]);
			}
		}
	}
	
	/**
	 * Aplica efeito de equalização à imagem.
	 */
	public void applyHistogramEqualization()
	{
		int[] pixelmap = createPixelmap(this.histogram);
		this.applyPixelmap(pixelmap);
	}
	
	/**
	 * Aplica o efeito de especificação de histograma utilizando o histograma a
	 * imagem em parâmetro.
	 * A chamada deste método é equivalente a:
	 * this.applySpecification(image.getHistogram());
	 * Porém, este método é otimizado e recomendado em relação trexo acima.
	 */
	public void applyHistogramSpecification(PGM image)
	{
		this.applyHistogramSpecification(image.histogram);
	}
	
	/**
	 * Aplica efeito de especificação da imagem para tentar se aproximar do
	 * histograma especificado.
	 * @param histogram especificação
	 */
	public void applyHistogramSpecification(int[] histogram)
	{
		this.applyHistogramEqualization();
		
		int[] pixelmap = createPixelmap(histogram);
		
		this.applyPixelmap(pixelmap);
	}

	private void applyPixelmap(int[] pixelmap)
	{
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.setPixel(x, y, pixelmap[this.bitmap[x][y]]);
			}
		}
	}
	
	private int[] createPixelmap(int[] histogram)
	{
		int npixels = this.width * this.height;
		double[] normalmap = new double[this.histogram.length];
		double[] normalsum = new double[this.histogram.length];
		int[] pixelmap = new int[this.histogram.length];
		
		// Gera histograma com a porcentagem de cada tom
		for (int i = 0; i < normalmap.length; i++)
		{
			normalmap[i] = ((double) this.histogram[i]) / npixels;
		}
		
		// Gera histograma do sumário das porcentagens
		normalsum[0] = normalmap[0];
		for (int i = 1; i < normalsum.length; i++)
		{
			normalsum[i] = normalmap[i] + normalsum[i - 1];
		}
		
		// Gera mapa da transformação da imagem
		for (int i = 0; i < pixelmap.length; i++)
		{
			pixelmap[i] = (int) Math.round(normalsum[i] * this.pixelLimit);
		}
		
		return pixelmap;
	}
	
	/**
	 * Aplica efeito de filtro a esta imagem. Chama internamente o método
	 * applyFilter(int[][], int, int, FilterAlgorithm) utilizando como pivot o
	 * pixel no centro do filtro.
	 * @param filter Filtro que será aplicado a esta imagem.
	 * @param strategy Implementação do algoritmo que será utilizado para
	 * calcular o valor de cada pixel quando o filtro for aplicado. Este
	 * algoritmo inclui o tratamento de bordas que será usado.
	 */
	public void applyFilter(int[][] filter, BorderTreatmentStrategy strategy)
	{
		if (filter.length > 0 && filter[0].length > 0)
		{
			int pivotX = filter[0].length / 2;
			int pivotY = filter.length / 2;
			
			this.applyFilter(filter, pivotX, pivotY, strategy);
		}
	}
	
	/**
	 * Aplica efeito de filtro a esta imagem.
	 * @param filter Filtro que será aplicado a esta imagem.
	 * @param pivotX Posição X do pixel pivot do filtro.
	 * @param pivotY Posição Y do pixel pivot do filtro.
	 * @param strategy Implementação do algoritmo que será utilizado para
	 * calcular o valor de cada pixel quando o filtro for aplicado. Este
	 * algoritmo inclui o tratamento de bordas que será usado.
	 */
	public void applyFilter(int[][] filter, int pivotX, int pivotY, BorderTreatmentStrategy strategy)
	{
		if (	pivotX < 0 || pivotY >= filter.length ||
				pivotY < 0 || pivotX >= filter[0].length)
		{
			throw new IllegalArgumentException("Pivot coordinates (" + pivotX + ", " + pivotY + ") is out of filter bounds.");
		}
		
		strategy.apply(this, filter, pivotX, pivotY);
	}
	
	/**
	 * Aplica efeito de binarização sob a imagem.
	 * O parâmetro strategy define o algoritmo que é utilizado para decidir o
	 * limiar para seeração dos tons que se tornarão preto ou branco.
	 */
	public void applyBinarity(BinarityAlgorithm strategy)
	{
		// Calcula o tom limiar que será usado para dividir as cores preto e branco
		int threshold = strategy.calculeThreshold(this);
		
		// "constantes locais" para facilitar a leitura e entendimento do código
		final int BLACK = 0;
		final int WHITE = this.pixelLimit;
		
		// Aplica a binarização do código, dividindo cada pixel em preto ou
		// branco com base do limiar calculado anteriormente 
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				int color = this.getPixel(x, y) < threshold ? BLACK : WHITE;
				this.setPixel(x, y, color);
			}
		}
	}
	
	/**
	 * Aplica efeito de dilatação, para ampliar a silhueta da imagem.
	 * Melhor utilizado em imagens binárias.
	 * Este método é equivalente a achar applyDilatation utilizando como máscara
	 * uma matriz completamente preenchida de tamanho (increment * 2 + 1)
	 * @param increment Valor em que a silhueta será incrementada.
	 */
	public void applyDilatation(int increment)
	{
		int length = increment * 2 + 1;
		boolean[][] mask = new boolean[length][length];
		
		for (int y = 0; y < length; y++)
		{
			for (int x = 0; x < length; x++)
			{
				mask[x][y] = true;
			}
		}
		
		this.applyDilatation(mask);
	}
	
	/**
	 * Aplica efeito de dilatação utilizando uma determinada máscara.
	 */
	public void applyDilatation(boolean[][] mask)
	{
		this.applyMorphology(mask, new MorphologyMethod() {
			public int compare(int a, int b) {
				return Math.max(a, b);
			}
		});
	}
	
	/**
	 * Aplica efeito de erosão, para diminuir a silhueta da imagem.
	 * Melhor utilizado em imagens binárias.
	 * Este método é equivalente a achar applyErosion utilizando como máscara
	 * uma matriz completamente preenchida de tamanho (increment * 2 + 1)
	 * @param decreasement Valor em que a silhueta será decrementada.
	 */
	public void applyErosion(int decreasement)
	{
		int length = decreasement * 2 + 1;
		boolean[][] mask = new boolean[length][length];
		
		for (int y = 0; y < length; y++)
		{
			for (int x = 0; x < length; x++)
			{
				mask[x][y] = true;
			}
		}
		
		this.applyErosion(mask);
	}
	
	/**
	 * Aplica efeito de erosão utilizando uma máscara especificada.
	 */
	public void applyErosion(boolean[][] mask)
	{
		this.applyMorphology(mask, new MorphologyMethod() {
			public int compare(int a, int b) {
				return Math.min(a, b);
			}
		});
	}
	
	/**
	 * Aplica efeito de Abertura.
	 * O efeito de abertura consiste em aplicar o efeito de erosão seguido do
	 * efeito de dilatação utilizando a mesma máscara.
	 */
	public void applyOpenning(int value)
	{
		this.applyErosion(value);
		this.applyDilatation(value);
	}

	/**
	 * Aplica efeito de Fechamento.
	 * O efeito de fechamento consiste em aplicar o efeito de dilatação seguido
	 * do efeito de erosão utilizando a mesma máscara.
	 */
	public void applyClosing(int value)
	{
		this.applyDilatation(value);
		this.applyErosion(value);
	}
	
	public PGM[] applySegmentation(SegmentationType segmentationType)
	{
		return this.applySegmentation(0, segmentationType);
	}
	
	public PGM[] applySegmentation(double tolerance, SegmentationType segmentationType)
	{
		int intTolerance = (int) tolerance * this.pixelLimit;
		return this.applySegmentation(intTolerance, segmentationType);
	}
	
	public PGM[] applySegmentation(int tolerance, SegmentationType segmentationType)
	{
		PGM[] result;
		
		switch (segmentationType)
		{
		case HORIZONTAL:
			result = this.applySegmentationHorizontal(tolerance);
			break;
		case VERTICAL:
			result = this.applySegmentationVertical(tolerance);
			break;
		default:
			result = null;
		}
		
		return result;
	}
	
	private PGM[] applySegmentationHorizontal(int tolerance) {
		LinkedList<PGM> result = new LinkedList<PGM>();
		int init = 0;
		
		for (int y = 0; y < this.getHeight(); y++)
		{
			int total = 0;
			
			for (int x = 0; x < this.getWidth(); x++)
			{
				total += this.pixelLimit - this.getPixel(x, y);
			}
			
			int average = total / this.getWidth();
			
			if (average <= tolerance)
			{
				if (y > init)
				{
					PGM subimage = this.getSubimage(0, init, this.getWidth(), y);
					result.add(subimage);
				}
				init = y + 1;
			}
		}
		
		return result.toArray(new PGM[result.size()]);
	}
	
	private PGM[] applySegmentationVertical(int tolerance) {
		LinkedList<PGM> result = new LinkedList<PGM>();
		int init = 0;
		
		for (int x = 0; x < this.getWidth(); x++)
		{
			int total = 0;
			
			for (int y = 0; y < this.getHeight(); y++)
			{
				total += this.pixelLimit - this.getPixel(x, y);
			}
			
			int average = total / this.getWidth();
			
			if (average <= tolerance)
			{
				if (x > init)
				{
					PGM subimage = this.getSubimage(init, 0, x, this.getHeight());
					result.add(subimage);
				}
				init = x + 1;
			}
		}
		
		return result.toArray(new PGM[result.size()]);
	}
	
	/**
	 * Substrai desta imagem uma outra imagem.
	 * Levanta exceção se as duas imagens não possuírem o mesmo tamanho.
	 * (cumprimento e largura)
	 */
	public void applySubtraction(PGM other)
	// TODO Não deveria levantar exceção
	// TODO Deveriam haver parâmetros para especificar o pivot da imagem,
	// permitindo aplicar o efeito de substração a partir de um determinado
	// ponto da imagem original. Isto é útil principalmente caso as imagens não
	// possuam o mesmo tamanho.
	{
		for (int y = 0; y < this.getHeight(); y++)
		{
			for (int x = 0; x < this.getWidth(); x++)
			{
				int thisPixel = this.getPixel(x, y);
				int otherPixel = other.getPixel(x, y);
				this.setPixel(x, y, thisPixel - otherPixel);
			}
		}
	}
	
	/**
	 * Gera uma nova imagem a partir de uma parte retangular desta imagem.
	 */
	public PGM getSubimage(int initX, int initY, int finalX, int finalY) {
		initX = Math.max(0, initX);
		initY = Math.max(0, initY);
		finalX = Math.min(this.getWidth(), finalX);
		finalY = Math.min(this.getHeight(), finalY);
		
		if (finalX <= initX || finalY <= initY)
		{
			return null;
		}
		
		int subimageWidth = finalX - initX;
		int subimageHeight = finalY - initY;
		int[][] subimage = new int[subimageWidth][subimageHeight];
		
		for (int y = 0; y < subimageHeight; y++)
		{
			for (int x = 0; x < subimageWidth; x++)
			{
				subimage[x][y] = this.getPixel(x + initX, y + initY);
			}
		}
		
		PGM result = new PGM(subimage, this.pixelLimit);
		return result;
	}

	private interface MorphologyMethod {	
		public int compare(int a, int b);
	}
	
	// Método auxiliar utilizado pelos métodos applyDilatation e applyErosion.
	private void applyMorphology(boolean[][] mask, MorphologyMethod segmentation)
	{
		int[][] newBitmap = new int[this.width][this.height];
		
		for (int bitmapY = 0; bitmapY < this.height; bitmapY++)
		{
			for (int bitmapX = 0; bitmapX < this.width; bitmapX++)
			{
				int value = this.bitmap[bitmapX][bitmapY];
				
				for (int maskY = 0; maskY < mask.length; maskY++)
				{
					for (int maskX = 0; maskX < mask[maskY].length; maskX++)
					{
						if (mask[maskX][maskY])
						{
							int bitmapMaskX = bitmapX + (maskX - mask[maskY].length / 2);
							int bitmapMaskY = bitmapY + (maskY - mask.length / 2);
							if (bitmapMaskX >= 0 && bitmapMaskX < this.width && bitmapMaskY >= 0 && bitmapMaskY < this.height)
							{
								value = segmentation.compare(value, this.bitmap[bitmapMaskX][bitmapMaskY]);
							}
						}
					}
				}
				
				newBitmap[bitmapX][bitmapY] = value;
			}
		}

		
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				this.setPixel(x, y, newBitmap[x][y]);
			}
		}
	}
	
	/**
	 * Move a imagem x pixels para a direita e y pixels para cima.
	 * Valores negativos de x e y irão mover a imagem para esquerda e para baixo
	 * espectivamente.
	 * A imagem é movida looping. Ou seja, os trexos que excederem a imagem
	 * serão jogados para a extremidade oposta da imagem.
	 */
	public void moveImage(int x, int y)
	{
		int[][] newBitmap = new int[this.width][this.height];
		for (int currY = 0; currY < this.height; currY++)
		{
			for (int currX = 0; currX < this.width; currX++)
			{
				int newX = (this.width + currX - x) % this.width;
				int newY = (this.height + currY - y) % this.height;
				int newColor = this.getPixel(newX, newY);
				newBitmap[currX][currY] = newColor;
			}
		}

		for (int currY = 0; currY < this.height; currY++)
		{
			for (int currX = 0; currX < this.width; currX++)
			{
				this.setPixel(currX, currY, newBitmap[currX][currY]);
			}
		}
	}
	
	/**
	 * Move a imagem x pixels para a direita e y pixels para cima.
	 * Valores negativos de x e y irão mover a imagem para esquerda e para baixo
	 * espectivamente.
	 * Os trexos que foram movidos receberão um determidado tom de cor para
	 * substituir.
	 */
	public void moveImage(int x, int y, int color)
	{
		this.moveImage(x, y);

		for (int currY = 0; currY < this.height; currY++)
		{
			for (int currX = 0; currX < x; currX++)
			{
				this.setPixel(currX, currY, color);
			}
			for (int currX = this.width + x; currX < this.width; currX++)
			{
				this.setPixel(currX, currY, color);
			}
		}

		for (int currX = 0; currX < this.width; currX++)
		{
			for (int currY = 0; currY < y; currY++)
			{
				this.setPixel(currX, currY, color);
			}
			for (int currY = this.height + y; currY < this.height; currY++)
			{
				this.setPixel(currX, currY, color);
			}
		}
	}

	/**
	 * Como muitas operações são necessárias para alterar o histograma, o ideal
	 * é que este método seja chamado para fazer alterações no histograma.
	 */
	public void setPixel(int x, int y, int newValue) {
		int oldValue = this.bitmap[x][y];
		newValue = truncate(newValue);
		
		// Muda o pixel no mapa
		this.bitmap[x][y] = newValue;
		
		// Ajusta o histograma
		this.histogram[oldValue]--;
		this.histogram[newValue]++;
		
		// Ajusta a escala atual do histograma (menor valor)
		if (newValue < this.scale.getFirst()) 
		{
			this.scale.setFirst(newValue);
		}
		else if (oldValue == this.scale.getFirst() && this.histogram[oldValue] == 0)
		{
			int i = oldValue;
			while (this.histogram[i] == 0) i++; // Procura pelo novo pixel com menor tom
			this.scale.setFirst(i);
		}

		// Ajusta a escala atual do histograma (maior valor)
		if (newValue > this.scale.getSecond())
		{
			this.scale.setSecond(newValue);
		}
		else if (oldValue == this.scale.getSecond() && this.histogram[oldValue] == 0)
		{
			int i = oldValue;
			while (this.histogram[i] == 0) i--; // Procura pelo novo pixel com maior tom
			this.scale.setSecond(i);
		}
	}
	
	/**
	 * Impede que o valor de um pixel saia do limite permitido pela imagem.
	 */
	private int truncate(int pixel) {
		return Math.max(Math.min(pixel, this.pixelLimit), 0);
	}

}
