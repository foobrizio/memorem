package graphic;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

@SuppressWarnings("serial")
public class ColoredPanel extends JPanel {

	private BufferedImage sfondo;
	private final static String directory="files//wallpapers//";
	
	public ColoredPanel(){
		
		this(directory+"wall5.jpg");
	}
	
	public ColoredPanel(String pathImmagine){
		
		super(true);
		if(pathImmagine==null)
			return;
		File f=new File(pathImmagine);
		try{
			setImage(ImageIO.read(f));
		}catch(IOException ioe){
			System.out.println("l'immagine non è stata trovata");
		}
	}
	
	public void setImage(BufferedImage bim){
		
		this.sfondo=bim;
	}
	
	public void setSfondo(String path){
		
		if(path==null){
			sfondo=null;
			return;
		}
		File f=new File(path);
		try{
			setImage(ImageIO.read(f));
		}catch(IOException oie){
			System.out.println("L'immagine non è stata trovata");
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		
		setOpaque(false);
		g.drawImage(sfondo, 1, 1, null);
		super.paintComponent(g);
		
	}
	
	public static void main(String[] args){
		
		JFrame f=new JFrame();
		f.setSize(new Dimension(200,200));
		f.setContentPane(new ColoredPanel());
		f.setVisible(true);
	}
}
