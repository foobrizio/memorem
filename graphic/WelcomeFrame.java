package graphic;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

@SuppressWarnings("serial")
public class WelcomeFrame extends JInternalFrame {
	
	private Image sfondo;
	private Mouse mouse;
	private JLabel nuovaSessione,login;
	private LoginDialog logD;

	/**
	 * Create the frame.
	 */
	public WelcomeFrame(LoginDialog logD) {
		setResizable(false);
		setClosable(false);
		setBorder(null);
		setOpaque(false);
		this.logD=logD;
		mouse=new Mouse();
		setBackground(new Color(0,0,0,0));
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		nuovaSessione = new JLabel("Sign up");
		getContentPane().add(nuovaSessione, "2, 2");
		nuovaSessione.setOpaque(false);
		nuovaSessione.addMouseListener(mouse);
		
		login = new JLabel("Login");
		login.setOpaque(false);
		login.addMouseListener(mouse);
		getContentPane().add(login, "2, 4");
		//getContentPane().setBackground(new Color(0,0,0));
		BasicInternalFrameUI ifui=(BasicInternalFrameUI)this.getUI();
		BasicInternalFrameTitlePane tp=(BasicInternalFrameTitlePane)ifui.getNorthPane();
		remove(tp);
		try{
			File f=new File("./src/graphic/wallpapers/Welcome.jpg");
			sfondo= ImageIO.read(f);
		}catch(IOException e){
			System.out.println("proprio niente :(");
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		
		Image scaledImage = sfondo.getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
		setOpaque(false);
		g.drawImage(scaledImage, 1, 1, null);
		super.paintComponent(g);
	}
	
	public void setLogD(LoginDialog logD){
		
		this.logD=logD;
	}
	
	private class Mouse extends MouseAdapter{
		
		public void mouseEntered(MouseEvent evt){
			
			((JLabel)evt.getSource()).setForeground(Color.GREEN);
		}
		public void mouseExited(MouseEvent evt){
			
			((JLabel)evt.getSource()).setForeground(Color.BLACK);
		}
		public void mousePressed(MouseEvent evt){
			
			((JLabel)evt.getSource()).setForeground(Color.RED);
		}
		public void mouseReleased(MouseEvent evt){
			
			((JLabel)evt.getSource()).setForeground(Color.GREEN);
		}
		public void mouseClicked(MouseEvent evt){
			
			if(evt.getSource()==login)
				logD.login(true);
			else if(evt.getSource()==nuovaSessione)
				logD.registrati(true);
		}
	}
}
