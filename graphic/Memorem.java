package graphic;


import net.miginfocom.swing.MigLayout;	//utilizzato da panel

import java.util.*;
import java.util.Timer;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import graphic.MemPanel.MyMenuItem;
import main.*;
import util.Data;
import util.MemoList;


/*
 * Nel momento in cui si creano le icone, ricordarsi di gestire l'eccezione in cui l'assenza di un icona
 * come file non mandi in crash l'intero programma
 */
@SuppressWarnings("serial")
public class Memorem extends JFrame{
	
	private Timer timer;
	private InnerClock orologio;
	private EzioAuditore listener;
	private DesmondMiles watcher;
	/*
	 * visualHandler gestisce il funzionamento del panel_2 (switch tra InternalFrame e CalendarFrame)
	 * dataHandler gestisce la scelta della durata dei memo da visualizzare
	 * innerVisualHandler gestisce la scelta del tipo di memo da visualizzare (scaduti o attivi)
	 */
	private Abbottonatore visualHandler, dataHandler, innerVisualHandler;
	private JMenuItem mntmEsci,mntmNuova,mntmGuest,mntmSalva,mntmLogin,mntmLogout,mntmAggiungi,mntmCancella,mntmReset,mntmRimuoviU,mntmAggiungiU,mntmModPass,mntmStatistiche;
	private JMenu mnNuovo,mnStrumenti,mnVisualizza,mnUtente,mnRimuoviU;
	private JCheckBox mntmHints;
	private JList<String> utenti;
	private Keeper k;
	private JPanel panel;
	private JInternalFrame panel_2;
	private CalendarFrame calendar;
	private ClassicFrame classic;
	private Popup p;
	private PopItem completa,elimina,modifica,one,three,seven,custom;
	private LoginDialog logD;
	private DeferDialog rinviaD;
	private ModPassDialog modPass;
	private JProgressBar progressBar;
	private SlidingTextPanel scorrevole;
	private JRadioButtonMenuItem classicRadio,calendarRadio;
	private JRadioButtonMenuItem[] vF=new JRadioButtonMenuItem[2],dF=new JRadioButtonMenuItem[5];
	private JCheckBoxMenuItem[] pF=new JCheckBoxMenuItem[3];
	private JButton okButton;
	private JFileChooser jfc;
	private JButton iconButton;
	//private Image sfondo;
	
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
			else if(nome.equals("data") || nome.equals("inner")){
				Memorem.this.createQuery();
			}
		}
	}
	
	private class DesmondMiles extends WindowAdapter{
		
		public void windowDeactivated(WindowEvent arg0) {
			
			/*SE ARRIVIAMO QUI POSSIAMO ANCHE AVER PREMUTO IL TASTO PERSONALIZZA
			 *PRIMA DI MUOVERSI BISOGNA CAPIRE SE È UNA MODIFICA O UNA PERSONALIZZAZIONE DI UN MEMO
			 *SCADUTO, OVVERO BISOGNA VEDERE SE IL p.getOld() È SCADUTO
			 */
			if(arg0.getSource()==p){	//se arriviamo qui significa che abbiamo ricevuto la notifica di modifica
				if( p.getOk() && p.getModified()){	//e possiamo andare avanti solamente se è stato premuto il tasto ok
					Memo memo=p.getCreated();
					if(k.contains(memo)){
						JOptionPane.showMessageDialog(Memorem.this, "Il memo esiste già");
						return;
					}
					Memo old=p.getOld();
					if(old.isScaduto()){					//si tratta di rinviare un memo scaduto
						if(p.getCreated().getEnd().compareTo(new Data())<0){
							JOptionPane.showMessageDialog(Memorem.this, "Impossibile rinviare. Il memo sarebbe comunque scaduto");
							return;
						}
					}
					mntmSalva.setEnabled(true);
					k.modifica(old, memo);								//il Keeper è stato aggiornato
					calendar.setMemolist(k.getTotalList());				//il Calendar è stato aggiornato
					calendar.getCustomRenderer().refresh();
					classic.remove(old);
					//System.out.println("modifica:"+memo.getIcon());
					MemPanel mp=new MemPanel(memo);
					mp.setBridges(p, jfc, iconButton);
					mp.getElimina().addActionListener(listener);
					mp.getCompleta().addActionListener(listener);
					mp.getArchivia().addActionListener(listener);
					mp.getOneDay().addActionListener(listener);
					mp.getThreeDays().addActionListener(listener);
					mp.getOneWeek().addActionListener(listener);
					classic.add(mp);
					mntmSalva.setEnabled(true);
					checkMemos();
				}
				else if(p.getOk() && !p.getModified()){	//qui invece gestiamo la notifica di un aggiunta di memo
					Memo m=p.getCreated();
					if(k.contains(m)){
						JOptionPane.showMessageDialog(Memorem.this, "Il memo esiste già");
						return;
					}
					else if(m.getEnd().compareTo(new Data())<0){
						JOptionPane.showMessageDialog(Memorem.this, "Impossibile aggiungere memo con scadenza nel passato");
						p.dispose();
						return;
					}
					k.add(m);							//il Keeper è stato aggiornato
					calendar.add(m);					//il CalendarFrame è stato aggiornato
					MemPanel mp=new MemPanel(m);
					mp.setBridges(p, jfc, iconButton);
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
					if(visualHandler.getSelected().equals(classicRadio))
						panel_2=classic;
					else
						panel_2=calendar;
					mntmSalva.setEnabled(true);
					panel_2.setVisible(false);
					panel_2.setVisible(true);
					p.dispose();
				}
			}//popup	
			else if(arg0.getSource()==logD){		//si è chiuso il LoginDialog
				if(logD.isOk()){	//è stato premuto il tasto ok
					
					String[] result=logD.result();
					String nick=result[0],pass=result[1];
					String name=null,surname=null;
					char genre='m';
					if(result.length==5){
						name=result[2];
						surname=result[3];
						genre=result[4].charAt(0);
						if(name.length()==0)
							name=null;
						if(surname.length()==0)
							surname=null;
					}
					MemoList mlGuest=new MemoList();								
					if(k.getUser().getNickname().equals("guest")){			//	Qui ci prendiamo i dati dei memo che
						mlGuest=new MemoList();								//  erano stati creati finora dal guest
						for(Memo m: k.getCurrentList())
							mlGuest.add(m,true);
					}
					//if(!(k.getUser().getNickname().equals("none")))
					//	mntmLogout.doClick();
					else if(!mntmHints.isSelected())
							mntmHints.doClick();
					if(logD.isForRegistration() && logD.autologin()){	//abbiamo premuto "Nuova Sessione"
						
						boolean guesty=false;
						if(k.getUser().getNickname().equals("guest"))
							guesty=true;
						if(!guesty)
							mntmLogout.doClick();
						int res=k.signUp(nick,pass,name,surname,genre,true);
						if(res==0){
							if(mlGuest.size()>0){
								for(Memo m:mlGuest){
									k.add(m);
								}
								k.salva();
								System.out.println("Il guest sta per connettersi");
								login();
								return;
							}
							JOptionPane.showMessageDialog(Memorem.this, "Benvenuto , "+k.getUser().toString());
							nuovo();
						}
						else if(res==1)
							JOptionPane.showMessageDialog(Memorem.this,"L'utente esiste già");
						else if(res==2)
							JOptionPane.showMessageDialog(Memorem.this,"Errore durante la creazione dell'utente");
					}
					else if(!logD.isForRegistration() && logD.autologin()){				//abbiamo premuto "login"
						
						int x=k.login(nick, pass);
						if(x==0){
							enableButtons();
							login();
							mntmGuest.setEnabled(false);
							int memos=0;
							MemoList ml=new MemoList();
							for(Memo m: k){
								memos++;
								//System.out.println("login:"+m.getIcon());
								MemPanel mp=new MemPanel(m);
								mp.setBridges(p, jfc, iconButton);
								mp.getElimina().addActionListener(listener);
								mp.getCompleta().addActionListener(listener);
								mp.getArchivia().addActionListener(listener);
								mp.getOneDay().addActionListener(listener);
								mp.getThreeDays().addActionListener(listener);
								mp.getOneWeek().addActionListener(listener);
								if(m.getEnd().compareTo(new Data())<0)
									ml.add(m,true);
								classic.add(mp);
								calendar.add(m);
							}
							if(ml.size()>0){
								rinviaD=new DeferDialog(Memorem.this,k.getPending());
								rinviaD.setVisible(true);
								rinviaD.addWindowListener(watcher);
								Toolkit.getDefaultToolkit().beep();
							}
							//panel_2=classic;
							if(memos>0)
								classic.setVisible(true);
							else{
								panel_2.setVisible(true);
								classic.setVisible(false);
							}
						}
						else{
							if(x==1)
								JOptionPane.showMessageDialog(Memorem.this,"L'utente non esiste");
							else
								JOptionPane.showMessageDialog(Memorem.this,"Password sbagliata");
						}
					}
					else if(logD.isForRegistration() && !logD.autologin()){		//semplice aggiunta di un utente, senza autologin
					
						k.signUp(nick, pass, name, surname, genre, false);
						mntmGuest.setEnabled(false);
						utenti=new JList<String>(k.userList());
					}
				}
			}//loginDialog
			else if(arg0.getSource()==rinviaD){
				System.out.println("YAHOOOOO");
				MemoList nuova=rinviaD.getHandled();
				MemoList vecchia=k.getPending();
				for(Memo mm:nuova){
					if(mm.isCompleted())
						Memorem.this.completa(mm, true);
					else{
						Memo vecchio=vecchia.get(mm.description(), mm.priority(), mm.getEnd());
						if(vecchio!=null){
							if(vecchio.identici(mm))
								Memorem.this.completa(mm,false);
							else
								Memorem.this.modifica(vecchio,mm);
						}
					}
				}//for
			}//rinviaD
		}//windowDeactivated
	}//DesmondMiles
	/**
	 * Classe private che funge da ActionListener per i pulsanti del Frame
	 * @author fabrizio
	 *
	 */
	private class EzioAuditore implements ActionListener{
		
		
		public void actionPerformed(ActionEvent evt){
			
			if(evt.getSource()==mntmEsci){					//premuto il tasto esci
				panel_2.dispose();
				Memorem.this.dispose();
				System.exit(0);
			}
			else if(evt.getSource()==mntmNuova){			//premuto il tasto nuovo
				logD=new LoginDialog(Memorem.this,true,true);
				logD.addWindowListener(watcher);
				logD.setVisible(true);
			}
			else if(evt.getSource()==mntmGuest){
				k=new Keeper();
				k.login("guest", null);
				classic.setVisible(false);
				classic.setGuestInterface(true);
				panel.remove(panel_2);
				panel_2=classic;
				panel.add(panel_2, "cell 0 0 7 26,grow");
				p=new Popup(Memorem.this,false);
				p.addWindowListener(watcher);
				orologio=new InnerClock();
				timer=new Timer();
				//panel_2=classic;
				calendar=new CalendarFrame(new MemoList());
				calendar.setListener(p);
				timer.schedule(orologio, 0,5000);
				progressBar.setValue(0);
				mntmLogin.setEnabled(false);
				mntmLogout.setEnabled(true);
				classic.setVisible(true);
				panel_2.setVisible(true);
				JOptionPane.showMessageDialog(Memorem.this, "Buongiorno, ospite. Prova il nostro programma e, se ti piace, iscriviti");
				interfacciaGuest();
			}
			else if(evt.getSource()==mntmLogin){			//premuto il tasto login
				k=new Keeper();
				panel.add(panel_2, "cell 0 0 7 26,grow");
				logD=new LoginDialog(Memorem.this,false,true);
				logD.addWindowListener(watcher);
				logD.setVisible(true);
			}
			else if(evt.getSource()==mntmLogout){			//premuto il tasto logout
				k.logout();
				classic.clearMemos();
				calendar.clear();
				classic.setVisible(false);
				panel.remove(panel_2);
				mntmLogin.setEnabled(true);
				mntmGuest.setEnabled(true);
				progressBar.setValue(0);
				setButtonsAtStart();
			}
			else if(evt.getSource()==mntmSalva){			//premuto il tasto salva
				if(k.getUser().getNickname().equals("guest")){
					int ans=JOptionPane.showConfirmDialog(Memorem.this, "Per salvare le tue modifiche devi essere iscritto. Vuoi iscriverti ora?");
					if(ans==JOptionPane.YES_OPTION){
						logD=new LoginDialog(Memorem.this,true,true);
						logD.addWindowListener(watcher);
						logD.setVisible(true);
					}
				}
				else
					Memorem.this.salva();
			}
			else if(evt.getSource()==mntmAggiungi)			//premuto il tasto aggiungi
				Memorem.this.aggiungi();
			else if(evt.getSource()==mntmCancella)			//premuto il tasto cancella
				Memorem.this.cancella();
			else if(evt.getSource()==mntmReset)				//resetta il database
				Memorem.this.reset();
			else if(evt.getSource()==mntmRimuoviU)			//rimuove utente
				Memorem.this.removeUser(k.getUser().getNickname());
			else if(evt.getSource()==mntmAggiungiU){		//aggiunge un utente
				logD=new LoginDialog(Memorem.this,true,false);
				logD.addWindowListener(watcher);
				logD.setVisible(true);
			}
			else if(evt.getSource()==mntmModPass){
				
				modPass=new ModPassDialog(okButton);
				modPass.setVisible(true);
			}
			else if(evt.getSource()==okButton){
				
				String[] passwords=modPass.getPasswords();
				int res=k.modificaPassword(passwords[0],passwords[1]);
				if(res==0)
					modPass.dispose();
				else if(res==2)
					JOptionPane.showMessageDialog(Memorem.this, "La password dell'utente è errata");
				else if(res==3)
					JOptionPane.showMessageDialog(Memorem.this, "La nuova password deve essere diversa");
				else
					JOptionPane.showMessageDialog(Memorem.this, "Qualcosa è andato storto durante la modifica");
			}
			else if(evt.getSource()==mntmHints)
				Memorem.this.attivaHints();
			else if(evt.getSource()==mntmStatistiche)
				new StatPanel(k).setVisible(true);
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
					p.setVisible(true);
				}
			}//PopItems
			else if(evt.getSource()==iconButton){
				
				MemPanel mp=(MemPanel)jfc.getAccessory();
				MemPanel x=classic.get(mp.getMemo());
				//System.out.println(x.getMemo());
				if(x!=null && x.iconaCambiata()){
					//System.out.println("Abbiamo catturato "+mp.getMemo().description());
					String relativePath=jfc.getSelectedFile().getPath();
					StringTokenizer st=new StringTokenizer(relativePath,"//", false);
					while(st.hasMoreTokens())
						relativePath=st.nextToken();
					Memo nuovo=mp.getMemo();
					Memo vecchio=new Memo(x.getMemo());
					nuovo.setIcon(relativePath);
					//System.out.println("nuovo:"+nuovo.getIcon());
					//System.out.println("vecchio:"+vecchio.getIcon());
					k.modifica(vecchio, nuovo);
					mntmSalva.setEnabled(true);
				}
			}
		}
	}//EzioAuditore
	
	/**
	 * Lancia il programma
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
	 * Costruttore di default
	 */
	public Memorem() {
		
		setUndecorated(true);									//rimuove la title bar di Windows
		setTitle("MemoRem 1.0");								//titolo del frame
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		listener=new EzioAuditore();							// ascoltatore pulsanti
		watcher=new DesmondMiles();								// ascoltatore finestra
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
		menuBar.add(mnVisualizza);
		manageVisualizzaButtons();
		
		
		mnUtente = new JMenu("Utente");
		menuBar.add(mnUtente);
		manageUtenteButtons();
		
		panel = new ColoredPanel("./src/graphic/wallpapers/wall5.jpg");
		JPanel PBPanel=new JPanel();
		setInternalFrameAtStart();
		classic=new ClassicFrame(vF,pF,dF);
		panel_2=classic;
		panel.setLayout(new MigLayout("", "[][grow][grow][][][][grow]", "[grow][grow][grow][grow][grow][][][][][][][][][][][][][][][][][][][][][][grow][]"));
		setContentPane(panel);
		panel.add(panel_2, "cell 0 0 7 26,grow");
		
		PBPanel.setLayout(new BorderLayout(0, 0));
		progressBar = new JProgressBar();
		scorrevole=new SlidingTextPanel();
		//progressBar.setForeground(new Color(50, 205, 50));
		progressBar.setForeground(new Color(150,200,255));
		progressBar.setStringPainted(true);
		scorrevole.setVisible(false);
		scorrevole.setAlignmentY(Component.CENTER_ALIGNMENT);
		PBPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		PBPanel.add(progressBar,BorderLayout.NORTH);
		PBPanel.add(scorrevole,BorderLayout.CENTER);
		PBPanel.setOpaque(false);
		panel.add(PBPanel, "cell 0 26 7 1,growx,aligny bottom");
		okButton=new JButton();
		iconButton=new JButton();
		iconButton.addActionListener(listener); // QUESTI DUE BOTTONI IN REALTA'
		okButton.addActionListener(listener);   // SONO INVISIBILI
		okButton.setActionCommand("Ok");
		setButtonsAtStart();
		//UIManager UI=new UIManager();
		//UI.put("OptionPane.background",new Color(255,255,150));
		//UI.put("Panel.background", new Color(255,255,150));
		//sfondo=Toolkit.getDefaultToolkit().createImage("/home/fabrizio/workspace/MemoRem/src/graphic/wallpapers/wall3");
		
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
		//panel_2.setVisible(false);
		
	}
	/**
	 * prima di inizializzare il programma premendo il tasto "Nuovo" o "Apri", è impossibile utilizzare questi tasti
	 */
	private void setButtonsAtStart(){
		
		mnVisualizza.setEnabled(false);
		mnStrumenti.setEnabled(false);
		mnUtente.setEnabled(false);
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
		if(visualHandler.getSelected().equals(calendarRadio))
			classicRadio.doClick();
	}
	
	/**
	 * Dopo aver premuto il tasto "Nuovo" o "Apri", i tasti possono essere abilitati
	 */
	private void enableButtons(){
		
		mnVisualizza.setEnabled(true);
		mnStrumenti.setEnabled(true);
		mntmLogout.setEnabled(true);
		mnUtente.setEnabled(true);
	}
	
	/**
	 * Dopo aver premuto il tasto "Guest", solo alcuni tasti possono essere abilitati
	 */
	private void interfacciaGuest(){
		
		mnUtente.setEnabled(false);
		mntmSalva.setEnabled(false);
		classic.setGuestInterface(true);
		mntmGuest.setEnabled(false);
		mnStrumenti.setEnabled(true);
		mnVisualizza.setEnabled(true);
	}
	
	/*
	 * Crea il menu File, con relativi bottoni
	 */
	private void manageFileButtons(){
		mntmNuova = new JMenuItem("Nuova sessione");
		mntmNuova.addActionListener(listener);
		mnNuovo.add(mntmNuova);
		
		mntmGuest = new JMenuItem("Entra come guest");
		mntmGuest.addActionListener(listener);
		mnNuovo.add(mntmGuest);
		
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
	
	private void manageVisualizzaButtons(){
		
		classicRadio = new JRadioButtonMenuItem("Classica");
		classicRadio.setSelected(true);
		mnVisualizza.add(classicRadio);
		visualHandler.add(classicRadio);
		
		calendarRadio = new JRadioButtonMenuItem("Calendario");
		mnVisualizza.add(calendarRadio);
		visualHandler.add(calendarRadio);
		mnVisualizza.add(new JSeparator());
		mntmHints=new JCheckBox("Aiuti");
		mntmHints.addActionListener(listener);
		mntmHints.setSelected(false);
		mnVisualizza.add(mntmHints);
	}
	
	private void manageUtenteButtons(){
		
		mntmReset= new JMenuItem("Resetta il database");
		mntmReset.addActionListener(listener);
		mnUtente.add(mntmReset);
		
		mntmRimuoviU= new JMenuItem("Rimuovi Utente");
		mntmRimuoviU.addActionListener(listener);
		mnUtente.add(mntmRimuoviU);
		
		mnRimuoviU=new JMenu("Rimuovi utente:");
		mnUtente.add(mnRimuoviU);
		
		mntmAggiungiU= new JMenuItem("Aggiungi Utente");
		mntmAggiungiU.addActionListener(listener);
		mnUtente.add(mntmAggiungiU);
		
		mntmModPass= new JMenuItem("Modifica password");
		mntmModPass.addActionListener(listener);
		mnUtente.add(mntmModPass);
		mntmStatistiche= new JMenuItem("Statistiche");
		mntmStatistiche.addActionListener(listener);
		mnUtente.add(mntmStatistiche);
		
	}
	
	private void checkMemos(){
		
		if(visualHandler.getSelected().equals(classicRadio)){
			int res=classic.updateMemos();
			if(res!=0){
				rinviaD=new DeferDialog(Memorem.this,k.getPending());
				rinviaD.setVisible(true);
				rinviaD.addWindowListener(watcher);
				Toolkit.getDefaultToolkit().beep();
			}
		}
		else{
			if(!calendar.getMemoList().equals(k.getTotalList())){
				((CalendarFrame)panel_2).getCustomRenderer().refresh();
				//System.out.println("pop down2");
			}
			//System.out.println("pop down2");
			
		}
		panel_2.repaint();
		//panel_2.updateUI();
		//panel_2.setVisible(false);
		//panel_2.setVisible(true);
	}
	
	/**
	 * Cambia la visualizzazione del Memorem dalla modalità classica alla modalità calendario
	 * e viceversa
	 */
	private void switchVisual(){
		
		panel_2.dispose();
		panel.remove(panel_2);
		if(visualHandler.getSelected().equals(classicRadio)){ //passiamo da visualizzazione Calendario a Classica
			panel_2=classic;
			if(k.getTotalList().size()==0)
				classic.setVisible(false);
			else
				classic.setVisible(true);
			System.out.println("da calendario a classico");
		}
		else{												//passiamo da visualizzazione Classica a Calendario
			System.out.println("da classico a calendario");
			panel_2=calendar;
			calendar.setVisible(true);
			PopItem[] items=new PopItem[7];
			items[0]=completa;
			items[1]=modifica;
			items[2]=elimina;
			items[3]=one;
			items[4]=three;
			items[5]=seven;
			items[6]=custom;
			calendar.setPopItems(items);
			calendar.setMemolist(k.getTotalList());
			calendar.getCustomRenderer().refresh();
			panel_2.setVisible(true);
		}
		panel.add(panel_2, "cell 0 0 7 26,grow");
		panel.setVisible(false);
		panel.setVisible(true);
	}
	
	private void attivaHints(){
		
		scorrevole.setString(k.aiuti());
		if(mntmHints.isSelected()){
			scorrevole.setVisible(true);
			scorrevole.start();
		}
		else
			scorrevole.setVisible(false);
	}
	private void createQuery(){
		
		if(k.getUser().getNickname().equals("guest")){
			return;
		}
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
			if(pF[i].isSelected())
				prioritatibus.add(pF[i].getText());
		if(prioritatibus.size()==0){
			classic.mnPriorit.setForeground(Color.RED);
		}
		else
			classic.mnPriorit.setForeground(Color.BLACK);
		k.formaQuery(data, visual, prioritatibus);
		MemoList nuova=k.getRealList();
		calendar.setMemolist(nuova);
		classic.clearMemos();
		for(Memo m:nuova){
			//System.out.println("memorem:"+m.getIcon());
			MemPanel mp=new MemPanel(m);
			mp.setBridges(p, jfc, iconButton);
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
		panel_2.validate();
		panel_2.repaint();
		//panel_2.setVisible(false);
		//panel_2.setVisible(true);
	}
	
	/**
	 * Metodo che viene richiamato quando l'admin si connette o si disconnette, fornendogli comandi 
	 * che gli altri utenti non possono vedere
	 * @param abilita: se true l'admin si è connesso, se false l'admin si è disconnesso
	 */
	private void enableSpecialPowers(boolean abilita){
		
		if(abilita){
			mnRimuoviU.setVisible(true);
			DefaultListModel<String> dlm=new DefaultListModel<String>();
			String[] ics=k.userList();
			for(int i=0 ; i<ics.length ; i++)
				dlm.addElement(ics[i]);
			utenti=new JList<String>(dlm);
			utenti.addMouseListener(new MouseAdapter(){
				
				public void mouseClicked(MouseEvent evt0){
					String uten=utenti.getSelectedValue();
					//System.out.println(utenti.getModel().getElementAt(utenti.getSelectedIndex())+"    size:"+utenti.getModel().getSize());
					((DefaultListModel<String>)utenti.getModel()).remove(utenti.getSelectedIndex());
					utenti.clearSelection();
					k.removeUser(uten);
					
				}
			});
			mnRimuoviU.add(utenti);
			mntmRimuoviU.setVisible(false);
			mntmReset.setVisible(true);
			mntmAggiungiU.setVisible(true);
		}
		else{
			mnRimuoviU.setVisible(false);
			mntmRimuoviU.setVisible(true);
			mntmReset.setVisible(false);
			mntmAggiungiU.setVisible(false);
		}
	}
	/* .___________________________________________________________________________________________		*
	 * |¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯|	*
	 * |						QUI INIZIANO I METODI PER IL FUNZIONAMENTO DEI TASTI DEL MENU	   |	*
	 * |___________________________________________________________________________________________|	*
	 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯		*
	 *																									*/
	
	
	public void nuovo(){
		
		k=new Keeper();
		panel_2=classic;
		jfc=new JFileChooser();
		jfc.addActionListener(listener); //aggiusta ezio
		p=new Popup(this,false);
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
		
		//setInternalFrameAtStart();
		classic.setGuestInterface(false);
		p=new Popup(this,false);
		jfc=new JFileChooser();
		jfc.addActionListener(listener); //aggiusta ezio
		p.addWindowListener(watcher);
		if(k.getUser().getNickname().equals("admin"))
			enableSpecialPowers(true);
		else 
			enableSpecialPowers(false);
		timer=new Timer();
		panel_2=classic;
		orologio=new InnerClock();
		calendar=new CalendarFrame(new MemoList());
		calendar.setListener(p);
		timer.schedule(orologio, 0,5000);
		progressBar.setValue(k.percentualeCompletati());
		mntmLogin.setEnabled(false);
		mntmLogout.setEnabled(true);
		enableButtons();
		//JOptionPane.showMessageDialog(this, "Buongiorno, "+k.getUser().toString());
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
	
	public void modifica(Memo vecchio,Memo nuovo){
		if(k.contains(nuovo)){
			JOptionPane.showMessageDialog(Memorem.this, "Il memo esiste già");
			return;
		}
		else if(nuovo.getEnd().compareTo(new Data())<0){
			JOptionPane.showMessageDialog(Memorem.this, "Impossibile aggiungere memo con scadenza nel passato");
			p.dispose();
			return;
		}
		k.modifica(vecchio, nuovo);							//il Keeper è stato aggiornato
		calendar.remove(vecchio);
		calendar.add(nuovo);					//il CalendarFrame è stato aggiornato
		MemPanel mp=new MemPanel(nuovo);
		mp.setBridges(p, jfc, iconButton);
		if(nuovo.isCompleted()){
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
		classic.remove(vecchio);
		if(visualHandler.getSelected().equals(classicRadio))
			panel_2=classic;
		else
			panel_2=calendar;
		mntmSalva.setEnabled(true);
		panel_2.setVisible(false);
		panel_2.setVisible(true);
	}
	/**
	 * Cancella tutti i memo presenti
	 */
	public void cancella(){
		
		int choice=JOptionPane.showConfirmDialog(this, "I cambiamenti saranno irreversibili. Continuare?");
		if(choice==JOptionPane.OK_OPTION){
			k.clear();
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
	
	public void reset(){
		
		if(!(k.getUser().getNickname().equals("admin"))){
			JOptionPane.showMessageDialog(Memorem.this, "Tu non puoi passare!!!");
			return;
		}
		k.reset();
		classic.clearMemos();
		calendar.setMemolist(new MemoList());
	}
	
	public void removeUser(String utente){
		
		int choice=JOptionPane.OK_OPTION;
		if(k.getUser().getNickname()!="admin")
			choice=JOptionPane.showConfirmDialog(Memorem.this,"Attenzione. Verranno eliminati tutti i dati relativi all'utente '"+utente+"'. Continuare?");
		if(choice==JOptionPane.OK_OPTION){
			k.removeUser(utente);
			if(k.getUser().getNickname()!="admin"){
				mntmLogout.doClick();
				JOptionPane.showMessageDialog(Memorem.this, "Utente '"+utente+"' eliminato");
			}
		}	
	}
	public void elimina(Memo m){
		
		k.remove(m);					//il Keeper è stato aggiornato
		mntmSalva.setEnabled(true);	
		//progressBar.setValue(k.percentualeCompletati());
		classic.remove(m);				//l'InternalFrame è stato aggiornato
		calendar.remove(m);				//il CalendarFrame è stato aggiornato
		if(visualHandler.getSelected().equals(calendarRadio))
			panel_2=calendar;
		else{
			panel_2=classic;
			if(k.getTotalList().size()==0)
				classic.setVisible(false);
		}
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
		}
		if(tasto instanceof MyMenuItem)
			((MyMenuItem)tasto).apochiudira();
		Memo nuovo=new Memo(m.description(),nuova.anno(),nuova.mese(),nuova.giorno(),nuova.ora(),nuova.minuto());
		nuovo.setPriority(m.priority());
		MemPanel mp=new MemPanel(nuovo);
		mp.setBridges(p, jfc, iconButton);
		//panel_2.add(mp);
		k.modifica(m, nuovo);
		//k.add(nuovo);
		classic.add(mp);
		calendar.add(nuovo);
	}
}