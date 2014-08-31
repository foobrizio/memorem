package main;

import graphic.MemoremGUI.Lang;

import java.util.*;

import database.DBManager;
import util.*;


/**
 * 
 * La classe Keeper sarà il server della situazione
 * @author fabrizio
 * 
 * 
 * Cosa implementare ancora:
 *
 * 1) Memo più personalizzati (compleanni, festività, ecc..)
 * 2) Aggiustare tutti i metodi per sistemare la compatibilità tra memoList e database
 *
 */
public class Keeper{
	
	private User user;
	/*
	 * La MemoList conterrà i memo da utilizzare e visualizzare in runtime
	 */
	private MemoList DBMemos;		
	private TreeSet<Memo> removenda;
	private TreeSet<Memo> nuovi;
	private HashMap<String,Memo> mutanda;//che in latino significa "da modificare"..ma in italiano lol xD
	private TreeSet<Memo> today;
	private double completati;
	private double scaduti;
	
	public Keeper(){
		
		this.user=new User("none");
		user.setLingua("en");
		DBManager.prepareConnection("memodatabase");
	}
	
	public void start(){
		
		this.user=new User("none");
		this.DBMemos=new MemoList();
		this.removenda=new TreeSet<Memo>();
		this.nuovi=new TreeSet<Memo>();
		this.mutanda=new HashMap<String,Memo>();
		this.today=new TreeSet<Memo>();
	}
	/**
	 * Aggiunge un nuovo task nella lista, aggiornando l'archivio
	 * @param t
	 */
	public boolean add(Memo t){
		
		if(!t.isScaduto()){
			if(t.getEnd().isToday())
				today.add(t);
			System.out.println("Aggiunta di "+t.getId());
			return nuovi.add(t);
			
		}
		else{
			System.out.println("Il task e' gia' scaduto, pertanto non verra' aggiunto");
			return false;
		}
	}
	
	public void add(Memo... t){
		
		for(Memo tt : t)
			add(tt);
	}
	
	/**
	 * Si connette al database e raccoglie i suggerimenti
	 * @return
	 */
	public String[] aiuti(){
		
		String lang="en";
		Lang x;
		if(user==null)
			x=Lang.EN;
		else x=user.getLingua();
		switch(x){
		case IT: lang="it"; break;
		case ES: lang="es"; break;
		case DE: lang="de"; break;
		default: lang="en"; break;
		}
		return DBManager.getHelps(lang);
	}

	
	public void cancellaStorico(){
		
		if(!user.isGuest())
		DBManager.cancellaStorico(user);
	}

	/**
	 * Cancella tutti i dati dell' utente nell'archivio. Una volta invocato il metodo non è possibile ritornare indietro
	 */
	public void clear(){
		
		DBMemos.clear();
		removenda= new TreeSet<Memo>();
		mutanda= new HashMap<String,Memo>();
		nuovi=new TreeSet<Memo>();
		today.clear();
		if(!user.isGuest())
			DBManager.clear(user);
	}

	/**
	 * Invia la notifica di completamento del memo, che viene salvato così tra i passati
	 * @param m
	 */
	public void completa(Memo m,boolean completato){
		
		System.out.println("Completamento di "+m.getId());
		if(user.isGuest()){
			System.out.println("Sono un guest");
			mutanda.remove(m);
			nuovi.remove(m);
			if(today.contains(m))
				today.remove(m);
			completati++;
			scaduti++;
			return;
		}
		if(DBManager.contains(user,m)){//è nel DB?
			System.out.println("é nel database xD");
			DBManager.move(user,m,completato);
		}
		else{
			if(completato){
				System.out.println("non è nel database");
				DBManager.completa(user,m);
				//DBMemos.add(m);
			}
			else
				DBManager.archivia(user,m);
		}
		nuovi.remove(m);
		today.remove(m);
		mutanda.remove(m);
		if(completato)
			completati++;
		scaduti++;
	}

	/**
	 * Ritorna true se esiste nel database un memo identico ma con iD diverso
	 * @param t
	 * @return
	 */
	public boolean containsEqualInDB(Memo t){
		
		MemoList ml=DBManager.list(user,true);
		for(Memo m:ml){
			String id=m.getId();
			String desc=m.description();
			Data end=m.getEnd();
			if(!id.equals(t.getId()))
				if(t.description().equals(desc))
					if(t.getEnd().equals(end))
						return true;
		}
		return false;
	}
	
	/**
	 * Elimina tutti i memo attivi dal database e dalle liste temporanee
	 */
	public void eliminaImpegni(){
		
		mutanda.clear();
		nuovi.clear();
		today.clear();
		Iterator<Memo> it=DBMemos.iterator();
		while(it.hasNext())
			if(!it.next().isScaduto())
				it.remove();
		if(!user.isGuest()){
			System.out.println("olle2");
			DBManager.eliminaImpegni(user);
		}
	}
	
	public MemoList getActiveList(){
		
		String query="SELECT * FROM memodatanew WHERE user='"+user+"' ORDER BY prior, end";
		MemoList ml=DBManager.processQuery(user, query, false);
		for(Memo m: nuovi)
			ml.add(m);
		for(Memo m: removenda)
			ml.remove(m);
		for(String id:mutanda.keySet()){
			ml.remove(id);
			ml.add(mutanda.get(id),false);
		}
		return ml;
	}
	/**
	 * Ritorna la lista corrente di memo (utile se invocata immediatamente dopo una query personalizzata
	 * @return
	 */
	public MemoList getDBList(){
		
		
		return DBMemos;
	}

	/**
	 * Ritorna la lista dei memo scaduti da gestire
	 * @return
	 */
	public MemoList getPending(){
		
		LinkedList<String> p=new LinkedList<String>();
		p.add("Alta");
		p.add("Media");
		p.add("Bassa");
		MemoList ml=new MemoList();
		if(!user.isGuest())
			formaQuery("pending", "attivi", p);
		ml=new MemoList(getDBList());
		for(Memo m:today){
			if(!ml.contains(m) && m.isScaduto())
				ml.add(m);
		}
		return ml;
	}

	public MemoList getRealList(){
		
		MemoList questa=new MemoList(DBMemos);
		for(Memo m: removenda)
			questa.remove(m);
		for(String id:mutanda.keySet()){
			questa.remove(id);
			questa.add(mutanda.get(id),false);
		}
		for(Memo m:nuovi)
			questa.add(m,false);
		return questa;
	}

	public MemoList getStandardMemos(){
		
		return DBManager.getStandardMemos(user);
	}
	public TreeSet<Memo> getTodayMemos(){
		
		return today;
	}
	/**
	 * Ritorna la lista completa di memo contenuti nel Keeper
	 * @return
	 */
	public MemoList getTotalList(){
		
		if(user.isGuest()){
			MemoList ml=new MemoList(DBMemos);
			for(Memo m: removenda) //controllata la removenda
				if(ml.contains(m))
					ml.remove(m);
			for(Memo m: nuovi)	//controllati i nuovi
				ml.add(m,false);
			for(String id: mutanda.keySet()){
				if(ml.contains(id)){
					System.out.println("Ma questo memo non dovrebbe essere contenuto");
					ml.remove(id);
				}
				ml.add(mutanda.get(id));
			}//controllata la mutanda (spero fosse pulita)
			return ml;
		}
		MemoList ml=DBManager.list(user,false);			//qui ci sono tutti i memo del database
		for(String id: mutanda.keySet()){
			if(ml.contains(id))
				ml.remove(id);
			ml.add(mutanda.get(id));	
		}//mutanda controllata
		for(Memo m: nuovi){
			if(ml.contains(m))
				System.out.println("Perchè c'è già????");
			else
				ml.add(m);
		}//nuovi controllati
		for(Memo m: removenda)
			if(ml.contains(m))
				ml.remove(m);
		return ml;
	}

	public User getUser(){
		
		return user;
	}

	/*
	 * Ritorna un iteratore per scandire i vari oggetti
	 
	@Override
	public Iterator<Memo> iterator() {
		
		return DBMemos.iterator();
	}*/

	/**
	 * Verifica che i filtri espressi nella GUI vengano rispettati (Funziona solo per l'interfaccia GUI)
	 * @param pF
	 * @param dF
	 * @param m
	 * @return
	 */
	public static boolean filtriViolati(boolean[] pF,boolean[] dF,Memo m){
			
		int prior=m.priority();
		Data d=m.getEnd();
		if(!pF[0] && prior==2)
			return true;
		if(!pF[1] && prior==1)
			return true;
		if(!pF[2] && prior==0)
			return true;
		//ABBIAMO CONTROLLATO LE PRIORITA'
		Data now=new Data();
		int tempo=Data.diff(d, now);
		if(dF[0]){ 			//entro oggi
			if(tempo>1)
				return true;
		}
		else if(dF[1]){	//entro settimana
			//int tempo=Data.diff(d, now);
			//System.out.println(tempo);
			if(tempo>7)
				return true;
		}
		else if(dF[2]){	//entro un mese
			if(tempo>Data.daysOfMonth(now.anno(),now.mese()))
				return true;
		}
		else if(dF[3]){ //entro un anno
			if(tempo>Data.daysOfYear(now.anno()))
				return true;
		}
		else if(dF[4]){	//già scaduto
			//System.out.println("wewe");
			if(d.compareTo(now)>0)
				return true;
		}
		return false;
	}

	/**
	 * Forma la query da inviare al database per ricevere soltanto i dati che si vogliono visualizzare
	 * @param data
	 * @param visual
	 * @param prior
	 */
	public MemoList formaQuery(String data,String visual,LinkedList<String> prior){
		
		String query="SELECT * FROM ";
		boolean[] dF=new boolean[5];
		for(int i=0;i<dF.length;i++)
			dF[i]=false;
		boolean[] pF=new boolean[3];
		for(int i=0;i<pF.length;i++)
			pF[i]=true;
		if(data.equals("standard")){
			query=query+"memodatanew WHERE user='"+user.getNickname()+"' AND end<date_add(curdate(), interval 7 day) ";
			dF[1]=true;
			if(prior.size()==0){
				DBMemos=new MemoList();
				return DBMemos;
			}
			if(prior.size()==1){
				if(prior.get(0).equals("Alta")){
					pF[1]=false;
					pF[2]=false;
					query=query+" AND prior='2'";
				}
				else if(prior.get(0).equals("Bassa")){
					pF[0]=false;
					pF[1]=false;
					query=query+" AND prior='0'";
				}
				else{
					pF[0]=false;
					pF[2]=false;
					query=query+" AND prior='1'";
				}
			}
			else if(prior.size()==2){
				if(!prior.contains("Alta")){
					pF[0]=false;
					query=query+" AND prior NOT LIKE '2'";
				}
				else if(!prior.contains("Bassa")){
					pF[2]=false;
					query=query+" AND prior NOT LIKE '0'";
				}
				else{
					pF[1]=false;
					query=query+" AND prior NOT LIKE '1'";
				}
			}
			query=query+" ORDER BY prior DESC, end";
			DBMemos=DBManager.processQuery(user,query,false);
			MemoList questa=new MemoList(DBMemos);
			for(Memo m: removenda)
					questa.remove(m);
			for(Memo m: nuovi){
				if(!filtriViolati(pF,dF,m))
					questa.add(m);
			}
			for(String id:mutanda.keySet()){
				questa.remove(id);
				if(!filtriViolati(pF, dF,mutanda.get(id)))
					questa.add(mutanda.get(id),false);
			}
			return questa;
		}//non è standard
		switch(visual){
		case "attivi": query=query+"memodatanew"; break;
		default: query=query+"memodataold"; break;
		}
		query=query+" WHERE user='"+user.getNickname()+"' ";
		if(!data.equals("always")){
			if(visual.equals("attivi")){	//visualizzare da oggi verso il futuro
				switch(data){
				case "pending":
				dF[4]=true;
				//System.out.println("pending");
				query=query+"AND end<now() ";
				break;
				
				case "oggi":
				dF[0]=true;
				query=query+"AND date_add(curdate() , interval 0 day)>=end ";	
				break;
				
				case "week":
				dF[1]=true;
				query=query+"AND date_add(curdate(), interval 7 day)>=end ";
				break;
				
				case "month":
				dF[2]=true;
				query=query+"AND month(end)=month(curdate()) ";
				break;
				
				case "year":
				dF[3]=true;
				query=query+"AND year(end)=year(curdate()) ";
				break;
				
				default: break;		//nel caso di always
				}
				if(!data.equals("pending"))
					query=query+" AND end>curdate() ";
			}
			else{						//visualizzare da oggi verso il passato
				switch(data){
				case "oggi":
				query=query+"AND date_sub(curdate() , interval 0 day)<=end ";
				break;
				
				case "week":
				query=query+"AND date_sub(curdate() , interval 7 day)<=end ";
				break;
				
				case "month":
				query=query+"AND month(curdate())=month(end) ";
				break;
				
				case "year":
				query=query+"AND year(end)=year(curdate()) ";
				break;
				
				default: break;
				}
			}
		}//abbiamo analizzato in quale range di tempo bisogna visualizzare i memo
		if(prior.size()==0){
			DBMemos=new MemoList();
			return DBMemos;
		}
		if(prior.size()==1){
			if(prior.get(0).equals("Alta")){
				pF[1]=false;
				pF[2]=false;
				query=query+"AND prior='2' ";
			}
			else if(prior.get(0).equals("Bassa")){
				pF[0]=false;
				pF[1]=false;
				query=query+"AND prior='0' ";
			}
			else{
				pF[0]=false;
				pF[2]=false;
				query=query+"AND prior='1' ";
			}
		}
		else if(prior.size()==2){
			if(!prior.contains("Alta")){
				pF[0]=false;
				query=query+"AND prior NOT LIKE '2' ";
			}
			else if(!prior.contains("Bassa")){
				pF[2]=false;
				query=query+"AND prior NOT LIKE '0' ";
			}
			else{
				pF[1]=false;
				query=query+"AND prior NOT LIKE '1' ";
			}
		}
		query+="ORDER BY prior DESC, end";
		boolean scaduti=(visual!="attivi");
		DBMemos=DBManager.processQuery(user,query,scaduti);
		MemoList questa=new MemoList(DBMemos);
		if(!visual.equals("scaduti")){	
			/*i memo rimossi, quelli modificati e quelli nuovi hanno 
			 * senso solo se vogliamo visualizzare i memo attivi
			 */
			for(Memo m: removenda)
				questa.remove(m);
			for(String id:mutanda.keySet()){
				questa.remove(id);
				if(!filtriViolati(pF, dF,mutanda.get(id)))
					questa.add(mutanda.get(id),false);
			}
			for(Memo m:nuovi)
				if(!filtriViolati(pF, dF,m))
					questa.add(m,false);
		}
		return questa;
	}

	/**
	 * Connette un utente registrato al database
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean login(String user,String password){
		
		if(user.equals("guest")){
			this.user=new User("guest");
			return true;
		}
		User connesso=DBManager.login(user,password);
		if(connesso!=null){
			this.user=connesso;
			this.removenda=new TreeSet<Memo>();
			this.nuovi=new TreeSet<Memo>();
			this.mutanda=new HashMap<String,Memo>();
			this.today=new TreeSet<Memo>();
			DBMemos=new MemoList(DBManager.getStandardMemos(connesso));
			today();
		}
		int[] risu=DBManager.rapportoCompletati(connesso);
		completati=risu[0];
		scaduti=risu[1];
		return connesso!=null;
	}

	/**
	 * Disconnette l'utente attuale
	 * @return
	 */
	public boolean logout(){
		
		if(this.user.isGuest()){
			DBMemos.clear();
			removenda=new TreeSet<Memo>();
			mutanda=new HashMap<String,Memo>();
			nuovi=new TreeSet<Memo>();
			today=new TreeSet<Memo>();
			this.user=new User("none");
			return true;
		}
		if(DBManager.logout(user)){
			DBMemos.clear();
			removenda=new TreeSet<Memo>();
			mutanda=new HashMap<String,Memo>();
			nuovi=new TreeSet<Memo>();
			today=new TreeSet<Memo>();
			this.user=new User("none");
			return true;
		}
		else return false;
	}

	/**
	 * Ritorna il numero di memo attivi
	 * @return
	 */
	public int memoAttivi(){
		
		//aggiorna();
		return DBManager.size(user,true);
	}
	
	/**
	 * Ritorna il numero di memo nello storico
	 * @return
	 */
	public int memoStorici(){
		
		return DBManager.sizeVecchi(user);
	}
	/**
	 * Ritorna il numero di memo totali presenti nel database
	 * @return
	 */
	public int memoTotali(){
		
		//aggiorna();
		return DBManager.size(user,false);
	}

	/**
	 * Modifica un memo. Le modifiche saranno permanenti solo dopo il salvataggio
	 * @param vecchio
	 * @param nuovo
	 */
	public void modifica(Memo vecchio,Memo nuovo){
		
		if(DBMemos.contains(vecchio))
			DBMemos.remove(vecchio);
		if(nuovi.contains(vecchio))	
			nuovi.remove(vecchio); //il memo non è più considerato nuovo, ma modificato
		if(today.contains(vecchio)){
			today.remove(vecchio);
			if(nuovo.getEnd().isToday())
				today.add(nuovo);
		}
		mutanda.put(vecchio.getId(), nuovo);
		
	}
	/**
	 * 
	 * @param passV
	 * @param passN
	 * @return 	3 se la password nuova è identica a quella vecchia,
	 * 		 	2 se la vecchia password è sbagliata,
	 * 			1 in caso di altri eventuali errori
	 * 			0 se la modifica va a buon fine
	 */
	public int modificaPassword(String passN){
		
		return DBManager.modificaPassword(user,passN);
	}
	
	public boolean modificaUtente(User vecchio, User aggiornato){
		
		if(this.user.equals(vecchio))
			this.user=aggiornato;
		return DBManager.modificaUtente(vecchio,aggiornato);
	}

	/**
	 * Ritorna la percentuale di memo completati su memo totali
	 * @return
	 */
	public int percentualeCompletati(){
		
		if(scaduti==0)
			return 0;
		return (int)Math.round((completati/scaduti)*100);
	}
	
	/**
	 * Rimuove un task dalla lista e aggiorna l'archivio
	 * @param t
	 * @return
	 */
	public void remove(Memo t){
		
		if(user.isGuest()){
			DBMemos.remove(t);
			mutanda.remove(t);
			nuovi.remove(t);
			today.remove(t);
			return;
		}
		Memo x=DBManager.get(user, t.description(), t.getEnd());
		if(x!=null)
			removenda.add(t);
		DBMemos.remove(t);
		mutanda.remove(t);
		nuovi.remove(t);
		today.remove(t);
	}

	/**
	 * Rimuove un utente dal database
	 * @param utente
	 * @return
	 */
	public boolean removeUser(String utente){
		
		return DBManager.removeUser(utente);
	}

	/**
	 * Resetta tutto il DBManager. Utilizzabile soltanto dall'admin
	 */
	public void reset(){
		
		if(!user.isAdmin())
			return;
		DBManager.reset();
		clear();
	}

	/**
	 * Salva le modifiche nel database
	 */
	public void salva(){
		
		for(Memo m: removenda)
			DBManager.removeMemo(user,m);
		for(String id: mutanda.keySet())
			if(DBManager.modifyMemo(user, id, mutanda.get(id))){
				DBMemos.remove(id);
				DBMemos.add(mutanda.get(id));
			}
		for(Memo m: nuovi)
			if(DBManager.insertMemo(user, m))
				DBMemos.add(m);
			
		removenda=new TreeSet<Memo>();
		mutanda=new HashMap<String,Memo>();
		nuovi=new TreeSet<Memo>();
	}

	/**
	 * Creiamo un nuovo utente
	 * @param user
	 * @param password
	 * @param nome
	 * @param cognome
	 * @param genere
	 * @param login: se true ci si connette al nuovo utente
	 * @return 0 se l'utente è stato aggiunto, 1 se esiste già, 2 per eventuali altri errori.
	 */
	public int signUp(String user,String password,String nome,String cognome,char genere,String lingua,boolean login){
		
		User utente=new User(user,nome,cognome,genere,lingua);
		utente.setPassword(password);
		int res=DBManager.addUser(utente);
		if(res==0){
			if(login){
				DBMemos=new MemoList();
				removenda=new TreeSet<Memo>();
				mutanda=new HashMap<String,Memo>();
				nuovi=new TreeSet<Memo>();
				completati=0;
				scaduti=0;
				DBManager.login(user, password);
				this.user=utente;
			}
			System.out.println("Siamo loggati come "+this.user.getNickname());
		}
		return res;
	}

	/**
	 * Stampa in output i memo ancora attivi
	 * @return
	 */
	public String stampaMemoAttivi(){
		
		//aggiorna();
		MemoList ml=DBManager.list(user, true);
		StringBuilder sb=new StringBuilder(1000);
		int cont=0;
		for(Memo m:ml){
			if(cont>0)
				sb.append("\n");
			sb.append(m);
			cont++;
		}
		return sb.toString();
	}
	
	/**
	 * Stampa in output tutti i memo presenti nel database
	 * @return
	 */
	public String stampaMemoTotali(){
		
		//aggiorna();
		MemoList ml=getTotalList();
		StringBuilder sb=new StringBuilder(1000);
		int cont=0;
		for(Memo m:ml){
			if(cont>0)
				sb.append("\n");
			sb.append(m);
			cont++;
		}
		return sb.toString();
	}
	
	/**
	 * Si connette al database e raccoglie le statistiche
	 * @return
	 */
	public Object[] statistiche(){
		
		return DBManager.statistiche(user);
	}
	
	private void today(){
		
		String query="SELECT * FROM memodatanew WHERE user='"+user.getNickname()+"' AND date_add(curdate() , interval 0 day)>=end";
		MemoList ml=DBManager.processQuery(user, query, false);
		for(Memo m:ml)
			this.today.add(m);
	}
	
	public int updateMemos(){
		
		//System.out.println("Update di: "+today.size());
		Iterator<Memo> it=today.iterator();
		int cont=0;
		while(it.hasNext()){
			Memo x=it.next();
			if(x.isScaduto() && !x.isNotificato()){
				x.setScadenzaNotificata();
				cont++;
			}
		}
		return cont;
		
	}
	/**
	 * Ritorna la lista degli utenti nel database
	 * @return
	 */
	public HashSet<User> userList(){
		
		return DBManager.userList();
	}
	
	public static void main(String[] args){
		
		Memo t1=new Memo("t1","b",2014,11,30,15,00);
		Memo t2=new Memo("t2","b",2014,11,29,23,00);
		Memo t3=new Memo("t3","a",2013,3,9,15,00);
		Memo t4=new Memo("t4","c",2013,12,8,21,00);
		Memo t5=new Memo("t5","a",2015,2,2,13,00);
		Memo t6=new Memo("t6","c",2013,12,13,12,60);
		Keeper k=new Keeper();
		k.start();
		k.add(t1,t2,t3,t4,t5,t6);
		/*try{
			k.start();
		}catch(Exception emAll){
			
		}*/
		//System.out.println(t1.toString());
		System.out.println("Task attivi="+k.memoAttivi());
		System.out.println("Task totali="+k.memoTotali());
	}
}