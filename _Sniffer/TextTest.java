package hunkann;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class ClockPanel extends JPanel{
	private double minutes = 0;
	private int RADIUS = 100;
	private double MINUTE_HAND_LENGTH  = 0.8*RADIUS;
	private double HOUR_HAND_LENGTH = 0.6*RADIUS;
	public ClockPanel(){
		setPreferredSize(new Dimension(2*RADIUS+1,2*RADIUS+1));
	}
	// draw hand 
	public void drawHand(Graphics2D g2,double angle,double handLength){
		Point2D end = new Point2D.Double(
				RADIUS+handLength*Math.cos(angle),
				RADIUS+handLength*Math.sin(angle));
		Point2D center = new Point2D.Double(RADIUS, RADIUS);
		g2.draw(new Line2D.Double(center,end));
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		// Õ÷‘≤
		Ellipse2D circle = new Ellipse2D.Double(0, 0, 2*RADIUS, 2*RADIUS);
		g2.draw(circle);
		// draw the hour hand
		double hourAngle = Math.toRadians(90-360*minutes/(12*60));
		drawHand(g2, hourAngle, HOUR_HAND_LENGTH);
		// draw the minutes hand
		double minuteAngle = Math.toRadians(90-360*minutes/60);
		drawHand(g2, minuteAngle, MINUTE_HAND_LENGTH);
	}
	public void setTime(int h,int m){
		minutes = h*60 + m;
		repaint();
	}
}

class TestTextFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField hourField;
	private JTextField minuteField;
	private ClockPanel clock;
	public void setClock(){
		try{
			int hours = Integer.parseInt(hourField.getText().trim());
			int minutes = Integer.parseInt(minuteField.getText().trim());
			clock.setTime(hours,minutes);
		}catch(NumberFormatException e){}
	}
	private class ClockFieldListener implements DocumentListener{
		public void changedUpdate(DocumentEvent arg0) {}
		public void insertUpdate(DocumentEvent arg0) {
			setClock();
		}
		public void removeUpdate(DocumentEvent arg0) {
			setClock();
		}
	}
	
	// constructor 
	public TestTextFrame(){
		setTitle("text test");
		DocumentListener listener = new ClockFieldListener();
		JPanel panel = new JPanel();
		panel.add(new JLabel("Hours:"));
		hourField = new JTextField("12",3);
		panel.add(hourField);
		hourField.getDocument().addDocumentListener(listener);
		panel.add(new JLabel("Minutes:"));
		minuteField = new JTextField("00",3);
		panel.add(minuteField);
		minuteField.getDocument().addDocumentListener(listener);
		add(panel,BorderLayout.SOUTH);
		// add the clock
		clock = new ClockPanel();
		add(clock,BorderLayout.CENTER);
		pack();
	}
}

public class TextTest {
	public static void main(String[] args) {
		TestTextFrame frame = new TestTextFrame();
		frame.setSize(300,300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
