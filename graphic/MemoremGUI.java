package graphic;


import net.miginfocom.swing.MigLayout;	//utilizzato da panel

import java.util.*;
import java.util.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

import graphic.MemPanel.MyMenuItem;
import main.*;
import sound.Sound;
import util.Data;
import util.MemoList;
import util.User;


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
	private JMenuItem mntmEsci,mntmNuova,mntmGuest,mntmSalva,mntmLogin,mntmLogout,mntmAggiungi,mntmCancella;
	private JMenuItem mntmCancellaStorico,mntmEliminaImpegni,mntmReset,mntmRimuoviU,mntmAggiungiU,mntmModPass,mntmStatistiche;
	private JMenu mnFile,mnStrumenti,mnVisualizza,mnUtente,mnRimuoviU;
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
	private ExpiryDialog expiry;
	private UserDialog userD;
	private ModPassDialog modPass;
	private JProgressBar progressBar;
	private SlidingTextPanel scorrevole;
	private JRadioButtonMenuItem classicRadio,calendarRadio;
	//questi sono i contenitori dei bottoni dell'interfaccia classic
	/*
	 *vF[0]=attivi
	 *vF[1]=storico
	 */
	private JRadioButtonMenuItem[] vF=new JRadioButtonMenuItem[2];
	/*
	 * pF[0]=alta
	 * pF[1]=media
	 * pF[2]=bassa
	 */
	private JCheckBoxMenuItem[] pF=new JCheckBoxMenuItem[3];
	/*
	 * dF[0]=scaduti
	 * dF[1]=oggi
	 * dF[2]=settimana
	 * dF[3]=mese
	 * dF[4]=anno
	 * dF[5]=sempre
	 * dF[6]=standard
	 */
	private JRadioButtonMenuItem[] dF = new JRadioButtonMenuItem[7];
	
	//questi sono i bottoni dell'interfaccia classic
	private JCheckBoxMenuItem cbm1,cbm2,cbm3;
	private JRadioButtonMenuItem scaduti,today,week,month,year,always,standard;
	private JRadioButtonMenuItem radioAttivi,radioScaduti;
	
	private JButton okButton;
	private JFileChooser jfc;
	private JButton iconButton;
	private Lang language;
	private StatPanel stats;
	//private Image sfondo;
	private JMenuItem mntmProfilo;
	
	//queste sono le lingue disponibili nel programma
	public enum Lang{ IT, EN, ES, DE};
	
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
		private JRadioButtonMenuItem selected=null;
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
			selected=button;
		}
		
		/**
		 * Ritorna quale bottone all'interno della lista è quello premuto
		 * @return
		 */
		public JRadioButtonMenuItem getSelected(){
			
			return selected;
		}
		
		public void setSelected(JRadioButtonMenuItem jr){
			
			System.out.println(bottoni.length);
			for(int i=0;i<bottoni.length;i++){
				if(bottoni[i]==jr){
					bottoni[i].setSelected(true);
					selected=bottoni[i];
				}
				else if(bottoni[i]!=null)
					bottoni[i].setSelected(false);
			}
		}
		
		public void actionPerformed(ActionEvent evt){
			
			if(evt.getSource()==selected){
				System.out.println("cazzu premi");
				selected.setSelected(true);
				return;
			}
			this.setSelected((JRadioButtonMenuItem)evt.getSource());
		/*	selected.setSelected(false);
			selected=(JRadioButtonMenuItem)evt.getSource();
			selected.setSelected(true);*/
			for(int i=0;i<size;i++){
				if(bottoni[i].equals(selected))
					bottoni[i].setSelected(true);
				else
					bottoni[i].setSelected(false);
			}
			if(nome.equals("visual")){
				MemoremGUI.this.switchVisual();
				MemoremGUI.this.createQuery();		//questo va fatto perchè bisogna aggiornare delle aggiunte fatte
				return;
			}
			else if(nome.equals("inner")){	//se c'è il click vogliamo visualizzare i memo attivi o lo storico
				
				pF[0].setSelected(true);
				pF[1].setSelected(true);
				pF[2].setSelected(true);
				//dataHandler.getSelected().setSelected(false);
				/*if(dF[6].isSelected()){	//standard è selezionato
					dF[5].setSelected(true);
					dF[6].setSelected(false);
				}
				else{	//standard non è selezionato
					System.out.println("ecco il problema");
					
					dataHandler.getSelected().setSelected(false);
					dF[2].setSelected(true);
				}*/
				if(vF[0].isSelected()){ //vogliamo visualizzare gli attivi
					//preselezioniamo "standard" come tasto di default
					dataHandler.setSelected(dF[6]);
					dF[0].setVisible(true);
					dF[6].setSelected(true);//standard
					dF[6].setVisible(true);
				}
				else{//se vogliamo visualizzare lo storico
					//preselezioniamo "sempre" come tasto di default
					dataHandler.setSelected(dF[5]);
					dF[0].setVisible(false);//nello storico il tasto "scaduti" non ha senso
					dF[6].setVisible(false);//nello storico il tasto "standard" non ha funzionalità
				}
			}
			MemoremGUI.this.createQuery();
			repaint();
		}
	}
	
	/**
	 * DesmondMiles si occupa di controllare le varie finestre dialog di supporto che si aprono,
	 * entra in funziona quando una di esse viene disattivata.
	 * @author fabrizio
	 *
	 */
	private class DesmondMiles extends WindowAdapter{
		
		public void windowDeactivated(WindowEvent arg0) {
			

			if(arg0.getSource()==p){	//se arriviamo qui significa che abbiamo ricevuto la notifica di modifica
				/*SE ARRIVIAMO QUI POSSIAMO ANCHE AVER PREMUTO IL TASTO PERSONALIZZA
				 *PRIMA DI MUOVERSI BISOGNA CAPIRE SE È UNA MODIFICA O UNA PERSONALIZZAZIONE DI UN MEMO
				 *SCADUTO, OVVERO BISOGNA VEDERE SE IL p.getOld() È SCADUTO
				 */
				if( p.isOk() && p.getModified()){	//e possiamo andare avanti solamente se è stato premuto il tasto ok
					Memo memo=p.getCreated();
					Memo old=p.getOld();
					if(memo.identici(old))
						if(memo.priority()!=old.priority()){
							MemoremGUI.this.modifica(old,memo);
							checkMemos();
						}
					else{
						if(language==Lang.IT)
							JOptionPane.showMessageDialog(MemoremGUI.this, "La modifica è nulla");
						else
							JOptionPane.showMessageDialog(MemoremGUI.this, "Nothing was modified");
					}
				}
				else if(p.isOk() && !p.getModified()){	//qui invece gestiamo la notifica di un aggiunta di memo
					Memo m=p.getCreated();
					MemoremGUI.this.aggiungi(m);
					p.dispose();
				}
			}//popup	
			
			else if(arg0.getSource()==logD){		//si è chiuso il LoginDialog
				
				if(logD.isOk()){	//è stato premuto il tasto ok, quindi c'è qualcosa da fare
					String[] result=logD.result();
					String nick=result[0],pass=result[1];
					String name=null,surname=null;
					String language=null;
					char genre='m';
					if(result.length==6){
						name=result[2];
						surname=result[3];
						genre=result[4].charAt(0);
						language=result[5];
						if(name.length()==0)
							name=null;
						if(surname.length()==0)
							surname=null;
					}
					MemoList mlGuest=new MemoList();								
					if(k.getUser().isGuest()){			//	Qui ci prendiamo i dati dei memo che
						mlGuest=new MemoList();								//  erano stati creati finora dal guest
						for(Memo m: k.getTotalList())
							mlGuest.add(m,true);
					}
					if(logD.isForRegistration() && logD.autologin()){	//abbiamo premuto "Nuova Sessione"
						
						boolean guesty=false;
						if(k.getUser().isGuest())
							guesty=true;
						if(!guesty)
							mntmLogout.doClick();
						((ColoredPanel)panel).setSfondo("files//wallpapers//wall5.jpg");
						panel_2.dispose();
						panel.remove(panel_2);
						panel_2=classic;
						panel.setOpaque(false);
						panel.setBackground(new Color(0,0,0,0));
						panel.add(panel_2, "cell 0 0 7 26,grow");
						panel.setVisible(false);
						panel.setVisible(true);
						k.start();
						int res=k.signUp(nick,pass,name,surname,genre, language,true);
						if(res==0){
							if(mlGuest.size()>0){
								for(Memo m:mlGuest){
									k.add(m);
								}
								k.salva();
								login();
								return;
							}
							if(language.equals("it"))
								JOptionPane.showMessageDialog(MemoremGUI.this, "Benvenuto , "+k.getUser().toString());
							else if(language.equals("es"))
								JOptionPane.showMessageDialog(MemoremGUI.this, "Bienvenido, "+k.getUser().toString());
							else if(language.equals("de"))
								JOptionPane.showMessageDialog(MemoremGUI.this, "Willkommen, "+k.getUser().toString());
							else
								JOptionPane.showMessageDialog(MemoremGUI.this, "Welcome, "+k.getUser().toString());			
							nuovo();
						}
						else if(res==1){
							if(language.equals("it"))
								JOptionPane.showMessageDialog(MemoremGUI.this,"L'utente esiste già");
							else if(language.equals("es"))
								JOptionPane.showMessageDialog(MemoremGUI.this, "Ya existe el usuario");
							else if(language.equals("de"))
								JOptionPane.showMessageDialog(MemoremGUI.this, "Der Benutzer bereits vorhanden");
							else
								JOptionPane.showMessageDialog(MemoremGUI.this, "This user already exists");
						}
						else if(res==2){
							if(language.equals("it"))
								JOptionPane.showMessageDialog(MemoremGUI.this,"Errore durante la creazione dell'utente");
							else if(language.equals("es"))
								JOptionPane.showMessageDialog(MemoremGUI.this, "Error durante la creación de usuario");
							else if(language.equals("de"))
								JOptionPane.showMessageDialog(MemoremGUI.this, "Fehler bei der Erstellung der Benutzer");
							else
								JOptionPane.showMessageDialog(MemoremGUI.this,"Error during registration");
						}
					}
					else if(!logD.isForRegistration() && logD.autologin()){				//abbiamo premuto "login"
						
						k.start();
						boolean x=k.login(nick, pass);
						if(!mntmHints.isSelected())
							mntmHints.doClick();
						Lang lang=k.getUser().getLingua();
						if(x){
							((ColoredPanel)panel).setSfondo("files//wallpapers//wall5.jpg");
							panel_2.dispose();
							panel.remove(panel_2);
							panel_2=classic;
							panel.setOpaque(false);
							panel.setBackground(new Color(0,0,0,0));
							panel.add(panel_2, "cell 0 0 7 26,grow");
							panel.setVisible(false);
							panel.setVisible(true);
							enableButtons();
							//faccio il login ora
							login();
							mntmGuest.setEnabled(false);
							int memos=0;
							int cont=0;
							for(Memo m: k.getStandardMemos()){
								memos++;
								Data d=m.getEnd();
								d.setLanguage(lang);
								m.setEnd(d);
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
							}
							calendar.setMemolist(k.getActiveList());
							if(cont>0)
								rinviaD.gestisci(new MemoList(k.getPending()));
							setLanguage(lang);
							if(memos>0)
								classic.setVisible(true);
							else{
								panel_2.setVisible(true);
								if(k.memoTotali()==0){
									classic.setVisible(false);
									System.out.println("Non hai memo");
								}
								else
									classic.setVisible(true);
							}
						}
					}
					else if(logD.isForRegistration() && !logD.autologin()){		//semplice aggiunta di un utente, senza autologin
					
						k.signUp(nick, pass, name, surname, genre, language, false);
						mntmGuest.setEnabled(false);
						utenti=new JList<Object>(k.userList().toArray());
							
					}
				}
			}//loginDialog
			else if(arg0.getSource()==rinviaD){
				MemoList nuova=rinviaD.getHandled();//memolist= gestiti
				
				for(Memo mm:nuova){
					if(mm.isCompleted())
						MemoremGUI.this.completa(mm, true);
					else if(mm.getEnd().compareTo(new Data())>0){	//non è più scaduto
						Memo old=k.getPending().get(mm.getId());
						if(old!=null){
							Data updated=mm.getEnd();
							int diff=updated.giorno()-new Data().giorno();
							MemoremGUI.this.rinvia(mm,diff,true);
						}
					}
					else if(mm.isNotificato())
						MemoremGUI.this.completa(mm, false);
				}//for
					
				repaint();
			}//rinviaD
			else if(arg0.getSource()==userD){
				
				if(userD.isModified() && User.sonoDiversi(k.getUser(), userD.getUser())){
					boolean eheh=false;
					if(k.getUser().getLingua()!=userD.getUser().getLingua())
						eheh=true;
					k.modificaUtente(k.getUser(),userD.getUser());
					if(eheh)//il cambio lingua va fatto solo dopo il cambio delle informazioni utente
						MemoremGUI.this.setLanguage(userD.getUser().getLingua());
				}
			}
		}//windowDeactivated
	}//DesmondMiles
	/**
	 * EzioAuditore è un actionListener di tutti i principali bottoni del programma
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
				((ColoredPanel)panel).setSfondo("files//wallpapers//wall5.jpg");
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
				JOptionPane.showMessageDialog(MemoremGUI.this, "Welcome, guest. Try our software, and sign up if you like it");
				interfacciaGuest();
			}
			else if(evt.getSource()==mntmLogin){			//premuto il tasto login

				panel.add(panel_2, "cell 0 0 7 26,grow");
				logD.addWindowListener(watcher);
				logD.login(true);
			}
			else if(evt.getSource()==mntmLogout)			//premuto il tasto logout
				MemoremGUI.this.logout();
			else if(evt.getSource()==mntmSalva){			//premuto il tasto salva
				if(k.getUser().getNickname().equals("guest")){
					int ans=JOptionPane.showConfirmDialog(MemoremGUI.this, "You must be registered to save your memos. Would you subscribe now?");
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
			else if(evt.getSource()==mntmProfilo)
				userD.visualizza(k.getUser());
			else if(evt.getSource()==mntmReset)				//resetta il database
				MemoremGUI.this.reset();
			else if(evt.getSource()==mntmRimuoviU)			//rimuove utente
				MemoremGUI.this.removeUser(k.getUser().getNickname());
			else if(evt.getSource()==mntmAggiungiU){		//aggiunge un utente
				logD.registrati(false);
			}
			else if(evt.getSource()==mntmModPass){
				
				modPass=new ModPassDialog(okButton,k.getUser());
				modPass.setLanguage(language);
				modPass.setVisible(true);
			}
			else if(evt.getSource()==okButton){
				
				String[] passwords=modPass.getPasswords();
				int res=k.modificaPassword(passwords[1]);
				if(res==0)
					modPass.dispose();
			}
			else if(evt.getSource()==mntmHints)
				MemoremGUI.this.attivaHints();
			else if(evt.getSource()==mntmStatistiche){
				stats=new StatPanel(k);
				stats.setLanguage(language);
				stats.setVisible(true);
			}
			else if(evt.getSource().equals(pF[0]) || evt.getSource().equals(pF[1]) || evt.getSource().equals(pF[2])){
				/*if(dF[6].isSelected()){
					dF[2].setSelected(true);
					dF[6].setSelected(false);
				}*/
				MemoremGUI.this.createQuery();
			}
			else if(evt.getSource() instanceof MyMenuItem){		//qui gestiamo i tasti del MemPanel
				Memo m=((MyMenuItem)evt.getSource()).getMemo();
				String name=((MyMenuItem)evt.getSource()).getName();
				if(name.equals("elimina"))	//premuto il tasto elimina
					MemoremGUI.this.elimina(m);
				else if(name.equals("completa"))	//premuto il tasto completa
					MemoremGUI.this.completa(m,true);
				else if(name.equals("archivia")) //premuto il tasto archivia
					MemoremGUI.this.completa(m,false);
				else if(name.equals("1 giorno"))	//premuto il tasto 1 giorno
					MemoremGUI.this.rinvia(m,1,false);
				else if(name.equals("3 giorni"))	//premuto il tasto 3 giorni
					MemoremGUI.this.rinvia(m,3,false);
				else if(name.equals("1 settimana"))//premuto il tasto 1 settimana
					MemoremGUI.this.rinvia(m,7,false);
				else if(name.equals("1 mese")){//premuto il tasto 1 mese
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
		int errore=k.getErrore();
		ConfDialog cf= new ConfDialog(this);
		while(errore!=0){
			errore=k.getErrore();
			boolean adm=false;
			if(errore==1){
				JOptionPane.showMessageDialog(this, "Il file di configurazione non è stato trovato");
				this.dispose();
			}
			else if(errore==4){
				cf.configuraAdmin();
				adm=true;
			}
			else if(errore==5)
				cf.configuraDatabase(new File("files//.database.properties"));
			if(adm){
				String pass=cf.getAdminPass();
				k.createAdmin(pass);
			}
			k=new Keeper();
		}
		rinviaD=new DeferDialog(this);
		expiry=new ExpiryDialog(this);
		userD=new UserDialog(this);
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		listener=new EzioAuditore();							// ascoltatore pulsanti
		watcher=new DesmondMiles();								// ascoltatore finestra
		visualHandler=new Abbottonatore("visual");				// ascoltatore radioButton per tipo di visualizzazione
		this.language=Lang.EN;									// impostazione lingua di default
		JMenuBar menuBar = new JMenuBar();						//la barra dei menu in alto
		setJMenuBar(menuBar);
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		manageFileButtons();		//qui abbiamo creato e gestito i bottini del 1° JMenu
											
		mnStrumenti = new JMenu("Tools");
		menuBar.add(mnStrumenti);
		manageStrumentiButtons();	//qui abbiamo creato e gestito i bottoni del 2° JMenu
		
		mnVisualizza= new JMenu("View");
		menuBar.add(mnVisualizza);
		manageVisualizzaButtons();
		
		mnUtente = new JMenu("User");
		menuBar.add(mnUtente);
		manageUtenteButtons();
		
		//Creo il LoginDialog
		logD=new LoginDialog(MemoremGUI.this);
		logD.setUserList(k.userList());
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
		assegnaListeners();
		setVisible(true);
		//sfondo=Toolkit.getDefaultToolkit().createImage("/home/fabrizio/workspace/MemoRem/src/graphic/wallpapers/wall3");
		
	}
	
	private void assegnaListeners(){
		
		rinviaD.addWindowListener(watcher);
		logD.addWindowListener(watcher);
		userD.addWindowListener(watcher);
		completa.addActionListener(listener);
		elimina.addActionListener(listener);
		modifica.addActionListener(listener);
		one.addActionListener(listener);
		three.addActionListener(listener);
		seven.addActionListener(listener);
		custom.addActionListener(listener);
		
	}
	private void attivaHints(){
		
		if(mntmHints.isSelected()){
			scorrevole.setString(k.aiuti());
			scorrevole.setVisible(true);
			scorrevole.start();
		}
		else
			scorrevole.setVisible(false);
	}

	private void checkMemos(){
		
		if(k.getUser().getNickname().equals("none"))
			return;
		int res=k.updateMemos();
		if(res>0){
			classic.updateMemos();
			MemoList ml=new MemoList(k.getPending());
			Sound.playExpired();
			rinviaD.gestisci(ml);
		}
		else{
			int exp=k.analyzeMemos();
			if(exp>0){
				Sound.playAdvice();
				expiry.visualizza(k.getTodayMemos(), language);
			}
		}
	}

	private void createQuery(){
		
		if(k.getUser().getNickname().equals("guest")){
			return;
		}
		String data="always";
		String visual="attivi";
		if(innerVisualHandler.getSelected().getName().equals("radioScaduti"))
			visual="scaduti";
		String x=dataHandler.getSelected().getName();
		MemoList nuova=new MemoList();
		if(x.equals("standard"))
			data="standard";
		else if(x.equals("scaduti"))
			data="pending";
		else if(x.equals("today"))
			data="oggi";
		else if(x.equals("week"))
			data="week";
		else if(x.equals("month"))
			data="month";
		else if(x.equals("year"))
			data="year";
		LinkedList<String> prioritatibus=new LinkedList<String>();
		for(int i=0;i<3;i++)
			if(pF[i].isSelected())
				prioritatibus.add(pF[i].getText());
		if(prioritatibus.size()==0)
			classic.mnPriorit.setForeground(Color.RED);
		else
			classic.mnPriorit.setForeground(Color.BLACK);
		nuova=k.formaQuery(data, visual, prioritatibus);
		//calendar.setMemolist(nuova);
		classic.clearMemos();
		for(Memo m:nuova){
			Data d=m.getEnd();
			d.setLanguage(k.getUser().getLingua());
			m.setEnd(d);
			MemPanel mp=new MemPanel(m);
			mp.setBridges(p, jfc, iconButton);
			mp.setLanguage(k.getUser().getLingua());
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
			HashSet<User> users=k.userList();
			for(User s:users)
				if(s.getNickname().equals("admin")){
					users.remove(s);
					break;
				}
			Object[] ics=users.toArray();
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

	/**
	 * Dopo aver premuto il tasto "Guest", solo alcuni tasti possono essere abilitati
	 */
	private void interfacciaGuest(){
		
		mnUtente.setEnabled(false);
		mntmSalva.setEnabled(false);
		classic.setGuestInterface(true);
		mntmNuova.setEnabled(false);
		mntmGuest.setEnabled(false);
		mnStrumenti.setEnabled(true);
		mnVisualizza.setEnabled(true);
	}

	/*
	 * Crea il menu File, con relativi bottoni
	 */
	private void manageFileButtons(){
		mntmNuova = new JMenuItem("New session");
		mntmNuova.addActionListener(listener);
		mnFile.add(mntmNuova);
		
		mntmGuest = new JMenuItem("Enter as guest");
		mntmGuest.addActionListener(listener);
		mnFile.add(mntmGuest);
		
		mntmLogin = new JMenuItem("Login");
		mntmLogin.addActionListener(listener);
		mnFile.add(mntmLogin);
		
		mntmLogout = new JMenuItem("Logout");
		mntmLogout.addActionListener(listener);
		mnFile.add(mntmLogout);
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		
		mntmSalva = new JMenuItem("Save");
		mntmSalva.addActionListener(listener);
		mnFile.add(mntmSalva);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		mntmEsci = new JMenuItem("Exit");
		mntmEsci.addActionListener(listener);
		mnFile.add(mntmEsci);
		
		
	}

	private void manageStrumentiButtons(){
		
		mntmAggiungi = new JMenuItem("Add");
		mntmAggiungi.addActionListener(listener);
		mnStrumenti.add(mntmAggiungi);
		
		JSeparator separator = new JSeparator();
		mnStrumenti.add(separator);
		
		mntmCancella = new JMenuItem("Clear");
		mntmCancella.addActionListener(listener);
		mnStrumenti.add(mntmCancella);
		
		mntmEliminaImpegni = new JMenuItem("Delete commitments");
		mntmEliminaImpegni.addActionListener(listener);
		mnStrumenti.add(mntmEliminaImpegni);
		
		mntmCancellaStorico = new JMenuItem("Clear history");
		mntmCancellaStorico.addActionListener(listener);
		mnStrumenti.add(mntmCancellaStorico);
		
	}

	private void manageUtenteButtons(){
		
		mntmProfilo= new JMenuItem("Profile");
		mntmProfilo.addActionListener(listener);
		mnUtente.add(mntmProfilo);
		
		mntmReset= new JMenuItem("Reset database");
		mntmReset.addActionListener(listener);
		mnUtente.add(mntmReset);
		
		mntmRimuoviU= new JMenuItem("Remove user");
		mntmRimuoviU.addActionListener(listener);
		mnUtente.add(mntmRimuoviU);
		
		mnRimuoviU=new JMenu("Remove user:");
		mnUtente.add(mnRimuoviU);
		
		mntmAggiungiU= new JMenuItem("Add user");
		mntmAggiungiU.addActionListener(listener);
		mnUtente.add(mntmAggiungiU);
		
		mntmModPass= new JMenuItem("Modify password");
		mntmModPass.addActionListener(listener);
		mnUtente.add(mntmModPass);
		
		mntmStatistiche= new JMenuItem("Stats");
		mntmStatistiche.addActionListener(listener);
		mnUtente.add(mntmStatistiche);
		
	}

	private void manageVisualizzaButtons(){
		
		classicRadio = new JRadioButtonMenuItem("Classic");
		classicRadio.setSelected(true);
		mnVisualizza.add(classicRadio);
		visualHandler.add(classicRadio);
		
		calendarRadio = new JRadioButtonMenuItem("Calendar");
		mnVisualizza.add(calendarRadio);
		visualHandler.add(calendarRadio);
		mnVisualizza.add(new JSeparator());
		mntmHints=new JCheckBox("Hints");
		mntmHints.addActionListener(listener);
		mntmHints.setSelected(false);
		visualHandler.setDefaultButton(classicRadio);
		mnVisualizza.add(mntmHints);
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
		completa=new PopItem("Complete");
		elimina=new PopItem("Delete");
		modifica=new PopItem("Modify");
		one=new PopItem("One day");
		three=new PopItem("Three days");
		seven=new PopItem("One week");
		custom=new PopItem("Custom");
		if(visualHandler.getSelected().equals(calendarRadio))
			classicRadio.doClick();
	}

	/**
	 * Questo metodo si occupa di settare l'inner bar allo stato di default e con tutti i pulsanti
	 * equipaggiati di ActionListener
	 */
	private void setInternalFrameAtStart(){
		
		cbm1=new JCheckBoxMenuItem("High");
		cbm2=new JCheckBoxMenuItem("Normal");
		cbm3=new JCheckBoxMenuItem("Low");
		cbm1.setName("alta");
		cbm2.setName("media");
		cbm3.setName("bassa");
		cbm1.setSelected(true);
		cbm2.setSelected(true);
		cbm3.setSelected(true);
		cbm1.addActionListener(listener);
		cbm2.addActionListener(listener);
		cbm3.addActionListener(listener);
		pF[0]=cbm1;pF[1]=cbm2;pF[2]=cbm3; 	//creiamo pF
		
		scaduti=new JRadioButtonMenuItem("Expired");
		today=new JRadioButtonMenuItem("Today");
		week=new JRadioButtonMenuItem("Week");
		month=new JRadioButtonMenuItem("In this month");
		year=new JRadioButtonMenuItem("In this year");
		always=new JRadioButtonMenuItem("Always");
		standard=new JRadioButtonMenuItem("Standard");
		
		scaduti.setName("scaduti");
		today.setName("today");
		week.setName("week");
		month.setName("month");
		year.setName("year");
		always.setName("always");
		standard.setName("standard");
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
		
		radioAttivi=new JRadioButtonMenuItem("Active memos");
		radioScaduti=new JRadioButtonMenuItem("History");
		radioScaduti.setSelected(false);
		radioAttivi.setName("radioAttivi");
		radioScaduti.setName("radioScaduti");
		innerVisualHandler=new Abbottonatore("inner");
		innerVisualHandler.add(radioAttivi);
		innerVisualHandler.add(radioScaduti);
		innerVisualHandler.setDefaultButton(radioAttivi);
		vF[0]= radioAttivi; vF[1]= radioScaduti; //creiamo vF	
	}
	
	private void setInternalFrameToolTips(){
		
		if(language==Lang.IT){
			standard.setToolTipText("Visualizza i memo scaduti + quelli in scadenza entro una settimana");
			scaduti.setToolTipText("Visualizza i memo scaduti");
			today.setToolTipText("Visualizza i memo odierni");
			week.setToolTipText("Visualizza i memo in scadenza entro una settimana");
			month.setToolTipText("Visualizza i memo in scadenza nel mese corrente");
			year.setToolTipText("visualizza i memo in scadenza entro un anno");
			always.setToolTipText("Visualizza tutti i memo");
		}
		else if(language==Lang.EN){
			standard.setToolTipText("Shows expired memos and those which will expire within a week");
			scaduti.setToolTipText("Shows expired memos");
			today.setToolTipText("Shows today memos");
			week.setToolTipText("Shows memos which will expire within a week");
			month.setToolTipText("Shows memos which will expire in the current month");
			year.setToolTipText("Shows memos which will expire within a year");
			always.setToolTipText("Shows all memos");
		}
		else if(language==Lang.DE){
			standard.setToolTipText("Es zeigt abgelaufenen memos und solche, die innerhalb einer Woche ablaufen wird");
			scaduti.setToolTipText("Zeigt abgelaufenen memos");
			today.setToolTipText("Zeigt heute memos");
			week.setToolTipText("Zeigt memos, die innerhalb einer woche ablaufen wird");
			month.setToolTipText("Zeigt memos, die im aktuellen monat auslauf");
			year.setToolTipText("zeigt memos, die innerhalb eines jahres auslaufen wird");
			always.setToolTipText("zeigt alle memos");
		}
		else if(language==Lang.ES){
			standard.setToolTipText("Muestra los memos caducados y que expiran en una semana");
			scaduti.setToolTipText("Muestra los memos caducados");
			today.setToolTipText("Muestra los memos de hoy");
			week.setToolTipText("Muestra los memos que expiran en una semana");
			month.setToolTipText("Muestra los memos que expiran en el mes actual");
			year.setToolTipText("Muestra los memos que expirará en un año");
			always.setToolTipText("Muestra todos los memos");
		}
	}

	private void setLanguage(Lang language){
		
		if(this.language.equals(language))
			return;
		if(language==Lang.EN){
			mnStrumenti.setText("Tools");
			mnUtente.setText("User");
			mnVisualizza.setText("View");
			mntmEsci.setText("Exit");
			mntmSalva.setText("Save");
			mntmNuova.setText("New session");
			mntmGuest.setText("Enter as guest");
			mntmAggiungi.setText("Add");
			mntmCancella.setText("Clear");
			mntmCancellaStorico.setText("Clear history");
			mntmEliminaImpegni.setText("Delete committments");
			classicRadio.setText("Classic");	
			calendarRadio.setText("Calendar");
			mntmHints.setText("Hints");
			mntmProfilo.setText("Profile");
			mntmRimuoviU.setText("Remove user");
			mntmModPass.setText("Modify password");
			mntmStatistiche.setText("Stats");
			cbm1.setText("High");
			cbm2.setText("Normal");
			cbm3.setText("Low");
			scaduti.setText("Expired");
			today.setText("Today");
			week.setText("Week");
			month.setText("In this month");
			year.setText("In this year");
			always.setText("Always");
			radioAttivi.setText("Active memos");
			radioScaduti.setText("History");
		}
		else if(language==Lang.IT){
			mnStrumenti.setText("Strumenti");
			mnUtente.setText("Utente");
			mnVisualizza.setText("Visualizzazione");
			mntmEsci.setText("Esci");
			mntmSalva.setText("Salva");
			mntmNuova.setText("Nuova Sessione");
			mntmGuest.setText("Entra come utente guest");
			mntmAggiungi.setText("Aggiungi");
			mntmCancella.setText("Cancella tutto");
			mntmCancellaStorico.setText("Cancella storico dei memo");
			mntmEliminaImpegni.setText("Elimina impegni");
			classicRadio.setText("Classica");	
			calendarRadio.setText("Calendario");
			mntmHints.setText("Aiuti");
			mntmProfilo.setText("Profilo");
			mntmRimuoviU.setText("Rimuovi utente");
			mntmModPass.setText("Modifica password");
			mntmStatistiche.setText("Statistiche");
			cbm1.setText("Alta");
			cbm2.setText("Media");
			cbm3.setText("Bassa");
			scaduti.setText("Scaduti");
			today.setText("Solo oggi");
			week.setText("In settimana");
			month.setText("In questo mese");
			year.setText("In quest'anno");
			always.setText("Sempre");
			radioAttivi.setText("Memo attivi");
			radioScaduti.setText("Storico");
		}
		else if(language==Lang.ES){
			mnStrumenti.setText("Instrumentos");
			mnUtente.setText("Usuario");
			mnVisualizza.setText("Vista");
			mntmEsci.setText("Salte");
			mntmSalva.setText("Salva");
			mntmNuova.setText("Nueva sesiòn");
			mntmGuest.setText("Entra como usuario guest");
			mntmAggiungi.setText("Añadir");
			mntmCancella.setText("Borra todo");
			mntmCancellaStorico.setText("Borra archivo");
			mntmEliminaImpegni.setText("Elimina compromisos");
			classicRadio.setText("Clàsica");	
			calendarRadio.setText("Calendario");
			mntmHints.setText("Consejos");
			mntmProfilo.setText("Perfil");
			mntmRimuoviU.setText("Elimina usuario");
			mntmModPass.setText("Cambiar password");
			mntmStatistiche.setText("Estadística");
			cbm1.setText("Alta");
			cbm2.setText("Media");
			cbm3.setText("Baja");
			scaduti.setText("Caducados");
			today.setText("Sòlo hoy");
			week.setText("En esta semana");
			month.setText("En este mes");
			year.setText("En este año");
			always.setText("Siempre");
			radioAttivi.setText("Memos activos");
			radioScaduti.setText("Archivo");
		}
		else if(language==Lang.DE){
			mnStrumenti.setText("Werkzeuge");
			mnUtente.setText("Nutzer");
			mnVisualizza.setText("Ansicht");
			mntmEsci.setText("Beenden");
			mntmSalva.setText("Rette");
			mntmNuova.setText("Neu Sitzung");
			mntmGuest.setText("Eingabe als gast");
			mntmAggiungi.setText("Hinzufügen");
			mntmCancella.setText("Löschen Sie alles");
			mntmCancellaStorico.setText("Archiv löschen");
			mntmEliminaImpegni.setText("Löschen verpflichtungen");
			classicRadio.setText("Klassische");	
			calendarRadio.setText("Kalender");
			mntmProfilo.setText("Profil");
			mntmHints.setText("Hinweise");
			mntmRimuoviU.setText("Benutzer entfernen");
			mntmModPass.setText("Password ändern");
			mntmStatistiche.setText("Statistiken");
			cbm1.setText("Hoch");
			cbm2.setText("Mittel");
			cbm3.setText("Niedrig");
			scaduti.setText("Überschritten");
			today.setText("Bis heute");
			week.setText("Diese woche");
			month.setText("In diesem monat");
			year.setText("In diesem jahr");
			always.setText("Immer");
			radioAttivi.setText("Aktiv memos");
			radioScaduti.setText("Archiv");
		}
		this.language=language;
		p.setLanguage(language);		//tradotto
		rinviaD.setLanguage(language);	//tradotto
		userD.setLanguage(language);	//tradotto
		classic.setLanguage(language);	//tradotto
		calendar.setLanguage(language);	//tradotto
		setInternalFrameToolTips();
		attivaHints();

	}
	private void setProgressBarToolTip(){
		
		int x=k.percentualeCompletati();
		if(language==Lang.IT){
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
		else if(language==Lang.EN){
			if(x<=20)
				progressBar.setToolTipText("You'd need a MemoRem to remember that MemoRem exists");
			else if(x<=40)
				progressBar.setToolTipText("Hey guy, d'ya know you're lame?");
			else if(x<=60)
				progressBar.setToolTipText("Come on, work harder!!!!");
			else if(x<=80)
				progressBar.setToolTipText("You're getting good, but I think you can do more!");
			else if(x<99)
				progressBar.setToolTipText("Wow, you rock dude!!!");
			else
				progressBar.setToolTipText("You didn't miss a thing!!! Can you even spend some time for yourself???");
		}
		else if(language==Lang.ES){
			if(x<=20)
				progressBar.setToolTipText("Necesitas un MemoRem para recordar que MemoRem existe");
			else if(x<=40)
				progressBar.setToolTipText("Hey chico, ¿ya sabes que eres tonto?");
			else if(x<=60)
				progressBar.setToolTipText("¡Vamos, trabaja duro!");
			else if(x<=80)
				progressBar.setToolTipText("Lo estás haciendo bien, pero creo que lo puedes hacer más.");
			else if(x<=99)
				progressBar.setToolTipText("Wow, ¡¡molas tío!!");
			else
				progressBar.setToolTipText("¡No perdiste nada! ¿Puedes incluso gastar más tiempo en ti mismo?");
		}
		else if(language==Lang.DE){
			if(x<=20)
				progressBar.setToolTipText("Sie möchten eine Memo Rem dann müssen Sie sich daran erinnern, dass Memo Rem existieren");
			else if(x<=40)
				progressBar.setToolTipText("Sie arbeitest wie ein Krüppel. ");
			else if(x<=60)
				progressBar.setToolTipText("Kommen Sie, härter arbeiten!!!! ");
			else if(x<=80)
				progressBar.setToolTipText("Sie sind immer gut, aber ich denke, dass Sie mehr tun können! ");
			else if(x<=99)
				progressBar.setToolTipText("Guaoo Sie sind ein Rocker.");
			else
				progressBar.setToolTipText("Sie haben nichts verpasst!!! Sie können sogar einige Zeit für sich selbst einsetzen???");
		}//tedesco
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
			//calendar.setMemolist(k.getTotalList());
			calendar.getCustomRenderer().refresh();
			panel_2.setVisible(true);
		}
		panel.add(panel_2, "cell 0 0 7 26,grow");
		panel.setVisible(false);
		panel.setVisible(true);
	}
	
	/**
	 * Aggiunge un nuovo Memo
	 */
	public void aggiungi(Memo m){
		if(!k.getUser().getNickname().equals("guest") && k.containsEqualInDB(m)){
			if(language==Lang.IT)
				JOptionPane.showMessageDialog(MemoremGUI.this, "Il memo esiste già");
			else if(language==Lang.ES)
				JOptionPane.showMessageDialog(MemoremGUI.this, "Ya existe este memo");
			else if(language==Lang.DE)
				JOptionPane.showMessageDialog(MemoremGUI.this, "Dieses Memo ist bereits vorhanden");
			else
				JOptionPane.showMessageDialog(MemoremGUI.this, "This memo already exists");
			return;
		}
		k.add(m);							//il Keeper è stato aggiornato
		calendar.add(m);					//il CalendarFrame è stato aggiornato
		if(visualHandler.getSelected().equals(classicRadio)){	//se siamo nella visualizzazione classica
			System.out.println("L'abbiamo aggiunto");
			boolean[] prior=new boolean[3];
			boolean[] data=new boolean[5];
			for(int i=0;i<3;i++)
				prior[i]=pF[i].isSelected();
			for(int i=0;i<4;i++){
				switch(i){
				case 0: data[i]=dF[1].isSelected(); break; 										//giorno
				case 1: data[i]=(dF[2].isSelected() || dF[6].isSelected())? true: false; break;	//settimana o standard
				case 2: data[i]=dF[3].isSelected(); break;										//mese
				case 3: data[i]=dF[4].isSelected(); break;										//anno
				case 4: data[i]=dF[0].isSelected(); break;										//scaduti
				}
			}
			if(k.getUser().isGuest() || !Keeper.filtriViolati(prior, data, m)){
				Data d=m.getEnd();
				d.setLanguage(language);
				m.setEnd(d);
				MemPanel mp=new MemPanel(m);
				mp.setBridges(p, jfc, iconButton);
				if(k.getTotalList().size()>0)
					classic.setBarVisible(true);
				if(m.isCompleted())
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
				panel_2=classic;
			}
		}
		else
			panel_2=calendar;
		mntmSalva.setEnabled(true);
		panel_2.setVisible(false);
		panel_2.setVisible(true);
	}//aggiungi

	/**
	 * Cancella tutti i memo presenti
	 */
	public void cancella(){
		
		int choice=0;
		if(language==Lang.IT)
			choice=JOptionPane.showConfirmDialog(this, "I cambiamenti saranno irreversibili. Continuare?");
		if(language==Lang.DE)
			choice=JOptionPane.showConfirmDialog(this, "Änderungen werden irreversibel sein. Weiter?");
		if(language==Lang.ES)
			choice=JOptionPane.showConfirmDialog(this, "Los cambios serán irreversibles. ¿Desea continuar?");
		else
			choice=JOptionPane.showConfirmDialog(this, "Changes will be irreversible. Continue?");
		
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

	public void cancellaStorico(){
		
		int choice=0;
		if(language==Lang.IT)
			choice=JOptionPane.showConfirmDialog(this, "I cambiamenti saranno irreversibili. Continuare?");
		if(language==Lang.DE)
			choice=JOptionPane.showConfirmDialog(this, "Änderungen werden irreversibel sein. Weiter?");
		if(language==Lang.ES)
			choice=JOptionPane.showConfirmDialog(this, "Los cambios serán irreversibles. ¿Desea continuar?");
		else
			choice=JOptionPane.showConfirmDialog(this, "Changes will be irreversible. Continue?");
		
		if(choice==JOptionPane.YES_OPTION){
			if(k.memoStorici()==0){
				if(language==Lang.IT)
					JOptionPane.showMessageDialog(this, "Non c'è nulla da eliminare");
				else if(language==Lang.DE)
					JOptionPane.showMessageDialog(this, "Es gibt nichts zu löschen");
				else if(language==Lang.ES)
					JOptionPane.showMessageDialog(this, "No hay nada que borrar");
				else
					JOptionPane.showConfirmDialog(this, "Nothing to delete");
				return;
			}
			vF[0].doClick();
			k.cancellaStorico();
			progressBar.setValue(0);
		}
	}

	public void completa(Memo m,boolean completato){
		
		if(completato){
			if(language==Lang.IT)
				JOptionPane.showMessageDialog(this, "Complimenti!!! Hai completato un' attività");
			else if(language==Lang.DE)
				JOptionPane.showMessageDialog(this, "Herzlichen Glückwunsch !!! Sie eine Aktivität abgeschlossen");
			else if(language==Lang.ES)
				JOptionPane.showMessageDialog(this, "¡Felicitaciones!!! Que completo una actividad");
			else
				JOptionPane.showMessageDialog(this, "Well done!!! You completed a memo");
			Sound.yeah();
		}
		k.completa(m,completato);
		calendar.remove(m);
		classic.remove(m);
		progressBar.setValue(k.percentualeCompletati());
		setProgressBarToolTip();
	}

	public void elimina(Memo m){
		
		k.remove(m);					//il Keeper è stato aggiornato
		mntmSalva.setEnabled(true);	
		//progressBar.setValue(k.percentualeCompletati());
		classic.remove(m);				//l'InternalFrame è stato aggiornato
		if(k.getTotalList().size()==0)
			classic.setBarVisible(false);
		calendar.remove(m);				//il CalendarFrame è stato aggiornato
		if(visualHandler.getSelected().equals(calendarRadio))
			panel_2=calendar;
		else{
			panel_2=classic;
			if(k.getTotalList().size()==0)
				classic.setVisible(false);
		}
		if(k.getUser().isGuest() && k.getTotalList().size()==0)
			mntmSalva.setEnabled(false);
		Sound.playRemove();
	}

	public void eliminaImpegni(){
		
		int choice=0;
		if(language==Lang.IT)
			choice=JOptionPane.showConfirmDialog(this, "I cambiamenti saranno irreversibili. Continuare?");
		if(language==Lang.DE)
			choice=JOptionPane.showConfirmDialog(this, "Änderungen werden irreversibel sein. Weiter?");
		if(language==Lang.ES)
			choice=JOptionPane.showConfirmDialog(this, "Los cambios serán irreversibles. ¿Desea continuar?");
		else
			choice=JOptionPane.showConfirmDialog(this, "Changes will be irreversible. Continue?");
		
		if(choice==JOptionPane.YES_OPTION){
			if(k.memoAttivi()==0){
				if(language==Lang.IT)
					JOptionPane.showMessageDialog(this, "Non c'è nulla da eliminare");
				else if(language==Lang.DE)
					JOptionPane.showMessageDialog(this, "Es gibt nichts zu löschen");
				else if(language==Lang.ES)
					JOptionPane.showMessageDialog(this, "No hay nada que borrar");
				else
					JOptionPane.showConfirmDialog(this, "Nothing to delete");
				return;
			}
			k.eliminaImpegni();
			classic.clearMemos();
			calendar.clear();
		}
	}

	public void login(){
		
		Sound.playLogin();
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
		setLanguage(k.getUser().getLingua());
		setProgressBarToolTip();
		mntmGuest.setEnabled(false);
		mntmLogin.setEnabled(false);
		mntmNuova.setEnabled(false);
		mntmLogout.setEnabled(true);
		//JOptionPane.showMessageDialog(this, "Buongiorno, "+k.getUser().toString());
	}

	public void logout(){
		
		if(k.logout()){
			Sound.playLogout();
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
			if(mntmHints.isSelected()){
				mntmHints.setSelected(false);
				scorrevole.setVisible(false);
			}
			System.out.println(k.userList());
			logD.setUserList(k.userList());
			welcome.setLogD(logD);
			panel_2=welcome;
			welcome.setVisible(true);
			panel.add(panel_2,"cell 0 0 7 26,grow");
			mntmLogin.setEnabled(true);
			mntmGuest.setEnabled(true);
			mntmNuova.setEnabled(true);
			progressBar.setValue(0);
			setButtonsAtStart();
		}
	}
	public void modifica(Memo vecchio,Memo nuovo){
		
		if(k.containsEqualInDB(nuovo)){
			if(language==Lang.IT)
				JOptionPane.showMessageDialog(MemoremGUI.this, "Il memo esiste già");
			else if(language==Lang.ES)
				JOptionPane.showMessageDialog(MemoremGUI.this, "Ya existe este memo");
			else if(language==Lang.DE)
				JOptionPane.showMessageDialog(MemoremGUI.this, "Dieses Memo ist bereits vorhanden");
			else
				JOptionPane.showMessageDialog(MemoremGUI.this, "This memo already exists");
		}
		
		mntmSalva.setEnabled(true);
		k.modifica(vecchio, nuovo);							//il Keeper è stato aggiornato
		calendar.remove(vecchio);
		calendar.add(nuovo);					//il CalendarFrame è stato aggiornato
		//ora prima di aggiungere un mempanel al classic bisogna verificare se la nuova data rientri
		//nei filtri preposti
		if(visualHandler.getSelected().equals(classicRadio)){
			boolean[] priors=new boolean[3];
			boolean[] data=new boolean[5];
			for(int i=0;i<3;i++)
				priors[i]=pF[i].isSelected();
			String d=dataHandler.getSelected().getName();
			for(int i=0;i<data.length;i++)
				data[i]=false;
			switch(d){
			case "today" : data[0]=true;
			case "week" : case"standard" : data[1]=true;
			case "month": data[2]=true;
			case "year" : data[3]=true;
			case "expired": data[4]=true;
			}
			if(!Keeper.filtriViolati(priors, data, nuovo))
				classic.modifica(vecchio, nuovo);
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

	public void nuovo(){
		
		p=new Popup(this);
		enableSpecialPowers(false);
		mntmGuest.setEnabled(false);
		mntmLogin.setEnabled(false);
		mntmNuova.setEnabled(false);
		mntmLogout.setEnabled(true);
		panel_2=classic;
		jfc=new JFileChooser();
		jfc.addActionListener(listener); //aggiusta ezio
		p.addWindowListener(watcher);
		panel_2.setVisible(true);
		timer=new Timer();
		orologio=new InnerClock();
		calendar=new CalendarFrame(k.getTotalList());
		calendar.setListener(p);
		if(visualHandler.getSelected().equals(classicRadio))
			((ClassicFrame)panel_2).clearMemos();
		timer.schedule(orologio, 0,3000);
		setLanguage(k.getUser().getLingua());
		enableButtons();
	}
	
	public void removeUser(String utente){
		
		int choice=0;
		if(k.getUser().getNickname()!="admin"){
			if(language==Lang.IT)
				choice=JOptionPane.showConfirmDialog(MemoremGUI.this,"Attenzione. Verranno eliminati tutti i dati relativi all'utente '"+utente+"'. Continuare?");
			else if(language==Lang.DE)
				choice=JOptionPane.showConfirmDialog(MemoremGUI.this, "Warnung!! Alle Daten bezüglich Benutzer '"+utente+"' werden gelöscht. Weiter?");
			else if(language==Lang.ES)
				choice=JOptionPane.showConfirmDialog(MemoremGUI.this, "Advertencia! Todos los datos relativos a '"+utente+"' se eliminarán. Continuar?");
			else
				choice=JOptionPane.showConfirmDialog(MemoremGUI.this, "Warning! All data relative to user '"+utente+"' will be deleted. Continue?");
		}
		if(choice==JOptionPane.OK_OPTION){
			k.removeUser(utente);
			if(k.getUser().getNickname()!="admin"){
				mntmLogout.doClick();
				if(language==Lang.IT)
					JOptionPane.showMessageDialog(MemoremGUI.this, "Utente '"+utente+"' eliminato");
				else if(language==Lang.DE)
					JOptionPane.showMessageDialog(MemoremGUI.this, "Benutzer '"+utente+"' gelöscht");
				else if(language==Lang.ES)
					JOptionPane.showMessageDialog(MemoremGUI.this, "Usuario '"+utente+"' borrado");
				else
					JOptionPane.showMessageDialog(MemoremGUI.this, "User '"+utente+"' deleted");
			}
		}	
	}

	public void reset(){
		
		if(!(k.getUser().getNickname().equals("admin"))){
			JOptionPane.showMessageDialog(MemoremGUI.this, "YOU SHALL NOT PASS!!!");
			return;
		}
		k.reset();
		classic.clearMemos();
		calendar.setMemolist(new MemoList());
	}
	
	/**
	 * rinvia un memo scaduto del numero di giorni indicato. Ritorna true se la nuova data viola
	 * i filtri preposti dal classic
	 */
	public void rinvia(Memo m,int giorni,boolean alreadyDone){
		
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
		String x=dataHandler.getSelected().getName();
		boolean[] data=new boolean[4];
		for(int i=0;i<4;i++)
			data[i]=false;
		if(x.equals("today"))
			data[0]=true;
		else if(x.equals("week") || x.equals("standard"))
			data[1]=true;
		else if(x.equals("month"))
			data[2]=true;
		else if(x.equals("year"))
			data[3]=true;
		if(Keeper.filtriViolati(priors, data, nuovo))
			classic.remove(m);
		else
			classic.modifica(m,nuovo);
	}

	public void salva(){
		
		k.salva();
		mntmSalva.setEnabled(false);
	}
}