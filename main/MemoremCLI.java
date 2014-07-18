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
	
	private Keeper k;
	private User user;			//è il nome dell'utente che utilizza il programma
	private String OS;				//è il nome del sistema operativo che si utilizza
	private Scanner sc;				//verrà utilizzato per l'interfacca CLI
	
	public MemoremCLI(){
		
		OS=System.getProperty("os.name");
		k=new Keeper();
		this.user=k.getUser();
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
	
	private void manageCommand(String comando){
		
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
				System.out.println("Arrivederci");
				Thread.currentThread().interrupt();
				System.exit(0);
			}
			else if(!user.equals("none")){		//Questi comandi sono utilizzabili solo ad utente loggato
				if(comando.equals("add"))					//aggiungi
					add();
				else if(comando.equals("clear"))			//cancella
					clear();
				else if(comando.equals("size"))				//size
					size();
				else if(comando.equals("list"))				//memoAttivi
					list();
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
	public void help(String comando){
		
		if(comando==null){
			System.out.println("Lista comandi:");
			System.out.println("help\t\tstampa questo elenco");
			System.out.println("login\t\teffettua l'accesso");
			System.out.println("sign-up\t\tcrea un nuovo utente");
			System.out.println("logout\t\tdisconnette l'utente corrente");
			System.out.println("quit\t\tchiude il programma");
			System.out.println("exit\t\tvedi 'quit'");
			System.out.println("add\t\tavvia l'editor per aggiungere un task");
			System.out.println("remove\t\tavvia l'editor per rimuovere un task");
			System.out.println("list\t\tstampa la lista totale dei task");
			System.out.println("list-today\tstampa la lista dei task odierni");
			System.out.println("list-week\tstampa la lista dei task della settimana corrente");
			System.out.println("list-active\tstampa la lista dei task attivi");
			System.out.println("size\t\tstampa il numero di task presenti");
			System.out.println("profilo\t\tstampa le informazioni relative all'utente");
		}
		else{
			comando=comando.trim().toLowerCase();	//per evitare qualunque incongruenza durante la digitazione
			if(comando.equals("add"))
				System.out.println("Avvia la creazione guidata di un nuovo Memo, che verrà infine aggiunto al proprio MemoRem");
			else if(comando.equals("quit"))
				System.out.println("Chiude il programma");
			else if(comando.equals("remove"))
				System.out.println("working");
			else if(comando.equals("size"))
				System.out.println("Restituisce il numero di memo attivi");
			else if(comando.equals("list"))
				System.out.println("Restituisce la lista di memo attivi");
			else if(comando.equals("list-today"))
				System.out.println("working");
			else if(comando.equals("list-week"))
				System.out.println("working");
			else if(comando.equals("list-all"))
				System.out.println("Restituisce la lista di memo totali");
			else if(comando.equals("login"))
				System.out.println("Permette ad un utente registrato di connettersi alla propria sessione");
			else if(comando.equals("sign-up"))
				System.out.println("Avvia la registrazione di un nuovo utente. Registrati anche tu!");
			else if(comando.equals("statistiche"))
				System.out.println("Visualizza le statistiche relative all'utente");
			else if(comando.equals("password"))
				System.out.println("Modifica la password dell'utente");
			else
				System.out.println("UNKNOWN COMMAND");
		}
	}
	
	public void login(){
		
		if(!this.user.getNickname().equals("none")){
			System.out.println("Un utente è già connesso. Disconnettersi prima di continuare");
			return;
		}
		System.out.print("Inserire username: ");
		String user=sc.nextLine();
		System.out.print("Inserire password: ");
		String password=sc.nextLine();
		int result=k.login(user, password);
		if(result==0){
			this.user=k.getUser();
			System.out.println("Benvenuto, "+user);
		}
		else if(result==2){
			System.out.println("La password è sbagliata");
		}
		else if(result==1){
			System.out.println("L'utente non è stato trovato nel nostro database");
		}
	}
	
	public void logout(){
		
		System.out.println("Arrivederci, "+k.getUser().toString());
		k.logout();
		this.user=new User("none");
	}
	
	public void signUp(){
		
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
		this.user=k.getUser();
		
	}
	/**
	 * Aggiunge un Memo al MemoRem
	 */
	public void add(){
		boolean ok=false;
		System.out.println("Creazione di un nuovo Memo");
		System.out.println("Step 1/7: Inserire descrizione del Memo");
		String desc=sc.nextLine().trim();
		String priority="";
		while(!ok){
			System.out.println("Step 2/7: Inserire priorità(default: normal)");
			priority=sc.nextLine().trim().toLowerCase();
			if(priority.length()==0){
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
		}	//La priorità è stata inserita
		ok=false;
		Data data=new Data();
		int yearS=-1;
		while(!ok){
			System.out.println("Step 3/7: Inserire anno(default:"+data.anno()+")");
			String year=sc.nextLine();
			try{
				if(year.length()==0)
				yearS=data.anno();
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
		}	//L'anno è stato inserito
		ok=false;
		int month=-1;
		while(!ok){
			System.out.println("Step 4/7: Inserire mese");
			
			String x="";
			try{
				x=sc.nextLine();
				month=Integer.parseInt(x);
			}catch(NumberFormatException e){
				//e.printStackTrace();
				month=Data.monthToInt(x);
			}finally{
				if(month>=0)
					ok=true;
				else
					System.out.println("Hai inserito un mese incorretto");
			}
		}	//Il mese è stato inserito
		ok=false;
		int day=-1;
		while(!ok){
			System.out.println("Step 5/7: Inserire giorno");
			try{
				String x=sc.nextLine();
				day=Integer.parseInt(x);
				if(day<=0 || day>Data.daysOfMonth(yearS, month))
					System.out.println("Il giorno non è stato inserito correttamente");
				else
					ok=true;
			}catch(NumberFormatException nfe){
				System.out.println("Il giorno non è stato inserito correttamente");
			}
		}	//Il giorno è stato inserito
		ok=false;
		int hour=-1;
		while(!ok){
			System.out.println("Step 6/7: Inserire ora");
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
			System.out.println("Step 7/7: Inserire minuto");
			String x=sc.nextLine();
			try{
				minute=Integer.parseInt(x);
				if(minute<0 || minute>59)
					System.out.println("Il minuto non è stato inserito correttamente");
				else ok=true;
			}catch(NumberFormatException nfe){
				System.out.println("Il minuto non è stato inserito correttamente");
			}
		}	//Il minuto è stato inserito correttamente
		Memo t=new Memo(desc,priority,yearS,month,day,hour,minute);
		k.add(t);
	}
	
	public boolean remove(){
		
		return false;
	}
	
	/**
	 * Stampa il numero di memo attivi
	 */
	public void size(){
		
		System.out.println(k.memoAttivi());
	}
	
	/**
	 * Stampa lista dei Memo attivi
	 */
	public void list(){
		
		System.out.println("Memo attivi:"+k.memoAttivi());
		MemoList ml=k.getTotalList();
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
	public void listAll(){
		
		System.out.println("Memo totali:"+k.memoTotali());
		System.out.println(k.stampaMemoTotali());
	}
	
	public void listToday(){
		
		k.f
	}
	
	public void listWeek(){
		
		
	}
	
	/**
	 * Salva le modifiche sul file
	 */
	public void salva(){
		
		System.out.println("Salvataggio in corso...");
		k.salva();
		System.out.println("Salvataggio effettuato");
	}
	
	public void statistiche(){
		
		Object[] stat=k.statistiche();
		System.out.print("Memo con priorità alta: \t"+stat[0]+"\n");
		System.out.print("Memo con priorità media:\t"+stat[1]+"\n");
		System.out.print("Memo con priorità bassa:\t"+stat[2]+"\n");
		System.out.print("Memo completati:\t\t"+stat[3]+"\n");
		System.out.print("Memo archiviati:\t\t"+stat[4]+"\n");
		System.out.print("Memo attivi:\t\t\t"+stat[5]+"\n");
		System.out.print("Memo totali:\t\t\t"+stat[6]+"\n");
	}
	
	public void modifyPassword(){
		
		System.out.print("Inserire la password corrente:");
		System.out.println("Inserire nuova password:");
		System.out.println("Conferma nuova password:");
	}
	
	public void deleteUser(){
		
		System.out.println("Attenzione, questo processo rimuoverà in modo irreversibile tutti i tuoi dati. Sei sicuro di voler continuare? s/n");
		
	}
	public void profilo(){
		
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
	
	public void clear(){
		
		System.out.println("Database svuotato");
		k.clear();
	}
	public static void main(String[] args){
		
		MemoremCLI t=new MemoremCLI();
		System.out.println(t.OS);
		t.start();
	}

}
