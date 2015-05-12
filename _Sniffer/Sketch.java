package hunkann;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.sql.Time;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Sketch {
	public static void main(String[] args){
		SketchFrame frame = new SketchFrame();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}

class SketchFrame extends JFrame{
	public SketchFrame(){
		setTitle("Sketch");
		setSize(300,200);
		SketchPanel panel = new SketchPanel();
		add(panel);
	}
}

class SketchPanel extends JPanel{
	private Point2D last;
	private ArrayList<Line2D> lines;
	private static final int SMALL_INC = 1;
	private static final int LARGE_INC = 5;
	
	public void add(int dx,int dy){
		// compute new end point
		Point2D end = new Point2D.Double(last.getX()+dx,last.getY()+dy);
		// add line segment
		Line2D line = new Line2D.Double(last, end);
		lines.add(line);
		
		// 调用这个方法会让系统自动调用paintComponent方法
		repaint();
		// remeber new end point
		last = end;
	}
	
	private class KeyHandler implements KeyListener{
		@Override
		public void keyPressed(KeyEvent event) {
			int keyCode = event.getKeyCode();
			int d;
			// set distance;
			if(event.isShiftDown()){
				d = LARGE_INC;
			}else{
				d = SMALL_INC;
			}
			// add line segment
			if(keyCode == KeyEvent.VK_LEFT){
				add(-d,0);
			}else if(keyCode == KeyEvent.VK_RIGHT){
				add(d,0);
			}else if(keyCode == KeyEvent.VK_DOWN){
				add(0,d);
			}else if(keyCode == KeyEvent.VK_UP){
				add(0,-d);
			}
		}
		@Override
		public void keyReleased(KeyEvent arg0) {}
		@Override
		public void keyTyped(KeyEvent event) {
			char keyChar = event.getKeyChar();
			// setDistance 
			int d;
			if(Character.isUpperCase(keyChar)){
				d = LARGE_INC;;
				keyChar = Character.toLowerCase(keyChar);
			}else{
				d = SMALL_INC;
			}
			// add line segement
			if(keyChar=='h'){
				add(-d,0);
			}else if(keyChar=='l'){
				add(d,0);
			}else if(keyChar=='k'){
				add(0,-d);
			}else if(keyChar =='j'){
				add(0,d);
			}
		}
	}
	//  为了能在面板上绘图需要：
	// 1 . 定义一个集成了JPanel 的类
	// 2. 重写paintComponent()方法
	// 只要窗口需要重新绘图，时间处理器就会通告组件，从而引发所有组件的paintComponent()方法
	// 一定不要自己调用paintComponent方法,在应用程序需要重绘的时候，自动 会调用 这个方法
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		int r,gg,b;
		r = (int) ((Math.random()*123455)%255);
		gg = (int) ((Math.random()*123455)%255);
		b = (int) ((Math.random()*123455)%255);
		g2.setColor(new Color(r,gg,b));
		// draw Lines
		for(Line2D l:lines){
			g2.draw(l);
		}
	}
	
	public SketchPanel(){
		last = new Point2D.Double(100,100);
		lines = new ArrayList<Line2D>();
		KeyHandler listener = new KeyHandler();
		addKeyListener(listener);
		setFocusable(true);
	}
}
