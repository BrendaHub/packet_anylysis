package hunkann;

import java.awt.ActiveEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

class SimpleFrame extends JFrame{
	public SimpleFrame(){
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 200;
}

class CenteredFrame extends JFrame{
	public CenteredFrame(){
		// get screen dimensions
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		
		// center frame int screen 
		this.setSize(screenWidth/2,screenHeight/2 );
		this.setLocation(screenWidth/4,screenHeight/4);
		
		/// set frame icon and title
//		Image img = kit.getImage("icon.gif");
//		this.setIconImage(img);
		setTitle("centered Frame");
		
	}
}

class NotHelloWorldPanel extends JPanel{
	public NotHelloWorldPanel(){
		this.setBackground(Color.cyan);
	}
	public void paintComponent(Graphics g){
		g.setColor(new Color(0,128,128));
		super.paintComponent(g);
		g.drawString("Not a hello world program", MESSAGE_X, MESSAGE_Y);
	}
	public static final int MESSAGE_X = 75;
	public static final int MESSAGE_Y = 100;
}
class NotHelloWorldFrame extends JFrame{
	public NotHelloWorldFrame(){
		setTitle("not a hello world frame");
		setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		
		// add panel to frame;
		NotHelloWorldPanel panel  = new NotHelloWorldPanel();
		add(panel);
	}
	
	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 200;
}

// 事件相应测试

class ButtonPanel extends Panel{
	
	private class ColorAction implements ActionListener{
		
		public ColorAction(Color c){
			backgroundColor = c;
		}
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			setBackground(backgroundColor);
			
		}
		private Color backgroundColor;
	}
	private void makeButton(String colorName,final Color c){
		// create Buttons 
		JButton button = new JButton(colorName);
		add(button);
		// create button actions 
		ColorAction action = new ColorAction(c);
		/// associate actions with buttons
//		button.addActionListener(action);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				setBackground(c);
			}
			
		});
	}
	public ButtonPanel(){
		makeButton("yellow",Color.YELLOW);
		makeButton("blue",Color.BLUE);
		makeButton("red",Color.RED);
//		JButton yellowButton = new JButton("Yellow");
//		JButton redButton = new JButton("red");
//		JButton blueButton = new JButton("blue");
//		add(yellowButton);
//		add(blueButton);
//		add(redButton);
//		ColorAction yellowAction = new ColorAction(Color.YELLOW);
//		ColorAction redAction = new ColorAction(Color.RED);
//		ColorAction blueAction = new ColorAction(Color.BLUE);
//		
//		
//		
//		yellowButton.addActionListener(yellowAction);
//		blueButton.addActionListener(blueAction);
//		redButton.addActionListener(redAction);
	}
}

class ButtonFrame extends JFrame{
	public ButtonFrame(){
		setTitle("Button Action Test");
		setSize(WIDTH,HEIGHT);
		ButtonPanel bpanel = new ButtonPanel();
		add(bpanel);
	}
	public static final int WIDTH = 300;
	public static final int HEIGHT = 200;
}

public class TestSwing {
	public static void main(String[] args) {
		
//		SimpleFrame frame  = new SimpleFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(false);
//		
		
//		// Centered Frame 
//		CenteredFrame cf = new CenteredFrame();
//		cf.setVisible(true);
		
//		NotHelloWorldFrame frame = new NotHelloWorldFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setVisible(true);
		
		// button frame 
		ButtonFrame frame = new ButtonFrame();
		frame.setVisible(true);
		
	}
}
