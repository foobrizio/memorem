package util;

import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import org.apache.commons.codec.digest.DigestUtils;	//classe DigestUtils per la decodifica SHA

import com.mysql.jdbc.exceptions.jdbc4.*; 			//eccezione MySQLIntegrityConstraintException

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
	
	private User user;
	private String DBpassword;
	
	private static final String createDB="create database if not exists ";
	private String grant="grant usage on ";
	private Connection conn;
	private Statement stmt;
	
	/**
	 * Costruttore 
	 * @param dbName
	 */
	public MemoDBManager(String dbName){
		
		this.dbName=dbName;
		grant=grant+dbName+".* to root@localhost";
		this.user=new User("none");
		conn=null;
		stmt=null;
		DBpassword="my,u.i-o";
		addUser("admin","Iamtheadmin",null,null,'m');
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			conn=DriverManager.getConnection(DB_URL,"root",DBpassword);
			stmt=conn.createStatement();
			stmt.executeUpdate(createDB+" "+dbName);
			stmt.executeUpdate(grant);
			createUsersTable();
			createTables();
			createHintsTable();
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
		"password CHAR(40) NOT NULL,"+
		"nome VARCHAR(16) DEFAULT '---',"+
		"cognome VARCHAR(16) DEFAULT '---'"+
		")";
		int res=executeUpdate(sql);
		if(res!=0){
			System.out.println("Tabella utenti creata");
			createHintsTable();
		}
	}
	
	/**
	 * Crea la tabella che conterrà i suggerimenti e i consigli per conoscere le potenzialità del
	 * programma
	 */
	private void createHintsTable(){
		
		String sql="CREATE TABLE IF NOT EXISTS hints"+
		"("+
		"hint VARCHAR(256) NOT NULL,"+
		"hash CHAR(40) PRIMARY KEY)";
		int res=executeUpdate(sql);
		//System.out.println(res);
		if(res!=0)
			populateHints();
	}
	
	private void populateHints(){
		
		LinkedList<String> aiuti=new LinkedList<String>();
		aiuti.add("Puoi completare un memo prima della sua scadenza cliccando sul tasto 'completa' nel menu 'Opzioni'");
		aiuti.add("La barra azzurra in basso indica il rapporto in percentuale tra tutti i memo notificati e quelli che hai completato.");
		aiuti.add("Quando un memo scade puoi notificarlo al database, indicando se sei riuscito a completarlo oppure se non hai fatto in tempo (in questo caso cliccherai il tasto 'archivia' nel menu 'Opzioni').");
		aiuti.add("Puoi visualizzare a piacimento lo storico dei tuoi memo utilizzando i filtri presenti nella barra degli strumenti interna.");
		aiuti.add("Se hai inserito un memo utilizzando dati errati puoi modificarlo: utilizza il tasto 'modifica' nel menu 'Opzioni'. Se invece vuoi eliminarlo basterà premere il tasto 'elimina'.");
		aiuti.add("I memo vengono colorati in base alla loro priorità. Un memo rosso indicherà che ha priorità alta, uno giallo ha priorità normale e uno blu ha priorità bassa");
		aiuti.add("Puoi visualizzare il countdown alla scadenza di un memo semplicemente passando il mouse sopra la sua data di scadenza. Puoi visualizzare il countdown in modo permanente premendo il tasto 'cambia visualizzazione' nel menu 'Opzioni'");
		aiuti.add("Puoi attivare e disattivare i suggerimenti premendo il tasto 'hints' nel menu 'Visualizzazione'.");
		aiuti.add("Dal menu 'Visualizzazione' puoi attivare o disattivare l'interfaccia a calendario.");
		aiuti.add("Quando utilizzi l'interfaccia a calendario puoi aggiungere un memo semplicemente facendo doppio clic sulla data in cui vuoi aggiungerlo.");
		aiuti.add("I memo nell'interfaccia a calendario vengono visualizzati tramite colori nelle date. Clicca col tasto destro per accedere alle funzioni.");
		aiuti.add("Vuoi controllare quanti memo hai creato finora e le loro caratteristiche? Clicca sul tasto 'Statistiche' nel menu 'Utente'");
		aiuti.add("Puoi aggiungere tutti i memo che vuoi dal tasto 'Aggiungi' nel menu 'Strumenti'");
		aiuti.add("Puoi cancellare i tuoi memo in ogni momento premendo il tasto 'Cancella' nel menu 'Strumenti'. Attenzione, questa operazione è irreversibile.");
		for(String s: aiuti){
			String sql="INSERT INTO hints(hint,hash) VALUES(\""+s+"\",SHA(\""+s+"\"))";
			int result=executeUpdate(sql);
			if(result!=0)
				System.out.println("aiuto inserito");
		}
	}
	
	/**
	 * Aggiunge un utente al database
	 * @param user, il nickname dell'utente, che può essere massimo di 16 caratteri
	 * @param password, la password dell'utente, che può essere massimo di 16 caratteri
	 * @return 0 se l'utente è stato aggiunto, 1 se esiste già un utente con lo stesso nickname,
	 * 			2 per altri errori
	 */
	public int addUser(String user,String password,String nome,String cognome,char genere){
		
		if(nome==null)
			nome="---";
		if(cognome==null)
			cognome="---";
		boolean gen=true;
		if(genere=='f')
			gen=false;
		String sqlCheck="SELECT nickname FROM memousers WHERE nickname='"+user+"'";
		ResultSet rs=executeQuery(sqlCheck);
		boolean alreadyExist=false;
		try{
			while(rs.next())
				if(rs.getString("nickname").trim().equals(user.trim()))
					alreadyExist=true;
			rs.close();
		}catch(SQLException sqle){
			System.out.println("SQL exception in addUser");
			sqle.printStackTrace();
		}
		if(alreadyExist)
			return 1;
		String sql="INSERT INTO memousers(nickname,password,nome,cognome,genere) VALUES('"+verificaStringa(user)+"',SHA('"+password+"','"+verificaStringa(nome)+"','"+verificaStringa(cognome)+"',b'"+(gen?"1":"0")+"')";
		int result=executeUpdate(sql);
		if(result==1){
			if(login(user,password)==0) //il login va a buon fine
				return 0;
			else return 2;				//il login non va a buon fine
		}
		else{
			return 2;					//l'utente non è stato creato
		}
	}
	
	public User getUser(){
		
		return user;
	}
	
	/**
	 * Rimuove un utente dal database
	 * @param user
	 * @return
	 */
	public boolean removeUser(String utont){
		
		boolean autoremove=false;
		if(this.user.equals(utont))
			autoremove=true;
		else if(!this.user.equals("admin")){
			System.out.println("Non hai i permessi per farlo");
			return false;
		}
		String sqlnew="DELETE FROM "+mn+" WHERE user='"+utont+"'";
		String sqlold="DELETE FROM "+mo+" WHERE user='"+utont+"'";
		String sqluser="DELETE FROM memousers WHERE nickname='"+utont+"'";
		int result=executeUpdate(sqlnew);
		int result2=executeUpdate(sqlold);
		executeUpdate(sqluser);
		if(result==0 && result2==0){	//l'utente è stato rimosso
			if(autoremove)
				logout();
			return true;
		}
		else return false;				//l'utente non è stato rimosso
	}
	
	public int login(String user,String password){
		
		String sql="SELECT * FROM memousers WHERE nickname='"+user+"'";
		ResultSet rs=executeQuery(sql);
		String pass=null,nome=null,cognome=null;
		boolean isMaschio=true;
		try {
			while(rs.next()){
				pass=rs.getString("password");
				nome=rs.getString("nome");
				cognome=rs.getString("cognome");
				isMaschio=rs.getBoolean("genere");
			}
		}catch(SQLException e){ 
			e.printStackTrace();
		}
		int res=1;
		if(pass==null)					//l'utente non esiste
			return 1;
		else{
			String pass2=DigestUtils.shaHex(password);
			if(pass.equals(pass2)){		//password corretta
				char genere='m';
				if(!isMaschio)
					genere='f';
				this.user=new User(user,nome,cognome,genere);
				res=0;
			}
			else res=2;					//password sbagliata
		}
		return res;
	}
	
	/**
	 * Disconnette l'utente attivo dall'utilizzo del database
	 * @return
	 */
	public boolean logout(){
		
		System.out.println(user+" si è disconnesso");
		user=new User("none");
		return true;
	}
	
	public int modificaPassword(String passV,String passN){
		
		if(passV.equals(passN))
			return 3;
		String sql="SELECT password FROM memousers WHERE nickname='"+user+"'";
		ResultSet rs=executeQuery(sql);
		String pass=null;
		try{
			if(rs.next())
				pass=rs.getString("password");
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("problemi durante la modifica password, fase1");
		}
		if(pass==null)
			return 1;
		if(!pass.equals(DigestUtils.shaHex(passV)))	//le due password non combaciano
			return 2;
		sql="UPDATE memousers SET password=SHA('"+passN+"') WHERE nickname='"+verificaStringa(user.getNickname())+"'";
		int res=executeUpdate(sql);
		if(res!=0)
			return 0;
		else return 1;
	}
	
	public String[] userList(){
		
		String[] utenti=null;
		String sql="SELECT nickname FROM memousers";
		ResultSet rs=executeQuery(sql);
		int cont=0;
		try{
			while(rs.next()){		//prima scansione per stabilire la dimensione dell'array
				cont++;
			}
			rs.beforeFirst(); 		//riavvolgiamo e ricominciamo la scansione
			int i=0;
			utenti=new String[cont-1];
			while(rs.next()){
				String nick=rs.getString("nickname");
				//System.out.println("utente:"+nick+" in posizione i:"+i);
				if(!nick.equals("admin"))
					utenti[i++]=nick;
			}
		}catch(SQLException sqle){
			sqle.printStackTrace();
			System.out.println("Eccezione durante la userList");
		}
		return utenti;
	}
	
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
		"end DATETIME NOT NULL, ";
		if(table.equals(mo))
			tbl=tbl+"completed BIT DEFAULT 0, ";
		tbl=tbl+"user VARCHAR(20) NOT NULL,"+
		"icon VARCHAR(30) DEFAULT 'note.png',"+
		"FOREIGN KEY(user) REFERENCES memousers(nickname),"+
		"PRIMARY KEY(descrizione,end,user))";
		int res=executeUpdate(tbl);
		//System.out.println("RES= "+res);
		if(res!=0)
			System.out.println("tabella "+table+" creata");
		//eliminaDuplicati(table);	
	}
	/**
	 * Elimina le ridondanze all'interno di una tabella
	 * @param table
	 */
	/*public void eliminaDuplicati(String table){		//funziona
		
		String sql="ALTER TABLE "+table+" ADD UNIQUE KEY(descrizione,end,user)";
		executeUpdate(sql);
	}*/
	
	/**
	 * Inserisce un nuovo memo nella tabella del database
	 * @param m
	 */
	public boolean insertMemo(Memo m){		//funziona
		
		if(user.equals("none"))  		return false;	//nessun utente connesso
		return insertMemo(m,mn);
	}
	
	private boolean insertMemo(Memo m,String table){
		
		if(user.equals("none"))			return false;	//nessun utente connesso
		String sql="";
		if(table.equals(mn))
			sql="INSERT INTO "+mn+" (descrizione,prior,end,user,icon)";
		else sql="INSERT INTO "+mo+" (descrizione,prior,end,completed,user,icon)";
		//valori primitivi
		String desc=m.description();
		int result=-1;
		int prior=m.priority();
		boolean completed=m.isCompleted();
		//valori che vanno convertiti
		String end=Data.convertDateToString(m.getEnd());
		String icon=m.getIcon();
		sql+=" VALUES('";
		//sql+=desc+"',"+prior+",'"+end+"','"+user+"',b'"+(m.isCompleted()?"1":"0")+"')";
		if(table.equals(mn))
			sql+=verificaStringa(desc)+"',"+prior+",'"+end+"','"+verificaStringa(user.getNickname())+"','"+verificaStringa(icon)+"')";
		else
			sql+=verificaStringa(desc)+"',"+prior+",'"+end+"',b'"+(completed?"1":"0")+"','"+verificaStringa(user.getNickname())+"','"+verificaStringa(icon)+"')";
		result=executeUpdate(sql);
		return result==1;
	}
	
	/**
	 * Verifica la presenza dell'apostrofo nella stringa e ritorna una stringa compatibile con il 
	 * database
	 * @param desc
	 * @return
	 */
	private static String verificaStringa(String desc){
		
		StringBuilder sb=new StringBuilder(255);
		StringTokenizer st=new StringTokenizer(desc,"'",false);
		while(st.hasMoreTokens()){
			String token=st.nextToken();
			//System.out.println(token);
			sb.append(token);
			if(st.hasMoreTokens())
				sb.append("\\'");
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
	/**
	 * Rimuove un memo dalla tabella(eliminandolo quindi per sempre)
	 * @param m
	 */
	public boolean removeMemo(Memo m){	//funziona
		
		if(user.equals("none")) 		return false;	//nessun utente connesso
		if(removeMemo(m,mn))			return true;	//rimozione di un memo nuovo
		else if(removeMemo(m,mo))		return true;	//rimozione di un memo vecchio
		else return false;
	}
	
	private boolean removeMemo(Memo m,String table){
		
		if(user.equals("none"))			return false;	//nessun utente connesso
		String sql="DELETE FROM "+table+" WHERE descrizione='"+verificaStringa(m.description())+"' AND end='";
		sql=sql+Data.convertDateToString(m.getEnd())+"'";
		return executeUpdate(sql)==1;
	}
	
	/**
	 * Modifica un memo esistente sulla tabella, aggiornandone i valori
	 * @param nuovo, il memo con le modifiche che verranno passate alla query
	 * @param vecchio, il memo dentro la tabella che verrà modificato
	 * @return
	 */
	public boolean modifyMemo(Memo vecchio,Memo nuovo){
		
		if(user.equals("none"))			return false;	//nessun utente connesso
		if(!contains(vecchio))
			return false;
		String sql="UPDATE "+mn+" SET";
		boolean desc=nuovo.description().equals(vecchio.description());
		boolean prior=nuovo.priority()==vecchio.priority();
		boolean data=nuovo.getEnd().equals(vecchio.getEnd());
		boolean icon=nuovo.getIcon().equals(vecchio.getIcon());
		if(!desc)
			sql+=" descrizione='"+verificaStringa(nuovo.description())+"'";
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
		if(!icon){
			if(!desc || !prior || !data)
				sql+=", ";
			sql+=" icon='"+verificaStringa(nuovo.getIcon())+"'";
		}
		sql+=" WHERE descrizione='"+verificaStringa(vecchio.description())+"' AND end='"+Data.convertDateToString(vecchio.getEnd())+"'";
		//System.out.println(sql);
		return executeUpdate(sql)!=0;
	}
	/**
	 * Muove un memo registrato in memnew dentro memold (evidentemente è stato completato)
	 * @param m
	 */
	public void move(Memo m,boolean completato){
		
		if(user.equals("none"))				return;		//nessun utente connesso
		if(contains(m,mn)){								//memo trovato
			if(removeMemo(m,mn))						//memo rimosso da mn
				if(completato)							//memo eventualmente spuntato
					m.spunta();
			insertMemo(m,mo);							//memo inserito in mo
		}
	}
	
	/**
	 * Ritorna i memo contenuti nel database sottoforma di lista
	 * @param soloAttivi
	 * @return
	 */
	public MemoList list(boolean soloAttivi){
		
		if(user.equals("none"))				return null;	//nessun utente connesso
		String sql="SELECT * FROM "+mn+" where user='"+verificaStringa(user.getNickname())+"' ORDER BY prior DESC, end";
		String sql2="SELECT * FROM "+mo+" where user='"+verificaStringa(user.getNickname())+"' ORDER BY prior DESC, end";
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
				String icon=rs.getString("icon");
				Data fine=Data.convertStringToData(end);
				m=new Memo(desc,prior,fine,icon);
				//System.out.println("manager:"+m.getIcon());
				ml.add(m);
			}
			if(!soloAttivi){
				while(rs2.next()){
					m=null;
					String desc=rs2.getString("descrizione");
					int prior=rs2.getInt("prior");
					String end=rs2.getString("end");
					boolean completed=rs2.getBoolean("completed");
					String icon=rs2.getString("icon");
					Data fine=Data.convertStringToData(end);
					m=new Memo(desc,prior,fine,icon);
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
		
		if(user.equals("none"))				return null;	//nessun utente connesso
		return list(true).iterator();
	}
	
	/**
	 * Ritorna true se il memo m è contenuto nella tabella table, false altrimenti
	 */
	public boolean contains(Memo m){
		
		if(user.equals("none"))				return false;	//nessun utente connesso
		return contains(m,mn);
	}
	
	private boolean contains(Memo m,String table){
		
		if(user.equals("none"))				return false;	//nessun utente connesso
		String end=Data.convertDateToString(m.getEnd());
		String sql="SELECT * FROM "+table+" WHERE descrizione='"+verificaStringa(m.description())+"'"+
		" AND end='"+end+"'";
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
		
		if(user.equals("none"))					return null;	//nessun utente connesso
		boolean scaduto=end.compareTo(new Data())<0;
		String sql="SELECT * FROM ";
		if(scaduto)
			sql+=mo+" ";
		else
			sql+=mn+" ";
		sql+="WHERE descrizione='"+verificaStringa(desc)+"' AND end='"+Data.convertDateToString(end)+"'";
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
	 * Cancella tutti i record di un utente nella tabella
	 */
	public void clear(){
		
		if(user.equals("none"))				return;		//nessun utente connesso
		clearFromTable(mn);
		clearFromTable(mo);
	}
	
	/**
	 * Resetta tutto il database. Solo l'amministratore può usare questo metodo
	 */
	public void reset(){
		
		if(!user.equals("admin")){
			System.out.println("Non hai i privilegi di amministratore");
			return;
		}
		String sql1="TRUNCATE TABLE "+mn;
		String sql2="TRUNCATE TABLE "+mo;
		executeUpdate(sql1);
		executeUpdate(sql2);
	}
	
	public MemoList processQuery(String query,boolean scaduti){
		
		ResultSet rs=executeQuery(query);
		MemoList sancarlo=new MemoList();
		try{
			while(rs.next()){
				String d=rs.getString("descrizione");
				int pr=rs.getInt("prior");
				String e=rs.getString("end");
				boolean c=false;
				if(scaduti)
					c=rs.getBoolean("completed");
				String icon=rs.getString("icon");
				Memo m=new Memo(d,pr,Data.convertStringToData(e),icon);
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
		
		String sql="DELETE FROM "+table+" WHERE user='"+verificaStringa(user.getNickname())+"'";	//questo metodo è più veloce di DELETE FROM perchè cancella l'intera tabella e ne ricrea una nuova
		executeUpdate(sql);
	}
	
	/**
	 * Ritorna il numero di righe della tabella
	 * @param soloAttivi, se messo a true conta anche il numero di righe della tabella mo
	 * @return
	 */
	public int size(boolean soloAttivi){
		
		if(user.equals("none"))					return 0;		//nessun utente connesso
		int x=size(mn);
		if(!soloAttivi)
			x+=size(mo);
		return x;
	}
	
	private int size(String table){		//funziona
		
		if(user.getNickname().equals("none"))					return 0;		//nessun utente connesso
		String sql="SELECT COUNT(*) AS ehi FROM "+table;
		if(!(user.getNickname().equals("admin")))
			sql=sql+" WHERE user='"+verificaStringa(user.getNickname())+"'";
		ResultSet rs=executeQuery(sql);
		int size=-1;
		try{
			if(rs.next())
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
		String sql="SELECT COUNT(*) AS hei FROM "+mo+" WHERE completed=(1) and user='"+verificaStringa(user.getNickname())+"'";
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
		
		if(user.equals("none"))			return null;	//nessun utente connesso
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
		
		if(user.equals("none"))			return null;	//nessun utente connesso
		String sql="SELECT * FROM "+table+" ORDER BY prior DESC, end";
		ResultSet rs=null;
		String result="";
		rs=executeQuery(sql);
		try {
			while(rs.next()){
				String desc= rs.getString("descrizione");
				int prior=rs.getInt("prior");
				String end=rs.getString("end");
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
	 * Ritorna un array di String contenente i suggerimenti da visualizzare nel frame
	 * @return
	 */
	public String[] getHelps(){
		
		String[] helps=null;
		String sql="SELECT COUNT(*) AS ehi FROM hints";
		String sql2="SELECT * FROM hints";
		ResultSet rs=executeQuery(sql);
		try{
			if(rs.next())
				helps=new String[rs.getInt("ehi")];
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("problemi con la verifica della dimensione");
			return null;
		}
		rs=executeQuery(sql2);
		try{
			int cont=0;
			while(rs.next()){
				helps[cont]="           "+rs.getString("hint")+"    ";
				cont++;
			}
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("problemi durante la creazione dell'array");
			return null;
		}
		return helps;
	}
	/**
	 * Raccoglie statistiche sui memo dell'utente e le inserisce in un array
	 * @return Object[] result, l'array con le statistiche ordinate in questo modo:
	 * 
	 * 		result[0]= (Integer)priorità alta
	 * 		result[1]= (Integer)priorità media
	 * 		result[2]= (Integer)priorità bassa
	 * 		result[3]= (Integer)memo completati
	 * 		result[4]= (Integer)memo archiviati
	 * 		result[5]= (Integer)memo attivi
	 * 		result[6]= (Integer)memo totali
	 */
	public Object[] statistiche(){
		
		/*
		 * result[0]= (Integer)priorità alta
		 * result[1]= (Integer)priorità media
		 * result[2]= (Integer)priorità bassa
		 * result[3]= (Integer)memo completati
		 * result[4]= (Integer)memo archiviati
		 * result[5]= (Integer)memo attivi
		 * result[6]= (Integer)memo totali
		 */
		Object[] result=new Object[7];
		String prior1="SELECT COUNT(*) AS ehi FROM ";
		String prior2=" WHERE user='"+verificaStringa(user.getNickname())+"' AND prior='";
		String comp1="SELECT COUNT(*) AS wei FROM ";
		String comp2=" WHERE user='"+verificaStringa(user.getNickname())+"' AND completed='";
		try{
			conn=DriverManager.getConnection(DB_URL+dbName, "root", DBpassword);
			stmt=conn.createStatement();
			String str="";
			boolean admin=user.getNickname().equals("admin");
			if(admin)
				str=prior1+mn+" WHERE prior='2'";
			else str=prior1+mn+prior2+"2'";
			ResultSet hmn=stmt.executeQuery(str);
			if(hmn.next())
				result[0]=hmn.getInt("ehi");
			hmn.close();
			if(admin)
				str=prior1+mo+" WHERE prior='2'";
			else str=prior1+mo+prior2+"2'";
			ResultSet hmo=stmt.executeQuery(str);
			if(hmo.next()){
				Integer i=new Integer((Integer)result[0]+hmo.getInt("ehi"));
				result[0]=i;
			}//priorità alta
			hmo.close();
			if(admin)
				str=prior1+mn+" WHERE prior='1'";
			else
				str=prior1+mn+prior2+"1'";
			ResultSet mmn=stmt.executeQuery(str);
			if(mmn.next())
				result[1]=mmn.getInt("ehi");
			mmn.close();
			if(admin)
				str=prior1+mo+" WHERE prior='1'";
			else str=prior1+mo+prior2+"1'";
			ResultSet mmo=stmt.executeQuery(str);
			if(mmo.next()){
				Integer i=new Integer((Integer)result[1]+mmo.getInt("ehi"));
				result[1]=i;	
			}//priorità media
			mmo.close();
			if(admin)
				str=prior1+mn+" WHERE prior='0'";
			else str=prior1+mn+prior2+"0'";
			ResultSet lmn=stmt.executeQuery(str);
			if(lmn.next())
				result[2]=lmn.getInt("ehi");
			lmn.close();
			if(admin)
				str=prior1+mo+" WHERE prior='0'";
			else str=prior1+mo+prior2+"0'";
			ResultSet lmo=stmt.executeQuery(str);
			if(lmo.next()){
				Integer i=new Integer((Integer)result[2]+lmo.getInt("ehi"));
				result[2]=i;	
			}//priorità bassa
			lmo.close();
			if(admin)
				str=comp1+mo+" WHERE completed='1'";
			else str=comp1+mo+comp2+"1'";
			ResultSet comp=stmt.executeQuery(str);
			if(comp.next())
				result[3]=comp.getString("wei");						//completati
			comp.close();
			if(admin)
				str=comp1+mo+" WHERE completed='0'";
			else str=comp1+mo+comp2+"0'";
			ResultSet arch=stmt.executeQuery(str);
			if(arch.next())
				result[4]=arch.getString("wei");						//archiviati
			arch.close();
			str="SELECT COUNT(*) as hehe FROM "+mn;
			if(!admin)
				str=str+" WHERE user='"+verificaStringa(user.getNickname())+"'";
			ResultSet act=stmt.executeQuery(str);
			if(act.next())
				result[5]=act.getString("hehe");							//attivi		
			act.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("problemi durante la raccolta di statistiche");
		}finally{
			try{
				stmt.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("problemi durante la chiusura");
			}
		}
		result[6]=size(false);
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
			System.out.println("Impossibile continuare..");
			e.printStackTrace();
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
		//mdbm.addUser("fabrizio", "wewe");
		mdbm.login("fabrizio", "uprising");
		//Memo patrick=new Memo("St Patrick's Day a Dublino",2015,3,17,0,0);
		//mdbm.insertMemo(patrick);
		//System.out.println(x);
		//mdbm.insertMemo(m);
		//mdbm.insertMemo(m3);
		
		//mdbm.clearTable("memnew");
		//mdbm.deleteTable("memold");

		System.out.println("la size è "+mdbm.size(true));
		System.out.println(mdbm.toString(true));
		mdbm.logout();
		/*LinkedList<String> utenti=mdbm.userList();
		for(String s: utenti)
			System.out.println(s);
		*/
	}
}