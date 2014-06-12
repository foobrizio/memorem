package util;

import java.util.*;
import main.Memo;



public class MemoList implements Iterable<Memo>{
	
	@SuppressWarnings("hiding")
	private class Iteratore<T> implements Iterator<T>{

		private int index=-1;
		
		public boolean hasNext() {
			return index<=size-2;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			
			if(index>=size()-1)	//sono finiti gli elementi o la tasklist è vuota
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
			}
			else if(index>=0 && index<highs.size()){			//ci troviamo nella LL highs
				if(index==highs.size()-1){			//ultimo elemento di priorità A
					if(normals.size()!=0){
						index++;
						return (T)normals.get(0);
					}
					else if(lows.size()!=0){
						index++;
						return (T)lows.get(0);
					}
					else return null;
				}
				else{								//tutti gli altri casi
					index++;
					return (T)highs.get(index);
				}
			}
			else if(index>=highs.size() && index<(highs.size()+normals.size())){ //ci troviamo nella LL normals
				if(index==(highs.size()+normals.size()-1)){		//ultimo elemento di priorità B
					index++;
					return (T)lows.get(0);
				}
				else{
					index++;
					return (T)normals.get(index-highs.size());
				}
			}
			else{				//ci troviamo nella LL lows
				System.out.println("low-next");
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
		
		highs=ml.highs;
		normals=ml.normals;
		lows=ml.lows;
		size=ml.size;
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
		
		if(contains(t))		//non si può inserire qualcosa che esiste già
			return false;
		LinkedList<Memo> current;
		switch(t.priority()){			//ora andiamo a controllare la priorità del task
		case 2:/*high*/	current=highs;break;
		case 1:/*norm*/	current=normals;break;
		case 0:/*low */	current=lows;break;
		default: throw new IllegalArgumentException("Che cazzo di priorità ha???");
		}
		int i=0;
		for(;i<current.size();i++){
			if(t.getEnd().compareTo(current.get(i).getEnd())<0)
				break;
		}
		current.add(i, t);
		size++;
		return true;
	}//add
	
	public boolean add(Memo... t){
		
		for(int i=0;i<t.length;i++)
			if(!add(t[i])){
				System.out.println("ho aggiunto solo "+i+1+" dei"+t.length+" Task");
				return false;
		}
		return true;
	}//add multiplo

	public boolean contains(Memo t){
		
		LinkedList<Memo> current;
		switch(t.priority()){
		case 2:/*high*/	current=highs;break;
		case 1:/*norm*/	current=normals;break;
		case 0:/*low */	current=lows;break;
		default: throw new IllegalArgumentException("Che cazzo di priorità ha???");
		}
		return current.contains(t);
	}//contains
	
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
	
	public boolean remove(Memo t){
		
		if( !contains(t) )	//non si può rimuovere qualcosa che non c'è
			return true;
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
	
	public boolean remove(Memo... t){
		
		for( int i=0 ; i<t.length ; i++ )
			if( !remove( t[i] ) )
				return false;
		return true;
	}//remove multiplo
	
	@Override
	public Iterator<Memo> iterator() {
	
		return new Iteratore<Memo>();
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
		for(int i=0;i<highs.size();i++){
			sb.append(highs.get(i)+"\n");
		}
		for(int i=0;i<normals.size();i++)
			sb.append(normals.get(i)+"\n");
		for(int i=0;i<lows.size();i++){
			sb.append(lows.get(i)+"\n");
		}
		return sb.toString();
	}//toString
	
	public static void main(String[] args){
		
		Memo one=new Memo("Canapisa!!!","high",2014,5,31,16,0);
		Memo two=new Memo("vigilia Canapisa",2014,5,30,16,0);
		Memo three=new Memo("antivigilia Canapisa","low",2014,5,29,16,0);
		Memo past=new Memo("questo è vecchio",2014,2,21,13,0);
		MemoList ml=new MemoList();
		ml.add(one,two,three,past);
		System.out.println(ml);
		Memo nuovo=new Memo("questo è vecchiohoho",2014,6,21,13,0);
		ml.remove(past);
		ml.add(nuovo);
		System.out.println(ml);
		
	}
}//Memolist
