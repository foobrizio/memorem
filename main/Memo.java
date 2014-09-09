package main;
import util.Data;
import graphic.MemoremGUI.Lang;

import java.io.*;

import org.apache.commons.codec.digest.DigestUtils;



@SuppressWarnings("serial")
public class Memo implements Comparable<Memo>, Serializable{
	
	private enum Priority{LOW, NORMAL, HIGH};
	
	//final static int SEC4MIN=60;
	//final static int SEC4HOUR=3600;
	//final static int SEC4DAY=86400;
	
	private Priority priority;	//valutazione della priorità
	private String desc;		//descrizione del compito
	private Data end;
	private boolean completion;
	private String icon;
	private String id;
	private boolean scadenzanotificata;
	private boolean pure;
	public Lang language;
	
	/*											*
	 * 											*
	 * 				COSTRUTTORI                 *
	 * 											*
	 *											*/
	public Memo(String desc, int year,int month, int day,int hour, int minute){
		
		this.desc=desc;
		priority=Priority.NORMAL;
		completion=false;
		scadenzanotificata=false;
		end=new Data(year,month,day,hour,minute);
		icon="note.png";
		id=DigestUtils.shaHex(desc+end.toString());
		language=Lang.EN;
	}
	
	/**
	 * Da database va sempre richiamato questo costruttore
	 * @param desc
	 * @param priority
	 * @param end
	 * @param icon
	 * @param id
	 */
	public Memo(String desc, int priority, Data end, String icon,String id){
		
		this(desc,priority,end,icon);
		scadenzanotificata=false;
		this.id=id;
		pure=true;
		language=Lang.EN;
	}
	
	public Memo(String desc, String priority, int year, int month, int day, int hour, int minute){
		
		this.desc=desc;
		this.setPriority(priority);
		completion=false;
		scadenzanotificata=false;
		end=new Data(year,month,day,hour,minute);
		icon="note.png";
		id=DigestUtils.shaHex(desc+end.toString());
		pure=false;
		language=Lang.EN;
	}
	
	public Memo(String desc, Data end){
		
		this.desc=desc;
		priority=Priority.NORMAL;
		completion=false;
		scadenzanotificata=false;
		end.setLanguage(Lang.EN);
		this.end=end;
		icon="note.png";
		id=DigestUtils.shaHex(desc+end.toString());
		pure=false;
		language=Lang.EN;
	}
	
	public Memo(String desc, int priority, Data end){
		
		this.desc=desc;
		this.setPriority(priority);
		completion=false;
		scadenzanotificata=false;
		this.end=end;
		icon="note.png";
		id=DigestUtils.shaHex(desc+end.toString());
		pure=false;
		language=Lang.EN;
	}
	
	public Memo(String desc, int priority, Data end, String icon){
		
		this(desc,priority,end);
		scadenzanotificata=false;
		completion=false;
		this.icon=icon;
		pure=false;
		language=Lang.EN;
	}
	
	/**
	 * Costruttore di copia
	 * @param m
	 */
	public Memo(Memo m){
		
		this.desc=m.desc;
		this.priority=m.priority;
		this.end=m.end;
		this.completion=m.completion;
		this.scadenzanotificata=m.scadenzanotificata;
		this.icon=m.icon;
		this.id=m.id;
		this.language=m.language;
		pure=false;
	}
	
	
	/*					Fine costruttori					*/
	
	
	/**
	 * Modifica il campo del memo, indicando che esso è stato completato
	 */
	public void spunta(){
		
		completion=true;
		scadenzanotificata=true;
	}
	
	public void setLanguage(Lang language){
		
		this.end.setLanguage(language);
		this.language=language;
	}
	public void setScadenzaNotificata(){
		
		completion=false;
		scadenzanotificata=true;
	}
	
	public void setRevalued(){
		
		completion=false;
		scadenzanotificata=false;
	}
	
	/**
	 * Si può cambiare priorità al task inserendo le seguenti stringhe:
	 * "low" o "LOW" o "C";
	 * "normal" o "NORMAL" o "B";
	 * "high" o "HIGH" o "A".
	 * Altri valori non verranno considerati
	 *
	 * @param priorità
	 */
	public void setPriority(String priorità){
		
		priorità=priorità.toLowerCase();
		if(priorità.equals("low") || priorità.equals("c"))
			priority=Priority.LOW;
		else if(priorità.equals("normal") || priorità.equals("b"))
			priority=Priority.NORMAL;
		else if(priorità.equals("high") || priorità.equals("a"))
			priority=Priority.HIGH;
		else
			System.out.println("Hai inserito qualche boiata...per cambiare priorità scrivere low, normal o high");
	}
	
	/**
	 * Si può cambiare priorità al task inserendo i seguenti valori:
	 * "0" per low;
	 * "1" per normal;
	 * "2" per high.
	 * Altri valori non verranno considerati
	 * @param priorità
	 */
	public void setPriority(int priorità){
		
		if(priorità==2)
			priority=Priority.HIGH;
		else if(priorità==1)
			priority=Priority.NORMAL;
		else if(priorità==0)
			priority=Priority.LOW;
	}
	
	/**
	 * Modifica il termine di un task inserendo gli opportuni valori
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 */
	public void setEnd(int year, int month, int day, int hour, int minute){
		
		this.end=new Data(year,month,day,hour,minute);
	}
	
	public void setEnd(Data end){
		
		this.end=end;
	}
	
	/**
	 * Cambia l'icona del memo
	 * @param icon
	 */
	public void setIcon(String icon){
		
		this.icon=icon;
	}
	
	public String StampaPriority(){
		
		if(priority==Priority.HIGH)
			return "high";
		if(priority==Priority.NORMAL)
			return "normal";
		return "low";
	}
	
	public int priority(){
		
		if(priority==Priority.HIGH)
			return 2;
		if(priority==Priority.NORMAL)
			return 1;
		return 0;
	}
	
	public String description(){
		
		return desc;
	}
	
	public boolean isCompleted(){
		
		return completion;
	}
	
	public String endDate(){
		
		return end.toString();
	}
	
	
	public String countDown(){
		
		if(completion)
			switch(language){
			case IT: return "COMPLETATO";
			case DE: return "VOLLENTED";
			case ES: return "COMPLETADO";
			default: return "COMPLETED";
			}
		Data ora=new Data();
		int dAn=end.anno()-ora.anno();
		int dMe=end.mese()-ora.mese();
		int dGi=end.giorno()-ora.giorno();
		int dHo=end.ora()-ora.ora();
		int dMi=end.minuto()-ora.minuto()-1;
		if(dMi<0){
			dMi=60+dMi;
			dHo--;
		}
		if(dHo<0){
			dHo=24+dHo;
			dGi--;
		}
		if(dGi<0){
			dGi=Data.daysOfMonth(ora.mese(), ora.anno())+dGi;
			dMe--;
		}
		if(dMe<0){
			dMe=12+dMe;
			dAn--;
		}
		if(dAn<0){
			switch(language){
			case IT: return "SCADUTO";
			case DE: return "VERFALLEN";
			case ES: return "EXPIRADO";
			default: return "EXPIRED";
			}
		}
		String finale="";
		if(dAn!=0)
			finale+=dAn+"y,";
		if(dMe!=0)
			finale+=dMe+"m,";
		if(dGi!=0)
			finale+=dGi+"d,";
		if(dHo!=0)
			finale+=dHo+"hh,";
		finale+=dMi+"mm";
		return finale;
	}
	/**
	 * Ritorna true se il task non è stato ancora svolto e se non è scaduto
	 * @return
	 */
	public boolean isActive(){
		
		return !completion && !isScaduto();
	}
	
	/**
	 * Ritorna true se il task è scaduto
	 * @return
	 */
	public boolean isScaduto(){
		
		Data oggi=new Data();
		return oggi.compareTo(end)>=0;	//oggi>end significa che la fine è già passata, per cui il task è scaduto
	}
	
	public boolean isNotificato(){
		
		return scadenzanotificata;
	}
	
	public Data getEnd(){
		
		return end;
	}
	
	public String getIcon(){
		
		return icon;
	}
	
	public String getId(){
		
		return id;
	}
	
	/*public void setId(String id){
		
		this.id=id;
	}*/
	
	/**
	 * Ritorna true se il memo viene dal database, false se è stato creato o modificato dall'utente
	 * @return
	 */
	public boolean getPure(){
		
		return pure;
	}
	
	public void setPure(boolean t){
		
		pure=t;
	}
	
	/**
	 * Ritorna true se la data e la descrizione sono identiche, false altrimenti
	 * N.B.: non tiene conto di altre caratteristiche, come la priorità 
	 * @param m
	 * @return
	 */
	public boolean identici(Memo m){
		
		if(!this.desc.trim().equals(m.desc.trim()))
			return false;
		if(!this.end.equals(m.end))
			return false;
		return true;
	}
	
	@Override
	public boolean equals(Object m){
		
		if(!(m instanceof Memo))
			return false;
		if(m==this)
			return true;
		return this.getId().equals(((Memo)m).getId());
	}
	
	@Override
	public int compareTo(Memo arg0) {
		
		if(this.priority()>arg0.priority())
			return -1;
		if(this.priority()<arg0.priority())
			return 1; //andrea è scemo
		else
			return this.getEnd().compareTo(arg0.getEnd());
	}
	
	public String toString(){
		
		char pr=' ';
		if(priority()==2)
			pr='A';
		else if(priority()==0)
			pr='C';	
		return pr+"\t"+this.endDate()+"  \t"+desc;
	}
	
	public int hashCode(){
		
		final int PRIMO=113;
		int x=PRIMO*priority()*(PRIMO*desc.hashCode()+PRIMO*end.hashCode());
		if(completion)
			return x;
		else return -x;
	}
	
	public static void main(String[] args){
		
		Memo t1=new Memo("t1","b",2014,11,30,15,00);
		Memo t2=new Memo("t2","b",2014,11,29,23,00);
		Memo t3=new Memo("t3","a",2016,3,9,15,00);
		Memo t4=new Memo("t4","c",2014,11,28,12,00);
		Memo t5=new Memo("t5","a",2015,2,2,13,00);
		Memo t6=new Memo("t6","c",2014,12,12,12,12);
		//System.out.println("diff="+differenzaTempo(t1,t2));
		System.out.println(t4.compareTo(t6));
		System.out.println(t1);
		System.out.println(t2);
		System.out.println(t3);
		System.out.println(t4);
		System.out.println(t5);
		System.out.println(t6);
		t3.countDown();
		if(t5.isScaduto())
			System.out.println("sono scaduto");
		else
			System.out.println("non sono scaduto");
		if(t5.isActive())
			System.out.println("sono attivo");
		else System.out.println("sono inattivo");
	}
}