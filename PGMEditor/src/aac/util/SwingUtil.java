package aac.util;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class SwingUtil {
	public static class LoadingWindow extends JFrame {
		private static final long serialVersionUID = 1L;

		public LoadingWindow(String title) {
			this(title, "Loading...");
		}
		
		public LoadingWindow(String title, String body) {
			super(title);
			this.add(new JLabel(body));
			this.setSize(200, 100);
			SwingUtil.centralizeWindow(this);
			this.setVisible(true);
			this.addWindowListener(new WindowCloser());
		}

	}
	
	public static class WindowCloser implements WindowListener {

		public void windowActivated(WindowEvent arg0) {}
		public void windowClosed(WindowEvent arg0) {}
		
		public void windowClosing(WindowEvent arg0)
		{
			arg0.getWindow().dispose();
		}
		
		public void windowDeactivated(WindowEvent arg0) {}
		public void windowDeiconified(WindowEvent arg0) {}
		public void windowIconified(WindowEvent arg0) {}
		public void windowOpened(WindowEvent arg0) {}

	}
	
	public static void centralizeWindow(Window window)
	{
		int windowWidth = window.getWidth();
		int windowHeight = window.getHeight();
		int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		window.setBounds(
				screenWidth / 2 - windowWidth / 2,
				screenHeight / 2 - windowHeight / 2,
				windowWidth,
				windowHeight
			);
	}
}
