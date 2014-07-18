package graphic;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*; 					//per la BufferedImage

import main.Memo;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.border.LineBorder;

import util.Data;


public class MemPanel extends JPanel implements ActionListener{
	
	
	@SuppressWarnings("serial")
	class MyMenuItem extends JMenuItem{
		
		public MyMenuItem(String text){
			
			super(text);
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
	private boolean iconaCambiata;
	private Popup p;
	private JPanel paneleft;
	private JLabel iconTainer;
	private ImageIcon icon;
	private MouseTrap mouse;
	private JFileChooser jfc;
	private JButton iconButton;
	
	/**
	 * Costruttore di Default
	 */
	public MemPanel(Memo memo){
		
		this.memo=memo;
		this.p=null;
		this.jfc=null;
		iconButton=null;
		mouse=new MouseTrap();
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
		combo.addMouseListener(mouse);
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
		File icona=new File("/home/fabrizio/workspace/MemoRem/src/graphic/icons/"+memo.getIcon());
		if(!icona.exists())
			memo.setIcon(".error.png");
		icon=new ImageIcon(currentDir+"/src/graphic/icons/"+memo.getIcon());
		iconaCambiata=false;
		putIcon();
		paneleft.setLayout(new BorderLayout(0, 0));
		//iconTainer.setSize(20,20);
		paneleft.add(iconTainer, BorderLayout.WEST);
		orario=new JLabel(memo.endDate(),SwingConstants.LEFT);
		orario.setBackground(new Color(0,0,0,0));
		paneleft.add(orario);
		//orario.setForeground(coloreClock);
		orario.setVerticalAlignment(SwingConstants.CENTER);
		orario.addMouseListener(mouse);
		descrizione=new JLabel(memo.description(),SwingConstants.CENTER);
		//descrizione.setFont(descrizione.getFont().deriveFont(descrizione.getFont().getStyle() | Font.BOLD));
		descrizione.setFont(descrizione.getFont().deriveFont(Font.BOLD+Font.ITALIC));
		descrizione.setVerticalAlignment(SwingConstants.CENTER);
		descrizione.addMouseListener(mouse);
		//setVisible(true);
		add(descrizione,BorderLayout.CENTER);
		add(bar,BorderLayout.EAST);
		this.setMaximumSize(new Dimension(20000,32));
		checkMemo();
		//ora assegniamo i colori
		setColour(memo.priority());	
	}
	
	/**
	 * Costruttore di copia
	 * @param mp
	 */
	public MemPanel(MemPanel mp){
		
		this(mp.memo);
		this.setBridges(mp.p, mp.jfc, mp.iconButton);
	}
	
	
	public Memo getMemo(){
		
		return memo;
	}
	
	public void setBridges(Popup p,JFileChooser jfc,JButton ok){
		
		this.p=p;
		this.jfc=jfc;
		this.iconButton=ok;
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
		BufferedImage bim=null;
		try{
			bim=new BufferedImage(im.getWidth(null), im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		}catch( IllegalArgumentException iae){
			icon=new ImageIcon("home/fabrizio/workspace/MemoRem/src/graphic/icons/.error.png");
			bim=new BufferedImage(im.getWidth(null), im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		}
		Graphics g=bim.createGraphics();
		g.drawImage(im, 2, 10, 25, 25, null);
		ImageIcon ii=new ImageIcon(bim);
		if(iconTainer!=null)
			paneleft.remove(iconTainer);
		iconTainer=new JLabel(ii,SwingConstants.CENTER);
		paneleft.add(iconTainer, BorderLayout.WEST);
		iconTainer.setVisible(false);
		iconTainer.setVisible(true);
		if(mouse!=null)
			iconTainer.addMouseListener(mouse);
		
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
		setBackground(new Color(coloreSfondo.getRed(),coloreSfondo.getGreen(),coloreSfondo.getBlue()));
		
		setBorder(new LineBorder(coloreSfondo.darker()));			//definisce il bordo del memo
	}
	
	/**
	 * Questo metodo esegue automaticamente delle funzionalità in base allo stato del memo
	 * (se è attivo o scaduto).
	 * @return true se il memo è appena scaduto e non era stato già notificato. False se è ancora attivo
	 * o se la sua scadenza era già stata notificata
	 */
	public boolean checkMemo(){
		
		/*
		 * se il memo è scaduto cambiano i tasti
		 */
		if(this.memo.isScaduto()){
			if(scadenzanotificata==false){
				scadenzanotificata=true;
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
			
			p.setModified(true);
			Data d=memo.getEnd();
			//p.setOld(memo);
			p.setDesc(memo.description());
			p.setPrior(memo.priority());
			p.setYear(d.anno());
			p.setMonth(d.mese()-1);
			p.setDay(d.giorno()-1);
			p.setHour(d.ora());
			p.setMinute(d.minuto());
			p.setOld(memo);
			p.setVisible(true);
			p.addWindowListener(new WindowAdapter(){
				
				@Override
				public void windowDeactivated(WindowEvent arg0) {
					if(p.getOk()){
						MemPanel.this.setVisible(false);
						if(p.getCreated().getEnd().compareTo(new Data())<0){
							System.out.println("non posso accettare questa data");
							MemPanel.this.setVisible(true);
							return;
						}
						memo=p.getCreated();
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
	
	public boolean iconaCambiata(){
		
		return iconaCambiata;
	}
	
	public boolean isTrashed(){
		
		if(memo.isScaduto())
			return true;
		else return !isVisible();
	}
	class MouseTrap extends MouseAdapter{

		@Override
		public void mouseClicked(MouseEvent e) {
			
			if(e.getSource()==iconTainer){
				if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2){
					//System.out.println("MemPanel doppio Click");
					jfc.setCurrentDirectory(new File("/home/fabrizio/workspace/MemoRem/src/graphic/icons/"));
					jfc.setDialogTitle("Scegli icona");
					jfc.setVisible(true);
					jfc.setAccessory(new MemPanel(MemPanel.this));
					jfc.getAccessory().setVisible(false);
					int value=jfc.showOpenDialog(MemPanel.this);
					if(value==JFileChooser.APPROVE_OPTION){
						iconaCambiata=true;
						iconButton.doClick();
						String path=jfc.getSelectedFile().getAbsolutePath();
						icon= new ImageIcon(path);
						putIcon();
					}
				}
			}//iconTainer
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
			if(e.getSource()==combo)
				combo.doClick();
			else if(combo.isSelected())
				MenuSelectionManager.defaultManager().clearSelectedPath();
			else if(e.getSource()==orario){
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
			}//orario
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
			if(e.getSource()==orario){
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
		}
	}
	
	public static void main(String [] args){
		
		JFrame f=new JFrame();
		Memo uno=new Memo("alta priorità","high",2014,3,3,3,30);
		Memo due=new Memo("media priorità",2015,3,3,3,30);
		Memo tre=new Memo("bassa priorità","low",2014,3,3,3,30);
		MemPanel mp=new MemPanel(uno);
		MemPanel mp2=new MemPanel(due);
		MemPanel mp3=new MemPanel(tre);
		JPanel p=new JPanel();
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