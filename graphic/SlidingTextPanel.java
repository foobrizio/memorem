package graphic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class SlidingTextPanel extends JPanel implements Runnable{

	private String[] helps;
	private String cur;
	private TrappolaPerTopi jerry;
	private Thread t ;
	
	
	private class TrappolaPerTopi extends MouseAdapter{
		
		@Override
		public void mouseClicked(MouseEvent evt){
			
			int casual=Math.round((int)(Math.random()*helps.length));
			cur=helps[casual];
			System.out.println("helps.length:"+helps.length+"\ncasual:"+casual);
		}
	}
	/**
	 * Create the panel.
	 */
	public SlidingTextPanel() {
		
		super(true);
		this.helps=null;
		cur=" Benvenuto uto uto uto uto uto uto";
		setMaximumSize(new Dimension(20000,32));
		setMinimumSize(new Dimension(2,32));
		
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
		
		jerry=new TrappolaPerTopi();
		addMouseListener(jerry);
	}

	/** 
	 * Fa partire il thread
	 */
	public void start(){
		
		t = new Thread(this);
		t.start();
	}

	/**
	 * Gestisce il comportamento del Thread durante l'esecuzione
	 */
	public void run() {
		char ch;
		for(;;){
			try{
				repaint();
				Thread.sleep(150);
				ch = cur.charAt(0);
				cur = cur.substring(1, cur.length());
				cur = cur + ch;
			}catch(InterruptedException e){
				System.out.println("ops...si è interrotto");
			}
		}
	}

	/**
	 * Gestisce i cambiamenti nella grafica prodotti dal Thread
	 */
	public void paintComponent(Graphics g){
		
		Graphics2D g2=(Graphics2D)g;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.0f));
		g2.drawRect(1,1,500,32);
		
		//g2.fillRect(1,1,500,32);
		g2.setColor(Color.BLACK);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		g2.drawString(cur, 1, 20);
	}
	
	/**
	 * Setta l'array di stringhe che verranno visualizzate
	 * @param helps
	 */
	public void setString(String[] helps){
		
		this.helps=helps;
		cur=helps[Math.round((int)(Math.random()*helps.length))];
		
	}
	
	public static void main(String[] args){
		
		String[] helps={"prova1","prova2","prova3","prova4","prova5"};
		JFrame f=new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setBackground(Color.red);
		SlidingTextPanel stp=new SlidingTextPanel();
		stp.setString(helps);
		f.getContentPane().add(stp);
		f.setVisible(true);
		stp.setVisible(true);
		stp.start();
	}

}
