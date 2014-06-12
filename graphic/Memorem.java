package graphic;


import net.miginfocom.swing.MigLayout;	//utilizzato da panel

import java.util.LinkedList;
import java.util.Timer;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import graphic.MemPanel.MyMenuItem;
import main.*;
import util.Data;
import util.MemoList;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;


/*
 * Nel momento in cui si creano le icone, ricordarsi di gestire l'eccezione in cui l'assenza di un icona
 * come file non mandi in crash l'intero programma
 */
@SuppressWarnings("serial")
public class Memorem extends JFrame{
	
	
	private Timer timer;
	private InnerClock orologio;
	private ezioAuditore listener;
	private desmondMiles watcher;
	/*
	 * visualHandler gestisce il funzionamento del panel_2 (switch tra InternalFrame e CalendarFrame)
	 * dataHandler gestisce la scelta della durata dei memo da visualizzare
	 * innerVisualHandler gestisce la scelta del tipo di memo da visualizzare (scaduti o attivi)
	 */
	private Abbottonatore visualHandler, dataHandler, innerVisualHandler;
	private JMenuItem mntmEsci,mntmNuova,mntmSalva,mntmLogin,mntmLogout,mntmAggiungi,mntmCancella;
	private JMenu mnNuovo,mnStrumenti,mnVisualizza;
	private Keeper k;
	private JPanel panel;
	private JInternalFrame panel_2;
	private CalendarFrame calendar;
	private ClassicFrame classic;
	private Popup p;
	private PopItem completa,elimina,modifica,one,three,seven,custom;
	private LoginDialog logD;
	private JProgressBar progressBar;
	private JRadioButtonMenuItem classicRadio,calendarRadio;
	private JRadioButtonMenuItem[] vF=new JRadioButtonMenuItem[2],dF=new JRadioButtonMenuItem[5];
	private JCheckBoxMenuItem[] pF=new JCheckBoxMenuItem[3];
	
	private class InnerClock extends java.util.TimerTask{
		
		/**
		 * Ogni tot secondi viene richiamato questo metodo
		 */
		public void run(){
			
			Memorem.this.checkMemos();
		}
	}
	/**
	 * Classe privata che serve per gestire i radiobutton presenti dentro il programma
	 * @author fabrizio
	 *
	 */
	private class Abbottonatore implements ActionListener{
		
		private JRadioButtonMenuItem[] bottoni=new JRadioButtonMenuItem[10];
		private int size=0;
		private String nome;
		
		public Abbottonatore(String nome){
			this.nome=nome;
		}
		
		public void add(JRadioButtonMenuItem bottone){
			
			if(size==bottoni.length){
				System.out.println("servono più buchi per questi bottoni");
				JRadioButtonMenuItem[] nb=new JRadioButtonMenuItem[bottoni.length*2];
				for(int i=0;i<bottoni.length;i++)
					nb[i]=bottoni[i];
				bottoni=nb;
			}
			bottone.addActionListener(this);
			bottoni[size]=bottone;
			size++;
		}
		
		/**
		 * Stabilisce qual'è il bottone che dev'essere premuto dall'inizio
		 * @param button
		 */
		public void setDefaultButton(JRadioButtonMenuItem button){
			
			for(int i=0;i<bottoni.length;i++)
				if(bottoni[i]==button)
					bottoni[i].setSelected(true);
		}
		
		/**
		 * Ritorna quale bottone all'interno della lista è quello premuto
		 * @return
		 */
		public JRadioButtonMenuItem getSelected(){
			
			for(int i=0;i<bottoni.length;i++)
				if(bottoni[i].isSelected())
					return bottoni[i];
			throw new IllegalStateException("Impossibile che nessun bottone sia premuto");
		}
		
		public void actionPerformed(ActionEvent evt){
			
			JRadioButtonMenuItem pressed=(JRadioButtonMenuItem)evt.getSource();
			for(int i=0;i<size;i++){
				if(bottoni[i].equals(pressed))
					bottoni[i].setSelected(true);
				else
					bottoni[i].setSelected(false);
			}
			if(nome.equals("visual")){
				Memorem.this.switchVisual();
			}
			else if(nome.equals("data") || nome.equals("visual") || nome.equals("inner")){
				Memorem.this.createQuery();
			}
		}
	}
	
	private class desmondMiles extends WindowAdapter{
		
		public void windowDeactivated(WindowEvent arg0) {
			
			/*SE ARRIVIAMO QUI POSSIAMO ANCHE AVER PREMUTO IL TASTO PERSONALIZZA
			 *PRIMA DI MUOVERSI BISOGNA CAPIRE SE È UNA MODIFICA O UNA PERSONALIZZAZIONE DI UN MEMO
			 *SCADUTO, OVVERO BISOGNA VEDERE SE IL p.getOld() È SCADUTO
			 */
			if(arg0.getSource()==p){	//se arriviamo qui significa che abbiamo ricevuto la notifica di modifica
				System.out.println("grazie al Popup: "+p.hashCode());
				if( p.getOk() && p.getModified()){	//e possiamo andare avanti solamente se è stato premuto il tasto ok
					
					System.out.println("captata una modifica");
					mntmSalva.setEnabled(true);
					Memo old=p.getOld();
					//System.out.println(old.toString());
					Memo memo=p.getCreated();
					System.out.println("vecchio:\t"+old.toString());
					System.out.println("nuovo:  \t"+memo.toString());
					k.modifica(old, memo);								//il Keeper è stato aggiornato
					calendar.setMemolist(k.getTotalList());				//il Calendar è stato aggiornato
					calendar.getCustomRenderer().refresh();
					checkMemos();
				}
				else if(p.getOk() && !p.getModified()){	//qui invece gestiamo la notifica di un aggiunta di memo
					System.out.println("aggiunta in corso..");
					Memo m=p.getCreated();
					if(m.getEnd().compareTo(new Data())<0){
						JOptionPane.showMessageDialog(Memorem.this, "Impossibile aggiungere memo con scadenza nel passato");
						p.dispose();
						return;
					}
					k.add(m);							//il Keeper è stato aggiornato
					calendar.add(m);					//il CalendarFrame è stato aggiornato
					MemPanel mp=new MemPanel(m,p);
					if(m.isCompleted()){
						mp.completa();
					}
					else{
						mp.getElimina().addActionListener(listener);
						mp.getCompleta().addActionListener(listener);
						mp.getArchivia().addActionListener(listener);
						mp.getOneDay().addActionListener(listener);
						mp.getThreeDays().addActionListener(listener);
						mp.getOneWeek().addActionListener(listener);
					}
					classic.add(mp);	//l' InternalFrame è stato aggiornato
					System.out.println("dimensione di k:"+k.getTotalList().size());
					System.out.println("dimensione di calendar:"+calendar.getMemoList().size());
					if(visualHandler.getSelected().equals(classicRadio)){
						System.out.println("dopo l'aggiunta ci troviamo nell'internalFrame");
						panel_2=classic;
					}
					else{
						System.out.println("dopo l'aggiunta ci troviamo nel calendarFrame");
						panel_2=calendar;
					}
					panel_2.setVisible(false);
					panel_2.setVisible(true);
					p.dispose();
				}
			}//popup	
			else if(arg0.getSource()==logD){
				if(logD.isForRegistration() && logD.isOk()){	//abbiamo premuto "Nuova Sessione"
					
					String[] result=logD.result();
					String nick=result[0],pass=result[1];
					try{
						if(k.signUp(nick,pass)){
							System.out.println("Abbiamo registrato un utonto");
							nuovo();
						}
					}catch(MySQLIntegrityConstraintViolationException e){
						
						JOptionPane.showMessageDialog(Memorem.this,"L'utente esiste già");
					}
				}
				else if(!logD.isForRegistration() && logD.isOk()){				//abbiamo premuto "login"
					
					String[] result=logD.result();
					String nick=result[0],pass=result[1];
					int x=k.login(nick, pass);
					if(x==0){
						enableButtons();
						System.out.println("stamu traseeendu");
						login();
						int cont=0;
						for(Memo m: k){
							if(visualHandler.getSelected().equals(classicRadio)){
								MemPanel mp=new MemPanel(m,p);
								mp.getElimina().addActionListener(listener);
								mp.getCompleta().addActionListener(listener);
								mp.getArchivia().addActionListener(listener);
								mp.getOneDay().addActionListener(listener);
								mp.getThreeDays().addActionListener(listener);
								mp.getOneWeek().addActionListener(listener);
								if(m.getEnd().compareTo(new Data())<0)
									cont++;
								((ClassicFrame)panel_2).add(mp);
								calendar.add(m);
							}
						}
						if(cont==1){
							JOptionPane.showMessageDialog(Memorem.this, "Un memo è scaduto!");
							Toolkit.getDefaultToolkit().beep();
						}
						else if(cont>1){
							JOptionPane.showMessageDialog(Memorem.this, cont+" memo sono scaduti");
							Toolkit.getDefaultToolkit().beep();
						}
					}
					else{
						if(x==1)
							JOptionPane.showMessageDialog(Memorem.this,"L'utente non esiste");
						else
							JOptionPane.showMessageDialog(Memorem.this,"Password sbagliata");
					}
				}
			}
		}
	}
	/**
	 * Classe private che funge da ActionListener per i pulsanti del Frame
	 * @author fabrizio
	 *
	 */
	private class ezioAuditore implements ActionListener{
		
		
		public void actionPerformed(ActionEvent evt){
			
			if(evt.getSource()==mntmEsci){					//premuto il tasto esci
				/*if(mntmSalva.isEnabled())
					k.salva();*/
				
				panel_2.dispose();
				Memorem.this.dispose();
				System.exit(0);
			}
			else if(evt.getSource()==mntmNuova){			//premuto il tasto nuovo
				k=new Keeper();
				logD=new LoginDialog(true);
				logD.addWindowListener(watcher);
				logD.setVisible(true);
			}
			else if(evt.getSource()==mntmLogin){			//premuto il tasto apri
				k=new Keeper();
				logD=new LoginDialog(false);
				logD.addWindowListener(watcher);
				logD.setVisible(true);
			}
			else if(evt.getSource()==mntmLogout){
				k.logout();
				mntmLogin.setEnabled(true);
				progressBar.setValue(0);
				setButtonsAtStart();
			}
			else if(evt.getSource()==mntmSalva)				//premuto il tasto salva
				Memorem.this.salva();
			else if(evt.getSource()==mntmAggiungi)			//premuto il tasto aggiungi
				Memorem.this.aggiungi();
			else if(evt.getSource()==mntmCancella)			//premuto il tasto cancella
				Memorem.this.cancella();
			else if(evt.getSource().equals(pF[0]) || evt.getSource().equals(pF[1]) || evt.getSource().equals(pF[2]))
				Memorem.this.createQuery();
			else if(evt.getSource() instanceof MyMenuItem){		//qui gestiamo i tasti del MemPanel
				if(((MyMenuItem)evt.getSource()).getText().equals("elimina"))	//premuto il tasto elimina
					Memorem.this.elimina(((MyMenuItem)evt.getSource()).getMemo());
				else if(((MyMenuItem)evt.getSource()).getText().equals("completa"))	//premuto il tasto completa
					Memorem.this.completa(((MyMenuItem)evt.getSource()).getMemo(),true);
				else if(((MyMenuItem)evt.getSource()).getText().equals("archivia")) //premuto il tasto archivia
					Memorem.this.completa(((MyMenuItem)evt.getSource()).getMemo(),false);
				else if(((MyMenuItem)evt.getSource()).getText().equals("1 giorno"))	//premuto il tasto 1 giorno
					Memorem.this.rinvia(((MyMenuItem)evt.getSource()),1);
				else if(((MyMenuItem)evt.getSource()).getText().equals("3 giorni"))	//premuto il tasto 3 giorni
					Memorem.this.rinvia((MyMenuItem)evt.getSource(),3);
				else if(((MyMenuItem)evt.getSource()).getText().equals("1 settimana"))//premuto il tasto 1 settimana
					Memorem.this.rinvia((MyMenuItem)evt.getSource(),7);
			}
			else if(evt.getSource() instanceof PopItem){		//qui gestiamo i tasti del CalendarFrame
				
				if(evt.getSource()==elimina)
					Memorem.this.elimina(elimina.getMemo());
				else if(evt.getSource()==modifica){
					Memo m=modifica.getMemo();
					p.setModified(true);
					p.setOld(m);
					p.setDesc(m.description());
					p.setPrior(m.priority());
					p.setYear(m.getEnd().anno());
					p.setMonth(m.getEnd().mese());
					p.setDay(m.getEnd().giorno());
					p.setHour(m.getEnd().ora());
					p.setMinute(m.getEnd().minuto());
					p.setVisible(true);
				}
				else if(evt.getSource()==completa)
					Memorem.this.completa(completa.getMemo(),true);
				else if(evt.getSource()==one)
					Memorem.this.rinvia(one,1);
				else if(evt.getSource()==three)
					Memorem.this.rinvia(three,3);
				else if(evt.getSource()==seven)
					Memorem.this.rinvia(seven,7);
				else if(evt.getSource()==custom){
					p.setModified(true);
					Memo x=custom.getMemo();
					p.setOld(x);
					p.setDesc(x.description());
					p.setPrior(x.priority());
					p.setYear(x.getEnd().anno());
					p.setMonth(x.getEnd().mese());
					p.setDay(x.getEnd().giorno());
					p.setHour(x.getEnd().ora());
					p.setMinute(x.getEnd().minuto());
					
				}
			}
			
					
				
		}
		
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Memorem frame=new Memorem();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Memorem() {
		
		getContentPane().setBackground(new Color(0, 0, 0));		//colora lo sfondo di nero
		setUndecorated(true);									//rimuove la title bar di Windows
		setTitle("MemoRem 1.0");								//titolo del frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		listener=new ezioAuditore();							// ascoltatore pulsanti
		watcher=new desmondMiles();								// ascoltatore finestra
		visualHandler=new Abbottonatore("visual");				// ascoltatore radioButton per tipo di visualizzazione
		
		JMenuBar menuBar = new JMenuBar();						//la barra dei menu in alto
		setJMenuBar(menuBar);
		
		mnNuovo = new JMenu("File");
		menuBar.add(mnNuovo);
		manageFileButtons();		//qui abbiamo creato e gestito i bottini del 1° JMenu
		
		mnStrumenti = new JMenu("Strumenti");
		menuBar.add(mnStrumenti);
		manageStrumentiButtons();	//qui abbiamo creato e gestito i bottoni del 2° JMenu
		
		mnVisualizza= new JMenu("Visualizzazione");
		classicRadio = new JRadioButtonMenuItem("Classica");
		classicRadio.setSelected(true);
		mnVisualizza.add(classicRadio);
		visualHandler.add(classicRadio);
		
		calendarRadio = new JRadioButtonMenuItem("Calendario");
		mnVisualizza.add(calendarRadio);
		visualHandler.add(calendarRadio);
		menuBar.add(mnVisualizza);
		
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		panel = new JPanel();
		JPanel PBPanel=new JPanel();
		setInternalFrameAtStart();
		panel.setBackground(new Color(0, 0, 0));
		panel.setLayout(new MigLayout("", "[][grow][grow][][][][grow]", "[grow][grow][grow][grow][grow][][][][][][][][][][][][][][][][][][][][][][grow][]"));
		setContentPane(panel);
		panel.add(panel_2, "cell 0 0 7 26,grow");
		
		PBPanel.setLayout(new BorderLayout(0, 0));
		progressBar = new JProgressBar();
		progressBar.setForeground(new Color(50, 205, 50));
		progressBar.setStringPainted(true);
		PBPanel.add(progressBar);
		panel.add(PBPanel, "cell 0 26 7 1,growx,aligny bottom");
		setButtonsAtStart();
	}
	
	/**
	 * Questo metodo si occupa di settare l'inner bar allo stato di default e con tutti i pulsanti
	 * equipaggiati di ActionListener
	 */
	private void setInternalFrameAtStart(){
		
		JCheckBoxMenuItem cbm1 = new JCheckBoxMenuItem("Alta");
		JCheckBoxMenuItem cbm2 = new JCheckBoxMenuItem("Media");
		JCheckBoxMenuItem cbm3 = new JCheckBoxMenuItem("Bassa");
		cbm1.setSelected(true);
		cbm2.setSelected(true);
		cbm3.setSelected(true);
		cbm1.addActionListener(listener);
		cbm2.addActionListener(listener);
		cbm3.addActionListener(listener);
		pF[0]=cbm1;pF[1]=cbm2;pF[2]=cbm3; 	//creiamo pF
		JRadioButtonMenuItem today=new JRadioButtonMenuItem("Solo oggi");
		JRadioButtonMenuItem week=new JRadioButtonMenuItem("In settimana");
		JRadioButtonMenuItem month=new JRadioButtonMenuItem("In questo mese");
		JRadioButtonMenuItem year=new JRadioButtonMenuItem("In quest'anno");
		JRadioButtonMenuItem always=new JRadioButtonMenuItem("Sempre");
		dataHandler=new Abbottonatore("data");
		dataHandler.add(today);
		dataHandler.add(week);
		dataHandler.add(month);
		dataHandler.add(year);
		dataHandler.add(always);
		dataHandler.setDefaultButton(always);
		dF[0]=today; dF[1]=week; dF[2]=month; dF[3]=year; dF[4]=always; //creiamo dF
		JRadioButtonMenuItem radioAttivi=new JRadioButtonMenuItem("Memo attivi");
		JRadioButtonMenuItem radioScaduti=new JRadioButtonMenuItem("Memo scaduti");
		innerVisualHandler=new Abbottonatore("inner");
		innerVisualHandler.add(radioAttivi);
		innerVisualHandler.add(radioScaduti);
		innerVisualHandler.setDefaultButton(radioAttivi);
		vF[0]= radioAttivi; vF[1]= radioScaduti; //creiamo vF
		classic=new ClassicFrame(vF,pF,dF);
		panel_2=classic;
		panel_2.setVisible(false);
		
	}
	/**
	 * prima di inizializzare il programma premendo il tasto "Nuovo" o "Apri", è impossibile utilizzare questi tasti
	 */
	private void setButtonsAtStart(){
		
		mnVisualizza.setEnabled(false);
		mnStrumenti.setEnabled(false);
		mntmSalva.setEnabled(false);
		mntmLogout.setEnabled(false);
		completa=new PopItem("Completa");
		elimina=new PopItem("Elimina");
		modifica=new PopItem("Modifica");
		one=new PopItem("Un giorno");
		three=new PopItem("Tre giorni");
		seven=new PopItem("Una settimana");
		custom=new PopItem("Personalizza");
		completa.addActionListener(listener);
		elimina.addActionListener(listener);
		modifica.addActionListener(listener);
		one.addActionListener(listener);
		three.addActionListener(listener);
		seven.addActionListener(listener);
		custom.addActionListener(listener);
	
	}
	
	/**
	 * Dopo aver premuto il tasto "Nuovo" o "Apri", i tasti possono essere abilitati
	 */
	private void enableButtons(){
		
		mnVisualizza.setEnabled(true);
		mnStrumenti.setEnabled(true);
		mntmLogout.setEnabled(true);
	}
	
	/*
	 * Crea il menu File, con relativi bottoni
	 */
	private void manageFileButtons(){
		mntmNuova = new JMenuItem("Nuova sessione");
		mntmNuova.addActionListener(listener);
		mnNuovo.add(mntmNuova);
		
		mntmLogin = new JMenuItem("Login");
		mntmLogin.addActionListener(listener);
		mnNuovo.add(mntmLogin);
		
		mntmLogout = new JMenuItem("Logout");
		mntmLogout.addActionListener(listener);
		mnNuovo.add(mntmLogout);
		
		JSeparator separator_1 = new JSeparator();
		mnNuovo.add(separator_1);
		
		mntmSalva = new JMenuItem("Salva");
		mntmSalva.addActionListener(listener);
		mnNuovo.add(mntmSalva);
		
		JSeparator separator_2 = new JSeparator();
		mnNuovo.add(separator_2);
		
		mntmEsci = new JMenuItem("Esci");
		mntmEsci.addActionListener(listener);
		mnNuovo.add(mntmEsci);
		
		
	}
	
	private void manageStrumentiButtons(){
		
		mntmAggiungi = new JMenuItem("Aggiungi");
		mntmAggiungi.addActionListener(listener);
		mnStrumenti.add(mntmAggiungi);
		
		JSeparator separator = new JSeparator();
		mnStrumenti.add(separator);
		
		mntmCancella = new JMenuItem("Cancella");
		mntmCancella.addActionListener(listener);
		mnStrumenti.add(mntmCancella);
		
	}
	
	private void checkMemos(){
		
		Data ora=new Data();
		//System.out.print(ora.toString()+". Check in corso..");
		if(visualHandler.getSelected().equals(classicRadio)){
			//System.out.println("El clasico");
			if(((ClassicFrame)panel_2).updateMemos()){
				JOptionPane.showMessageDialog(this, "Un memo è scaduto");
				Toolkit.getDefaultToolkit().beep();
			}
		}
		else{
			//System.out.println("El calendario");
			if(!calendar.getMemoList().equals(k.getTotalList())){
				//System.out.println("Aggiorniamo il calendario :D");
				((CalendarFrame)panel_2).getCustomRenderer().refresh();
			}
		}
		panel_2.setVisible(false);
		panel_2.setVisible(true);
		
		
	}
	
	/**
	 * Cambia la visualizzazione del Memorem dalla modalità classica alla modalità calendario
	 * e viceversa
	 */
	private void switchVisual(){
		
		panel_2.dispose();
		panel.remove(panel_2);
		if(visualHandler.getSelected().equals(classicRadio)){ //passiamo da visualizzazione Calendario a Classica
			System.out.println("quindi si passa alla visualizzazione Classica");
			panel_2=classic;
		}
		else{ 	//passiamo da visualizzazione Classica a Calendario
			System.out.println("quindi si passa alla visualizzazione Calendario");
			PopItem[] items=new PopItem[7];
			items[0]=completa;
			items[1]=modifica;
			items[2]=elimina;
			items[3]=one;
			items[4]=three;
			items[5]=seven;
			items[6]=custom;
			calendar.setPopItems(items);
			calendar.getCustomRenderer().refresh();
			panel_2=calendar;
		}
		panel.add(panel_2, "cell 0 0 7 26,grow");
		panel_2.setVisible(true);
	}
	
	private void createQuery(){
		
		String data="always";
		String visual="attivi";
		if(innerVisualHandler.getSelected().getText().equals("Memo scaduti"))
			visual="scaduti";
		String x=dataHandler.getSelected().getText();
		if(x.equals("Solo oggi"))
			data="oggi";
		else if(x.equals("In settimana"))
			data="week";
		else if(x.equals("In questo mese"))
			data="month";
		else if(x.equals("In quest'anno"))
			data="year";
		LinkedList<String> prioritatibus=new LinkedList<String>();
		for(int i=0;i<3;i++)
			if(pF[i].isSelected()){
				System.out.println(pF[i].getText());
				prioritatibus.add(pF[i].getText());
			}
		k.formaQuery(data, visual, prioritatibus);
		MemoList nuova=k.getTotalList();
		calendar.setMemolist(nuova);
		classic.clearMemos();
		for(Memo m:nuova){
			MemPanel mp=new MemPanel(m,p);
			if(visual=="scaduti")
				mp.completa();
			else{
				mp.getElimina().addActionListener(listener);
				mp.getCompleta().addActionListener(listener);
				mp.getArchivia().addActionListener(listener);
				mp.getOneDay().addActionListener(listener);
				mp.getThreeDays().addActionListener(listener);
				mp.getOneWeek().addActionListener(listener);
			}
			classic.add(mp);	//l' InternalFrame è stato aggiornato
		}
		panel_2.setVisible(false);
		panel_2.setVisible(true);
	}
	/* .___________________________________________________________________________________________		*
	 * |¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯|	*
	 * |						QUI INIZIANO I METODI PER IL FUNZIONAMENTO DEI TASTI DEL MENU	   |	*
	 * |___________________________________________________________________________________________|	*
	 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯		*
	 *																									*/
	
	
	public void nuovo(){
		
		k=new Keeper();
		p=new Popup(false);
		p.addWindowListener(watcher);
		panel_2.setVisible(true);
		timer=new Timer();
		orologio=new InnerClock();
		calendar=new CalendarFrame(k.getTotalList());
		calendar.setListener(p);
		if(visualHandler.getSelected().equals(classicRadio))
			((ClassicFrame)panel_2).clearMemos();
		timer.schedule(orologio, 0,5000);
		enableButtons();
	}
	
	public void login(){
		
		p=new Popup(false);
		p.addWindowListener(watcher);
		timer=new Timer();
		panel_2.setVisible(true);
		orologio=new InnerClock();
		calendar=new CalendarFrame(new MemoList());
		calendar.setListener(p);
		timer.schedule(orologio, 0,5000);
		progressBar.setValue(k.percentualeCompletati());
		mntmLogin.setEnabled(false);
		JOptionPane.showMessageDialog(this, "Buongiorno, "+k.getUser());
	}
	
	public void salva(){
		
		k.salva();
		mntmSalva.setEnabled(false);
	}
	
	/**
	 * Aggiunge un nuovo Memo
	 */
	public void aggiungi(){
		
		p.setModified(false);
		p.setVisible(true);
		//p.addWindowListener(this);
	}//aggiungi
	
	/**
	 * Cancella tutti i memo presenti
	 */
	public void cancella(){
		
		int choice=JOptionPane.showConfirmDialog(this, "I cambiamenti saranno irreversibili. Continuare?");
		if(choice==JOptionPane.OK_OPTION){
			k.clear();
			System.out.println("cancelliamo");
			panel_2.setVisible(false);
			calendar=new CalendarFrame(k.getTotalList());
			calendar.setListener(p);
			classic.clearMemos();
			if(visualHandler.getSelected().equals(classicRadio))
				panel_2=classic;
			else
				panel_2=calendar;
			panel_2.setVisible(true);
			mntmSalva.setEnabled(true);
		}
	}
	
	public void elimina(Memo m){
		
		System.out.println("eliminiamo: "+m.toString());
		k.remove(m);					//il Keeper è stato aggiornato
		mntmSalva.setEnabled(true);	
		//progressBar.setValue(k.percentualeCompletati());
		classic.remove(m);				//l'InternalFrame è stato aggiornato
		calendar.remove(m);				//il CalendarFrame è stato aggiornato
		if(visualHandler.getSelected().equals(calendarRadio))
			panel_2=calendar;
		else
			panel_2=classic;
	}
	
	public void completa(Memo m,boolean completato){
		
		if(completato)
			JOptionPane.showMessageDialog(this, "Complimenti!!! Hai completato un' attività");
		k.completa(m,completato);
		calendar.remove(m);
		classic.remove(m);
		progressBar.setValue(k.percentualeCompletati());
	}
	
	/**
	 * rinvia un memo scaduto
	 */
	private void rinvia(JMenuItem tasto,int giorni){
		
		Memo m;
		if(tasto instanceof MyMenuItem)
			m=((MyMenuItem)tasto).getMemo();
		else if(tasto instanceof PopItem)
			m=((PopItem)tasto).getMemo();
		else{
			JOptionPane.showMessageDialog(this, "Errore. Il tasto cliccato non contiene informazioni");
			return;
		}
		Data nuova=m.getEnd();
		for(int i=0;i<giorni;i++)
			nuova=nuova.domani();
		if(nuova.compareTo(new Data())<=0){
			JOptionPane.showMessageDialog(this, "Impossibile rinviare di "+giorni+" giorni. \nIl memo sarebbe comunque scaduto");
			return;
		}
		if(k.contains(m)){
			calendar.remove(m);
			classic.remove(m);
			/*if(visualHandler.getSelected().equals(classicRadio))
				((ClassicFrame)panel_2).remove(m);
				*/
		}
		if(tasto instanceof MyMenuItem)
			((MyMenuItem)tasto).apochiudira();
		Memo nuovo=new Memo(m.description(),nuova.anno(),nuova.mese(),nuova.giorno(),nuova.ora(),nuova.minuto());
		nuovo.setPriority(m.priority());
		MemPanel mp=new MemPanel(nuovo,p);
		//panel_2.add(mp);
		k.modifica(m, nuovo);
		//k.add(nuovo);
		classic.add(mp);
		calendar.add(nuovo);
	}
}