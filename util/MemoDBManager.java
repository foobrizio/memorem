package util;

import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;

import com.mysql.jdbc.exceptions.jdbc4.*;

import main.Memo;


/*
 * Cosa manca ancora:
 * 1) Metodi di ricerca all'interno della tabella (ad esempio contains(Memo m);)
 * 2) Metodi di analisi dei record della tabella per fornire statistiche
 * 3) Supporto multi-utente (ogni utente ha la sua coppia di tabelle dentro il database)
 */
public class MemoDBManager implements Iterable<Memo>{

	private String dbName;		//nome database
	
	private final String mo="memodataold";
	private final String mn="memodatanew";
	
	static final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	static final String DB_URL="jdbc:mysql://127.0.0.1/";
	
	private String user;
	private String DBpassword;
	
	private static final String createDB="create database if not exists ";
	//private static final String createUS="create user "+user+"@localhost";
	private String grant="grant usage on ";
	private Connection conn;
	private Statement stmt;
	public MemoDBManager(String dbName){
		
		this.dbName=dbName;
		this.user="none";
		grant=grant+dbName+".* to "+user+"@localhost";
		conn=null;
		stmt=null;
		DBpassword="my,u.i-o";
		//createUS=createUS+"\""+"hashed\"";
		//registriamo jdbc.Driver
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			conn=DriverManager.getConnection(DB_URL,"root",DBpassword);
			stmt=conn.createStatement();
			stmt.executeUpdate(createDB+" "+dbName);
			System.out.println("database creato");
			//System.out.println(stmt.executeUpdate(createUS));
			System.out.println(stmt.executeUpdate(grant));
			createUsersTable();
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch (SQLException e){
				e.printStackTrace();
			}
			try{
				if(conn!=null)
					conn.close();
			}catch (SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Crea la tabella che gestisce gli utenti che possono accedere al database
	 */
	private void createUsersTable(){
		
		String sql="CREATE TABLE IF NOT EXISTS memousers"+
		"("+
		"nickname VARCHAR(16) PRIMARY KEY,"+
		"password VARCHAR(16) NOT NULL"+
		")";
		int res=executeUpdate(sql);
		if(res!=0)
			System.out.println("Tabella utenti creata");
	}
	
	/**
	 * Aggiunge un utente al database
	 * @param user, il nickname dell'utente, che può essere massimo di 16 caratteri
	 * @param password, la password dell'utente, che può essere massimo di 16 caratteri
	 * @return true se l'utente è stato aggiunto
	 */
	public boolean addUser(String user,String password) throws MySQLIntegrityConstraintViolationException{
		
		String sql="INSERT INTO memousers(nickname,password) VALUES(?,?)";
		boolean alreadyExists=false;
		int result=-1;
		PreparedStatement ps=null;
		try{
			conn=DriverManager.getConnection(DB_URL+dbName, "root", DBpassword);
			ps=conn.prepareStatement(sql);
			ps.setString(1, user);
			ps.setString(2, password);
			result=ps.executeUpdate();
		}catch(SQLException e){
			if(e instanceof MySQLIntegrityConstraintViolationException)
				throw new MySQLIntegrityConstraintViolationException();
			e.printStackTrace();
			System.out.println("E' successo qualcosa di strano durante l'aggiunta dell'utente");
		}finally{
		
			try{
				if(ps!=null)
					ps.close();
				if(conn!=null)
					conn.close();
			}catch(SQLException f){
				f.printStackTrace();
			}
		}
		if(alreadyExists)
			return false;
		if(result==1){
			System.out.println("Abbiamo creato l'utente. Ora proviamo il login... ");
			login(user,password);
			return true;
		}
		else{
			System.out.println("Non abbiamo creato l'utente "+user);
			return false;
		}
	}
	
	/**
	 * Rimuove un utente dal database
	 * @param user
	 * @return
	 */
	private boolean removeUser(String user){
		
		String sql="DELETE FROM memousers WHERE nickname='"+user+"'";
		int result=executeUpdate(sql);
		if(result==0){
			System.out.println("Abbiamo rimosso l'utente "+user);
			sql="DELETE * from "+mn+" where user='"+user+"'";
			executeUpdate(sql);
			sql="DELETE * from "+mo+" where user='"+user+"'";
			executeUpdate(sql);
			if(this.user.equals(user))
				logout();
			return true;
		}
		else{
			System.out.println("Non abbiamo creato l'utente "+user);
			return false;
		}
	}
	
	public int login(String user,String password){
		
		String sql="SELECT password FROM memousers WHERE nickname='"+user+"'";
		ResultSet rs=executeQuery(sql);
		String pass=null;
		try {
			while(rs.next()){
				pass=rs.getString("password");
			}
		}catch(SQLException e){ 
			e.printStackTrace();
		}
		if(pass==null){
			System.out.println("l'utente non esiste");
			return 1;
		}
		else if(!pass.equals(password)){
			System.out.println("La password per l'utente "+user+" è sbagliata");
			return 2;
		}
		else{
			System.out.println("Login riuscito!!!");
			this.user=user;
			return 0;
		}
	}
	
	/**
	 * Disconnette l'utente attivo dall'utilizzo del database
	 * @return
	 */
	public boolean logout(){
		
		System.out.println(user+" si è disconnesso");
		user="none";
		return true;
	}
	
	public LinkedList<String> userList(){
		
		LinkedList<String> ll=new LinkedList<String>();
		String sql="SELECT nickname FROM memousers";
		ResultSet rs=executeQuery(sql);
		try{
			while(rs.next()){
				ll.add(rs.getString("nickname"));
			}
		}catch(SQLException sqle){
			sqle.printStackTrace();
			System.out.println("Sono un'eccezione bastarda muahuahuauhauua");
		}
		return ll;
	}
	/**
	 * Elimina una tabella dal database
	 * @param table
	 */
	private void deleteTable(String table){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return;
		}
		String delTbl="DROP TABLE "+table;
		try{
			conn=DriverManager.getConnection(DB_URL+dbName,"root",DBpassword);
			stmt=conn.createStatement();
			stmt.executeUpdate(delTbl);
		}catch (SQLException e){
			if(e.getMessage().substring(0, 13).equals("Unknown table"))
				System.out.println("la tabella non esiste");
			else e.printStackTrace();
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
				if(conn!=null)
					conn.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}//deleteTable
	
	private void createTables(){
		
		createTable(mn);
		createTable(mo);
	}
	/**
	 * Crea la tabella "table" all'interno del database
	 * @param table
	 */
	private void createTable(String table){		//funziona
		
		String tbl="CREATE TABLE IF NOT EXISTS "+table+
		"("+
		"descrizione VARCHAR(255),"+
		"prior TINYINT(3) DEFAULT 1,"+
		"end DATETIME NOT NULL, "+
		"completed BIT DEFAULT 0,"+
		"user VARCHAR(20) NOT NULL,"+
		"FOREIGN KEY(user) REFERENCES memousers(nickname),"+
		"PRIMARY KEY(descrizione,end,user))";
		int res=executeUpdate(tbl);
		//System.out.println("RES= "+res);
		if(res!=0)
			System.out.println("tabella "+table+" creata");
		eliminaDuplicati(table);
	}
	/**
	 * Elimina le ridondanze all'interno di una tabella
	 * @param table
	 */
	public void eliminaDuplicati(String table){		//funziona
		
		String sql="ALTER IGNORE TABLE "+table+" ADD UNIQUE KEY (descrizione,end,user)";
		executeUpdate(sql);
	}
	/**
	 * Inserisce un nuovo memo nella tabella del database
	 * @param m
	 */
	public boolean insertMemo(Memo m){		//funziona
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return false;
		}
		return insertMemo(m,mn);
	}
	
	private boolean insertMemo(Memo m,String table){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return false;
		}
		String sql="INSERT INTO "+table+"(descrizione,prior,end,user,completed)";
		//valori primitivi
		String desc=m.description();
		int result=-1;
		int prior=m.priority();
		//valori che vanno convertiti
		String end=Data.convertDateToString(m.getEnd());
		//System.out.println(" >>>> "+end.toString());
		//PreparedStatement stmt=null;
		sql+=" VALUES('";
			/* conn=DriverManager.getConnection(DB_URL+dbName,"root", DBpassword);
			 * stmt=conn.prepareStatement(sql);
			 * stmt.setString(1, desc);
			 * stmt.setInt(2, prior);
			 * stmt.set(3, end);
			 * stmt.setBoolean(4, m.isCompleted());
			 * result=stmt.executeUpdate();
			 */
			sql+=desc+"',"+prior+",'"+end+"','"+user+"',b'"+(m.isCompleted()?"1":"0")+"')";
			result=executeUpdate(sql);
		
		/*catch(SQLException e) {
			System.out.println("insert e");
			e.printStackTrace();
		} finally{
			
			try {
				if(stmt!=null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try{
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}*/
		return result==1;
	}
	
	/**
	 * Rimuove un memo dalla tabella(eliminandola quindi per sempre)
	 * @param m
	 */
	public boolean removeMemo(Memo m){	//funziona
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return false;
		}
		if(removeMemo(m,mn))
			return true;
		else if(removeMemo(m,mo))
			return true;
		else return false;
	}
	
	private boolean removeMemo(Memo m,String table){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return false;
		}
		String sql="DELETE FROM "+table+" WHERE descrizione='"+m.description()+"' AND end='";
		sql=sql+Data.convertDateToString(m.getEnd())+"'";
		System.out.println(sql);
		return executeUpdate(sql)==1;
	}
	
	/**
	 * Modifica un memo esistente sulla tabella, aggiornandone i valori
	 * @param nuovo, il memo con le modifiche che verranno passate alla query
	 * @param vecchio, il memo dentro la tabella che verrà modificato
	 * @return
	 */
	public boolean modifyMemo(Memo vecchio,Memo nuovo){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return false;
		}
		if(!contains(vecchio))
			return false;
		String sql="UPDATE "+mn+" SET";
		boolean desc=nuovo.description().equals(vecchio.description());
		boolean prior=nuovo.priority()==vecchio.priority();
		boolean data=nuovo.getEnd().equals(vecchio.getEnd());
		if(!desc)
			sql+=" descrizione='"+nuovo.description()+"'";
		if(!prior){
			if(!desc)
				sql+=", ";
			sql+=" prior='"+nuovo.priority()+"'";
		}
		if(!data){
			if(!desc || !prior)
				sql+=", ";
			sql+=" end='"+Data.convertDateToString(nuovo.getEnd())+"'";
		}
		sql+=" WHERE descrizione='"+vecchio.description()+"' AND end='"+Data.convertDateToString(vecchio.getEnd())+"'";
		System.out.println(sql);
		return executeUpdate(sql)!=0;
	}
	/**
	 * Muove un memo registrato in memnew dentro memold (evidentemente è stato completato)
	 * @param m
	 */
	public void move(Memo m,boolean completato){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return;
		}
		if(contains(m,mn)){
			System.out.println("il memo è stato trovato");
			if(removeMemo(m,mn))
				System.out.println("il memo è stato rimosso da "+mn);
			if(completato)
				m.spunta();
			if(insertMemo(m,mo))
				System.out.println("il memo è stato aggiunto in "+mo);
		}
	}
	
	/**
	 * Ritorna i memo contenuti nel database sottoforma di lista
	 * @param soloAttivi
	 * @return
	 */
	public MemoList list(boolean soloAttivi){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return null;
		}
		String sql="SELECT * FROM "+mn;
		String sql2="SELECT * FROM "+mo;
		ResultSet rs=executeQuery(sql);
		ResultSet rs2 = null;
		if(!soloAttivi)
			rs2=executeQuery(sql2);
		MemoList ml=new MemoList();
		try{
			Memo m;
			while(rs.next()){
				m=null;
				String desc=rs.getString("descrizione");
				int prior=rs.getInt("prior");
				String end=rs.getString("end");
				boolean completed=rs.getBoolean("completed");
				Data fine=Data.convertStringToData(end);
				m=new Memo(desc,prior,fine);
				if(completed)
					m.spunta();
				ml.add(m);
			}
			if(!soloAttivi){
				while(rs2.next()){
					m=null;
					String desc=rs.getString(0);
					int prior=rs.getInt(1);
					String end=rs.getString(2);
					boolean completed=rs.getBoolean(3);
					Data fine=Data.convertStringToData(end);
					m=new Memo(desc,prior,fine);
					if(completed)
						m.spunta();
					ml.add(m);
					
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("qualcosa è andato storto durante la creazione della list");
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(SQLException e2){
				e2.printStackTrace();
			}
		}
		return ml;
	}
	

	@Override
	public Iterator<Memo> iterator() {
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return null;
		}
		return list(true).iterator();
	}
	
	/**
	 * Ritorna true se il memo m è contenuto nella tabella table, false altrimenti
	 */
	public boolean contains(Memo m){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return false;
		}
		return contains(m,mn);
	}
	
	private boolean contains(Memo m,String table){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return false;
		}
		String end=Data.convertDateToString(m.getEnd());
		String sql="SELECT * FROM "+table+" WHERE descrizione='"+m.description()+"'"+
		" AND end='"+end+"'";
		//System.out.println(sql);
		System.out.println(sql);
		ResultSet rs=executeQuery(sql);
		int x=0;
		try{ 
			rs.last(); //posizioniamo il cursore nell'ultima riga
			x=rs.getRow();	//ci facciamo dire in che riga si trova
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(SQLException e2){
				e2.printStackTrace();
			}
		}
		return x==1;
	}
	
	/**
	 * Ritorn il memo che nel database corrisponde alla descrizione "desc" e alla data "end" 
	 * @param desc
	 * @param end
	 * @return
	 */
	public Memo get(String desc, Data end){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return null;
		}
		boolean scaduto=end.compareTo(new Data())<0;
		String sql="SELECT * FROM ";
		if(scaduto)
			sql+=mo+" ";
		else
			sql+=mn+" ";
		sql+="WHERE descrizione='"+desc+"' AND end='"+Data.convertDateToString(end)+"'";
		ResultSet rs=executeQuery(sql);
		Memo m=null;
		try{
			while(rs.next()){
				String rizione=rs.getString("descrizione");
				int prior=rs.getInt("prior");
				String data=rs.getString("end");
				m=new Memo(rizione,prior,Data.convertStringToData(data));
				break;
			}
			rs.close();
		}catch(SQLException e){
			System.out.println("mmm problemi nel get");
			e.printStackTrace();
		}
		return m;
	}
	/**
	 * Cancella tutti i record di una tabella
	 */
	public void clear(){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return;
		}
		clearFromTable(mn);
		clearFromTable(mo);
	}
	
	public MemoList processQuery(String query){
		
		ResultSet rs=executeQuery(query);
		MemoList sancarlo=new MemoList();
		try{
			while(rs.next()){
				String d=rs.getString("descrizione");
				int pr=rs.getInt("prior");
				String e=rs.getString("end");
				boolean c=rs.getBoolean("completed");
				Memo m=new Memo(d,pr,Data.convertStringToData(e));
				if(c)
					m.spunta();
				sancarlo.add(m);
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Errori durante la creazione della MemoList");
		}
		return sancarlo;
	}
	
	private void clearFromTable(String table){	//funziona
		
		String sql="DELETE FROM "+table+" WHERE user='"+user+"'";	//questo metodo è più veloce di DELETE FROM perchè cancella l'intera tabella e ne ricrea una nuova
		executeUpdate(sql);
	}
	
	/**
	 * Ritorna il numero di righe della tabella
	 * @param table
	 * @return
	 */
	public int size(boolean soloAttivi){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return 0;
		}
		int x=size(mn);
		if(!soloAttivi)
			x+=size(mo);
		return x;
	}
	
	private int size(String table){		//funziona
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return 0;
		}
		String sql="SELECT COUNT(*) AS ehi FROM "+table;
		ResultSet rs=executeQuery(sql);
		int size=-1;
		try{
			while(rs.next())
				size=rs.getInt("ehi");
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("non siamo riusciti a valutare la size");
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
				if(conn!=null)
					conn.close();
			}catch(SQLException eq){
				eq.printStackTrace();
				System.out.println("problemi durante la chiusura");
			}
		}
		return size;
	}
	
	/**
	 * Ritorna il rapporto tra memo completati e memo totali scaduti, utile per settare la progressBar
	 * @return
	 */
	public int[] rapportoCompletati(){
		
		int[] risultato=new int[2];	//la prima posizione indica il numero di memo completati, la seconda indica il numero di memo totali scaduti;
		int comp=0,scad;
		scad=size(mo);
		String sql="SELECT COUNT(*) AS hei FROM "+mo+" WHERE completed=(1) and user='"+user+"'";
		ResultSet rs=executeQuery(sql);
		try{
			while(rs.next())
				comp=rs.getInt("hei");
			rs.close();
		}catch(SQLException e){
			System.out.println("Errore durante il rapporto dei memo completati");
		}
		risultato[0]=comp;
		risultato[1]=scad;
		return risultato;
	}
	/**
	 * Ritorna la tabella 'table' sottoforma di stringa
	 */
	public String toString(boolean soloAttivi){
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return null;
		}
		StringBuilder sb=new StringBuilder(1000);
		sb.append(toString(mn));
		if(!soloAttivi){
			System.out.println("anche quelli scaduti");
			sb.append("\n\tSCADUTI\n");
			sb.append(toString(mo));
		}
		return sb.toString();
	}
	
	private String toString(String table){	//funziona
		
		if(user.equals("none")){
			System.out.println("Nessun utente connesso al database");
			return null;
		}
		String sql="SELECT * FROM "+table;
		ResultSet rs=null;
		String result="";
		rs=executeQuery(sql);
		try {
			while(rs.next()){
				String desc= rs.getString("descrizione");
				int prior=rs.getInt("prior");
				String end=rs.getString("end");
				//System.out.println("end after all >>> "+end.toString());
				//boolean comp=rs.getBoolean("completed");
				result+=new Memo(desc,prior,Data.convertStringToData(end))+"\n";
				}
			rs.close();
		} catch (SQLException e2) {
			System.out.println("toString e2");
			e2.printStackTrace();
		} finally{
			try{
		        if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		}
		return result;
	}
	
	/**
	 * Esegue un update e ne ritorna il risultato, che può essere utilizzato in fase di debug
	 * @param sql
	 * @return
	 */
	private int executeUpdate(String sql){
		
		int x=0;
		try{
			conn=DriverManager.getConnection(DB_URL+dbName, "root", DBpassword);
			stmt=conn.createStatement();
			x=stmt.executeUpdate(sql);
		}catch(MySQLIntegrityConstraintViolationException e){
			System.out.println("Impossibile inserire duplicati yeeeah");
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Errore in apertura per query "+sql);
		}finally{
			try{
				if(stmt!=null)
					stmt.close();
				if(conn!=null)
					conn.close();
			}catch(SQLException e2){
				e2.printStackTrace();
				System.out.println("Errore in chiusura");
			}
		}
		return x;
	}
	
	/**
	 * Esegue una query e ne ritorna il ResultSet su cui lavorare
	 * @param sql
	 * @return
	 */
	private ResultSet executeQuery(String sql){
		
		ResultSet rs=null;
		try{
			conn=DriverManager.getConnection(DB_URL+dbName, "root", DBpassword);
			stmt=conn.createStatement();
			rs=stmt.executeQuery(sql);
		}catch(SQLException e){
			System.out.println("e");
			e.printStackTrace();
		}
		return rs;
	}
	
	
	public static void main(String[] args){
		
		MemoDBManager mdbm=new MemoDBManager("memodatabase");
		Memo m=new Memo("compleanno di Judy","normal",2014,2,28,22,00);
		Memo m2=new Memo("Canapisa!!!!","high",2014,5,31,16,00);
		Memo m3=new Memo("compleanno Francesco","normal",2014,2,27,21,00);
		Memo m4=new Memo("Canapisa!!!","high",2014,5,31,15,00);
		/*try{
			mdbm.addUser("fabrizio", "sticazzi");
			mdbm.addUser("roberto", "cujjhuni");
			mdbm.addUser("raffaele", "quellachevuoitu");
		}catch(MySQLIntegrityConstraintViolationException me){
			System.out.println("l'utente esiste già");
		}*/
		
		mdbm.login("fabrizio", "sticazzi");
		mdbm.insertMemo(m);
		mdbm.insertMemo(m3);
		//System.out.println("rimozione:"+mdbm.removeMemo(m4));
		//System.out.println("inserimento:"+mdbm.insertMemo(m4));
		
		//mdbm.move(m3);
		
		//mdbm.modifyMemo(m2, m4);
		//mdbm.clearTable("memnew");
		//mdbm.deleteTable("memold");
		//mdbm.removeMemo(m4);
		//mdbm.insertMemo(m2);
		
		//System.out.println("lo contiene?"+mdbm.contains(m));
		//System.out.println("ora cancelliamo..."); mdbm.clear(table);
		System.out.println("la size è "+mdbm.size(true));
		System.out.println(mdbm.toString(true));
		mdbm.logout();
		/*LinkedList<String> utenti=mdbm.userList();
		for(String s: utenti)
			System.out.println(s);
		*/
	}
}