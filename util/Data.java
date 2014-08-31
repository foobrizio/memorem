package util;
import java.util.GregorianCalendar;
import java.io.*;
import graphic.MemoremGUI.Lang;


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
	private Lang language;
		
	public Data(){
		
		
		GregorianCalendar gc=new GregorianCalendar();
		this.anno=gc.get(GregorianCalendar.YEAR);
		this.giorno=gc.get(GregorianCalendar.DAY_OF_MONTH);
		this.minuto=gc.get(GregorianCalendar.MINUTE);
		this.ora=gc.get(GregorianCalendar.HOUR_OF_DAY);
		this.mese=gc.get(GregorianCalendar.MONTH)+1;
		language=Lang.EN;
		//System.currentTimeMillis();
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
		while(resto>daysOfMonth(anno,mese)){
			resto=resto%daysOfMonth(anno,mese);
			adding++;
		}
		this.giorno=resto;
		resto=mese+adding;		//resettiamo i valori per il mese
		adding=0;	
		while(resto>12){
			resto=resto%12;
			adding++;
		}
		this.mese=resto;
		resto=anno+adding;
		this.anno=resto;
		
		if(this.giorno==0){
			this.mese--;
			if(this.mese==0){
				this.anno--;
			}
			else if(this.mese==-1){
				this.mese=11;
			}
			this.giorno=daysOfMonth(this.anno,this.mese);
		}
		language=Lang.EN;
	}
	
	public int anno(){return anno;}
	
	/**
	 * Ritorna true se l'anno a è bisestile
	 */
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
		
		
		if(data.length()<16)
			throw new IllegalArgumentException("La data passata al metodo non è conforme allo standard");
		int anno= Integer.parseInt(data.substring(0, 4));
		int mese= Integer.parseInt(data.substring(5,7));
		int giorno= Integer.parseInt(data.substring(8,10));
		int h= Integer.parseInt(data.substring(11,13));
		int minuto= Integer.parseInt(data.substring(14,16));
		return new Data(anno,mese,giorno,h,minuto);
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

	/**
	 *Ritorna il giorno della settimana di una specifica data. Variante dell'algoritmo doomsDay
	 */
	public static int dayOfWeek(int year,int month, int day){
		
		int ab=Integer.parseInt(String.valueOf(year).substring(0, 2)); //prime due cifre dell'anno
		while(ab<18)
			ab+=4;
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
		return mil*1000;
		
	}

	/**
	 * Ritorna il numero di giorni del mese month nell'anno year
	 */
	public static int daysOfMonth(int year,int month){
		
		switch(month%12){
		case 2: return bisestile(year)? 29: 28;
		case 1: case 3: case 5: case 7: case 8: case 10: case 0: return 31;		//Gennaio,Marzo,Maggio,Luglio,Agosto,Ottobre,Dicembre
		case 4: case 6: case 9: case 11: return 30;						//Aprile,Giugno,Settembre,Novembre
		default: return 32;
		}
	}

	/**
	 * Ritorna il numero di giorni dell'anno inserito come parametro
	 */
	public static int daysOfYear(int year){
		
		if(bisestile(year))
			return 366;
		else return 365;
	}

	/**
	 * Ritorna la differenza in giorni tra due date 
	 * @param uno
	 * @param due
	 * @return
	 */
	public static int diff(Data uno,Data due){
		
		int giorni=0;
		Data prima,dopo;
		if(uno.compareTo(due)<0){
			prima=uno;
			dopo=due;
		}
		else{
			prima=due;
			dopo=uno;
		}	//ora sappiamo quale data precede l'altra nel calendario
		int i=prima.anno;
		for(;i<dopo.anno;i++){
			if(dopo.anno-prima.anno==1){	//l'ultimo anno va controllato per bene
				if(prima.mese>dopo.mese)
					break;
				else if(prima.mese==dopo.mese && prima.giorno>dopo.giorno)
					break;	
			}
			giorni+=daysOfYear(i);
		}//arrivati qui abbiamo le due date ad una distanza minore di 12 mesi.
		prima.anno=i;
		if(prima.anno!=dopo.anno){
			giorni+=daysOfMonth(prima.anno,prima.mese)-prima.giorno;
			while(prima.mese<12){
				prima.mese=prima.mese+1;
				giorni+=daysOfMonth(prima.anno,prima.mese);
			}
			prima.giorno=1;
			prima.mese=1;
			prima.anno=prima.anno+1;
		}//a questo punto le due date si trovano nello stesso anno, si procede con il conto dei mesi
		i=prima.mese;
		for(;i<dopo.mese;i++){
			if(dopo.mese-prima.mese==1){
				if(prima.giorno>dopo.giorno)
					break;
			}
			giorni+=daysOfMonth(prima.anno,prima.mese);
			prima.mese=prima.mese+1;
		}//a questo punto la distanza è minore di un mese, aggiungiamo giorno per giorno
		while(prima.compareTo(dopo)<0){
			prima=prima.domani();
			giorni++;
		}
		return giorni;
	}

	/**
	 * Riporta la data corrente al giorno dopo
	 * @return
	 */
	public Data domani(){
		
		return new Data(this.anno,this.mese,(this.giorno+1),this.ora,this.minuto);
	}

	public boolean equals(Data d){
		
		if(this.anno!=d.anno)
			return false;
		else if(this.mese!=d.mese)
			return false;
		else if(this.giorno!=d.giorno)
			return false;
		else if(this.ora!=d.ora)
			return false;
		else if(this.minuto!=d.minuto)
			return false;
		return true;
	}

	/**
	 * Ritorna la data sottoforma di stringa
	 * @return
	 */
	public String getData(){
		
		if(language==Lang.EN){
			StringBuilder sb=new StringBuilder(30);
			sb.append(Data.monthToString(mese,Lang.EN)+" ");
			sb.append(giorno);
			if(giorno==1 || giorno==21 || giorno==31)
				sb.append("st ");
			else if(giorno==2 || giorno==22)
				sb.append("nd ");
			else if(giorno==3 || giorno==23)
				sb.append("rd ");
			else
				sb.append("th ");
			sb.append(anno);
			return sb.toString();
		}
		//System.out.println(language);
		return giorno+" "+Data.monthToString(mese,Lang.IT)+" "+anno;
		
	}

	/**
	 * Ritorna l'ora sottoforma di stringa
	 * @return
	 */
	public String getOra(){
		
		String h=ora+"";
		if(ora<10)
			h="0"+h;
		String m=minuto+"";
		if(minuto<10)
			m="0"+m;
		return h+":"+m;
	}

	public int giorno(){return giorno;}

	@Override
	public int hashCode(){
		
		final int PRIMO=43;
		return PRIMO*anno+PRIMO*mese+PRIMO*giorno+PRIMO*ora+PRIMO*minuto;
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

	/**
	 * Riporta la data corrente al giorno prima
	 * @return
	 */
	public Data ieri(){
		
		return new Data(this.anno,this.mese,(this.giorno-1),this.ora,this.minuto);
	}
	
	/**
	 * Ritorna true se this equivale ad oggi
	 * @return
	 */
	public boolean isToday(){
		
		Data d=new Data();
		if(this.anno==d.anno && this.mese==d.mese && this.giorno==d.giorno)
			return true;
		else return false;
	}

	public int mese(){return mese;}
	public int minuto(){return minuto;}
	
	/**
	 *	Trasforma la stringa inserita come parametro nel mese corrispondente
	 */
	public static int monthToInt(String month, Lang lang){
		
		month=month.trim().substring(0, 3).toLowerCase();
		if(lang==Lang.DE){
			switch(month){
			case "jan" : return 1;
			case "feb" : return 2;
			case "mar" : case "mär" 	: return 3;
			case "apr" : return 4;
			case "mai" : return 5;
			case "jun" : return 6;
			case "jul" : return 7;
			case "aug" : return 8;
			case "sep" : return 9;
			case "okt" : return 10;
			case "nov" : return 11;
			case "dez" : return 12;
			}
		}
		else if(lang==Lang.ES){
			switch(month){
			case "ene" : return 1;
			case "feb" : return 2;
			case "mar" : return 3;
			case "abr" : return 4;
			case "may" : return 5;
			case "jun" : return 6;
			case "jul" : return 7;
			case "ago" : return 8;
			case "sep" : return 9;
			case "oct" : return 10;
			case "nov" : return 11;
			case "dic" : return 12;
			}
		}
		else if(lang==Lang.IT){
			switch(month){
			case "gen": return 1;
			case "feb": return 2;
			case "mar": return 3;
			case "apr": return 4;
			case "mag": return 5;
			case "giu": return 6;
			case "lug": return 7;
			case "ago": return 8;
			case "set": return 9;
			case "ott": return 10;
			case "nov": return 11;
			case "dic": return 12;
			}
		}
		else switch(month){
		case "jan": return 1;
		case "feb": return 2;
		case "mar": return 3;
		case "apr": return 4;
		case "may": return 5;
		case "jun": return 6;
		case "jul":	return 7;
		case "aug":	return 8;
		case "sep": return 9;
		case "oct": return 10;
		case "nov": return 11;
		case "dec":	return 12;
		default: return -1;
		}
		return -1;
	}

	/**
	 * Ritorna il numero inserito nel mese corrispondente sottoforma di stringa
	 * @param month
	 * @return
	 */
	public static String monthToString(int month,Lang lang){
		if(lang==Lang.IT)
			switch((month)%12){
			case 1: return "Gen";
			case 2: return "Feb"; 
			case 3: return "Mar"; 
			case 4: return "Apr"; 
			case 5: return "Mag";
			case 6: return "Giu";
			case 7: return "Lug";
			case 8: return "Ago";
			case 9: return "Set";
			case 10: return "Ott";
			case 11: return "Nov";
			case 0: return "Dic";
			default: return "???";
			}
		else if(lang==Lang.DE){
			switch((month)%12){
			case 1: return "Jan";
			case 2: return "Feb"; 
			case 3: return "Mar"; 
			case 4: return "Apr"; 
			case 5: return "Mai";
			case 6: return "Jun";
			case 7: return "Jul";
			case 8: return "Aug";
			case 9: return "Sep";
			case 10: return "Okt";
			case 11: return "Nov";
			case 0: return "Dez";
			default: return "???";
			}
		}
		else if(lang==Lang.ES){
			switch((month)%12){
			case 1: return "Ene";
			case 2: return "Feb"; 
			case 3: return "Mar"; 
			case 4: return "Abr"; 
			case 5: return "May";
			case 6: return "Jun";
			case 7: return "Jul";
			case 8: return "Ago";
			case 9: return "Sep";
			case 10: return "Oct";
			case 11: return "Nov";
			case 0: return "Dic";
			default: return "???";
			}
		}
		else if(lang==Lang.EN)
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
		else return null;
	}
	
	public int ora(){return ora;}

	
	public void setLanguage(Lang lang){
		
		this.language=lang;
	}
	@Override
	public String toString(){
			
		String minuto=""+this.minuto;
		if(this.minuto<10)
			minuto="0"+this.minuto;
		return getData()+" "+ora+":"+minuto;
	}
	
	public static void main(String[] args){
		
		Data fine=new Data(2014,5,31,16,0);
		Data nuova=new Data(2014,7,30,0,0);
		System.out.println("Differenza date:"+Data.diff(new Data(), nuova));
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
		//System.out.println(Data.convertDateToString(fine));
		//System.out.println(fine.compareTo(finepiuuno));
	}
}