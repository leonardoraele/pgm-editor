package aac.gui;

import javax.swing.JComponent;

import aac.PGM;

public interface AlgorithmSelector<T> {
	
	public default void initialize(PGM image) {};
	public JComponent getComponent();
	public T getAlgorithm();
	public String getName();

}
