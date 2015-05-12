package hunkann;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;



class ButtonPanel2 extends JPanel{
//	public void makeButton(String name,final Color c){
//		Action action = new ColorAction(name,c);
//		JButton button = new JButton(action);
//		button.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent event) {
//				setBackground(c);
//			}
//		});
//		add(button);
//	}
	public ButtonPanel2(){
		Action blueAction = new ColorAction("blue",Color.blue);
		Action yellowAction = new ColorAction("yellow",Color.yellow);
		Action redAction = new ColorAction("red",Color.GREEN);
		add(new JButton(blueAction));
		add(new JButton(yellowAction));
		add(new JButton(redAction));
		
		InputMap imap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		
		imap.put(KeyStroke.getKeyStroke("ctrl Y"), "panel.yellow");
		imap.put(KeyStroke.getKeyStroke("ctrl B"), "panel.blue");
		imap.put(KeyStroke.getKeyStroke("ctrl R"), "panel.red");
		
		ActionMap amap = getActionMap();
		amap.put("panel.red", redAction);
		amap.put("panel.yellow",yellowAction);
		amap.put("panel.blue", blueAction);
		
	}
	private class ColorAction extends AbstractAction{
		public ColorAction(String name,Color c){
			putValue(Action.NAME,name);
			putValue("color",c);
			putValue(Action.SHORT_DESCRIPTION,"set panel color to "+name.toLowerCase());
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			Color c = (Color)getValue("color");
			setBackground(c);
		}
	}
}

public class TestSwingAction extends JFrame{
	public static void main(String[] args) {
		TestSwingAction instance = new TestSwingAction();
		instance.add(new ButtonPanel2());
		instance.setSize(300,200);
		instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		instance.setVisible(true);
	}
}
