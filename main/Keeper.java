package main;

import java.util.*;

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
public class Keeper implements Iterable<Memo>{
	
	private User user;
	/*
	 * La MemoList conterrà i memo da utilizzare e visualizzare in runtime
	 */
	private MemoList currentMemos;		
	private LinkedHashSet<Memo> removenda;
	private LinkedHashMap<Memo,Memo> mutanda;//che in latino significa "da modificare"..ma in italiano lol xD
	private MemoDBManager manager;
	private double completati;
	private double scaduti;
	
	public Keeper(){
		
		this.user=new User("none");
		this.currentMemos=new MemoList();
		this.removenda=new LinkedHashSet<Memo>();
		this.mutanda=new LinkedHashMap<Memo,Memo>();
		this.manager=new MemoDBManager("memodatabase");
	}
	
	/**
	 * Aggiunge un nuovo task nella lista, aggiornando l'archivio
	 * @param t
	 */
	public boolean add(Memo t){
		
		if(!t.isScaduto()){
			return currentMemos.add(t);
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
	
	public void modifica(Memo vecchio,Memo nuovo){
		
		currentMemos.remove(vecchio);
		currentMemos.add(nuovo);
		mutanda.put(vecchio, nuovo);
		
	}
	/**
	 * Invia la notifica di completamento del memo, che viene salvato così tra i passati
	 * @param m
	 */
	public void completa(Memo m,boolean completato){
		
		if(manager.contains(m)){
			currentMemos.remove(m);
			manager.move(m,completato);
		}
		if(completato)
			completati++;
		scaduti++;
	}
	
	/**
	 * Invia la notifica di scaduta al memo, che viene salvato così tra i passati
	 * @param m
	 */
	public void scade(Memo m){
		
		if(manager.contains(m)){
			manager.removeMemo(m);
			manager.move(m,false);
		}
		scaduti++;
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
	 * Stampa in output i memo ancora attivi
	 * @return
	 */
	public String stampaMemoAttivi(){
		
		//aggiorna();
		MemoList ml=manager.list(true);
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
	 * Ritorna il numero di task attivi
	 * @return
	 */
	public int memoAttivi(){
		
		//aggiorna();
		return manager.size(true);
	}
	/**
	 * Ritorna il numero di task totali presenti nel database
	 * @return
	 */
	public int memoTotali(){
		
		//aggiorna();
		return manager.size(false);
	}
	
	/**
	 * Ritorna la lista completa di memo
	 * @return
	 */
	public MemoList getTotalList(){
		
		MemoList ml=manager.list(false);			//qui ci sono tutti i memo del database
		MemoList ml2=new MemoList(currentMemos);
		for(Memo m: removenda)
			if(ml.contains(m))
				ml.remove(m);
		for(Memo m:ml2)
			if(!ml.contains(m)){
				//System.out.println(m);
				ml.add(m);
			}
		return ml;
	}
	
	/**
	 * Si connette al database e raccoglie le statistiche
	 * @return
	 */
	public Object[] statistiche(){
		
		return manager.statistiche();
	}
	
	/**
	 * Si connette al database e raccoglie i suggerimenti
	 * @return
	 */
	public String[] aiuti(){
		
		return manager.getHelps();
	}

	/**
	 * Rimuove un task dalla lista e aggiorna l'archivio
	 * @param t
	 * @return
	 */
	public void remove(Memo t){
		
		Memo x=manager.get(t.description(), t.getEnd());
		if(x!=null && x.equals(t) && !currentMemos.contains(t))		//significa che il memo è contenuto soltanto nel manager, ma non per forza anche in currentMemos
			removenda.add(t);
		else if(!manager.contains(t) && currentMemos.contains(t)){	//significa che il memo da eliminare è stato modificato di recente, quindi non c'è nel manager
			mutanda.remove(t);
			currentMemos.remove(t);
		}
		else if(manager.contains(t) && currentMemos.contains(t)){
			currentMemos.remove(t);
			removenda.add(t);
		}
	}
	
	/**
	 * Cancella tutti i dati dell' utente nell'archivio. Una volta invocato il metodo non è possibile ritornare indietro
	 */
	public void clear(){
		
		currentMemos.clear();
		removenda.clear();
		manager.clear();
	}
	
	public void reset(){
		
		if(!user.equals("admin"))
			return;
		manager.reset();
		clear();
	}
	
	public void removeUser(String utente){
		
		manager.removeUser(utente);
	}
	
	/**
	 * Ritorna true se il memo è contenuto nel database (sia se attivo che se scaduto)
	 * @param t
	 * @return
	 */
	public boolean contains(Memo t){
		
		return currentMemos.contains(t) && !removenda.contains(t);
	}

	/**
	 * Forma la query da inviare al database per ricevere soltanto i dati che si vogliono visualizzare
	 * @param data
	 * @param visual
	 * @param prior
	 */
	public void formaQuery(String data,String visual,LinkedList<String> prior){
		
		String query="SELECT * FROM ";
		switch(visual){
		case "attivi": query=query+"memodatanew"; break;
		default: query=query+"memodataold"; break;
		}
		query=query+" WHERE user='"+user+"' ";
		if(!data.equals("always")){
			if(visual.equals("attivi")){	//visualizzare da oggi verso il futuro
				switch(data){
				case "oggi":
				query=query+"AND date_add(curdate() , interval 0 day)>=end";	
				break;
				
				case "week":
				query=query+"AND date_add(curdate(), interval 7 day)>=end";
				break;
				
				case "month":
				query=query+"AND month(end)=month(curdate())";
				break;
				
				case "year":
				query=query+"AND year(end)=year(curdate())";
				break;
				
				default: break;		//nel caso di always
				}
				query=query+" AND end>curdate()";
			}
			else{						//visualizzare da oggi verso il passato
				switch(data){
				case "oggi":
				query=query+"AND date_sub(curdate() , interval 0 day)<=end";
				break;
				
				case "week":
				query=query+"AND date_sub(curdate() , interval 7 day)<=end";
				break;
				
				case "month":
				query=query+"AND month(curdate())=month(end)";
				break;
				
				case "year":
				query=query+"AND year(end)=year(curdate())";
				break;
				
				default: break;
				}
			}
		}//abbiamo analizzato in quale range di tempo bisogna visualizzare i memo
		if(prior.size()==0){
			currentMemos=new MemoList();
			return;
		}
		if(prior.size()==1){
			if(prior.get(0).equals("Alta"))
				query=query+" AND prior='2'";
			else if(prior.get(0).equals("Bassa"))
				query=query+" AND prior='0'";
			else query=query+" AND prior='1'";
		}
		else if(prior.size()==2){
			if(!prior.contains("Alta"))
				query=query+" AND prior NOT LIKE '2'";
			else if(!prior.contains("Bassa"))
				query=query+" AND prior NOT LIKE '0'";
			else query=query+" AND prior NOT LIKE '1'";
		}
		query+=" ORDER BY prior DESC, end";
		//System.out.println(query);
		boolean scaduti=visual!="attivi";
		currentMemos.clear();
		currentMemos=manager.processQuery(query,scaduti);
	}
	
	/**
	 * Ritorna la lista corrente di memo (utile se invocata immediatamente dopo una query personalizzata
	 * @return
	 */
	public MemoList getCurrentList(){
		
		return currentMemos;
	}
	
	public void salva(){
		
		for(Memo m: removenda)
			manager.removeMemo(m);
		for(Memo m: mutanda.keySet())
			manager.modifyMemo(m, mutanda.get(m));
		for(Memo m: currentMemos)
			if(!manager.contains(m))
				manager.insertMemo(m);
	}
	
	public int login(String user,String password){
		
		int x=manager.login(user, password);
		if(x==0){
			this.user=manager.getUser();
			for(Memo m: manager.list(true))
				currentMemos.add(m);
		}
		int[] risu=manager.rapportoCompletati();
		completati=risu[0];
		scaduti=risu[1];
		return x;
	}
	
	public boolean logout(){
		
		if(manager.logout()){
			currentMemos.clear();
			removenda.clear();
			mutanda.clear();
			this.user=new User("none");
			return true;
		}
		else return false;
	}
	
	
	public int signUp(String user,String password,String nome,String cognome,char genere){
		
		int res=manager.addUser(user, password,nome,cognome,genere);
		if(res==0)
			this.user=manager.getUser();
		return res;
	}
	
	public int modificaPassword(String passV,String passN){
		
		return manager.modificaPassword(passV,passN);
	}
	
	public User getUser(){
		
		return user;
	}
	
	public String[] userList(){
		
		return manager.userList();
	}
	
	/**
	 * Ritorna un iteratore per scandire i vari oggetti
	 */
	@Override
	public Iterator<Memo> iterator() {
		
		return currentMemos.iterator();
	}
			
	
	public static void main(String[] args){
		
		Memo t1=new Memo("t1","b",2014,11,30,15,00);
		Memo t2=new Memo("t2","b",2014,11,29,23,00);
		Memo t3=new Memo("t3","a",2013,3,9,15,00);
		Memo t4=new Memo("t4","c",2013,12,8,21,00);
		Memo t5=new Memo("t5","a",2015,2,2,13,00);
		Memo t6=new Memo("t6","c",2013,12,13,12,60);
		Keeper k=new Keeper();
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