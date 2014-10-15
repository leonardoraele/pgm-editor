package aac.gui.impl;

import java.awt.BorderLayout;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import aac.BinarityAlgorithm;
import aac.gui.AlgorithmSelector;
import aac.impl.binarity.BlackPercentageThresholding;

public class BlackPercentageSelector implements AlgorithmSelector<BinarityAlgorithm> {
	
	private DefaultBoundedRangeModel model;
	private JSlider slider;
	private JLabel label;

	public BlackPercentageSelector()
	{
		this.model = new DefaultBoundedRangeModel(50, 0, 0, 100);
		this.label = new JLabel(this.model.getValue() + "%");
		this.slider = new JSlider(this.model);
		this.slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				BlackPercentageSelector.this.stateChanged(event);
			}
		});
	}

	private void stateChanged(ChangeEvent event)
	{
		int value = this.model.getValue();
		this.label.setText(Integer.toString(value) + "%");
	}

	@Override
	public JComponent getComponent()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(this.slider, BorderLayout.WEST);
		panel.add(this.label, BorderLayout.EAST);
		return panel;
	}

	@Override
	public BinarityAlgorithm getAlgorithm()
	{
		double percentage = Integer.valueOf(this.model.getValue()).doubleValue() / 100.0d;
		return new BlackPercentageThresholding(percentage);
	}

	@Override
	public String getName()
	{
		return "Percentual de Preto";
	}

}
