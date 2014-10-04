package aac.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import aac.BinarityAlgorithm;
import aac.util.SwingUtil;

public class AlgorithmSelectorDialog {
	
	public static BinarityAlgorithm selectBinarityAlgorithm()
	{
		AlgorithmSelectorDialog dialog = new AlgorithmSelectorDialog(null, "Seleção de algoritmo");
		AlgorithmSelector<BinarityAlgorithm> selectedItem = dialog.getSelectedItem();
		return selectedItem != null ? selectedItem.getAlgorithm() : null;
	}
	
	private static List<AlgorithmSelector<BinarityAlgorithm>> getAlgorithms()
	{
		List<AlgorithmSelector<BinarityAlgorithm>> algorithms = new LinkedList<>();
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream("binarityAlgorithms.conf"));
			
			while (scanner.hasNext())
			{
				String className = scanner.nextLine();
				try {
					Class<?> readClass = Class.forName(className);
					if (AlgorithmSelector.class.isAssignableFrom(readClass))
					{
						try {
							Class<? extends AlgorithmSelector<BinarityAlgorithm>> selectorClass =
									(Class<? extends AlgorithmSelector<BinarityAlgorithm>>) readClass;
							try {
								AlgorithmSelector<BinarityAlgorithm> selector =
										selectorClass.newInstance();
								algorithms.add(selector);
							} catch (Exception e) {
								System.err.println("Error: " + e.getMessage());
							}
						} catch (Exception e) {
							System.err.println("Error: " + e.getMessage());
						}
					} else {
						System.err.println("Error: " + readClass + " is not an AlgorithmSelector.");
					}
				} catch (ClassNotFoundException e) {
					System.err.println("Error: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} finally {
			if (scanner != null)
			{
				scanner.close();
			}
		}
		
		return algorithms;
	}

	private JDialog dialog;
	private DefaultComboBoxModel<AlgorithmSelector<BinarityAlgorithm>> model;
	private JPanel centerPanel;
	
	public AlgorithmSelectorDialog(Window owner, String title)
	{
		List<AlgorithmSelector<BinarityAlgorithm>> algorithms = getAlgorithms();
		
		this.centerPanel = new JPanel();
		
		this.model = new DefaultComboBoxModel<AlgorithmSelector<BinarityAlgorithm>>
				(algorithms.toArray(new AlgorithmSelector[algorithms.size()]));
		
		JComboBox<AlgorithmSelector<BinarityAlgorithm>> comboBox =
				new JComboBox<AlgorithmSelector<BinarityAlgorithm>>(this.model);
		
		comboBox.setRenderer(new ListCellRenderer<AlgorithmSelector<BinarityAlgorithm>>() {
			@Override
			public Component getListCellRendererComponent(
					JList<? extends AlgorithmSelector<BinarityAlgorithm>> list,
					AlgorithmSelector<BinarityAlgorithm> value,
					int index,
					boolean isSelected,
					boolean cellHasFocus) {
				return new JLabel(value != null ? value.getName() : "");
			}
		});

		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				AlgorithmSelectorDialog.this.refreshSelector();
			}
		});
		
		JButton buttonOk = new JButton("Ok");
		buttonOk.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0)
					{
						AlgorithmSelectorDialog.this.dialog.dispose();
					}
				}
			);

		this.dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
		this.dialog.setLayout(new BorderLayout());
		this.dialog.add(comboBox, BorderLayout.NORTH);
		this.dialog.add(this.centerPanel, BorderLayout.CENTER);
		this.dialog.add(buttonOk, BorderLayout.SOUTH);
		this.refreshSelector();
		this.dialog.pack();
		SwingUtil.centralizeWindow(this.dialog);
		this.dialog.addWindowListener(new SwingUtil.WindowCloser());
		this.dialog.setVisible(true);
	}
	
	private void refreshSelector() {
		this.centerPanel.removeAll();
		
		AlgorithmSelector<BinarityAlgorithm> selectedItem = this.getSelectedItem();
		
		if (selectedItem != null)
		{
			this.centerPanel.add(selectedItem.getComponent());
			this.dialog.pack();
		}
	}

	public AlgorithmSelector<BinarityAlgorithm> getSelectedItem()
	{
		return ((AlgorithmSelector<BinarityAlgorithm>) this.model.getSelectedItem());
	}

}
