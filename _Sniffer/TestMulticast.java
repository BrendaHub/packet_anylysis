package hunkann;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

// 测试多点传送 ： 同一个事件可以发送给多个监听器对象

class MulticastFrame extends JFrame{
	public MulticastFrame(){
		setTitle("Multicasttest");
		setSize(300,200);
		add(new MuticastPanel());
	}
}

class MuticastPanel extends JPanel{
	public MuticastPanel(){
		JButton button = new JButton("New");
		add(button);
		final JButton closeAllButton = new JButton("Close all");
		add(closeAllButton);
		
		ActionListener newListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				BlankFrame frame = new BlankFrame(closeAllButton);
				frame.setVisible(true);
			}
		};
		button.addActionListener(newListener);
	}
}

class BlankFrame extends JFrame{
	private ActionListener closeListener;
	public static int counter = 0;
	public BlankFrame(final JButton closeButton){
		counter++;
		setTitle("Frame " +counter);
		setSize(300,200);
		setLocation(40*counter,40*counter);
		
		closeListener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				closeButton.removeActionListener(closeListener);
				dispose();
			}
		};
		closeButton.addActionListener(closeListener);
	}
}


public class TestMulticast {
	public static void main(String[] args) {
		MulticastFrame frame = new MulticastFrame();
		frame.setVisible(true);
	}
}
