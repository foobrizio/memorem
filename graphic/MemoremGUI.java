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
public class MemoremGUI extends JFrame{
	
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
	private JMenuItem mntmEsci,mntmNuova,mntmGuest,mntmSalva,mntmLogin,mntmLogout,mntmAggiungi,mntmCancella,mntmCancellaStorico,mntmEliminaImpegni,mntmReset,mntmRimuoviU,mntmAggiungiU,mntmModPass,mntmStatistiche;
	private JMenu mnNuovo,mnStrumenti,mnVisualizza,mnUtente,mnRimuoviU;
	private JCheckBox mntmHints;
	private JList<Object> utenti;
	private Keeper k;
	private JPanel panel;
	private JInternalFrame panel_2;
	private CalendarFrame calendar;
	private ClassicFrame classic;
	private WelcomeFrame welcome;
	private Popup p;
	private PopItem completa,elimina,modifica,one,three,seven,custom;
	private LoginDialog logD;
	private DeferDialog rinviaD;
	private ModPassDialog modPass;
	private JProgressBar progressBar;
	private SlidingTextPanel scorrevole;
	private JRadioButtonMenuItem classicRadio,calendarRadio;
	private JRadioButtonMenuItem[] vF=new JRadioButtonMenuItem[2];
	private JCheckBoxMenuItem[] pF=new JCheckBoxMenuItem[3];
	private JRadioButtonMenuItem[] dF = new JRadioButtonMenuItem[7];
	private JButton okButton;
	private JFileChooser jfc;
	private JButton iconButton;
	//private Image sfondo;
	
	private class InnerClock extends java.util.TimerTask{
		
		/**
		 * Ogni tot secondi viene richiamato questo metodo
		 */
		public void run(){
					
			MemoremGUI.this.checkMemos();
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
				MemoremGUI.this.switchVisual();
			}
			else if(nome.equals("inner")){
				if(dF[6].isSelected()){
					dF[5].setSelected(true);
					dF[6].setSelected(false);
				}
				else{
					pF[0].setSelected(true);
					pF[1].setSelected(true);
					pF[2].setSelected(true);
					dataHandler.getSelected().setSelected(false);
					dF[2].setSelected(true);
				}
				MemoremGUI.this.createQuery();
			}
			else if(nome.equals("data") || nome.equals("inner")){
				MemoremGUI.this.createQuery();
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
				if( p.isOk() && p.getModified()){	//e possiamo andare avanti solamente se è stato premuto il tasto ok
					Memo memo=p.getCreated();
					Memo old=p.getOld();
					if(!memo.identici(old)){
						MemoremGUI.this.modifica(old,memo);
						checkMemos();
					}
					else
						JOptionPane.showMessageDialog(MemoremGUI.this, "La modifica è nulla");
				}
				else if(p.isOk() && !p.getModified()){	//qui invece gestiamo la notifica di un aggiunta di memo
					Memo m=p.getCreated();
					MemoremGUI.this.aggiungi(m);
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
						for(Memo m: k.getDBList())
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
						((ColoredPanel)panel).setSfondo("./src/graphic/wallpapers/wall5.jpg");
						panel_2.dispose();
						panel.remove(panel_2);
						panel_2=classic;
						panel.setOpaque(false);
						panel.setBackground(new Color(0,0,0,0));
						panel.add(panel_2, "cell 0 0 7 26,grow");
						panel.setVisible(false);
						panel.setVisible(true);
						int res=k.signUp(nick,pass,name,surname,genre,true);
						if(res==0){
							if(mlGuest.size()>0){
								for(Memo m:mlGuest){
									k.add(m);
								}
								k.salva();
								login();
								return;
							}
							JOptionPane.showMessageDialog(MemoremGUI.this, "Benvenuto , "+k.getUser().toString());
							nuovo();
						}
						else if(res==1)
							JOptionPane.showMessageDialog(MemoremGUI.this,"L'utente esiste già");
						else if(res==2)
							JOptionPane.showMessageDialog(MemoremGUI.this,"Errore durante la creazione dell'utente");
					}
					else if(!logD.isForRegistration() && logD.autologin()){				//abbiamo premuto "login"
						
						k.start();
						boolean x=k.login(nick, pass);
						if(x){
							((ColoredPanel)panel).setSfondo("./src/graphic/wallpapers/wall5.jpg");
							panel_2.dispose();
							panel.remove(panel_2);
							panel_2=classic;
							panel.setOpaque(false);
							panel.setBackground(new Color(0,0,0,0));
							panel.add(panel_2, "cell 0 0 7 26,grow");
							panel.setVisible(false);
							panel.setVisible(true);
							enableButtons();
							//faccio il login oram
							login();
							mntmGuest.setEnabled(false);
							int memos=0;
							int cont=0;
							for(Memo m: k.getStandardMemos()){
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
								mp.getOneMonth().addActionListener(listener);
								if(m.getEnd().compareTo(new Data())<0)
									cont++;
								classic.add(mp);
								calendar.add(m);
							}
							if(cont>0){
								System.out.println("Dimensione del mio caos:"+k.getPending().size());
								rinviaD.gestisci(new MemoList(k.getPending()));
							}
							if(memos>0)
								classic.setVisible(true);
							else{
								panel_2.setVisible(true);
								classic.setVisible(false);
							}
						}
					}
					else if(logD.isForRegistration() && !logD.autologin()){		//semplice aggiunta di un utente, senza autologin
					
						k.signUp(nick, pass, name, surname, genre, false);
						mntmGuest.setEnabled(false);
						utenti=new JList<Object>(k.userList().toArray());
							
					}
				}
			}//loginDialog
			else if(arg0.getSource()==rinviaD){
				MemoList nuova=rinviaD.getHandled();//memolist= gestiti
				for(Memo mm:nuova){
					if(mm.isCompleted()){
						System.out.println("completa");
						MemoremGUI.this.completa(mm, true);
					}
					else if(mm.getEnd().compareTo(new Data())>0){	//non è più scaduto
						System.out.println("rinvia");
						Memo old=k.getPending().get(mm.getId());
						if(old!=null){
							Data updated=mm.getEnd();
							int diff=updated.giorno()-new Data().giorno();
							MemoremGUI.this.rinvia(mm,diff,true);
						}
					}
					else if(mm.isNotificato()){
						System.out.println("archivia");
						MemoremGUI.this.completa(mm, false);
					}
					else{
						System.out.println("Come si gestisce questo????");
					}
				}//for
				repaint();
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
				MemoremGUI.this.dispose();
				System.exit(0);
			}
			else if(evt.getSource()==mntmNuova){			//premuto il tasto nuovo
				logD.registrati(true);
			}
			else if(evt.getSource()==mntmGuest){
				k.start();
				k.login("guest", null);
				classic.setVisible(false);
				classic.setGuestInterface(true);
				((ColoredPanel)panel).setSfondo("./src/graphic/wallpapers/wall5.jpg");
				panel_2.dispose();
				panel.remove(panel_2);
				panel_2=classic;
				panel.setOpaque(false);
				panel.setBackground(new Color(0,0,0,0));
				panel.add(panel_2, "cell 0 0 7 26,grow");
				panel.setVisible(false);
				panel.setVisible(true);
				p=new Popup(MemoremGUI.this);
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
				JOptionPane.showMessageDialog(MemoremGUI.this, "Buongiorno, ospite. Prova il nostro programma e, se ti piace, iscriviti");
				interfacciaGuest();
			}
			else if(evt.getSource()==mntmLogin){			//premuto il tasto login

				panel.add(panel_2, "cell 0 0 7 26,grow");
				logD.addWindowListener(watcher);
				logD.login(true);
			}
			else if(evt.getSource()==mntmLogout){			//premuto il tasto logout
				k.logout();
				vF[0].setSelected(true);
				vF[1].setSelected(false);
				dF[0].setSelected(false);
				classicRadio.setSelected(true);
				calendarRadio.setSelected(false);
				classic.clearMemos();
				calendar.clear();
				classic.setVisible(false);
				((ColoredPanel)panel).setSfondo(null);
				panel.setBackground(Color.WHITE);
				panel.setOpaque(true);
				panel.remove(panel_2);
				panel_2=welcome;
				welcome.setVisible(true);
				panel.add(panel_2,"cell 0 0 7 26,grow");
				mntmLogin.setEnabled(true);
				mntmGuest.setEnabled(true);
				progressBar.setValue(0);
				setButtonsAtStart();
			}
			else if(evt.getSource()==mntmSalva){			//premuto il tasto salva
				if(k.getUser().getNickname().equals("guest")){
					int ans=JOptionPane.showConfirmDialog(MemoremGUI.this, "Per salvare le tue modifiche devi essere iscritto. Vuoi iscriverti ora?");
					if(ans==JOptionPane.YES_OPTION){
						logD.registrati(true);
					}
				}
				else
					MemoremGUI.this.salva();
			}
			else if(evt.getSource()==mntmAggiungi){			//premuto il tasto aggiungi
				p.aggiungi();
			}
			else if(evt.getSource()==mntmCancella)			//premuto il tasto cancella
				MemoremGUI.this.cancella();
			else if(evt.getSource()==mntmCancellaStorico)	//premuto il tasto cancellaStorico
				MemoremGUI.this.cancellaStorico();
			else if(evt.getSource()==mntmEliminaImpegni)
				MemoremGUI.this.eliminaImpegni();
			else if(evt.getSource()==mntmReset)				//resetta il database
				MemoremGUI.this.reset();
			else if(evt.getSource()==mntmRimuoviU)			//rimuove utente
				MemoremGUI.this.removeUser(k.getUser().getNickname());
			else if(evt.getSource()==mntmAggiungiU){		//aggiunge un utente
				logD.registrati(false);
			}
			else if(evt.getSource()==mntmModPass){
				
				modPass=new ModPassDialog(okButton,k.getUser());
				modPass.setVisible(true);
			}
			else if(evt.getSource()==okButton){
				
				String[] passwords=modPass.getPasswords();
				int res=k.modificaPassword(passwords[1]);
				if(res==0)
					modPass.dispose();
				else if(res==2)
					JOptionPane.showMessageDialog(MemoremGUI.this, "La password dell'utente è errata");
				else if(res==3)
					JOptionPane.showMessageDialog(MemoremGUI.this, "La nuova password deve essere diversa");
				else
					JOptionPane.showMessageDialog(MemoremGUI.this, "Qualcosa è andato storto durante la modifica");
			}
			else if(evt.getSource()==mntmHints)
				MemoremGUI.this.attivaHints();
			else if(evt.getSource()==mntmStatistiche)
				new StatPanel(k).setVisible(true);
			else if(evt.getSource().equals(pF[0]) || evt.getSource().equals(pF[1]) || evt.getSource().equals(pF[2])){
				if(dF[6].isSelected()){
					dF[2].setSelected(true);
					dF[6].setSelected(false);
				}
				MemoremGUI.this.createQuery();
			}
			else if(evt.getSource() instanceof MyMenuItem){		//qui gestiamo i tasti del MemPanel
				Memo m=((MyMenuItem)evt.getSource()).getMemo();
				if(((MyMenuItem)evt.getSource()).getText().equals("elimina"))	//premuto il tasto elimina
					MemoremGUI.this.elimina(m);
				else if(((MyMenuItem)evt.getSource()).getText().equals("completa"))	//premuto il tasto completa
					MemoremGUI.this.completa(m,true);
				else if(((MyMenuItem)evt.getSource()).getText().equals("archivia")) //premuto il tasto archivia
					MemoremGUI.this.completa(m,false);
				else if(((MyMenuItem)evt.getSource()).getText().equals("1 giorno")){	//premuto il tasto 1 giorno
					MemoremGUI.this.rinvia(m,1,false);
					//((MyMenuItem)evt.getSource()).apochiudira();
				}
				else if(((MyMenuItem)evt.getSource()).getText().equals("3 giorni")){	//premuto il tasto 3 giorni
					MemoremGUI.this.rinvia(m,3,false);
					//((MyMenuItem)evt.getSource()).apochiudira();
				}
				else if(((MyMenuItem)evt.getSource()).getText().equals("1 settimana")){//premuto il tasto 1 settimana
					MemoremGUI.this.rinvia(m,7,false);
					//((MyMenuItem)evt.getSource()).apochiudira();
				}
				else if(((MyMenuItem)evt.getSource()).getText().equals("1 mese")){//premuto il tasto 1 mese
					Data d=new Data();
					MemoremGUI.this.rinvia(m,Data.daysOfMonth(d.anno(), d.mese()),false);
				}
			}
			else if(evt.getSource() instanceof PopItem){		//qui gestiamo i tasti del CalendarFrame
				
				if(evt.getSource()==elimina)
					MemoremGUI.this.elimina(elimina.getMemo());
				else if(evt.getSource()==modifica)
					p.modifica(modifica.getMemo());
				else if(evt.getSource()==completa)
					MemoremGUI.this.completa(completa.getMemo(),true);
				else if(evt.getSource()==one)
					MemoremGUI.this.rinvia(one.getMemo(),1,false);
				else if(evt.getSource()==three)
					MemoremGUI.this.rinvia(three.getMemo(),3,false);
				else if(evt.getSource()==seven)
					MemoremGUI.this.rinvia(seven.getMemo(),7,false);
				else if(evt.getSource()==custom)
					p.modifica(custom.getMemo());
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
					
					k.modifica(vecchio, nuovo);
					mntmSalva.setEnabled(true);
				}
			}
		}
	}//EzioAuditore

	/**
	 * Costruttore di default
	 */
	public MemoremGUI() {
		
		setUndecorated(true);									//rimuove la title bar di Windows
		setTitle("MemoRem 1.0");								//titolo del frame
		k=new Keeper();
		rinviaD=new DeferDialog(this);
		
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		listener=new EzioAuditore();							// ascoltatore pulsanti
		watcher=new DesmondMiles();								// ascoltatore finestra
		visualHandler=new Abbottonatore("visual");				// ascoltatore radioButton per tipo di visualizzazione
		
		JMenuBar menuBar = new JMenuBar();						//la barra dei menu in alto
		setJMenuBar(menuBar);
		rinviaD.addWindowListener(watcher);
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
		
		//Creo il LoginDialog
		logD=new LoginDialog(MemoremGUI.this);
		logD.setUserList(k.userList());
		logD.addWindowListener(watcher);
		panel = new ColoredPanel(null);
		panel.setBackground(Color.WHITE);
		JPanel PBPanel=new JPanel();
		setInternalFrameAtStart();
		classic=new ClassicFrame(vF,pF,dF);
		welcome=new WelcomeFrame(logD);
		welcome.setVisible(true);
		panel_2=welcome;
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
		setFonts();
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
		JRadioButtonMenuItem scaduti=new JRadioButtonMenuItem("Scaduti");
		JRadioButtonMenuItem today=new JRadioButtonMenuItem("Solo oggi");
		JRadioButtonMenuItem week=new JRadioButtonMenuItem("In settimana");
		JRadioButtonMenuItem month=new JRadioButtonMenuItem("In questo mese");
		JRadioButtonMenuItem year=new JRadioButtonMenuItem("In quest'anno");
		JRadioButtonMenuItem always=new JRadioButtonMenuItem("Sempre");
		JRadioButtonMenuItem standard=new JRadioButtonMenuItem("Standard");
		dataHandler=new Abbottonatore("data");
		dataHandler.add(scaduti);
		dataHandler.add(today);
		dataHandler.add(week);
		dataHandler.add(month);
		dataHandler.add(year);
		dataHandler.add(always);
		dataHandler.add(standard);
		dataHandler.setDefaultButton(standard);
		dF[0]=scaduti; dF[1]=today; dF[2]=week; dF[3]=month; dF[4]=year; dF[5]=always; dF[6]=standard; //creiamo dF
		JRadioButtonMenuItem radioAttivi=new JRadioButtonMenuItem("Memo attivi");
		JRadioButtonMenuItem radioScaduti=new JRadioButtonMenuItem("Storico");
		innerVisualHandler=new Abbottonatore("inner");
		innerVisualHandler.add(radioAttivi);
		innerVisualHandler.add(radioScaduti);
		innerVisualHandler.setDefaultButton(radioAttivi);
		vF[0]= radioAttivi; vF[1]= radioScaduti; //creiamo vF
		
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
	
	private void setFonts(){
		
		
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
		
		mntmEliminaImpegni = new JMenuItem("Elimina impegni");
		mntmEliminaImpegni.addActionListener(listener);
		mnStrumenti.add(mntmEliminaImpegni);
		
		mntmCancellaStorico = new JMenuItem("Cancella storico dei memo");
		mntmCancellaStorico.addActionListener(listener);
		mnStrumenti.add(mntmCancellaStorico);
		
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
		
		if(k.getUser().getNickname().equals("none"))
			return;
		int res=k.updateMemos();
		if(res>0){
			classic.updateMemos();
			MemoList ml=new MemoList(k.getPending());
			rinviaD.gestisci(ml);
		}
	}
	/*private void checkMemos(){
		
		if(visualHandler.getSelected().equals(classicRadio)){
			if(vF[0].isSelected() && !dF[0].isSelected()){
				int res=classic.updateMemos();
				if(res>0){
					MemoList ml=new MemoList(k.getPending());
					rinviaD.gestisci(ml);
				}
			}
			else{
				MemoList ml=new MemoList(k.getPending());
				MemoList t=new MemoList();
				for(Memo m:ml)
					if(m.isScaduto()){
						Data d=m.getEnd();
						Data ora=new Data();
						if(d.anno()==ora.anno() && d.mese()==ora.mese() && d.giorno()==ora.giorno() && d.ora()==ora.ora() && d.minuto()-ora.minuto()<1){
							m.setScadenzaNotificata();
							t.add(m);
						}
					}
				if(t.size()>0){
					rinviaD.gestisci(t);
				}
			}
		}
		else{
			if(!calendar.getMemoList().equals(k.getTotalList()))
				((CalendarFrame)panel_2).getCustomRenderer().refresh();
		}
		panel_2.repaint();
	}*/
	
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
		}
		else{												//passiamo da visualizzazione Classica a Calendario
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
		if(innerVisualHandler.getSelected().getText().equals("Storico")){
			visual="scaduti";
			pF[0].setSelected(true);
			pF[1].setSelected(true);
			pF[2].setSelected(true);
			dF[0].setSelected(false);
			dF[1].setSelected(false);
			dF[2].setSelected(false);
			dF[3].setSelected(false);
			dF[4].setSelected(false);
			dF[5].setSelected(true);
			dF[6].setSelected(false);
		}
		String x=dataHandler.getSelected().getText();
		MemoList nuova=new MemoList();
		if(x.equals("Standard")){
			vF[0].setSelected(true);
			vF[1].setSelected(false);
			classic.mnPriorit.setForeground(Color.BLACK);
			pF[0].setSelected(true);
			pF[1].setSelected(true);
			pF[2].setSelected(true);
			data="standard";
		}	
		else if(x.equals("Scaduti"))
			data="pending";
		else if(x.equals("Solo oggi"))
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
		if(prioritatibus.size()==0)
			classic.mnPriorit.setForeground(Color.RED);
		else
			classic.mnPriorit.setForeground(Color.BLACK);
		k.formaQuery(data, visual, prioritatibus);
		nuova=k.getRealList();
		calendar.setMemolist(nuova);
		classic.clearMemos();
		for(Memo m:nuova){
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
			mp.getOneMonth().addActionListener(listener);
		}
		classic.add(mp);	//l' InternalFrame è stato aggiornato
		}
		panel_2.validate();
		panel_2.repaint();
		//panel_2.setVisible(false);
		//panel_2.setVisible(true);
	}
	
	private void setProgressBarToolTip(){
		
		int x=k.percentualeCompletati();
		if(x<=20)
			progressBar.setToolTipText("Per te ci vorrebbe un MemoRem per ricordarti che esiste MemoRem");
		else if(x<=40)
			progressBar.setToolTipText("Mmm francamente sei un pò scarso..");
		else if(x<=60)
			progressBar.setToolTipText("Dai, impegnati un pò di più");
		else if(x<=80)
			progressBar.setToolTipText("Sei andato molto bene, ma puoi ancora fare di più");
		else if(x<99)
			progressBar.setToolTipText("Wow, sei un mostro!!!");
		else
			progressBar.setToolTipText("Non ne hai mancato uno!!! Ma riesci anche a dedicare del tempo per te stesso?");
	}
	
	/**
	 * Metodo che viene richiamato quando l'admin si connette o si disconnette, fornendogli comandi 
	 * che gli altri utenti non possono vedere
	 * @param abilita: se true l'admin si è connesso, se false l'admin si è disconnesso
	 */
	private void enableSpecialPowers(boolean abilita){
		
		if(abilita){
			mnStrumenti.setEnabled(false);
			mnVisualizza.setEnabled(false);
			mnRimuoviU.setVisible(true);
			DefaultListModel<Object> dlm=new DefaultListModel<Object>();
			Object[] ics=k.userList().toArray();
			for(int i=0 ; i<ics.length ; i++)
				dlm.addElement(ics[i]);
			utenti=new JList<Object>(dlm);
			utenti.addMouseListener(new MouseAdapter(){
				
				public void mouseClicked(MouseEvent evt0){
					String uten=utenti.getSelectedValue().toString();
					//System.out.println(utenti.getModel().getElementAt(utenti.getSelectedIndex())+"    size:"+utenti.getModel().getSize());
					((DefaultListModel<Object>)utenti.getModel()).remove(utenti.getSelectedIndex());
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
		
		k.start();
		panel_2=classic;
		jfc=new JFileChooser();
		jfc.addActionListener(listener); //aggiusta ezio
		p=new Popup(this);
		p.addWindowListener(watcher);
		panel_2.setVisible(true);
		timer=new Timer();
		orologio=new InnerClock();
		calendar=new CalendarFrame(k.getTotalList());
		calendar.setListener(p);
		if(visualHandler.getSelected().equals(classicRadio))
			((ClassicFrame)panel_2).clearMemos();
		timer.schedule(orologio, 0,3000);
		enableButtons();
	}
	
	public void login(){
		
		//setInternalFrameAtStart();
		classic.setGuestInterface(false);
		p=new Popup(this);
		jfc=new JFileChooser();
		jfc.addActionListener(listener); //aggiusta ezio
		p.addWindowListener(watcher);
		if(k.getUser().getNickname().equals("admin"))
			enableSpecialPowers(true);
		else{
			enableSpecialPowers(false);
			enableButtons();
		}
		timer=new Timer();
		panel_2=classic;
		orologio=new InnerClock();
		calendar=new CalendarFrame(new MemoList());
		calendar.setListener(p);
		timer.schedule(orologio, 0,1000);
		int x=k.percentualeCompletati();
		progressBar.setValue(x);
		setProgressBarToolTip();
		mntmLogin.setEnabled(false);
		mntmLogout.setEnabled(true);
		//JOptionPane.showMessageDialog(this, "Buongiorno, "+k.getUser().toString());
	}
	
	public void salva(){
		
		k.salva();
		mntmSalva.setEnabled(false);
	}
	
	/**
	 * Aggiunge un nuovo Memo
	 */
	public void aggiungi(Memo m){
		if(k.containsEqualInDB(m)){
			JOptionPane.showMessageDialog(MemoremGUI.this, "Il memo esiste già");
			return;
		}
		else if(m.getEnd().compareTo(new Data())<0){
			JOptionPane.showMessageDialog(MemoremGUI.this, "Impossibile aggiungere memo con scadenza nel passato");
			p.dispose();
			return;
		}
		k.add(m);							//il Keeper è stato aggiornato
		calendar.add(m);					//il CalendarFrame è stato aggiornato
		if(visualHandler.getSelected().equals(classicRadio)){
			boolean[] prior=new boolean[3];
			boolean[] data=new boolean[4];
			for(int i=0;i<3;i++)
				prior[i]=pF[i].isSelected();
			for(int i=0;i<4;i++){
				switch(i){
				case 0: data[i]=dF[1].isSelected(); break; 										//giorno
				case 1: data[i]=(dF[2].isSelected() || dF[6].isSelected())? true: false; break;	//settimana o standard
				case 2: data[i]=dF[3].isSelected(); break;										//mese
				case 3: data[i]=dF[4].isSelected(); break;										//anno
				}
			}
			if(!Keeper.filtriViolati(prior, data, m)){
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
					mp.getOneMonth().addActionListener(listener);
				}	
				classic.add(mp);	//l' InternalFrame è stato aggiornato
				panel_2=classic;
			}
		}
		else
			panel_2=calendar;
		mntmSalva.setEnabled(true);
		panel_2.setVisible(false);
		panel_2.setVisible(true);
	}//aggiungi
	
	public void modifica(Memo vecchio,Memo nuovo){
		
		if(k.containsEqualInDB(nuovo)){
			JOptionPane.showMessageDialog(MemoremGUI.this, "Il memo esiste già");
			return;
		}
		else if(nuovo.getEnd().compareTo(new Data())<0){
			JOptionPane.showMessageDialog(MemoremGUI.this, "Impossibile aggiungere memo con scadenza nel passato");
			p.dispose();
			return;
		}
		mntmSalva.setEnabled(true);
		k.modifica(vecchio, nuovo);							//il Keeper è stato aggiornato
		calendar.remove(vecchio);
		calendar.add(nuovo);					//il CalendarFrame è stato aggiornato
		//ora prima di aggiungere un mempanel al classic bisogna verificare se la nuova data rientri
		//nei filtri preposti
		if(visualHandler.getSelected().equals(classicRadio)){
			boolean[] priors=new boolean[3];
			boolean[] data=new boolean[4];
			for(int i=0;i<3;i++)
				priors[i]=pF[i].isSelected();
			String d=dataHandler.getSelected().getText();
			for(int i=0;i<4;i++)
				data[i]=false;
			switch(d){
			case "Solo oggi" : data[0]=true;
			case "In settimana" : case"Standard" : data[1]=true;
			case "In questo mese": data[2]=true;
			case "In quest'anno" : data[3]=true;
			}
			if(!Keeper.filtriViolati(priors, data, nuovo)){
				System.out.println("I filtri non sono violati");
				classic.modifica(vecchio, nuovo);
			}//filtriviolati
			else
				classic.remove(vecchio);
			panel_2=classic;
		}//visualHandler
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
	public void eliminaImpegni(){
		
		int choice=JOptionPane.showConfirmDialog(this, "I cambiamenti saranno irreversibili. Continuare?");
		if(choice==JOptionPane.YES_OPTION){
			if(k.memoAttivi()==0){
				JOptionPane.showMessageDialog(this, "Non c'è nulla da eliminare");
				return;
			}
			k.eliminaImpegni();
			classic.clearMemos();
			calendar.clear();
		}
	}
	public void cancellaStorico(){
		
		int choice=JOptionPane.showConfirmDialog(this, "I cambiamenti saranno irreversibili. Continuare?");
		if(choice==JOptionPane.YES_OPTION){
			if(k.memoStorici()==0){
				JOptionPane.showMessageDialog(this, "Non c'è nulla da eliminare");
				return;
			}
			vF[0].doClick();
			k.cancellaStorico();
			progressBar.setValue(0);
		}
	}
	
	public void reset(){
		
		if(!(k.getUser().getNickname().equals("admin"))){
			JOptionPane.showMessageDialog(MemoremGUI.this, "Tu non puoi passare!!!");
			return;
		}
		k.reset();
		classic.clearMemos();
		calendar.setMemolist(new MemoList());
	}
	
	public void removeUser(String utente){
		
		int choice=JOptionPane.OK_OPTION;
		if(k.getUser().getNickname()!="admin")
			choice=JOptionPane.showConfirmDialog(MemoremGUI.this,"Attenzione. Verranno eliminati tutti i dati relativi all'utente '"+utente+"'. Continuare?");
		if(choice==JOptionPane.OK_OPTION){
			k.removeUser(utente);
			if(k.getUser().getNickname()!="admin"){
				mntmLogout.doClick();
				JOptionPane.showMessageDialog(MemoremGUI.this, "Utente '"+utente+"' eliminato");
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
		setProgressBarToolTip();
	}
	
	/**
	 * rinvia un memo scaduto del numero di giorni indicato. Ritorna true se la nuova data viola
	 * i filtri preposti dal classic
	 */
	public void rinvia(Memo m,int giorni,boolean alreadyDone){
		
		System.out.println("Rinviamo "+m);
		Data nuova=new Data();
		Memo nuovo=new Memo(m);
		if(!alreadyDone){
			for(int i=0;i<giorni;i++)
				nuova=nuova.domani();
			nuovo.setEnd(nuova);
		}
		mntmSalva.setEnabled(true);
		calendar.remove(m);
		k.modifica(m, nuovo);
		calendar.add(nuovo);
		boolean[] priors=new boolean[3];
		for(int i=0;i<3;i++)
			priors[i]=pF[i].isSelected();
		String x=dataHandler.getSelected().getText();
		boolean[] data=new boolean[4];
		for(int i=0;i<4;i++)
			data[i]=false;
		if(x.equals("Solo oggi"))
			data[0]=true;
		else if(x.equals("In settimana") || x.equals("Standard"))
			data[1]=true;
		else if(x.equals("In questo mese"))
			data[2]=true;
		else if(x.equals("In quest'anno"))
			data[3]=true;
		if(Keeper.filtriViolati(priors, data, nuovo))
			classic.remove(m);
		else
			classic.modifica(m,nuovo);
	}
}