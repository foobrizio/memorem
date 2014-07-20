package main;

import java.util.*;

import util.*;

public class MemoremCLI extends Thread{
	
	/**
	 * 
	 * Cosa implementare ancora:
	 * 
	 * 2) Struttura command line
	 * 4) Comandi di interazione principali col server
	 */
	
	private static Keeper k;
	private static User user;			//è il nome dell'utente che utilizza il programma
	private String OS;				//è il nome del sistema operativo che si utilizza
	private static Scanner sc;				//verrà utilizzato per l'interfacca CLI
	
	public MemoremCLI(){
		
		OS=System.getProperty("os.name");
		k=new Keeper();
		user=k.getUser();
		sc=new Scanner(System.in);
		//k.signUp(user, "ciao");
		
	}
	public void run(){
		
		System.out.println("Benvenuto in MemoRem 1.0!!!\n Effettuare il login per accedere al proprio database oppure inserire un nuovo utente");
		while(!isInterrupted()){
		
			while(user.getNickname().equals("none")){
				System.out.print("memorem: ");
				String comando=sc.nextLine();
				manageCommand(comando);
			}
			while(!user.getNickname().equals("none")){
				System.out.print(user.getNickname()+"@memorem: ");
				String comando=sc.nextLine();
				manageCommand(comando);
			}
		}
		System.out.println("Arrivederci, "+user);
	}
	
	public static Memo creaMemo(Memo m){
		
		boolean modifica=(m!=null);
		boolean ok=false;
		System.out.println("Creazione di un nuovo Memo");
		String desc="";
		while(!ok){
			System.out.print("Step 1/7: Inserire descrizione del Memo");
			if(modifica)
				System.out.print("( premi Invio per lasciare invariato)");
			System.out.println(":");
			desc=sc.nextLine().trim();
			if(modifica && desc.length()==0){
				desc=m.description();
				ok=true;
			}
			else System.out.println("questo campo non puo' rimanere vuoto");
		}//descrizione------------------------------------------------------
		String priority="";
		while(!ok){
			System.out.print("Step 2/7: Inserire priorità");
			if(modifica)
				System.out.print("( premi Invio per lasciare invariato):");
			else
				System.out.print("(default: normal):");
			priority=sc.nextLine().trim().toLowerCase();
			if(priority.length()==0){
				if(modifica)
					priority=m.priority()+"";
				else
					priority="normal";
				break;
			}
			try{
				int x=Integer.parseInt(priority);
				if(x==0)
					priority="low";
				else if(x==1)
					priority="normal";
				else if(x==2)
					priority="high";
				ok=true;
			}catch(NumberFormatException nfe){	//la priorità non è stata scritta a numero
				boolean check=true;
				if(!priority.equals("high")){
					if(!priority.equals("normal"))
						if(!priority.equals("low")){
							check=false;
							System.out.println("La priorità non è stata inserita correttamente");
						}
				}
				if(check)
					ok=true;
			}
		}//priorità--------------------------------------------------------------------
		ok=false;
		Data data=new Data();
		int yearS=-1;
		while(!ok){
			System.out.print("Step 3/7: Inserire anno");
			if(modifica)
				System.out.print("( premi Invio per lasciare invariato):");
			else System.out.print("(default:"+data.anno()+"):");
			String year=sc.nextLine();
			try{
				if(year.length()==0){
					if(modifica)
						yearS=m.getEnd().anno();
					else
						yearS=data.anno();
				}
				else if(year.length()==4){
					yearS=Integer.parseInt(year);
					if(yearS>=data.anno())
						ok=true;
					else
						System.out.println("Non inserire anni precedenti al corrente");
				}
			}catch(NumberFormatException nfe){
				System.out.println("Attenzione! L'anno non è stato inserito correttamente");
			}
		}//L'anno è stato inserito---------------------------------------------------
		ok=false;
		int month=-1;
		while(!ok){
			System.out.print("Step 4/7: Inserire mese");
			if(modifica)
				System.out.println("( premi Invio per lasciare invariato)");
			System.out.print(":");
			String x="";
			try{
				x=sc.nextLine();
				if(modifica && x.length()==0){
					month=m.getEnd().mese();
					ok=true;
				}
				else month=Integer.parseInt(x);
			}catch(NumberFormatException e){
				//e.printStackTrace();
				month=Data.monthToInt(x);
			}finally{
				if(month>=0)
					ok=true;
				else
					System.out.println("Hai inserito un mese incorretto");
			}
		}	//Il mese è stato inserito -----------------------------------------------------
		ok=false;
		int day=-1;
		while(!ok){
			System.out.print("Step 5/7: Inserire giorno");
			if(modifica)
				System.out.print("( premi Invio per lasciare invariato )");
			System.out.print(":");
			try{
				String x=sc.nextLine();
				if(modifica && x.length()==0){
					day=m.getEnd().giorno();
					ok=true;
				}
				else day=Integer.parseInt(x);
				if(day<=0 || day>Data.daysOfMonth(yearS, month))
					System.out.println("Il giorno non è stato inserito correttamente");
				else
					ok=true;
			}catch(NumberFormatException nfe){
				System.out.println("Il giorno non è stato inserito correttamente");
			}
		}	//Il giorno è stato inserito ----------------------------------------
		ok=false;
		int hour=-1;
		while(!ok){
			System.out.print("Step 6/7: Inserire ora");
			if(modifica)
				System.out.print("(premi Invio per lasciare invariato)");
			System.out.println(":");
			String x=sc.nextLine();
			try{
				hour=Integer.parseInt(x);
				if(hour<0 || hour>23)
					System.out.println("L'ora non è stata inserita correttamente");
				else ok=true;
			}catch(NumberFormatException nfe){
				System.out.println("L'ora non è stata inserita correttamente");
			}
		}	//L'ora è stata inserita correttamente
		ok=false;
		int minute=-1;
		while(!ok){
			System.out.print("Step 7/7: Inserire minuto");
			if(modifica)
				System.out.print("( premi Invio per lasciare invariato)");
			System.out.print(":");
			String x=sc.nextLine();
			try{
				if(modifica && x.length()==0){
					minute=m.getEnd().minuto();
					ok=true;
				}
				else{
					minute=Integer.parseInt(x);
					if(minute<0 || minute>59)
						System.out.println("Il minuto non è stato inserito correttamente");
					else ok=true;
				}
			}catch(NumberFormatException nfe){
				System.out.println("Il minuto non è stato inserito correttamente");
			}
		}	//Il minuto è stato inserito correttamente
		return new Memo(desc,priority,yearS,month,day,hour,minute);
		
	}
	private static void manageCommand(String comando){
		
		if(comando==null)
			return;
		else{
			if(comando.equals("help"))
				help(null);
			else if(comando.length()>5 && comando.substring(0, 4).equals("help")){
				String subcomando=comando.substring(5);
				help(subcomando);
			}
			else if(comando.equals("login"))			//login
				login();
			else if(comando.equals("sign-up"))			//registrazione
				signUp();
			else if(comando.equals("quit") || comando.equals("exit")){			//chiude
				String x=k.getUser().toString();
				if(!x.equals("none")){
					System.out.println("Arrivederci, "+k.getUser().toString());
					k.logout();
				}
				else
					System.out.println("Arrivederci");
				Thread.currentThread().interrupt();
				System.exit(0);
			}
			else if(!user.getNickname().equals("none")){		//Questi comandi sono utilizzabili solo ad utente loggato
				if(comando.equals("add"))					//aggiungi
					add();
				else if(comando.equals("remove"))
					remove();
				else if(comando.equals("clear"))			//cancella
					clear();
				else if(comando.equals("size"))				//size
					size();
				else if(comando.equals("list"))				//memoAttivi
					list();
				else if(comando.equals("pending"))			//memo da notificare
					pending();
				else if(comando.equals("completa"))			//completa un memo scaduto
					completa();
				else if(comando.equals("archivia"))			//archivia un memo scaduto
					archivia();
				else if(comando.equals("rinvia"))			//rinvia un memo scaduto
					rinvia();
				else if(comando.equals("modifica"))			//modifica un memo esistente
					modifica();
				else if(comando.equals("list-today"))		//memo odierni
					listToday();
				else if(comando.equals("list-week"))		//memo in settimana
					listWeek();
				else if(comando.equals("list-old"))			//memo scaduti
					listOld();
				else if(comando.equals("list-all"))			//memoTotali
					listAll();
				else if(comando.equals("statistiche"))		//statistiche
					statistiche();
				else if(comando.equals("delete-user"))  	//elimina l'utente
					deleteUser();
				else if(comando.equals("modify-password"))	//modifica la password
					modifyPassword();
				else if(comando.equals("salva"))			//salva
					salva();
				else if(comando.equals("logout"))			//logout
					logout();
				else if(comando.equals("profilo"))			//profilo
					profilo();
			}
		}
		return;	
	}
	
	/*	_____________________________________________________________________________________
	 * 
	 * 				*~._.~*¯*~._.~**~._.~**~._.~**~._.~**~._.~**~._.~**~._.~*
     *				*~._.~*¯*~<QUI INIZIA LA LISTA DEI COMANDI>*~._.~**~._.~*	
     *				*~._.~*¯*~._.~**~._.~**~._.~**~._.~**~._.~**~._.~**~._.~*
     *	_____________________________________________________________________________________						
	 */
	
	/**
	 * Stampa su CLI la lista dei comandi
	 * @param comando
	 */
	public static void help(String comando){
		
		boolean connesso=!user.getNickname().equals("none");
		if(comando==null){
			System.out.println("Lista comandi:");
			System.out.println("help\t\tstampa questo elenco");
			System.out.println("login\t\teffettua l'accesso");
			System.out.println("sign-up\t\tcrea un nuovo utente");
			if(connesso)
				System.out.println("logout\t\tdisconnette l'utente corrente");
			System.out.println("quit\t\tchiude il programma");
			System.out.println("exit\t\tvedi 'quit'");
			if(connesso){
				System.out.println("add\t\tavvia l'editor per aggiungere un memo");
				System.out.println("remove\t\tavvia l'editor per rimuovere un memo");
				System.out.println("modifica\tavvia l'editor per modificare un memo");
				System.out.println("clear\t\tcancella tutti i memo dell'utente");
				System.out.println("rinvia\t\tavvia l'editor per rinviare un memo scaduto");
				System.out.println("completa\tcompleta un memo esistente");
				System.out.println("archivia\tarchivia un memo scaduto");
				System.out.println("pending\t\tstampa la lista dei memo scaduti e in attesa di essere gestiti");
				System.out.println("list\t\tstampa la lista dei memo attivi");
				System.out.println("list-today\tstampa la lista dei memo odierni");
				System.out.println("list-week\tstampa la lista dei memo della settimana corrente");
				System.out.println("list-old\tstampa la lista dei memo scaduti");
				System.out.println("list-all\tstampa la lista di tutti i memo");
				System.out.println("size\t\tstampa il numero di memo presenti");
				System.out.println("profilo\t\tstampa le informazioni relative all'utente");
				System.out.println("statistiche\tvisualizza le statistiche dell'utente connesso");
				System.out.println("password\tmodifica la password dell'utente");
				System.out.println("del-user\tcancella l'utente e i relativi memo");
			}
		}
		else{
			comando=comando.trim().toLowerCase();	//per evitare qualunque incongruenza durante la digitazione
			if(comando.equals("add"))
				System.out.println("Avvia la creazione guidata di un nuovo Memo, che verrà infine aggiunto al proprio MemoRem");
			else if(comando.equals("quit") || comando.equals("exit"))
				System.out.println("Chiude il programma");
			else if(comando.equals("remove"))
				System.out.println("Avvia la rimozione guidata di un Memo contenuto nel database");
			else if(comando.equals("size"))
				System.out.println("Restituisce il numero di memo attivi");
			else if(comando.equals("list"))
				System.out.println("Restituisce la lista di memo attivi");
			else if(comando.equals("list-today"))
				System.out.println("Restituisce la lista dei memo che scadono oggi");
			else if(comando.equals("list-week"))
				System.out.println("Restituisce la lista dei memo che scadono in settimana");
			else if(comando.equals("list-all"))
				System.out.println("Restituisce la lista di memo totali");
			else if(comando.equals("list-old"))
				System.out.println("Restituisce la lista dei memo scaduti");
			else if(comando.equals("login"))
				System.out.println("Permette ad un utente registrato di connettersi alla propria sessione");
			else if(comando.equals("logout"))
				System.out.println("Disconnette l'utente corrente");
			else if(comando.equals("sign-up"))
				System.out.println("Avvia la registrazione di un nuovo utente. Registrati anche tu!");
			else if(comando.equals("statistiche"))
				System.out.println("Visualizza le statistiche relative all'utente");
			else if(comando.equals("password"))
				System.out.println("Modifica la password dell'utente");
			else if(comando.equals("rinvia"))
				System.out.println("Rinvia un memo scaduto");
			else if(comando.equals("pending"))
				System.out.println("Ritorna la lista dei memo scaduti che aspettano di essere gestiti");
			else if(comando.equals("completa"))
				System.out.println("Completa un memo, dopodiche' non sara' piu' modificabile");
			else if(comando.equals("archivia"))
				System.out.println("Archivia un memo, dopodiche' non sara' piu' modificabile");
			else if(comando.equals("modifica"))
				System.out.println("Avvia la modifica guidata di un memo");
			else if(comando.equals("salva"))
				System.out.println("Salva le modifiche nel database");
			else if(comando.equals("del-user"))
				System.out.println("Rimuove l'utente dal database, cancellando tutti i memo");
			else if(comando.equals("profilo"))
				System.out.println("Visualizza il profilo dell'utente");
			else if(comando.equals("clear"))
				System.out.println("Cancella tutti i memo presenti nel database");
			else
				System.out.println("UNKNOWN COMMAND");
		}
	}
	
	public static void login(){
		
		if(!user.getNickname().equals("none")){
			System.out.println("Un utente è già connesso. Disconnettersi prima di continuare");
			return;
		}
		System.out.print("Inserire username: ");
		String user=sc.nextLine();
		System.out.print("Inserire password: ");
		String password=sc.nextLine();
		int result=k.login(user, password);
		if(result==0){
			MemoremCLI.user=k.getUser();
			System.out.println("Benvenuto, "+user);
			LinkedList<String> prior=new LinkedList<String>();
			prior.add("Alta");
			prior.add("Media");
			prior.add("Bassa");
			k.formaQuery("always", "attivi", prior);
			MemoList ml=k.getDBList();
			int cont=0;
			for(Memo m:ml)
				if(m.isScaduto())
					cont++;
			if(cont!=0)
				System.out.println("Ci sono "+cont+" memo scaduti che vanno gestiti");
		}
		else if(result==2){
			System.out.println("La password è sbagliata");
		}
		else if(result==1){
			System.out.println("L'utente non è stato trovato nel nostro database");
		}
	}
	
	public static void logout(){
		
		System.out.println("Arrivederci, "+k.getUser().toString());
		k.logout();
		user=new User("none");
	}
	
	public static void signUp(){
		
		boolean ok=false;
		System.out.println("Registrazione 1/6");
		String nick="";
		while(!ok){
			System.out.print("Nickname: ");
			nick=sc.nextLine();
			if(nick.length()<=2)
				System.out.println("Nickname troppo corto. Il nickname deve essere composto da almeno 3 caratteri");
			else if(nick.length()>16)
				System.out.println("Nickname troppo lungo. Il nickname non deve superare i 16 caratteri");
				ok=true;
		}
		ok=false;
		System.out.println("Registrazione 2/6");
		String password="";
		while(!ok){
			System.out.print("Password: ");
			password=sc.nextLine();
			if(password.length()<4)
				System.out.println("Password troppo corta. La password deve essere composta da almeno 4 caratteri");
			else
				ok=true;
		}
		ok=false;
		System.out.println("Registrazione 3/6");
		System.out.print("Conferma password: ");
		String conferma=sc.nextLine();
		if(!password.equals(conferma)){
			System.out.println("Le due password non combaciano");
			return;
		}
		System.out.println("Registrazione 4/6");
		String nome=null;
		while(!ok){
			System.out.print("Nome: ");
			nome=sc.nextLine();
			if(nome.length()>16)
				System.out.println("Nome troppo lungo");
			else ok=true;
		}
		ok=false;
		String cognome=null;
		System.out.println("Registrazione 5/6");
		while(!ok){
			System.out.print("Cognome: ");
			cognome=sc.nextLine();
			if(cognome.length()>16)
				System.out.println("Cognome troppo lungo");
			else ok=true;
		}
		ok=false;
		String genere;
		char gen='m';
		System.out.println("Registrazione 6/6");
		while(!ok){
			System.out.println("genere: ");
			genere=sc.nextLine().toLowerCase();
			if(genere.equals("maschio") || genere.equals("male") || genere.equals("m") || genere.equals("uomo") || genere.equals("man")){
				gen='m';
				ok=true;
			}
			else if(genere.equals("femmina") || genere.equals("female") || genere.equals("f") || genere.equals("donna") || genere.equals("woman")){
				gen='f';
				ok=true;
			}
			else{
				System.out.println("Inserire il proprio genere. I seguenti input sono ammessi: maschio,male,uomo,man,m,femmina,female,donna,woman,f");
			}
		}
		k.signUp(nick, password,nome,cognome,gen);
		user=k.getUser();
		
	}
	/**
	 * Aggiunge un Memo al MemoRem
	 */
	public static void add(){
		
		Memo t=creaMemo(null);
		k.add(t);
	}
	
	/**
	 * Rimuove un Memo dal MemoRem
	 * @return
	 */
	public static boolean remove(){
		
		int cont=1;
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Media");
		prior.add("Bassa");
		k.formaQuery("always", "attivi", prior);
		MemoList ml=k.getRealList();
		Memo x;
		for(Memo m:ml){
			System.out.println(cont+"\t"+m.toString());
			cont++;
		}
		boolean ok=false;
		while(!ok){
			System.out.println("Inserire il numero del memo da rimuovere");
			String number=sc.nextLine().trim();
			int num=0;
			try{
				num=Integer.parseInt(number);
				x=ml.get(num-1);
				System.out.println("Questo è il memo che hai selezionato:\n\n\t"+x+"\n");
				System.out.println("Vuoi confermare la scelta?S/n");
				String risposta=sc.nextLine().trim().toLowerCase();
				if(risposta.equals("si") || risposta.equals("s") || risposta.equals("y") || risposta.equals("yes")){
					k.remove(ml.get(num-1));
					ok=true;
				}
				else if(risposta.equals("no") || risposta.equals("n"))
					return false;
			}catch(NumberFormatException e){
				System.out.println("Inserire un numero intero, per favore");
			}catch(NullPointerException e){
				System.out.println("Inserire un numero tra quelli visualizzati");
			}
		}//while
		return true;
	}
	
	public static void rinvia(){
		
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Media");
		prior.add("Bassa");
		k.formaQuery("always", "attivi", prior);
		MemoList ml=k.getRealList();
		Iterator<Memo> it=ml.iterator();
		while(it.hasNext())
			if(!(it.next().isScaduto()))
				it.remove();
		if(ml.size()==0){
			System.out.println("Non ci sono memo da rinviare");
			return;
		}
		boolean continua=true;
		while(true){
			int cont=1;
			for(Memo m:ml){
				System.out.println(cont+"\t"+m);
				cont++;
			}
			System.out.println("Inserire il numero del memo da rinviare");
			String number=sc.nextLine();
			int num=0;
			try{
				num=Integer.parseInt(number);
				Memo x=ml.get(num-1);
				System.out.println("Questo è il memo che hai selezionato:\n\n\t"+x+"\n");
				System.out.println("Vuoi confermare la scelta?S/n");
				String risposta=sc.nextLine().trim().toLowerCase();
				if(risposta.equals("si") || risposta.equals("s") || risposta.equals("y") || risposta.equals("yes")){
					while(true){
						System.out.println("Di quanti giorni vuoi rinviare il memo? (default: rinvia a domani)");
						number=sc.nextLine();
						Data nuova=x.getEnd();
						if(number.length()==0){
							nuova=new Data().domani();
							Memo xNuovo=new Memo(x.description(),x.priority(),nuova);
							k.modifica(x, xNuovo);
							System.out.println("Memo rinviato");
							return;
						}
						num=Integer.parseInt(number);
						for(int i=0;i<num;i++)
							nuova=nuova.domani();
						if(nuova.compareTo(new Data())<0){
							System.out.println("Non puoi inserire una data gia' passata");
							if(!continua()){
								continua=false;
								break;
							}
						}
						else{
							Memo xNuovo=new Memo(x.description(),x.priority(),nuova);
							k.modifica(x, xNuovo);
							System.out.println("Memo rinviato");
							return;
						}
					}//while
				}
				else if(risposta.equals("no") || risposta.equals("n"))
					if(!continua())
						break;
			}catch(NumberFormatException e){
				System.out.println("Inserire un numero intero, per favore");
			}catch(NullPointerException e){
				System.out.println("Inserire un numero tra quelli visualizzati");
			}
			if(!continua)
				break;
		}//while	
	}
	/**
	 * Stampa il numero di memo attivi
	 */
	public static void size(){
		
		System.out.println(k.memoAttivi());
	}
	
	/**
	 * Stampa lista dei Memo attivi
	 */
	public static void list(){
		
		System.out.println("Memo attivi:"+k.memoAttivi());
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Media");
		prior.add("Bassa");
		k.formaQuery("always", "attivi", prior);
		MemoList ml=k.getRealList();
		StringBuilder sb=new StringBuilder(1000);
		int cont=0;
		for(Memo m:ml){
			if(cont>0)
				sb.append("\n");
			sb.append(m);
			cont++;
		}
		System.out.println(sb.toString());
	}
	
	/**
	 * Stampa la lista di tutti i Memo
	 */
	public static void listAll(){
		
		System.out.println("Memo totali:"+k.memoTotali());
		System.out.println(k.stampaMemoTotali());
	}
	
	public static void listToday(){
		
		String data="oggi";
		String visual="attivi";
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Bassa");
		prior.add("Media");
		k.formaQuery(data, visual, prior);
		MemoList ml=k.getRealList();
		System.out.println("Memo del giorno:"+ml.size());
		System.out.println(ml.toString());
	}
	
	/**
	 * ERRORE
	 * 
	 */
	public static void listWeek(){
		
		String data="week";
		String visual="attivi";
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Bassa");
		prior.add("Media");
		k.formaQuery(data, visual, prior);
		MemoList ml=k.getRealList();
		System.out.println("Memo in questa settimana:"+ml.size());
		System.out.println(ml.toString());
	}
	
	public static void listOld(){
		
		String data="always";
		String visual="scaduti";
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Bassa");
		prior.add("Media");
		k.formaQuery(data, visual, prior);
		MemoList ml=k.getRealList();
		System.out.println("Memo scaduti:"+ml.size());
		System.out.println(ml.toString());
	}
	
	/**
	 * Stampa i memo che sono scaduti e non sono ancora stati notificati
	 */
	public static void pending(){
		
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Media");
		prior.add("Bassa");
		k.formaQuery("always", "attivi", prior);
		MemoList ml=k.getRealList();
		if(ml.size()==0)
			System.out.println("Non ci sono memo da notificare");
		else{
			Iterator<Memo> it=ml.iterator();
			while(it.hasNext())
				if(!(it.next().isScaduto()))
					it.remove();
			System.out.println("Memo in attesa:"+ml.size());
			for(Memo m:ml)
				System.out.println(m);
		}
	}
	
	private static boolean continua(){
		
		System.out.println("vuoi continuare?");
		String answer=sc.nextLine();
		if(answer.equals("si") || answer.equals("s") || answer.equals("yes") || answer.equals("y"))
			return true;
		return false;
	}
	
	/**
	 * Completa un memo
	 */
	public static void completa(){
		
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Media");
		prior.add("Bassa");
		k.formaQuery("always", "attivi", prior);
		MemoList ml=k.getRealList();
		while(true){
			int cont=1;
			for(Memo m:ml){
				System.out.println(cont+"\t"+m);
				cont++;
			}
			System.out.println("Inserire il numero del memo da completare");
			String number=sc.nextLine();
			int num=0;
			try{
				num=Integer.parseInt(number);
				Memo x=ml.get(num-1);
				System.out.println("Questo è il memo che hai selezionato:\n\n\t"+x+"\n");
				System.out.println("Vuoi confermare la scelta?S/n");
				String risposta=sc.nextLine().trim().toLowerCase();
				if(risposta.equals("si") || risposta.equals("s") || risposta.equals("y") || risposta.equals("yes")){
					k.completa(x, true);
					break;
				}
				else if(risposta.equals("no") || risposta.equals("n"))
					if(!continua())
						break;
			}catch(NumberFormatException e){
				System.out.println("Inserire un numero intero, per favore");
			}catch(NullPointerException e){
				System.out.println("Inserire un numero tra quelli visualizzati");
			}
		}//while	
	}
	
	/**
	 * Archivia un memo scaduto
	 */
	public static void archivia(){
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Media");
		prior.add("Bassa");
		k.formaQuery("always", "attivi", prior);
		MemoList ml=k.getRealList();
		if(ml.size()==0){
			System.out.println("Non ci sono memo da archiviare");
			return;
		}
		Iterator<Memo> it=ml.iterator();
		while(it.hasNext())
			if(!it.next().isScaduto())
				it.remove();
		while(true){
			int cont=1;
			for(Memo m:ml){
				System.out.println(cont+"\t"+m);
				cont++;
			}
			System.out.println("Inserire il numero del memo da archiviare");
			String number=sc.nextLine();
			int num=0;
			try{
				num=Integer.parseInt(number);
				Memo x=ml.get(num-1);
				System.out.println("Questo è il memo che hai selezionato:\n\n\t"+x+"\n");
				System.out.println("Vuoi confermare la scelta?S/n");
				String risposta=sc.nextLine().trim().toLowerCase();
				if(risposta.equals("si") || risposta.equals("s") || risposta.equals("y") || risposta.equals("yes")){
					k.completa(x, false);
					break;
				}
				else if(risposta.equals("no") || risposta.equals("n"))
					if(!continua())
						break;
			}catch(NumberFormatException e){
				System.out.println("Inserire un numero intero, per favore");
			}catch(NullPointerException e){
				System.out.println("Inserire un numero tra quelli visualizzati");
			}
		}//while
	}
	
	public static void modifica(){
		LinkedList<String> prior=new LinkedList<String>();
		prior.add("Alta");
		prior.add("Media");
		prior.add("Bassa");
		k.formaQuery("always", "attivi", prior);
		MemoList ml=k.getRealList();
		while(true){
			int cont=1;
			for(Memo m:ml){
				System.out.println(cont+"\t"+m);
				cont++;
			}
			System.out.println("Inserire il numero del memo da modificare");
			String number=sc.nextLine();
			int num=0;
			try{
				num=Integer.parseInt(number);
				Memo x=ml.get(num-1);
				System.out.println("Questo è il memo che hai selezionato:\n\n\t"+x+"\n");
				System.out.println("Vuoi confermare la scelta?S/n");
				String risposta=sc.nextLine().trim().toLowerCase();
				if(risposta.equals("si") || risposta.equals("s") || risposta.equals("y") || risposta.equals("yes")){
					Memo nuovo=creaMemo(x);
					if(!nuovo.equals(x))
						k.modifica(x, nuovo);
					else
						System.out.println("Non vi è stata alcuna modifica");
					return;
				}
				else if(risposta.equals("no") || risposta.equals("n"))
					if(!continua())
						break;
			}catch(NumberFormatException e){
				System.out.println("Inserire un numero intero, per favore");
			}catch(NullPointerException e){
				System.out.println("Inserire un numero tra quelli visualizzati");
			}
		}//while
	}
	
	/**
	 * Salva le modifiche sul file
	 */
	public static void salva(){
		
		System.out.println("Salvataggio in corso...");
		k.salva();
		System.out.println("Salvataggio effettuato");
	}
	
	public static void statistiche(){
		
		Object[] stat=k.statistiche();
		System.out.print("Memo con priorità alta: \t"+stat[0]+"\n");
		System.out.print("Memo con priorità media:\t"+stat[1]+"\n");
		System.out.print("Memo con priorità bassa:\t"+stat[2]+"\n");
		System.out.print("Memo completati:\t\t"+stat[3]+"\n");
		System.out.print("Memo archiviati:\t\t"+stat[4]+"\n");
		System.out.print("Memo attivi:\t\t\t"+stat[5]+"\n");
		System.out.print("Memo totali:\t\t\t"+stat[6]+"\n");
	}
	
	public static void modifyPassword(){
		
		System.out.print("Inserire la password corrente:");
		String cur=sc.nextLine();
		System.out.println("Inserire nuova password:");
		String nuova=sc.nextLine();
		System.out.println("Conferma nuova password:");
		String conf=sc.nextLine();
		if(!nuova.equals(conf)){
			System.out.println("Le due password non combaciano");
			return;
		}
		int result=k.modificaPassword(cur, nuova);
		switch(result){
		case 0: System.out.println("password modificata"); return;
		case 2: System.out.println("La password dell'utente è sbagliata"); return;
		case 3: System.out.println("La password nuova deve essere diversa dalla vecchia"); return;
		default: System.out.println("Errore durante la modifica della password"); return;
		}
	}
	
	public static void deleteUser(){
		
		System.out.print("Attenzione, questo processo rimuoverà in modo irreversibile tutti i tuoi dati. ");
		if(continua()){
			k.removeUser(k.getUser().getNickname());
			k.logout();
		}
		
		
	}
	public static void profilo(){
		
		System.out.println("Nome:\t\t"+user.getNome());
		System.out.println("Cognome:\t"+user.getCognome());
		System.out.print("Sesso:\t\t");
		if(user.isMaschio())
			System.out.println("maschio\n");
		else
			System.out.println("femmina\n");
		System.out.println("Memo attivi:\t"+k.memoAttivi());
		System.out.println("Memo totali:\t"+k.memoTotali());
	}
	
	public static void clear(){
		
		System.out.println("Database svuotato");
		k.clear();
	}
	public static void main(String[] args){
		
		MemoremCLI t=new MemoremCLI();
		System.out.println(t.OS);
		t.start();
	}

}
