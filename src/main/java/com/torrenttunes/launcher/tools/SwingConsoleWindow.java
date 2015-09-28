package com.torrenttunes.launcher.tools;

import java.awt.BorderLayout;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class SwingConsoleWindow extends JPanel {

	public JTextArea textArea = new JTextArea(15, 60);
	
	private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(
			textArea, "");

	public SwingConsoleWindow() {
		setLayout(new BorderLayout());
		add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		System.setOut(new PrintStream(taOutputStream));
	}

	private static JFrame createAndShowGui() {
		JFrame frame = new JFrame("Launching TorrentTunes...");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String imgPath = "cd_icon_black_512.png";
		ImageIcon img = new ImageIcon(SwingConsoleWindow.class.getClassLoader().getResource(
				imgPath));
		frame.setIconImage(img.getImage());
		frame.getContentPane().add(new SwingConsoleWindow());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		return frame;
		
	}

	
	public static JFrame start() {
		return createAndShowGui();
	}
	
	


}