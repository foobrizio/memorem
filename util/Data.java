package util;
import java.util.GregorianCalendar;
import java.io.*;


public class Data implements Comparable<Data>, Serializable{
		
	private static final long serialVersionUID = 5462755547341868425L;
	private static final Data UNIX_EPOCH_TIME=new Data(1970,1,1,0,0);
	private static final int SECONDS_X_YEAR=31536000;
	private static final int SECONDS_X_DAY=86400;
	private static final int SECONDS_X_HOUR=3600;
	private int anno;
	private int mese;
	private int giorno;
	private int ora;
	private int minuto;
		
	public Data(){
			
		GregorianCalendar gc=new GregorianCalendar();
		this.anno=gc.get(GregorianCalendar.YEAR);
		this.giorno=gc.get(GregorianCalendar.DAY_OF_MONTH);
		this.minuto=gc.get(GregorianCalendar.MINUTE);
		this.ora=gc.get(GregorianCalendar.HOUR_OF_DAY);
		this.mese=gc.get(GregorianCalendar.MONTH)+1;
	}
	
	public Data(int anno,int mese, int giorno, int ora, int minuto){
			
		if(minuto<0)
			throw new IllegalArgumentException("minuto illegale");
		else if(ora<0)
			throw new IllegalArgumentException("ora illegale");
		else if(giorno<0)
			throw new IllegalArgumentException("giorno illegale");
		//else if(mese<0)
		//	throw new IllegalArgumentException("mese illegale");
		int resto=minuto,adding=0; //con queste variabili verifichiamo i dati incongrui nei minuti
		while(resto>=60){	
			resto=resto%60;
			adding++;
		}
		this.minuto=resto;
		resto=ora+adding;	//resettiamo i due valori e verifichiamo l'ora
		adding=0;
		while(resto>=24){
			resto=resto%24;
			adding++;
		}
		this.ora=resto;
		resto=giorno+adding;	//resettiamo i valori per verificare il giorno
		adding=0;
		//System.out.println(monthToString(mese)+" ha "+daysOfMonth(mese,anno));
		while(resto>daysOfMonth(anno,mese)){
			//System.out.println("resto:"+resto+", daysOfMonth:"+daysOfMonth(anno,mese));
			resto=resto%daysOfMonth(anno,mese);
			adding++;
		}
		this.giorno=resto;
		resto=mese+adding;		//resettiamo i valori per il mese
		adding=0;	
		while(resto>12){
			System.out.println("resto:"+resto+",mese:"+mese);
			resto=resto%12;
			adding++;
		}
		this.mese=resto;
		resto=anno+adding;
		this.anno=resto;
		
		if(this.giorno==0){
			System.out.println("si va al mese precedente");
			System.out.println("mese:"+(this.mese-1));
			this.mese--;
			if(this.mese==0){
				this.anno--;
			}
			else if(this.mese==-1){
				this.mese=11;
			}
			this.giorno=daysOfMonth(this.anno,this.mese);
		}
	}
	
	public int anno(){return anno;}
	public int mese(){return mese;}
	public int giorno(){return giorno;}
	public int ora(){return ora;}
	public int minuto(){return minuto;}
	
	/**
	 * Riporta la data corrente al giorno dopo
	 * @return
	 */
	public Data domani(){
		
		return new Data(this.anno,this.mese,(this.giorno+1),this.ora,this.minuto);
	}
	
	/**
	 * Riporta la data corrente al giorno prima
	 * @return
	 */
	public Data ieri(){
		
		return new Data(this.anno,this.mese,(this.giorno-1),this.ora,this.minuto);
	}
	
	/**
	 * Ritorna true se la data d ha stesso giorno,stesso mese e stesso anno di this
	 * @param d
	 * @return
	 */
	public boolean hasSameDay(Data d){
		
		if(d.anno!=this.anno)
			return false;
		else if(d.mese!=this.mese)
			return false;
		else if(d.giorno!=this.giorno)
			return false;
		else return true;
	}
	
	public static int daysOfMonth(int year,int month){
		
		//System.out.println("month%12 fa="+month%12);
		switch(month%12){
		case 2: return bisestile(year)? 29: 28;
		case 1: case 3: case 5: case 7: case 8: case 10: case 0: return 31;		//Gennaio,Marzo,Maggio,Luglio,Agosto,Ottobre,Dicembre
		case 4: case 6: case 9: case 11: return 30;						//Aprile,Giugno,Settembre,Novembre
		default: return 32;
		}
	}
		
	public static boolean bisestile(int a){ //indipendente da this, perchè static
			
		if(a<0) throw new IllegalArgumentException("anno minore di 0");
		/* definizione generale di anno bisestile: divisibile per 4, ma quando è
		 * fine secolo dev'essere divisibile anche per 400
		 */
		if(a%4!=0)
			return false;
		if(a%100==0 && a%400!=0)
			return false;
		return true;
	} //bisestile
		
	public static String monthToString(int month){
			
		switch((month)%12){
		case 1: return "Jan";
		case 2: return "Feb"; 
		case 3: return "Mar"; 
		case 4: return "Apr"; 
		case 5: return "May";
		case 6: return "Jun";
		case 7: return "Jul";
		case 8: return "Aug";
		case 9: return "Sep";
		case 10: return "Oct";
		case 11: return "Nov";
		case 0: return "Dec";
		default: return "???";
		}
	}
	
	public static int monthToInt(String month){
		
		month=month.trim().toLowerCase();
		switch(month){
		case "gennaio": return 1;
		case "febbraio": return 2;
		case "marzo": return 3;
		case "aprile": return 4;
		case "maggio": return 5;
		case "giugno": return 6;
		case "luglio": return 7;
		case "agosto": return 8;
		case "settembre": return 9;
		case "ottobre": return 10;
		case "novembre": return 11;
		case "dicembre": return 12;
		default: return -1;
		}
	}
	
	
	public static int dayOfWeek(int year,int month, int day){
		
		int ab=Integer.parseInt(String.valueOf(year).substring(0, 2)); //prime due cifre dell'anno
		while(ab<18){
			System.out.println("ab="+ab);
			ab+=4;
		}
		int baseCentDay=0;
		switch(ab){
		case 18: baseCentDay=5; break;
		case 19: baseCentDay=3; break;
		case 20: baseCentDay=2; break;
		case 21: baseCentDay=0; break;
		}
		int cd=year%100;
		int quozA=cd/12;
		int restoB=cd%12;
		int quozC=restoB/4;
		int doomsDayYear=quozA+restoB+quozC;
		doomsDayYear%=7;
		doomsDayYear+=baseCentDay;
		//System.out.println("il doomsDay dell'anno è "+doomsDayYear);
		int nearestDoom=0;
		switch(month){				//scegliamo il giorno doomsday più vicino in base al mese della data da individuare
		case 1: nearestDoom= bisestile(year)? 4:3;break;
		case 2: nearestDoom= bisestile(year)? 29:28;break;
		case 3: nearestDoom=14; break;
		case 4: nearestDoom=4; break;
		case 5: nearestDoom=9; break;
		case 6: nearestDoom=6; break;
		case 7: nearestDoom=11; break;
		case 8: nearestDoom=8; break;
		case 9: nearestDoom=5; break;
		case 10: nearestDoom=10; break;
		case 11: nearestDoom= 7; break;
		case 12: nearestDoom= 12; break;
		}
		//System.out.println("il doomsDay più vicino è "+nearestDoom);
		nearestDoom=day-nearestDoom;	//sottraiamo al giorno della data da individuare
		nearestDoom+=doomsDayYear;
		nearestDoom%=7;
		//System.out.println("il nearestDoom è "+nearestDoom);
		if(nearestDoom<0)
			nearestDoom+=7;
		return nearestDoom;
		
	}
	/**
	 * Ritorna la data in millisecondi a partire dal 1° gennaio 1970, ore 00:00
	 * @return
	 */
	public long dateInMillis(){
		
		long mil=0;
		for(int i=UNIX_EPOCH_TIME.anno;i<this.anno;i++){
			if(bisestile(i))
				mil+=SECONDS_X_YEAR+SECONDS_X_DAY;	//366 giorni
			else mil+=SECONDS_X_YEAR;
		} 	//abbiamo calcolato anche i bisestili
		int diff_mesi=this.mese-1;
		if(diff_mesi!=0){
			int i=1;
			while(i<this.mese){
				mil+=daysOfMonth(i,this.anno)*SECONDS_X_DAY;
				i++;
			}
		} //trovati anche i mesi
		mil+=(this.giorno-1)*SECONDS_X_DAY;
		mil+=(this.ora-1)*SECONDS_X_HOUR;
		mil+=this.minuto*60;
		//ora prima di ritornare il tempo ci assicuriamo di trovarci nell'ora legale o nell'ora solare
		if(this.mese>=3 && this.mese<=10){
			if(this.mese==3 || this.mese==10)	//ci troviamo a marzo
				if(this.giorno>=25 && Data.dayOfWeek(this.anno, this.mese, this.giorno)==0)
					mil-=SECONDS_X_HOUR;
			else
				mil-=SECONDS_X_HOUR;
		
		}
		System.out.println("giorno della settimana:"+Data.dayOfWeek(this.anno,this.mese,this.giorno));
		return mil*1000;
		
	}
	/**
	 * Converte il formato java.sql.Timestamp nel formato util.Data
	 * @param stamp
	 * @return
	 */
	public static Data convertTime(java.sql.Timestamp stamp){
		
		String total=stamp.toString();
		int anno=Integer.parseInt(total.substring(0, 4));
		int mese=Integer.parseInt(total.substring(5, 7));
		int giorno=Integer.parseInt(total.substring(8,10));
		int ora=Integer.parseInt(total.substring(11, 13));
		int minuto=Integer.parseInt(total.substring(14,16));
		return new Data(anno,mese,giorno,ora,minuto);
		
	}
	
	/*
	public static Data convertTimestamp(java.sql.Timestamp stamp){
		
		long dm=stamp.getTime();
		dm/=1000; 	//ora abbiamo il dm in secondi
		int year=UNIX_EPOCH_TIME.anno;
		for(int i=year;;i++){
			if(bisestile(i)){
				if(dm<SECONDS_X_YEAR+SECONDS_X_DAY)			//c'è meno di un anno dentro dm
					break;
				else
					dm-=(SECONDS_X_YEAR+SECONDS_X_DAY); 	//togliamo 366 giorni a dm e aggiungiamo 1 anno al conto
			}
			else{
				if(dm<SECONDS_X_YEAR)
					break;
				else
					dm-=(SECONDS_X_YEAR);
			}
			//System.out.println("anno "+i);
			year++;
		}		//ora qui abbiamo il numero di anni (avendo contato anche i bisestili)
		int mese=1;
		for(;;){
			int dom=daysOfMonth(mese,year);
			if(dm<dom*SECONDS_X_DAY)
				break;
			else{
				//System.out.println("mese "+mese);
				dm-=dom*SECONDS_X_DAY;
				mese++;
			}
		} //ora qui abbiamo i mesi
		int giorno=1;
		//System.out.println(dm);
		while(dm>SECONDS_X_DAY){
			//System.out.println("giorno "+giorno);
			dm-=SECONDS_X_DAY;
			giorno++;
		}
		dm/=1000;	//ora abbiamo il tm in secondi
		int ora=1;
		while(dm>=SECONDS_X_HOUR){
			dm-=SECONDS_X_HOUR;
			ora++;
		}
		int minuto=(int)dm/60;
		return new Data(year,mese,giorno+1,ora,minuto);
	}
	*/
	/**
	 * Converte una data in un formato stringa compresso facilmente analizzabile
	 * @param data
	 * @return
	 */
	public static String convertDateToString(Data data){
		
		String st=""+data.anno+"-";
		if(data.mese<10)
			st+="0";
		st+=data.mese+"-";
		if(data.giorno<10)
			st+="0";
		st+=data.giorno+" ";
		if(data.ora<10)
			st+="0";
		st+=data.ora+":";
		if(data.minuto<10)
			st+="0";
		st+=data.minuto;
		return st;
	}
	
	/**
	 * Converte un orario in un formato stringa compresso facilmente analizzabile
	 * @param data
	 * @return
	 */
	public static String convertHourToString(Data data){
		
		String h="";
		
		return h;
	}
	
	public static Data convertStringToData(String data){
		
		
		if(data.length()<16){
			System.out.println("data:"+data+" ,length:"+data.length());
			throw new IllegalArgumentException("La data passata al metodo non è conforme allo standard");
		}
		int anno= Integer.parseInt(data.substring(0, 4));
		int mese= Integer.parseInt(data.substring(5,7));
		//System.out.println("data:"+data);
		int giorno= Integer.parseInt(data.substring(8,10));
		int h= Integer.parseInt(data.substring(11,13));
		int minuto= Integer.parseInt(data.substring(14,16));
		return new Data(anno,mese,giorno,h,minuto);
	}
	
	public String toString(){
			
		String minuto=""+this.minuto;
		if(this.minuto<10)
			minuto="0"+this.minuto;
		String mese= monthToString(this.mese);
		return ""+giorno+" "+mese+" "+anno+" "+ora+":"+minuto;
	}
	
	public int hashCode(){
		
		final int PRIMO=43;
		return PRIMO*anno+PRIMO*mese+PRIMO*giorno+PRIMO*ora+PRIMO*minuto;
	}
	
	/**
	 * Confronta due date e stabilisce quale sia la più recente. Ritorna 0 se le date sono identiche,
	 * -1 se this è meno recente di arg0, 1 altrimenti
	 */
	@Override
	public int compareTo(Data arg0) {
			
		if(this.anno>arg0.anno)
			return 1;
		else if(this.anno<arg0.anno)
			return -1;
		else{
			if(this.mese>arg0.mese)
				return 1;
			else if(this.mese<arg0.mese)
				return -1;
			else{
				if(this.giorno>arg0.giorno)
					return 1;
				else if(this.giorno<arg0.giorno)
					return -1;
				else{
					if(this.ora>arg0.ora)
						return 1;
					else if(this.ora<arg0.ora)
						return -1;
					else{
						if(this.minuto>arg0.minuto)
							return 1;
						else if(this.minuto<arg0.minuto)
							return -1;
						else return 0;
					}
				}
			}
		}
	}
	
	public static void main(String[] args){
		
		Data fine=new Data(2014,5,31,16,0);
		Data finepiuuno=new Data(2014,6,1,0,0);
		System.out.println(fine.dateInMillis());
		System.out.println(dayOfWeek(fine.anno,fine.mese,fine.giorno));
		/*for(int i=0;i<12;i++){
			System.out.println(monthToString(i+1)+" 2014");
			Data.dayOfWeek(2014,i+1,1);
		}*/
		/*System.out.println(fine.toString());
		System.out.println(convertDateToString(fine));
		fine=fine.domani();
		System.out.println(fine.toString());
		System.out.println("START");
		for(int i=0;i<365;i++){
			fine=fine.ieri();
			System.out.println(fine.toString());
		}
		
		System.out.println("\nORA SI FA AL CONTRARIO\n");
		for(int i=0;i<365;i++){
			fine=fine.domani();
			System.out.println(fine.toString());
		}*/
		System.out.println(Data.convertDateToString(fine));
		System.out.println(fine.compareTo(finepiuuno));
	}
}