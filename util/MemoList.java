package util;

import java.util.*;

import main.Memo;



public class MemoList implements Iterable<Memo>{

	/*private class Iteratore<T> implements Iterator<T>{

		private int index=-1;
	
		public boolean hasNext() {
			return index<size-1;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {

			if(index>=size-1)	//sono finiti gli elementi o la tasklist è vuota
				return null;
			else if(index==-1){	//prima chiamata dell'iteratore
				if(highs.size()!=0){
					index++;
					return (T)highs.get(0);
				}
				else if(normals.size()!=0){
					index++;				
					return (T)normals.get(0);
				}
				else if(lows.size()!=0){
					index++;
					return (T)lows.get(0);
				}
				else return null;
			}//prima chiamata non ha errori
			else if(index>=0 && index<highs.size()){			//ci troviamo nella LL highs
				if(index==highs.size()-1){			
					if(normals.size()!=0){
						index++;
						return (T)normals.get(0);
					}
					else if(lows.size()!=0){
						index++;
						return (T)lows.get(0);
					}
					else return null;
				}//ultimo elemento di priorità A
				else{								//tutti gli altri casi
					index++;
					return (T)highs.get(index);
				}
			}//HIGHS
			else if(index>=highs.size() && index<(highs.size()+normals.size())){ //ci troviamo nella LL normals
				if(index==(highs.size()+normals.size()-1)){		
					index++;
					return (T)lows.get(0);
				}//ultimo elemento di priorità B
				else{
					index++;
					return (T)normals.get(index-highs.size());
				}
			}//NORMALS
			else{				//ci troviamo nella LL lows
				index++;
				return (T)lows.get(index-highs.size()-normals.size());
			}
		}

		@Override
		public void remove() {
			if(index==-1)		//non abbiamo ancora passato nessun elemento
				return;
			if(index>=0 && index<highs.size()-1)
				highs.remove(index);
			else if(index>=highs.size() && index<(highs.size()+normals.size()))
				normals.remove(index-highs.size());
			else lows.remove(index-highs.size()-normals.size());
			index=-1;
			return;
		}
	}*/

	@SuppressWarnings({ "hiding" })
	private class myIterator<Memo> implements Iterator<Memo>{

		private int used=0;
		@SuppressWarnings("unchecked")
		private Iterator<Memo> highIt=(Iterator<Memo>) highs.iterator();		//used=1
		@SuppressWarnings("unchecked")
		private Iterator<Memo> normalIt=(Iterator<Memo>) normals.iterator();	//used=2
		@SuppressWarnings("unchecked")
		private Iterator<Memo> lowIt=(Iterator<Memo>) lows.iterator();		//used=3
		@Override
		public boolean hasNext() {

			if(used==0 || used==1){
				if(!highIt.hasNext())
					if(!normalIt.hasNext())
						return lowIt.hasNext();
			}
			else if(used==2){
				if(!normalIt.hasNext())
					return lowIt.hasNext();
			}
			else if(used==3){
				return lowIt.hasNext();
			}
			return true;
		}

		public Memo next() {

			if(used==0 || used==1){
				if(highIt.hasNext()){
					used=1;
					return highIt.next();
				}
				else if(normalIt.hasNext()){
					used=2;
					return normalIt.next();
				}
				else if(lowIt.hasNext()){
					used=3;
					return lowIt.next();
				}
			}
			else if(used==2){
				if(normalIt.hasNext())
					return normalIt.next();
				else if(lowIt.hasNext()){
					used=3;
					return lowIt.next();
				}
			}
			else if(used==3){
				if(lowIt.hasNext())
					return lowIt.next();
			}
			return null;
		}

		@Override
		public void remove() {

			if(used==1)
				highIt.remove();
			else if(used==2)
				normalIt.remove();
			else if(used==3)
				lowIt.remove();
			size--;
		}

	}
	private LinkedList<Memo> highs;
	private LinkedList<Memo> normals;
	private LinkedList<Memo> lows;

	private int size;

	public MemoList(){

		highs=new LinkedList<Memo>();
		normals=new LinkedList<Memo>();
		lows=new LinkedList<Memo>();
		size=0;
	}

	public MemoList(MemoList ml){

		this.highs=new LinkedList<Memo>(ml.highs);
		this.normals=new LinkedList<Memo>(ml.normals);
		this.lows=new LinkedList<Memo>(ml.lows);
		this.size=ml.size;
	}

	public int size(){

		return size;
	}//size

	public void clear(){

		highs.clear();
		normals.clear();
		lows.clear();
		size=0;
	}//clear

	public boolean isEmpty(){

		return size==0;
	}//isEmpty

	public boolean add(Memo t){
		
		return this.add(t,false);
	}
	public boolean add(Memo t,boolean fromDB){
		
		Memo precedente=null;
		if(!fromDB)
			if(get(t.getId())!=null){
				precedente=get(t.getId());
				System.out.println("ce n'era già uno:"+precedente.description());
				if(t.identici(precedente))
					return false;
				else{
					if(precedente.getPure())
						return false;
					else
						remove(precedente);
				}
			}
		LinkedList<Memo> current;
		switch(t.priority()){			//ora andiamo a controllare la priorità del task
		case 2:/*high*/	current=highs;break;
		case 1:/*norm*/	current=normals;break;
		case 0:/*low */	current=lows;break;
		default: throw new IllegalArgumentException("Che cazzo di priorità ha???");
		}
		if(!fromDB){
			ListIterator<Memo> lit=current.listIterator();
			while(lit.hasNext()){
				if(t.getEnd().compareTo(lit.next().getEnd())<0){
					lit.previous();
					break;
				}
			}
			lit.add(t);
		}
		else
			current.addLast(t);
		size++;
		return true;
	}//add

	public boolean contains(Memo t){
		
		switch(t.priority()){
		case 2:/*high*/	return highs.contains(t);
		case 1:/*norm*/	return normals.contains(t);
		case 0:/*low */	return lows.contains(t);
		default: throw new IllegalArgumentException("Che cazzo di priorità ha???");
		}
	}//contains
	
	/**
	 * Ricerca un memo che all'interno della lista corrisponda all'id inserito
	 * @param id
	 * @return
	 */
	public boolean contains(String id){
		
		for(Memo m:lows)
			if(m.getId().equals(id))
				return true;
		for(Memo m:normals)
			if(m.getId().equals(id))
				return true;
		for(Memo m:highs)
			if(m.getId().equals(id))
				return true;
		return false;
	}

	public boolean containsAll(MemoList tl){

		for(int i=0;i<tl.size;i++)
			if(!contains(tl.get(i)))
				return false;
		return true;
	}//containsAll

	public Memo get(int index){

		if(index<0 || index >=size)
			throw new NullPointerException("valore di index non supportato");
		if(index>=0 && index<highs.size())
			return highs.get(index);
		else if(index>=highs.size() && index<highs.size()+normals.size())
			return normals.get(index-highs.size());
		else if(index>=highs.size()+normals.size() && index<highs.size()+normals.size()+lows.size())
			return lows.get(index-highs.size()-normals.size());
		else return null;
	}//get
	
	public Memo get(String descrizione,int priorità,Data end){
		
		Memo tipo=new Memo(descrizione,end);
		Iterator<Memo> it=iterator();
		switch(priorità){
		case 2: it=highs.iterator(); break;
		case 1: it=normals.iterator(); break;
		case 0: it=lows.iterator(); break;
		}
		while(it.hasNext()){
			Memo x=it.next();
			if(x.identici(tipo))
				return x;
		}
		return null;
	}
	
	public Memo get(String id){
		
		Iterator<Memo> it=iterator();
		while(it.hasNext()){
			Memo x=it.next();
			if(x.getId().equals(id))
				return x;
		}
		return null;
	}

	public int indexOf(Memo m){

		switch(m.priority()){
		case 2: return highs.indexOf(m);
		case 1: return highs.size()+normals.indexOf(m);
		case 0: return highs.size()+normals.size()+lows.indexOf(m);
		default: return -1;
		}
	}
	
	/**
	 * Complessita nel caso peggiore O(n);
	 * @param t
	 * @return
	 */
	public boolean remove(Memo t){

		boolean result;
		switch ( t.priority() ){
		case 0: /*lows*/ 	result=lows.remove(t);		break;
		case 1: /*normals*/ result=normals.remove(t);	break;
		case 2: /*highs*/ 	result=highs.remove(t);		break;
		default: System.out.println("memolist non contiene questo memo");return false;
		}
		if(result)
			size--;
		return result;
	}//remove singolo
	
	/**
	 * Rimuove un elemento che nella lista corrisponde all'id inserito
	 * @param id
	 * @return
	 */
	public boolean remove(String id){
		
		Iterator<Memo> it=lows.iterator();
		while(it.hasNext())
			if(it.next().getId().equals(id)){
				it.remove();
				size--;
				return true;
			}
		it=normals.iterator();
		while(it.hasNext())
			if(it.next().getId().equals(id)){
				it.remove();
				size--;
				return true;
			}
		it=highs.iterator();
		while(it.hasNext())
			if(it.next().getId().equals(id)){
				it.remove();
				size--;
				return true;
			}
		return false;
	}
	


	@Override
	public Iterator<Memo> iterator() {

		return new myIterator<Memo>();
	}//iterator

	public boolean equals(Object x){

		if(x==this)
			return true;
		if(!(x instanceof MemoList))
			return false;
		MemoList tl=(MemoList)x;
		if(tl.size==size()){		//perchè le dimensioni contano
			int i=0;
			while(i<size()){
				if(!tl.get(i).equals(get(i)))
					return false;
				else i++;
			}
			return true;	//se abbiamo scandito tutta la lista e gli elementi erano uguali
		}
		return false; //se le dimensioni non combaciano
	}//equals

	public String toString(){

		StringBuffer sb=new StringBuffer(size*50);
		/*for(int i=0;i<highs.size();i++){
			sb.append(highs.get(i)+"\n");
		}
		for(int i=0;i<normals.size();i++)
			sb.append(normals.get(i)+"\n");
		for(int i=0;i<lows.size();i++){
			sb.append(lows.get(i)+"\n");
		}*/
		for(Memo m:this)
			sb.append(m+"\n");
		return sb.toString();
	}//toString

	public static void main(String[] args){

		Memo one=new Memo("Canapisa!!!","high",2014,5,31,16,0);
		Memo two=new Memo("vigilia Canapisa",2014,5,30,16,0);
		Memo three=new Memo("antivigilia Canapisa","low",2014,5,29,16,0);
		Memo past=new Memo("questo è vecchio",2014,2,21,13,0);
		Memo prova2=new Memo("prova2",2014,7,17,0,0);
		MemoList ml=new MemoList();
		ml.add(one,false);
		ml.add(two,false);
		ml.add(three,false);
		ml.add(past,false);
		ml.add(prova2,false);
		System.out.println("size:"+ml.size());
		System.out.println(ml);
		if(prova2.getEnd().compareTo(new Data())<0){
			System.out.println("Questo memo è più vecchio");
			ml.remove(prova2);
		}


	}
}//Memolist