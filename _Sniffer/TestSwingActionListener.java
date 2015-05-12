package hunkann;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


class ColorPanel extends JPanel implements ActionListener{
	
	public void makeButton(String name){
		JButton button = new JButton(name);
		add(button);
		button.addActionListener(this);
	}
	
	public ColorPanel(){
		makeButton("yellow");
		makeButton("red");
		makeButton("blue");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
//		Object source = event.getSource();
		String command = event.getActionCommand();
		if(command.toLowerCase().equals("yellow")){
			setBackground(Color.YELLOW);
		}else if(command.toLowerCase().equals("blue")){
			setBackground(Color.blue);
		}else{
			setBackground(Color.red);
		}
	}
	
}



class ColorFrame extends JFrame{
	public ColorFrame(){
		ColorPanel panel = new ColorPanel();
		add(panel);
		setTitle("Test Action Listner");
		setSize(300,200);
	}
}

public class TestSwingActionListener {
	public static void main(String[] args) {
		ColorFrame frame = new ColorFrame();
		frame.setVisible(true);
	}
}
