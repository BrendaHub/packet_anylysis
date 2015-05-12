package hunkann;

import java.awt.Button;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

class Terminator extends WindowAdapter{
	public void windowClosing(WindowEvent e){
		System.exit(0);
	}
}

class ClosingFrame extends JFrame{
	public ClosingFrame(){
		
	}
}

public class TestWindowAdapter {
	public static void main(String[] args) {
		ClosingFrame frame = new ClosingFrame();
		frame.setSize(300,200);
//		frame.addWindowListener(new Terminator());
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}
}
