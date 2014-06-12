package graphic;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*; 					//per la BufferedImage

import main.Memo;

import java.awt.event.*;
import java.io.IOException;

import javax.swing.border.LineBorder;

import util.Data;


public class MemPanel extends JPanel implements ActionListener{
	
	@SuppressWarnings("serial")
	class MyMenuItem extends JMenuItem{
		
		//private Memo m;
		
		public MyMenuItem(String text){
			
			super(text);
			//this.m=m;
		}
		
		public Memo getMemo(){
			
			return MemPanel.this.memo;
		}
		
		public void apochiudira(){
			
			MemPanel.this.setVisible(false);
			MemPanel.this.setEnabled(false);
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Color myYellow=new Color(255,255,150);
	private final Color myBlue=new Color(150,200,255);
	private final Color myRed=new Color(255,120,100);
	
	private Color coloreSfondo;			//il colore dei vari notes
	//private Color coloreClock;
	private JLabel descrizione;
	private JLabel orario;
	private Memo memo;
	private JMenuBar bar;
	private JMenu combo,rinvia;
	private JMenuItem modifica,tipo;
	private MyMenuItem elimina, completa,oneDay,threeDays,oneWeek,personalizza,archivia;
	private boolean tipoB;
	private boolean scadenzanotificata;
	private Popup p;
	private JPanel paneleft;
	private JLabel iconTainer;
	private ImageIcon icon;
	
	public MemPanel(Memo memo,Popup p){
		
		this.memo=memo;
		this.p=p;
		scadenzanotificata=false;
		//coloreClock=Color.BLACK;
		tipoB=false; //se impostato a false il memo è attivo
		bar=new JMenuBar();
		elimina=new MyMenuItem("elimina");
		modifica=new JMenuItem("modifica");
		tipo=new JMenuItem("cambia visualizzazione");
		
		rinvia = new JMenu("rinvia");
		archivia= new MyMenuItem("archivia");
		oneDay = new MyMenuItem("1 giorno");
		threeDays = new MyMenuItem("3 giorni");
		oneWeek = new MyMenuItem("1 settimana");
		personalizza = new MyMenuItem("personalizza");
		
		combo=new JMenu();
		combo.setFont(new Font("Dialog", Font.BOLD, 12));
		combo.setText("Opzioni");
		completa = new MyMenuItem("completa");
		combo.add(completa);
		combo.add(archivia);
		combo.add(elimina);
		combo.add(modifica);
		combo.add(rinvia);
		
		rinvia.add(oneDay);
		rinvia.add(threeDays);
		rinvia.add(oneWeek);
		rinvia.add(personalizza);
		
		combo.add(tipo);
		bar.add(combo);
		//p=new Popup(true);
		completa.addActionListener(this);
		archivia.addActionListener(this);
		elimina.addActionListener(this);
		modifica.addActionListener(this);
		tipo.addActionListener(this);
		oneDay.addActionListener(this);
		threeDays.addActionListener(this);
		oneWeek.addActionListener(this);
		personalizza.addActionListener(this);
		//this.setOpaque(true);
		setLayout(new BorderLayout(0, 0));
		
		paneleft = new JPanel();
		paneleft.setOpaque(false);
		paneleft.setPreferredSize(new Dimension(190,30));
		add(paneleft, BorderLayout.WEST);
		
		String currentDir="";
		try{
			currentDir=new java.io.File(".").getCanonicalPath();
		}catch(IOException eee){
			eee.printStackTrace();
			System.out.println("eee nenta catanzarì");
		}
		icon= new ImageIcon(currentDir+"/src/graphic/icons/stratocaster.png");
		putIcon();
		paneleft.setLayout(new BorderLayout(0, 0));
		//iconTainer.setSize(20,20);
		paneleft.add(iconTainer, BorderLayout.WEST);
		orario=new JLabel(memo.endDate(),SwingConstants.LEFT);
		orario.setBackground(Color.BLACK);
		paneleft.add(orario);
		//orario.setForeground(coloreClock);
		orario.setVerticalAlignment(SwingConstants.CENTER);
		orario.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				
				if(!tipoB){
					tipoB=true;
					orario.setVisible(false);
					orario.setText(MemPanel.this.memo.countDown());
					orario.setVisible(true);
				}
				else{
					tipoB=false;
					orario.setVisible(false);
					orario.setText(MemPanel.this.memo.endDate());
					orario.setVisible(true);
				}
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				if(!tipoB){
					tipoB=true;
					orario.setVisible(false);
					orario.setText(MemPanel.this.memo.countDown());
					orario.setVisible(true);
				}
				else{
					tipoB=false;
					orario.setVisible(false);
					orario.setText(MemPanel.this.memo.endDate());
					orario.setVisible(true);
				}
			}
		});
		descrizione=new JLabel(memo.description(),SwingConstants.CENTER);
		descrizione.setFont(descrizione.getFont().deriveFont(descrizione.getFont().getStyle() | Font.BOLD));
		descrizione.setVerticalAlignment(SwingConstants.CENTER);
		//setVisible(true);
		add(descrizione,BorderLayout.CENTER);
		add(bar,BorderLayout.EAST);
		setColour(memo.priority());
		checkMemo();
		
		//ora assegniamo i colori
		
	}
	public Memo getMemo(){
		
		return memo;
	}
	
	public void setPopup(Popup p){
		
		this.p=p;
	}
	
	public JMenuItem getModifica(){
		
		return modifica;
	}
	
	public MyMenuItem getElimina(){
		
		return elimina;
	}
	
	public MyMenuItem getCompleta(){
		
		return completa;
	}
	public MyMenuItem getArchivia() {
		
		return archivia;
	}
	
	public MyMenuItem getOneDay(){
		
		return oneDay;
	}
	
	public MyMenuItem getThreeDays(){
		
		return threeDays;
	}
	
	public MyMenuItem getOneWeek(){
		
		return oneWeek;
	}
	/**
	 * Segna un MemPanel come completato. Da allora non sarà più possibile modificarlo
	 */
	public void completa(){
		
		rinvia.setVisible(false);
		elimina.setVisible(false);
		modifica.setVisible(false);
		archivia.setVisible(false);
		completa.setVisible(false);
	}
	
	public MyMenuItem personalizza(){
		
		return personalizza;
	}
	
	/**
	 * Inserisce l'icona scelta all'interno del MemPanel. Fornire il metodo di un icon-chooser
	 */
	private void putIcon(){
		
		Image im=icon.getImage();
		BufferedImage bim=new BufferedImage(im.getWidth(null), im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		Graphics g=bim.createGraphics();
		g.drawImage(im, 2, 10, 25, 25, null);
		ImageIcon ii=new ImageIcon(bim);
		iconTainer=new JLabel(ii,SwingConstants.CENTER);
	}
	
	/**
	 * Colora il memPanel in base alla priorità del memo
	 * @param priority
	 */
	private void setColour(int priority){
		
		switch(priority){				//assegnamo un colore ad ogni memo
		case 0: coloreSfondo=myBlue;break;
		case 1: coloreSfondo=myYellow;break;
		case 2: coloreSfondo=myRed; break;
		default:
		}
		bar.setBackground(coloreSfondo);
		tipo.setBackground(coloreSfondo);
		combo.setBackground(coloreSfondo);
		elimina.setBackground(coloreSfondo);
		modifica.setBackground(coloreSfondo);
		completa.setBackground(coloreSfondo);
		archivia.setBackground(coloreSfondo);
		rinvia.setBackground(coloreSfondo);
		rinvia.setOpaque(true);
		oneDay.setBackground(coloreSfondo);
		threeDays.setBackground(coloreSfondo);
		oneWeek.setBackground(coloreSfondo);
		personalizza.setBackground(coloreSfondo);
		setBackground(coloreSfondo);
		setBorder(new LineBorder(coloreSfondo.darker()));			//definisce il bordo del memo
	}
	
	/**
	 * Questo metodo esegue automaticamente delle funzionalità in base allo stato del memo
	 * (se è attivo o scaduto)
	 */
	public boolean checkMemo(){
		
		/*
		 * se il memo è scaduto cambiano i tasti
		 */
		System.out.print("checkMemo dice che..");
		if(this.memo.isScaduto()){
			if(scadenzanotificata==false){
				scadenzanotificata=true;
				System.out.println("memo scadutooo!!!");
				modifica.setVisible(false);
				rinvia.setVisible(true);
				archivia.setVisible(true);
				tipoB=false;
				tipo.doClick();
				return true;
			}
			return false;
		}
		else{
			scadenzanotificata=false;
			System.out.println("memo non scaduto");
			modifica.setVisible(true);
			rinvia.setVisible(false);
			archivia.setVisible(false);
			tipoB=true;
			tipo.doClick();
			return false;
		}
	}
	
	/**
	 * gestisce i vari tasti
	 */
	public void actionPerformed(ActionEvent evt){
		
		if(evt.getSource()==elimina || evt.getSource()==completa || evt.getSource()==archivia){
			
			MemPanel.this.setVisible(false);
			MemPanel.this.setEnabled(false);
			MemPanel.this.completa();
		}//elimina,completa,oneDay,oneWeek,threeDays
		
		else if(evt.getSource()==modifica || evt.getSource()==personalizza){
			
			if(evt.getSource()==personalizza)
				System.out.println("premuto tasto personalizza");
			p.setModified(true);
			Data d=memo.getEnd();
			//p.setOld(memo);
			p.setDesc(memo.description());
			p.setPrior(memo.priority());
			p.setYear(d.anno());
			System.out.println("mese:"+d.mese());
			p.setMonth(d.mese()-1);
			System.out.println("giorno:"+d.giorno());
			p.setDay(d.giorno()-1);
			p.setHour(d.ora());
			p.setMinute(d.minuto());
			p.setOld(memo);
			p.setVisible(true);
			p.addWindowListener(new WindowAdapter(){
				
				@Override
				public void windowDeactivated(WindowEvent arg0) {
					if(p.getOk()){
						//System.out.print("il MemPanel è cambiato.");
						MemPanel.this.setVisible(false);
						memo=p.getCreated();
						//System.out.println("il popup ritorna:"+memo.toString());
						orario.setText(memo.endDate());
						descrizione.setText(memo.description());
						MemPanel.this.setColour(memo.priority());
					}
					checkMemo(); //controlla che il memo appena creato sia idoneo e non sia a sua volta scaduto
					MemPanel.this.setVisible(true);			
				}
			});	//WindowListener
		}//modifica & personalizza
		else if(evt.getSource()==tipo){
			if(!tipoB){
				tipoB=true;
				orario.setVisible(false);
				orario.setText(memo.countDown());
				orario.setVisible(true);
			}
			else{
				tipoB=false;
				orario.setVisible(false);
				orario.setText(memo.endDate());
				orario.setVisible(true);
			}
		}//tipo
	}

	public void setDefaultSize(){
		
		setSize(getParent().getWidth(), 9);
	}
	
	public boolean isTrashed(){
		
		if(memo.isScaduto())
			return true;
		else return !isVisible();
	}
	
	public static void main(String [] args){
		
		JFrame f=new JFrame();
		Memo uno=new Memo("alta priorità","high",2014,3,3,3,30);
		Memo due=new Memo("media priorità",2015,3,3,3,30);
		Memo tre=new Memo("bassa priorità","low",2014,3,3,3,30);
		Popup pp=new Popup(true);
		MemPanel mp=new MemPanel(uno,pp);
		MemPanel mp2=new MemPanel(due,pp);
		MemPanel mp3=new MemPanel(tre,pp);
		JPanel p=new JPanel();
		//System.out.println(f.getLayout().toString());
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		f.getContentPane().add(p,BorderLayout.NORTH);
		p.setBackground(Color.RED);
		f.getContentPane().setBackground(Color.BLACK);
		f.setVisible(true);
		System.out.println("contentPane layout:"+f.getContentPane().getLayout().toString());
		System.out.println("JPanel layout:"+p.getLayout().toString());
		p.add(mp);
		p.add(mp2);
		p.add(mp3);
		System.out.println(p.getComponent(1).toString()+" height:"+p.getComponent(1).getX());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	}
}