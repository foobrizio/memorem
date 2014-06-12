package main;

import java.util.*;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

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
	
	private String user;
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
		
		this.user="none";
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
			//return manager.insertMemo(t);
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
			System.out.println("spuntato...aggiungiamolo ai vecchi memo");
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
			System.out.println("Scaduto...aggiungiamolo ai vecchi memo");
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
	public String stampaTaskAttivi(){
		
		aggiorna();
		//return currentMemos.toString();
		return manager.toString(true);
	}
	
	/**
	 * Stampa in output tutti i memo presenti nel database
	 * @return
	 */
	public String stampaTaskTotali(){
		
		aggiorna();
		//return currentMemos.toString()+"\n"+pastMemos.toString();
		return manager.toString(false);
	}
	
	/**
	 * Ritorna il numero di task attivi
	 * @return
	 */
	public int taskAttivi(){
		
		aggiorna();
		//return currentMemos.size();
		return manager.size(true);
	}
	/**
	 * Ritorna il numero di task totali presenti nel database
	 * @return
	 */
	public int taskTotali(){
		
		aggiorna();
		return manager.size(false);
	}
	
	/**
	 * Ritorna la lista completa di memo
	 * @return
	 */
	public MemoList getTotalList(){
		
		MemoList ml=new MemoList(currentMemos);
		for(Memo m: removenda)
			if(ml.contains(m))
				ml.remove(m);
		return ml;
	}

	/**
	 * Rimuove un task dalla lista e aggiorna l'archivio
	 * @param t
	 * @return
	 */
	public void remove(Memo t){
		
		//aggiorna();
		System.out.println("descrizione:"+t.description());
		System.out.println("data:"+t.getEnd().toString());
		Memo x=manager.get(t.description(), t.getEnd());
		if(x!=null && x.equals(t) && !currentMemos.contains(t)){		//significa che il memo è contenuto soltanto nel manager, ma non per forza anche in currentMemos
			System.out.println("silent remove");
			removenda.add(t);
		}
		else if(!manager.contains(t) && currentMemos.contains(t)){	//significa che il memo da eliminare è stato modificato di recente, quindi non c'è nel manager
			System.out.println("after-modify remove");
			mutanda.remove(t);
			currentMemos.remove(t);
		}
		else if(manager.contains(t) && currentMemos.contains(t)){
			System.out.println("standard remove");
			currentMemos.remove(t);
			removenda.add(t);
		}
		//else if(pastMemos.contains(t))
		//	return pastMemos.remove(t);
		//return false; //non è stato trovato nulla
		
	}
	
	/**
	 * Cancella tutto l'archivio. Una volta invocato il metodo non è possibile ritornare indietro
	 */
	public void clear(){
		
		currentMemos.clear();
		removenda.clear();
		manager.clear();
	}
	
	/**
	 * Ritorna true se il memo è contenuto nel database (sia se attivo che se scaduto)
	 * @param t
	 * @return
	 */
	public boolean contains(Memo t){
		
		//return currentMemos.contains(t) || pastMemos.contains(t);
		return (currentMemos.contains(t)) && !removenda.contains(t);
	}
	
	/** 
	 * Scansiona l'archivio per vedere se qualche task è scaduto e lo 
	 * trasferisce nella lista apposita
	 */
	private void aggiorna(){
		
		if(manager.size(true)!=0){
			for(Memo t : manager){
				if(t.isScaduto()){ 	//il task è scaduto
					System.out.println("Task \""+t.description()+"\" scaduto...spostamento in corso");
					manager.move(t,false);
					return;
				}
				if(!t.isActive()){			//il task è già stato completato
					System.out.println("Task \""+t.description()+"\" disattivato...spostamento in corso");
					//anche in questo caso bisogna prima modificare il memo esistente e poi sostituirlo con quello già 
					//esistente dentro il database
					manager.move(t,true);
					return;
				}
			}
		}
	}
	
	/**
	 * Ritorna i task che scadono oggi sottoforma di String, più gli altri 
	 * task con priorità alta
	 * @param flag: Se settata su false non considera i task con priorità 
	 * alta che scadono tra più di un giorno
	 * @return
	 */
	public String taskOdierni(boolean flag){
		
		aggiorna();
		StringBuilder sb=new StringBuilder(100);
		Data oggi=new Data();
		MemoList high=new MemoList();
		for(Memo t: manager){
			Data ditti=t.getEnd();
			if(ditti.anno()==oggi.anno() && ditti.mese()==oggi.mese() && ditti.giorno()==oggi.giorno())
				sb.append(t.toString()+"\n");
			else if(t.priority()==2 && flag)	//i Task ad alta priorità vengono stampati dopo
				high.add(t);
		}
		if(flag){
			sb.append("Altri task con priorità alta:\n");
			for(Memo t:high){
				sb.append(t.toString()+"\n");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Ritorna i task che scadono entro una settimana sottoforma di String, più
	 * gli altri task con priorità alta
	 * @param flagOggi: Se settata su false non considera i task che scadono in meno di un giorno
	 * @param flagPriorita: Se settata su false non considera i task con priorità alta che scadono
	 * fra più di una settimana
	 * @return
	 */
	public String taskSettimana(boolean flagOggi,boolean flagPriorita){
		
		aggiorna();
		StringBuilder sb=new StringBuilder(100);
		Data oggi=new Data(); //data odierna
		Data oneWeek=new Data(oggi.anno(),oggi.mese(),(oggi.giorno()+7),oggi.ora(),oggi.minuto()); //data fra una settimana
		if(!flagOggi)
			oggi=new Data(oggi.anno(),oggi.mese(),(oggi.giorno()+1),0,0);
		MemoList high=new MemoList();
		for(Memo t: manager){
			Data dt=t.getEnd();
			if(dt.compareTo(oggi)>=0 && dt.compareTo(oneWeek)<=0)
				sb.append(t.toString()+"\n");
			else if(t.priority()==2)
				high.add(t);
		}
		if(flagPriorita){
			sb.append("Altri task con priorità alta:\n");
			for(Memo t:high){
				sb.append(t.toString()+"\n");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Forma la query da inviare al database per ricevere soltanto i dati che si vogliono visualizzare
	 * @param data
	 * @param visual
	 * @param prior
	 */
	public void formaQuery(String data,String visual,LinkedList<String> prior){
		
		System.out.println("formiamo la query");
		String query="SELECT * FROM ";
		switch(visual){
		case "attivi": query=query+"memodatanew"; break;
		default: query=query+"memodataold"; break;
		}
		Data ora=new Data();
		if(!data.equals("always")){
			query=query+" WHERE ";
			if(visual.equals("attivi")){	//visualizzare da oggi verso il futuro
				switch(data){
				case "oggi":
				query=query+"date_add(curdate() , interval 0 day)>=end";	
				break;
				
				case "week":
				query=query+"date_add(curdate(), interval 7 day)>=end";
				break;
				
				case "month":
				query=query+"month(end)=month(curdate())";
				break;
				
				case "year":
				query=query+"year(end)=year(curdate())";
				break;
				}
			}
			else{						//visualizzare da oggi verso il passato
				switch(data){
				case "oggi":
				query=query+"date_sub(curdate() , interval 0 day)<=end";
				break;
				
				case "week":
				query=query+"date_sub(curdate() , interval 7 day)<=end";
				break;
				
				case "month":
				query=query+"month(curdate())=month(end)";
				break;
				
				case "year":
				query=query+"year(end)=year(curdate())";
				break;
				}
			}
		}//abbiamo analizzato in quale range di tempo bisogna visualizzare i memo
		String com="";
		if(data.equals("always"))
			com=com+" WHERE ";
		else com=com+" AND ";
		if(prior.size()==0)
			return;
		else if(prior.size()==1){
			if(prior.get(0).equals("Alta"))
				query=query+com+"prior='2'";
			else if(prior.get(0).equals("Bassa"))
				query=query+com+"prior='0'";
			else query=query+com+"prior='1'";
		}
		else if(prior.size()==2){
			if(!prior.contains("Alta"))
				query=query+com+"prior NOT LIKE '2'";
			else if(!prior.contains("Bassa"))
				query=query+com+"prior NOT LIKE '0'";
			else query=query+com+"prior NOT LIKE '1'";
		}
		System.out.println(query);
		currentMemos=manager.processQuery(query);
			
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
			this.user=user;
			for(Memo m: manager){
				System.out.println("login:"+m);
				//if(!m.isCompleted())
				currentMemos.add(m);
			}
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
			this.user="none";
			return true;
		}
		else return false;
	}
	
	
	public boolean signUp(String user,String password) throws MySQLIntegrityConstraintViolationException{
		
		if(manager.addUser(user, password)){
			this.user=user;
			return true;
		}
		else return false;
	}
	
	public String getUser(){
		
		return user;
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
		System.out.println("Task attivi="+k.taskAttivi());
		System.out.println("Task totali="+k.taskTotali());
		//System.out.println(k.stampaTaskTotali());
		System.out.println("Cosa fare oggi:\n");
		System.out.println(k.taskOdierni(false)+"\n");
		System.out.println("Cosa fare in settimana:\n");
		System.out.println(k.taskSettimana(false, true));
	}
}