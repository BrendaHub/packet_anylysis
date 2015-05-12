package hunkann;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

class TextFrame extends JFrame {
	class TextPanel extends JPanel {
		public TextPanel() {
			
			add(tf);
			
			add(tf2);
			JButton button = new JButton("CopyText");
			add(button);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					// TODO Auto-generated method stub
					Object obj = event.getSource();
					JButton bt = (JButton) obj;
					tf2.setText(tf.getText());
				}
			});
		}

		public JTextField tf = new JTextField("Hello", 20);
		public JTextField tf2 = new JTextField(20);
	}

	public TextFrame() {
		setSize(300, 200);
		add(pn);
	}
	public final TextPanel pn = new TextPanel();
}

public class TestTextField {
	public static void main(String[] args) {
		final TextFrame frame = new TextFrame();
		JPanel pnel = new JPanel();
		JButton button = new JButton("clear text");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				frame.pn.tf2.setText("");
			}
		});
		pnel.add(button);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
