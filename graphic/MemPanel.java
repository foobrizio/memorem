package graphic;

import graphic.MemoremGUI.Lang;

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
	private MyMenuItem elimina, completa,oneDay,threeDays,oneWeek,oneMonth,personalizza,archivia;
	private boolean tipoB;
	private boolean iconaCambiata;
	private Popup p;
	private JPanel paneleft;
	private JLabel iconTainer;
	private ImageIcon icon;
	private MouseTrap mouse;
	private JFileChooser jfc;
	private JButton iconButton;
	private String iconPath;
	private Lang language;
	
	/**
	 * Costruttore di Default
	 */
	public MemPanel(Memo memo){
		
		this.memo=memo;
		this.language=Lang.EN;
		this.p=null;
		this.jfc=null;
		iconButton=null;
		mouse=new MouseTrap();
		//coloreClock=Color.BLACK;
		tipoB=false; //se impostato a false il memo è attivo
		bar=new JMenuBar();
		elimina=new MyMenuItem("delete");
		elimina.setName("elimina");
		elimina.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		modifica=new JMenuItem("modify");	
		modifica.setName("modifica");
		modifica.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		tipo=new JMenuItem("change view");
		tipo.setFont(new Font("Comic Sans MS", Font.BOLD, 12));

		rinvia = new JMenu("defer");
		rinvia.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		archivia= new MyMenuItem("store");
		archivia.setName("archivia");
		archivia.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		oneDay = new MyMenuItem("1 day");
		oneDay.setName("1 giorno");
		oneDay.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		threeDays = new MyMenuItem("3 giorni");
		threeDays.setName("3 giorni");
		threeDays.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		oneWeek = new MyMenuItem("1 week");
		oneWeek.setName("1 settimana");
		oneWeek.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		oneMonth = new MyMenuItem("1 month");
		oneMonth.setName("1 mese");
		oneMonth.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		personalizza = new MyMenuItem("custom");
		personalizza.setName("personalizza");
		personalizza.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		
		combo=new JMenu();
		combo.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		combo.setText("Options");
		completa = new MyMenuItem("complete");
		completa.setName("completa");
		completa.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		combo.add(completa);
		combo.add(archivia);
		combo.add(elimina);
		combo.add(modifica);
		combo.add(rinvia);
		
		rinvia.add(oneDay);
		rinvia.add(threeDays);
		rinvia.add(oneWeek);
		rinvia.add(oneMonth);
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
		File icona=new File(currentDir+"/src/graphic/icons/"+memo.getIcon());
		if(!icona.exists())
			memo.setIcon(".error.png");
		iconPath=icona.getAbsolutePath();
		icon=new ImageIcon(iconPath);
		iconaCambiata=false;
		boolean x=putIcon();
		paneleft.setLayout(new BorderLayout(0, 0));
		//iconTainer.setSize(20,20);
		if(x)
			paneleft.add(iconTainer, BorderLayout.WEST);
		orario=new JLabel(memo.endDate(),SwingConstants.LEFT);
		orario.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
		orario.setBackground(new Color(0,0,0,0));
		paneleft.add(orario);
		//orario.setForeground(coloreClock);
		orario.setVerticalAlignment(SwingConstants.CENTER);
		orario.addMouseListener(mouse);
		descrizione=new JLabel(memo.description(),SwingConstants.CENTER);
		descrizione.addMouseListener(mouse);
		//descrizione.setFont(descrizione.getFont().deriveFont(descrizione.getFont().getStyle() | Font.BOLD));
		descrizione.setFont(new Font("Linux Libertine Mono", Font.BOLD | Font.ITALIC, 12));
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
	
	public MyMenuItem getOneMonth(){
		
		return oneMonth;
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
	@SuppressWarnings("unused")
	private boolean putIcon(){
		
		Image im=icon.getImage();
		BufferedImage bim=null;
		try{
			bim=new BufferedImage(im.getWidth(null), im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		}catch( IllegalArgumentException iae){
			if(iconPath.equals("home/fabrizio/workspace/MemoRem/src/graphic/icons/.error.png")){
				iconPath="home/fabrizio/workspace/MemoRem/src/graphic/icons/note.png";
				icon=new ImageIcon(iconPath);
				return putIcon();
			}
			else if(iconPath.equals("home/fabrizio/workspace/MemoRem/src/graphic/icons/note.png")){
				return false;
			}
			iconPath="home/fabrizio/workspace/MemoRem/src/graphic/icons/.error.png";
			icon=new ImageIcon(iconPath);
			return putIcon();
		}
		if(bim!=null){
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
			return true;
		}
		return true;
		
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
		oneMonth.setBackground(coloreSfondo);
		personalizza.setBackground(coloreSfondo);
		setBackground(coloreSfondo);
		
		setBorder(new LineBorder(coloreSfondo.darker()));			//definisce il bordo del memo
	}
	
	public void setLanguage(Lang lang){
		
		if(this.language==lang)
			return;
		if(lang==Lang.IT){
			combo.setText("Opzioni");
			modifica.setText("modifica");
			rinvia.setText("rinvia");
			elimina.setText("elimina");
			completa.setText("completa");
			archivia.setText("archivia");
			tipo.setText("cambia visualizzazione");
			oneDay.setText("1 giorno");
			threeDays.setText("3 giorni");
			oneWeek.setText("1 settimana");
			oneMonth.setText("1 mese");
			personalizza.setText("personalizza");
		}
		else if(lang==Lang.DE){
			combo.setText("Optionen");
			modifica.setText("Ändern");
			rinvia.setText("Verzögerung");
			elimina.setText("Löschen");
			completa.setText("Absolvieren");
			archivia.setText("Speicher");
			tipo.setText("Ansicht wechseln");
			oneDay.setText("1 Tag");
			threeDays.setText("3 Tage");
			oneWeek.setText("1 Woche");
			oneMonth.setText("1 Monat");
			personalizza.setText("Anpassen");
		}
		else if(lang==Lang.ES){
			combo.setText("Opciones");
			modifica.setText("modifica");
			rinvia.setText("pospone");
			elimina.setText("elimina");
			completa.setText("completa");
			archivia.setText("tienda");
			tipo.setText("cambia las vistas");
			oneDay.setText("1 día");
			threeDays.setText("3 días");
			oneWeek.setText("1 semana");
			oneMonth.setText("1 mes");
			personalizza.setText("personaliza");
		}
		else{
			combo.setText("Options");
			modifica.setText("modify");
			rinvia.setText("postpone");
			elimina.setText("delete");
			completa.setText("complete");
			archivia.setText("store");
			tipo.setText("change view");
			oneDay.setText("1 day");
			threeDays.setText("3 days");
			oneWeek.setText("1 week");
			oneMonth.setText("1 month");
			personalizza.setText("custom");
		}
		repaint();
		Data end=this.memo.getEnd();
		end.setLanguage(lang);
		this.memo.setEnd(end);
		this.language=lang;
	}
	
	public void setMemo(Memo m){
		
		this.memo=m;
		setColour(m.priority());
		this.descrizione.setText(m.description());
		this.orario.setText(m.getEnd().toString());
		tipoB=false;
	}
	
	/**
	 * Trasferisce i bottoni da mp al nuovo MemPanel,in modo da avere gli ascoltatori dei vecchi memo 
	 * già configurati
	 * @param mp
	 */
	public void passaTestimone(MemPanel mp){
		
		this.completa=mp.completa;
		this.archivia=mp.archivia;
		this.modifica=mp.modifica;
		this.elimina=mp.elimina;
		this.rinvia=mp.rinvia;
		this.oneDay=mp.oneDay;
		this.oneMonth=mp.oneMonth;
		this.oneWeek=mp.oneWeek;
		this.threeDays=mp.threeDays;
		this.personalizza=mp.personalizza;
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
			if(!memo.isNotificato()){
				memo.setScadenzaNotificata();
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
			modifica.setVisible(true);
			rinvia.setVisible(false);
			archivia.setVisible(false);
			if(tipoB)
				orario.setText(memo.countDown());
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
			
			p.modifica(memo);
			
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
		Data d=new Data(2014,3,3,3,30);
		Memo uno=new Memo("alta priorità",2,d,"bench.png");
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
		mp3.setMemo(uno);
	}
}