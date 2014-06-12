package main;
import util.Data;
import java.io.*;



public class Memo implements Comparable<Memo>, Serializable{
	
	
	private static final long serialVersionUID = 3850876655640676539L;

	private enum Priority{LOW, NORMAL, HIGH};
	
	//final static int SEC4MIN=60;
	//final static int SEC4HOUR=3600;
	//final static int SEC4DAY=86400;
	
	private Priority priority;	//valutazione della priorità
	private String desc;		//descrizione del compito
	private Data end;
	private boolean completion;
	
	public Memo(String desc, int year,int month, int day,int hour, int minute){
		
		this.desc=desc;
		priority=Priority.NORMAL;
		completion=false;
		end=new Data(year,month,day,hour,minute);
	}
	
	public Memo(String desc, String priority, int year, int month, int day, int hour, int minute){
		
		this.desc=desc;
		this.setPriority(priority);
		completion=false;
		end=new Data(year,month,day,hour,minute);
	}
	
	public Memo(String desc, Data end){
		
		this.desc=desc;
		priority=Priority.NORMAL;
		completion=false;
		this.end=end;
	}
	
	public Memo(String desc, int priority, Data end){
		
		this.desc=desc;
		this.setPriority(priority);
		completion=false;
		this.end=end;
	}
	
	/**
	 * Modifica il campo del memo, indicando che esso è stato completato
	 */
	public void spunta(){
		
		completion=true;
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
			return "COMPLETATO";
		Data ora=new Data();
		int dAn=end.anno()-ora.anno();
		int dMe=end.mese()-ora.mese();
		int dGi=end.giorno()-ora.giorno();
		int dHo=end.ora()-ora.ora();
		int dMi=end.minuto()-ora.minuto();
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
			return "SCADUTO";
		}
		String finale="";
		if(dAn!=0)
			finale+=dAn+"y,";
		if(dMe!=0)
			finale+=dMe+"m,";
		if(dGi!=0)
			finale+=dGi+"d,";
		if(dHo!=0)
			finale+=dHo+"h,";
		finale+=dMi+"min";
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
		return oggi.compareTo(end)>0;	//oggi>end significa che la fine è già passata, per cui il task è scaduto
	}
	
	public Data getEnd(){
		
		return end;
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
		return pr+"\t"+this.endDate()+"\t"+desc;
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