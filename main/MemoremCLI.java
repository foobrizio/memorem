package main;

import java.io.*;
import java.util.*;

public class MemoremCLI extends Thread{
	
	/**
	 * 
	 * Cosa implementare ancora:
	 * 
	 * 1) Collegamento a interfaccia grafica
	 * 2) Struttura command line
	 * 3) Struttura Thread invia-ricevi, in collaborazione con Keeper
	 * 4) Comandi di interazione principali col server
	 */
	
	private Keeper k;
	private String user;			//è il nome dell'utente che utilizza il programma
	private String OS;				//è il nome del sistema operativo che si utilizza
	private String interFace;		//stabilisce l'interfaccia da utilizzare
	private File fileSalvataggio;	//file di salvataggio
	private Scanner sc;				//verrà utilizzato per l'interfacca CLI
	
	public MemoremCLI(){
		
		user=System.getProperty("user.name");
		OS=System.getProperty("os.name");
		System.out.println("Buongiorno "+user);
		File dir=new File("/home/"+user+"/.memorem/");
		k=new Keeper();
		k.signUp(user, "ciao")
		if(!dir.exists()){	//creiamo la cartella se non esiste
			if(dir.mkdir())
				System.out.println("Cartella creata");
			else System.out.println("Problemi durante la creazione della cartella home");
			//fileSalvataggio=new File(dir.getAbsolutePath()+"/.savefile.tsk");
			//k.setSaveFile(fileSalvataggio.getAbsolutePath());
		}
		else{
			fileSalvataggio=new File(dir.getAbsolutePath()+"/.savefile.tsk");
			//k.setSaveFile(fileSalvataggio.getAbsolutePath());
			if(fileSalvataggio.exists())
				k.carica();
		}
		
	}
	
	public void run(){
		
		while(!isInterrupted()){
			if(interFace.equals("CLI")){
				System.out.print(user+"@memorem: ");
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
			else if(comando.equals("add"))
				add();
			else if(comando.equals("clear"))
				clear();
			else if(comando.equals("size"))
				size();
			else if(comando.equals("list"))
				list();
			else if(comando.equals("list-all"))
				listAll();
			else if(comando.equals("salva"))
				salva();
			else if(comando.equals("carica"))
				carica();
			else if(comando.equals("quit"))
				this.interrupt();	
		}
		return;	
	}
	
	private String savePath(){
		
		return fileSalvataggio.getAbsolutePath();
	}
	
	private boolean setInterface(String interFace){
		if(interFace.equals("GUI") || interFace.equals("CLI")){
			System.out.println("Hai scelto la modalita' "+interFace);
			this.interFace=interFace;
			return true;
		}
		else{
			System.out.println("Interfaccia non supportata");
			return false;
		}
			
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
			System.out.println("quit\t\tchiude il programma");
			System.out.println("add\t\tavvia l'editor per aggiungere un task");
			System.out.println("remove\t\tavvia l'editor per rimuovere un task");
			System.out.println("list\t\tstampa la lista totale dei task");
			System.out.println("list-today\tstampa la lista dei task odierni");
			System.out.println("list-week\tstampa la lista dei task della settimana corrente");
			System.out.println("list-active\tstampa la lista dei task attivi");
			System.out.println("size\t\tstampa il numero di task presenti");
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
			else
				System.out.println("UNKNOWN COMMAND");
		}
	}
	
	/**
	 * Aggiunge un Memo al MemoRem
	 */
	@SuppressWarnings("static-access")
	public void add(){
		System.out.println("Creazione di un nuovo Memo");
		System.out.println("Step 1/7: Inserire descrizione del Memo");
		String desc=sc.nextLine().trim();
		System.out.println("Step 2/7: Inserire priorità(default: normal)");
		String priority=sc.nextLine().trim();
		if(priority.length()==0)
			priority="normal";
		GregorianCalendar gc=new GregorianCalendar();
		System.out.println("Step 3/7: Inserire anno(default:"+gc.get(gc.YEAR)+")");
		String year=sc.nextLine();
		int yearS;
		if(year.length()==0)
			yearS=gc.get(gc.YEAR);
		else yearS=Integer.parseInt(year);
		System.out.println("Step 4/7: Inserire mese");
		int month=sc.nextInt();
		System.out.println("Step 5/7: Inserire giorno");
		int day=sc.nextInt();
		System.out.println("Step 6/7: Inserire ora");
		int hour=sc.nextInt();
		System.out.println("Step 7/7: Inserire minuto");
		int minute=sc.nextInt();
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
		
		System.out.println(k.taskAttivi());
	}
	
	/**
	 * Stampa lista dei Memo attivi
	 */
	public void list(){
		
		System.out.println("Memo attivi:"+k.taskAttivi());
		System.out.println(k.stampaTaskAttivi());
	}
	
	/**
	 * Stampa la lista di tutti i Memo
	 */
	public void listAll(){
		
		System.out.println("Memo totali:"+k.taskTotali());
		System.out.println(k.stampaTaskTotali());
	}
	
	/**
	 * Salva le modifiche sul file
	 */
	public void salva(){
		
		System.out.println("Salvataggio in corso...");
		//fileSalvataggio=new File(k.getSaveFilePath());
		if(!(fileSalvataggio.exists()))
			try{
				fileSalvataggio.createNewFile();
				k.salva();
			}catch(IOException e){
				e.printStackTrace();
				System.out.println("Impossibile salvare al momento. Errore I/O");
				return;
			}
		else
			k.salva();
		System.out.println("Salvataggio effettuato");
	}
	
	/**
	 * Carica il file con i dati
	 */
	public void carica(){
		
		System.out.println("Caricamento in corso...");
		//fileSalvataggio=new File(k.getSaveFilePath());
		if(!fileSalvataggio.exists()){
			String saveFile="/home/"+user+"/.taskman/";
			System.out.print("Inserire il nome del file:");
			String nome=sc.nextLine();
			saveFile=saveFile+"."+nome;
			//k.setSaveFile(saveFile);
		}
		boolean loaded=k.carica();
		if(loaded)
			System.out.println("Caricamento riuscito");
		else
			System.out.println("Caricamento non riuscito");
	}
	
	public void clear(){
		
		System.out.println("Database svuotato");
		k.clear();
	}
	public static void main(String[] args){
		
		MemoremCLI t=new MemoremCLI();
		
		t.sc=new Scanner(System.in);
		System.out.println(t.OS);
		boolean reached=false;
		while(!reached){
			if(t.OS.equals("Windows")){
				reached=t.setInterface("GUI");
			}
			else if(t.OS.equals("Linux")){
				System.out.println("Che tipo di interfaccia preferisci? Scegliere tra GUI e CLI");
				String intfc=t.sc.nextLine().trim().toUpperCase();
				reached=t.setInterface(intfc);
			}
		}
		t.start();
	}

}
