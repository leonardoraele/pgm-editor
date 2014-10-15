package aac.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import aac.util.SwingUtil;

public class FilterDialog  {
	
	public static int[][] getFilter(int size) {
		return getFilter(size, size);
	}
	
	public static int[][] getFilter(int width, int height) {
		return getFilter("", width, height);
	}
	
	public static int[][] getFilter(String title, int size) {
		return getFilter(title, size, size);
	}
	
	public static int[][] getFilter(String title, int width, int height) {
		return getFilter(null, title, width, height);
	}
	
	public static int[][] getFilter(Window owner, String title, int size) {
		return getFilter(null, title, size, size);
	}
	
	public static int[][] getFilter(Window owner, String title, int width, int height) {
		FilterDialog editor = new FilterDialog(owner, title, width, height);
		return editor.getFinalFilter();
	}

	private JDialog dialog;
	private JPanel filterPanel;
	private int[][] finalFilter;
	
	private FilterDialog(Window owner, String title, int width, int height)
	{
		this.finalFilter = null;
		
		this.filterPanel = new JPanel();
		this.filterPanel.setLayout(new GridLayout(height, width));
		for (int i = 0; i < width * height; i++)
		{
			this.filterPanel.add(new JTextField());
		}
		
		JButton buttonOk = new JButton("Ok");
		buttonOk.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						FilterDialog.this.fillFilter();
						if (FilterDialog.this.getFinalFilter() != null)
						{
							FilterDialog.this.dispose();
						}
					}
				}
			);

		this.dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		this.dialog.setLayout(new BorderLayout());
		this.dialog.add(new JLabel("Preencha o filtro: "), BorderLayout.NORTH);
		this.dialog.add(this.filterPanel, BorderLayout.CENTER);
		this.dialog.add(buttonOk, BorderLayout.SOUTH);
		this.dialog.setSize(width * 32, height * 32 + 24 + 32);
		SwingUtil.centralizeWindow(this.dialog);
		this.dialog.addWindowListener(new SwingUtil.WindowCloser());
		this.dialog.setVisible(true);
	}

	public synchronized int[][] getFinalFilter() {
		return finalFilter;
	}

	public synchronized void setFinalFilter(int[][] finalFilter) {
		this.finalFilter = finalFilter;
	}
	
	public synchronized void fillFilter()
	{
		int size = (int) Math.sqrt((double) this.filterPanel.getComponentCount());
		int[][] result = new int[size][size];
		int i = 0;
		
		for (Component component : filterPanel.getComponents())
		{
			JTextField field = (JTextField) component;
			int value = Integer.valueOf(field.getText());
			result[i % size][i / size] = value;
			i++;
		}
		
		this.setFinalFilter(result);
	}
	
	public void dispose()
	{
		this.dialog.dispose();
	}

}
