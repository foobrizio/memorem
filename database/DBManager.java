package database;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.JOptionPane;

import org.apache.commons.codec.digest.DigestUtils;	//classe DigestUtils per la decodifica SHA

import util.*;

import com.mysql.jdbc.exceptions.jdbc4.*; 			//eccezione MySQLIntegrityConstraintException

import main.Memo;

/*
 * Cosa manca ancora:
 * 1) Metodi di ricerca all'interno della tabella (ad esempio contains(Memo m);)
 * 2) Metodi di analisi dei record della tabella per fornire statistiche
 * 3) Supporto multi-utente (ogni utente ha la sua coppia di tabelle dentro il database)
 */
public class DBManager{

	private static String DB;		//nome database
	
	private static final String mo="memodataold";
	private static final String mn="memodatanew";
	
	//private String grant="grant usage on ";
	private static String drivers;

	private static String DB_URL;

	private static String DBuser;

	private static String DBpassword;
	private static Connection conn;
	private static Statement stmt;
	private static String logged; 			//è il nickname dell'utente attualmente connesso
	
	
	/**
	 * Esegue un update e ne ritorna il risultato, che può essere utilizzato in fase di debug
	 * @param sql
	 * @return
	 */
	private static int executeUpdate(String sql){
		
		int x=-1;
		//System.out.println(sql);
		try{
			conn=DriverManager.getConnection(DB_URL+DB, DBuser, DBpassword);
			stmt=conn.createStatement();
			x=stmt.executeUpdate(sql);
		}catch(SQLException e){
			if(e instanceof MySQLIntegrityConstraintViolationException)
				System.out.println("Eccezione MySQLIntegrityContraintViolationException");
			else if(e.getMessage().substring(0,22).equals("Access denied for user")){
				return -5;
			}
			else
				System.out.println("Errore in apertura per query '"+sql+"'");
			//e.printStackTrace();
			//System.out.println(e.getMessage().substring(0,22));
		}finally{
			//System.out.println(sql);
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
	private static ResultSet executeQuery(String sql){
		
		ResultSet rs=null;
		try{
			conn=DriverManager.getConnection(DB_URL+DB, DBuser, DBpassword);
			stmt=conn.createStatement();
			rs=stmt.executeQuery(sql);
		}catch(SQLException e){
			System.out.println("e");
			e.printStackTrace();
		}finally{
			System.out.println(sql);
		}
		return rs;
	}
	
	public static int prepareConnection(String dbName){
		
		try{
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("files//.database.properties");
			props.load(in);
			in.close();
			drivers = props.getProperty("jdbc.drivers");
			DB_URL = props.getProperty("jdbc.url");
			DBuser = props.getProperty("jdbc.username");
			DBpassword = props.getProperty("jdbc.password");

			System.setProperty("jdbc.drivers", drivers);
			System.setProperty("jdbc.username", DBuser);
			//conn = DriverManager.getConnection(DB_URL, DBuser,DBpassword);

			//stmt= conn.createStatement();
			//grant=grant+dbName+".* to "+DBuser+"@localhost";
			//Class.forName(drivers);
			//stmt.executeUpdate(createDB+" "+DB);
			int x=createDatabase(dbName);
			//stmt.executeUpdate(grant);
			//int x=executeUpdate(createDB+DB);
			if(x==-5)
				return x;
			createUsersTable();
			createTables();
			createHintsTable();
			String sql="SELECT COUNT(*) as hei FROM memousers WHERE nickname='admin'";
			ResultSet rs=executeQuery(sql);
			while(rs.next()){
				int esiste=rs.getInt("hei");
				if(esiste==0)
					return -4;
			}
			return 0;
		}catch (FileNotFoundException e){
			return -1;
		} catch (IOException e){
			System.out.println("Errore di input/output");
			e.printStackTrace();
			return -1;
		} catch (SQLException e) {
			System.out.println("Errore di sql");
			e.printStackTrace();
			return -2;
		}finally{
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
	
	private static int createDatabase(String name){
		
		DB=name;
		int x=-1;
		String sql="CREATE DATABASE IF NOT EXISTS "+name;
		//System.out.println(sql);
		try{
			conn=DriverManager.getConnection(DB_URL, DBuser, DBpassword);
			stmt=conn.createStatement();
			x=stmt.executeUpdate(sql);
		}catch(SQLException e){
			if(e instanceof MySQLIntegrityConstraintViolationException)
				System.out.println("Impossibile continuare..");
			else if(e.getMessage().substring(0,22).equals("Access denied for user")){
				return -5;
			}
			else
				System.out.println("Errore in apertura per query '"+sql+"'");
			e.printStackTrace();
			System.out.println(e.getMessage().substring(0,22));
		}finally{
			//System.out.println(sql);
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
	 * Crea la tabella che conterrà i suggerimenti e i consigli per conoscere le potenzialità del
	 * programma
	 */
	private static void createHintsTable(){
		
		String sql="CREATE TABLE IF NOT EXISTS hints"+
		"("+
		"hint VARCHAR(256) NOT NULL,"+
		"hash CHAR(40) PRIMARY KEY,"+
		"language CHAR(2) DEFAULT 'en')";
		executeUpdate(sql);
		String sql2="SELECT COUNT(*) AS we FROM hints";
		ResultSet rs=executeQuery(sql2);
		int size=0;
		try{
			if(rs.next()){
				size=rs.getInt("we");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		if(size==0)
			populateHints();
	}

	/**
	 * Crea la tabella "table" all'interno del database
	 * @param table
	 */
	private static void createTable(String table){		//funziona
		
		String tbl="CREATE TABLE IF NOT EXISTS "+table+
		"("+
		"descrizione VARCHAR(255),"+
		"prior TINYINT(3) DEFAULT 1,"+
		"end DATETIME NOT NULL, ";
		if(table.equals(mo))
			tbl=tbl+"completed BIT DEFAULT 0, ";
		tbl=tbl+"user VARCHAR(20) NOT NULL,"+
		"icon VARCHAR(30) DEFAULT 'note.png',"+
		"id CHAR(40) NOT NULL,"+
		"FOREIGN KEY(user) REFERENCES memousers(nickname),"+
		"PRIMARY KEY(descrizione,end,user))";
		int res=executeUpdate(tbl);
		//System.out.println("RES= "+res);
		if(res!=0)
			System.out.println("tabella "+table+" creata");
		//eliminaDuplicati(table);	
	}

	private static void createTables(){
		
		createTable(mn);
		createTable(mo);
	}

	/**
	 * Crea la tabella che gestisce gli utenti che possono accedere al database
	 */
	private static void createUsersTable(){
		
		String sql="CREATE TABLE IF NOT EXISTS memousers"+
		"("+
		"nickname VARCHAR(16) PRIMARY KEY,"+
		"password CHAR(40) NOT NULL,"+
		"nome VARCHAR(16) DEFAULT '---',"+
		"cognome VARCHAR(16) DEFAULT '---',"+
		"genere BIT DEFAULT 1,"+
		"lingua CHAR(2) DEFAULT 'en'"+
		")";
		int res=executeUpdate(sql);
		if(res!=0){
			System.out.println("Tabella utenti creata");
			createHintsTable();
		}
	}
	
	private static void clearFromTable(User user,String table){	//funziona
		
		String sql="DELETE FROM "+table+" WHERE user='"+user.getNickname()+"'";	//questo metodo è più veloce di DELETE FROM perchè cancella l'intera tabella e ne ricrea una nuova
		executeUpdate(sql);
	}
	
	private static boolean contains(User user, String id,String table){
		
		String sql="SELECT * FROM "+table+" WHERE user='"+user.getNickname()+"' AND id='"+id+"'";
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
	
	private static boolean insertMemo(User user, Memo m,String table){
		
		String sql="";
		if(table.equals(mn))
			sql="INSERT INTO "+mn+" (id,descrizione,prior,end,user,icon)";
		else sql="INSERT INTO "+mo+" (id,descrizione,prior,end,completed,user,icon)";
		//valori primitivi
		String desc=m.description();
		String id=m.getId();
		int result=-1;
		int prior=m.priority();
		boolean completed=m.isCompleted();
		//valori che vanno convertiti
		String end=Data.convertDateToString(m.getEnd());
		String icon=m.getIcon();
		sql+=" VALUES('"+id+"','"+verificaStringa(desc)+"',"+prior+",'"+end+"'";
		if(table.equals(mn))
			sql+=",'"+verificaStringa(user.getNickname())+"','"+verificaStringa(icon)+"'";
		else
			sql+=",b'"+(completed?"1":"0")+"','"+verificaStringa(user.getNickname())+"','"+verificaStringa(icon)+"'";
		sql+=")";
		result=executeUpdate(sql);
		return result==1;
	}

	private static void populateHints(){
		
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
			String sql="INSERT INTO hints(hint,hash,language) VALUES(\""+s+"\",SHA(\""+s+"\"),'it')";
			int result=executeUpdate(sql);
			if(result!=0)
				System.out.println("aiuto inserito");
		}//italiano
		aiuti.clear();
		aiuti.add("You can complete a memo before its expiration by clicking on 'complete' button into 'Options' menu");
		aiuti.add("The blue bar below shows the percentage between all notified memos and the ones you completed");
		aiuti.add("You can notify a memo when it expires, suggesting if you reached to complete it or not (in this case you'll have to click on 'store' button into 'Options' menu)");
		aiuti.add("You can show at will the history of your memos by using filters onto the inner toolbar");
		aiuti.add("If you inserted a memo using wrong data, you can modify it through 'modify' button in 'Options' menu. If you instead wish to delete it, click on 'delete' button");
		aiuti.add("Memos are colored according to their priority. A red memo is an urgent one, a yellow one indicates average priority and a blue one has low priority");
		aiuti.add("You can show the countdown to the expiration of a memo simply by passing the cursor upon its expiry date. You can even permanently show it by clicking 'change view' in 'Options' menu");
		aiuti.add("You can activate and deactivate hints by clicking 'hints' button in 'View' menu");
		aiuti.add("When you use calendar interface you can add a memo just by double-clicking on the date in which you want to add your new memo");
		aiuti.add("Memos in calendar interface are showed through different colors. Click with right button of your mouse to access the functions");
		aiuti.add("Do you wish to know how many memos did you created so far and their features? Click on 'Stats' button in 'User' menu");
		aiuti.add("You can add all memos you want through 'Add' button in 'Tools' menu");
		aiuti.add("You can delete your memos at any time clicking 'Delete' button in 'Tools' menu. Pay attention, this operation is not reversible");
		aiuti.add("From 'View' menu you can activate or deactivate calendar interface");
		for(String s: aiuti){
			String sql="INSERT INTO hints(hint,hash,language) VALUES(\""+s+"\",SHA(\""+s+"\"),'en')";
			int result=executeUpdate(sql);
			if(result!=0)
				System.out.println("aiuto inserito");
		}//english
		aiuti.clear();
		aiuti.add("Puedes completar un memo antes de su caducidad clicando en el botón 'completa' del menú 'opciones'.");
		aiuti.add("La barra azul inferior indica la relación en porcentaje entre todos los memo notificados y aquellos que has completado.");
		aiuti.add("Cuando un memo expira puedes notificarlo a la base de datos, indicando si has llegado a completarlo o si no lo has hecho a tiempo ( en este caso clicarás el botón 'archiva' en el menú 'opciones”').");
		aiuti.add("Puedes visualizar cuando quieras el historial de tus memo utilizando los filtros presentes en la barra de instrumentos interna.");
		aiuti.add("Si has inscrito un memo utilizando datos erróneos puedes modificarlos: utiliza el botón 'modifica' en el menú 'opciones'. Si por el contrario quieres eliminarlo bastará con apretar el botón 'elimina'.");
		aiuti.add("Los memos están coloreados en base a su prioridad. Un memo rojo indicará que tiene prioridad alta, uno amarillo tiene prioridad normal y uno azul tiene prioridad baja.");
		aiuti.add("Puedes ver la cuenta atrás de la caducidad de un memo simplemente pasando el ratón sobre su fecha de caducidad. Puedes ver la cuenta atrás de forma permanente apretando el botón 'cambia visualización' en el menú 'opciones'.");
		aiuti.add("Puedes activar o desactivar los consejos apretando el botón 'consejos' en el menú 'visualización'.");
		aiuti.add("Desde el menú 'visualización' puedes activar o desactivar la interfaz de calendario.");
		aiuti.add("Cuando utilizas la interfaz de calendario puedes adjuntar un memo simplemente haciendo doble clic sobre la fecha en la que quieres adjuntarlo.");
		aiuti.add("Los memo en la interfaz de calendario se muestran utilizando colores en la fecha. Clica con el botón derecho para acceder a las funciones.");
		aiuti.add("¿Quieres ver cuantos memo has creado hasta el momento y sus características? Clica el botón 'Estadística' en el menú 'usuario'.");
		aiuti.add("Puedes adjuntar todos los memo que quieras desde el botón 'Adjuntar' en el menú 'Instrumentos'.");
		aiuti.add("Puedes borrar todos tus memo en cualquier momento apretando el botón 'Borra' en el menú 'Instrumentos'. Cuidado, esta operación en irreversible.");
		for(String s: aiuti){
			String sql="INSERT INTO hints(hint,hash,language) VALUES(\""+s+"\",SHA(\""+s+"\"),'es')";
			int result=executeUpdate(sql);
			if(result!=0)
				System.out.println("aiuto inserito");
		}//español
		aiuti.clear();
		aiuti.add("Sie können ein Memo vor Ablauf vervollständigen, indem Sie auf die Schaltfläche 'Complete' im Menü 'Optionen' anklicken");
		aiuti.add("Der blaue Balken am unteren Rand zeigt die prozentuale Verhältnis zwischen allen Notizen und benachrichtigt  die, die Sie abgeschlossen haben.");
		aiuti.add("Wenn ein Memo abläuft, können Sie es auf die Datenbank melden, der angibt, ob Sie es rechtzeitig abgeschlossen haben. (in diesem Fall gehen Sie im Menu Optionen um es archivieren.)");
		aiuti.add("Sie können jederzeit Ihr Werdegang im Memo mit Hilfe von Filtern in der Werkzeugleiste kontrollieren.");
		aiuti.add("Wenn Sie ein Memo mit falschen Daten eingegeben haben, können Sie es bearbeiten: Mit dem Button 'Bearbeiten' im Menü 'Optionen'. Wenn Sie nur Treffer löschen wollen dann mit der Taste 'Löschen'. ");
		aiuti.add("Das Memo wird nach ihrer Priorität eingefärbt. Ein Memo zeigt rot dass eine hohe Priorität hat, hat mit normaler Priorität eine gelbe und eine blaue Hintergrund hat eine niedrige Priorität.");
		aiuti.add("Sie können den Countdown nach der abgelaufene Zeit  kontrollieren in dem Sie mit der Maus über das Ablaufdatum fahren. Sie können den Countdown zu sehen,  durch Drücken der 'View Veränderungen' im Menü  'Optionen'");
		aiuti.add("Sie können die Mithilfe aktivieren und deaktivieren in dem Sie im Menu auf Hinweis Drücken.");
		aiuti.add("Im Menu 'Ansicht' können Sie mit aktivieren oder deaktivieren den Ablauf vom Kalender besichtigen. ");
		aiuti.add("Wenn Sie die Schnittstelle in den Kalender verwenden, können Sie ein Memo hinzufügen einfach durch Doppelklick auf das Datum, an dem Sie es hinzufügen möchten. ");
		aiuti.add("Die Termine für das Memo im Kalender sind durch Farben gekennzeichnet. Um die Funktionen zugreifen können benützen Sie die rechte Maustaste. ");
		aiuti.add("Möchten Sie überprüfen, wie viele Memos Sie bisher und ihre Eigenschaften erstellt haben? Klicken Sie auf die 'Statistik' im Menü in der 'User' ");
		aiuti.add("Sie können alle Notizen (Memo) 'Hinzufügen' in dem Sie im Menü die Werkzeugliste gebrauchen.");
		aiuti.add("Sie können Ihre Memo jederzeit löschen, indem Sie die Taste 'Löschen' in der Werkzeugliste benützen. Achtung dieser Vorgang ist nicht Rückgängig!!!" );
		for(String s: aiuti){
			String sql="INSERT INTO hints(hint,hash,language) VALUES(\""+s+"\",SHA(\""+s+"\"),'de')";
			int result=executeUpdate(sql);
			if(result!=0)
				System.out.println("aiuto inserito");
		}//tedesco
	}

	private static boolean removeMemo(User user, Memo m, String table){
		if(user.equals("none"))			return false;	//nessun utente connesso
		String sql="DELETE FROM "+table+" WHERE id='"+m.getId()+"'";
		return executeUpdate(sql)==1;
	}

	private static int size(User user,String table){		//funziona
		
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
	
	private static String toString(String table){	//funziona
		
		if(logged.equals("none"))			return null;	//nessun utente connesso
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
	 * Verifica la presenza dell'apostrofo nella stringa e ritorna una stringa compatibile con il 
	 * database
	 * @param desc
	 * @return
	 */
	private static String verificaStringa(String desc){
		
		StringBuilder sb=new StringBuilder(255);
		StringTokenizer st=new StringTokenizer(" "+desc+" ","'",false);
		while(st.hasMoreTokens()){
			String token=st.nextToken();
			//System.out.println(token);
			sb.append(token);
			if(st.hasMoreTokens())
				sb.append("\\'");
		}
		//System.out.println(sb.toString());
		return sb.toString().trim();
	}

	/**
	 * Aggiunge un utente al database
	 * @param user, il nickname dell'utente, che può essere massimo di 16 caratteri
	 * @param password, la password dell'utente, che può essere massimo di 16 caratteri
	 * @return 0 se l'utente è stato aggiunto, 1 se esiste già un utente con lo stesso nickname,
	 * 			2 per altri errori
	 */
	public static int addUser(User user){
		
		//if(logged!=null && logged.equals("admin"));
		String nome=user.getNome();
		String cognome=user.getCognome();
		String lingua;
		switch(user.getLingua()){
		case IT: lingua="it";break;
		case DE: lingua="de";break;
		case ES: lingua="es";break;
		default: lingua="en";
		}
		if(nome==null)
			user.setNome("---");
		if(cognome==null)
			user.setCognome("---");
		boolean gen=user.isMaschio();
		String sqlCheck="SELECT nickname FROM memousers WHERE nickname='"+user.getNickname()+"'";
		ResultSet rs=executeQuery(sqlCheck);
		boolean alreadyExist=false;
		try{
			while(rs.next())
				if(rs.getString("nickname").equals(user.getNickname()))
					alreadyExist=true;
			rs.close();
		}catch(SQLException sqle){
			System.out.println("SQL exception in addUser");
			sqle.printStackTrace();
		}
		if(alreadyExist)
			return 1;
		String sql="INSERT INTO memousers(nickname,password,nome,cognome,genere,lingua) VALUES('"+user.getNickname()+"',SHA('"+user.getPassword()+"'),'"+verificaStringa(nome)+"','"+verificaStringa(cognome)+"',b'"+(gen?"1":"0")+"','"+lingua+"')";
		int result=executeUpdate(sql);
		if(result==1){
			if(login(user.getNickname(),user.getPassword())!=null) //il login va a buon fine
				return 0;
			else return 2;				//il login non va a buon fine
		}
		else{
			return 2;					//l'utente non è stato creato
		}
	}

	/**
	 * Inserisce un memo non completato nello storico
	 */
	public static void archivia(User user, Memo m){
		
		if(!user.getNickname().equals(logged))
			return;
		insertMemo(user, m,mo);
	}

	public static void cancellaStorico(User user){
		
		if(!user.getNickname().equals(logged))				return;
		clearFromTable(user,mo);
	}

	/**
	 * Cancella tutti i record di un utente nella tabella
	 */
	public static void clear(User user){
		
		if(!user.getNickname().equals(logged))				return;		//nessun utente connesso
		clearFromTable(user,mn);
		clearFromTable(user,mo);
	}

	/**
	 * Inserisce un memo completato nello storico
	 */
	public static void completa(User user,Memo m){
		
		if(!user.getNickname().equals(logged))
			return;
		insertMemo(user, m,mo);
	}

	/**
	 * Ritorna true se il memo m è contenuto nella tabella table, false altrimenti
	 */
	public static boolean contains(User user, Memo m){
		
		if(!user.getNickname().equals(logged)){
			return false;	//nessun utente connesso
		}
		return contains(user,m.getId(),mn);
	}

	public static boolean eliminaImpegni(User user){
		
		if(!user.getNickname().equals(logged))
			return false;
		String query="DELETE FROM "+mn+" WHERE user='"+user+"' AND end>=curdate()";
		int x=executeUpdate(query);
		System.out.println(x);
		return x==1;
	}

	/**
	 * Ritorn il memo che nel database dei memo attivi corrisponde alla descrizione "desc" e alla data "end" 
	 * @param desc
	 * @param end
	 * @return
	 */
	public static Memo get( User user,String desc, Data end){
		
		if(!user.getNickname().equals(logged))					return null;	//nessun utente connesso
		String sql="SELECT * FROM ";
			sql+=mn+" ";
		sql+="WHERE user='"+user.getNickname()+"' AND descrizione='"+verificaStringa(desc)+"' AND end='"+Data.convertDateToString(end)+"'";
		ResultSet rs=executeQuery(sql);
		Memo m=null;
		try{
			if(rs.next()){
				String rizione=rs.getString("descrizione");
				int prior=rs.getInt("prior");
				String data=rs.getString("end");
				String icon=rs.getString("icon");
				String id=rs.getString("id");
				m=new Memo(rizione,prior,Data.convertStringToData(data),icon,id);
			}
			rs.close();
		}catch(SQLException e){
			System.out.println("mmm problemi nel get");
			e.printStackTrace();
		}
		return m;
	}

	/**
	 * Ritorna un array di String contenente i suggerimenti da visualizzare nel frame
	 * @return
	 */
	public static String[] getHelps(String language){
		
		String[] helps=null;
		String sql="SELECT COUNT(*) AS ehi FROM hints where language='"+language+"'";
		String sql2="SELECT * FROM hints where language='"+language+"'";
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
				helps[cont]="   	    "+rs.getString("hint")+"    ";
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
	 * Ritorna i memo da visualizzare appena si avvia il programma, ovvero quelli con scadenza 
	 * settimanale e quelli già scaduti in attesa di essere gestiti
	 * @return
	 */
	public static MemoList getStandardMemos(User user){
		
		if(!user.getNickname().equals(logged))
			return null;
		String sql="SELECT * FROM memodatanew WHERE user='"+verificaStringa(user.getNickname())+"' and date_add(curdate(), interval 7 day)>=end";
		ResultSet rs=executeQuery(sql);
		MemoList ml=new MemoList();
		try{
			while(rs.next()){
				String descrizione=rs.getString("descrizione");
				int prior=rs.getInt("prior");
				String end=rs.getString("end");
				String icon=rs.getString("icon");
				String id=rs.getString("id");
				ml.add(new Memo(descrizione,prior,Data.convertStringToData(end),icon,id),true);
			}
		}catch(SQLException e){
			return null;
		}
		return ml;
	}
	
	public static String getUser(){
		
		return logged;
	}

	/**
	 * Inserisce un nuovo memo nella tabella del database
	 * @param m
	 */
	public static boolean insertMemo(User user, Memo m){		//funziona
		
		if(!user.getNickname().equals(logged)) return false;
		return insertMemo(user, m ,mn);
	}

	/**
	 * Ritorna i memo contenuti nel database sottoforma di lista
	 * @param soloAttivi
	 * @return
	 */
	public static MemoList list(User user,boolean soloAttivi){
		
		if(!user.getNickname().equals(logged)){
			System.out.println("user:"+user);
			System.out.println("logged:"+logged);
			return null;	//nessun utente connesso
		}
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
				String id=rs.getString("id");
				Data fine=Data.convertStringToData(end);
				m=new Memo(desc,prior,fine,icon,id);
				ml.add(m,true);
			}
			if(!soloAttivi){
				while(rs2.next()){
					m=null;
					String desc=rs2.getString("descrizione");
					int prior=rs2.getInt("prior");
					String end=rs2.getString("end");
					boolean completed=rs2.getBoolean("completed");
					String icon=rs2.getString("icon");
					String id=rs2.getString("icon");
					Data fine=Data.convertStringToData(end);
					m=new Memo(desc,prior,fine,icon,id);
					if(completed)
						m.spunta();
					ml.add(m,true);
					
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

	public static User login(String user,String password){
		
		String sql="SELECT * FROM memousers WHERE nickname='"+user+"'";
		ResultSet rs=executeQuery(sql);
		String pass=null,nome=null,cognome=null,lingua=null;
		boolean isM;
		char genere='n';
		try {
			if(rs.next()){
				nome=rs.getString("nome");
				pass=rs.getString("password");
				cognome=rs.getString("cognome");
				isM=rs.getBoolean("genere");
				lingua=rs.getString("lingua");
				genere= isM? 'm' : 'f';
			}
		}catch(SQLException e){ 
			e.printStackTrace();
		}
		if(pass==null){				//l'utente non esiste
			JOptionPane.showMessageDialog(null, "L'utente non esiste");
			return null;
		}	
		else{
			String pass2=DigestUtils.shaHex(password);
			if(pass.equals(pass2)){		//password corretta
				logged=user;
				return new User(user,nome,cognome,genere,lingua);
			}
			else{
				JOptionPane.showMessageDialog(null , "La password è sbagliata");	//password sbagliata
			}
		}
		return null;
	}
	
	public static boolean logout(User user){
		
		if(user.getNickname().equals(logged)){
			logged=new String("none");
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param passV
	 * @param passN
	 * @return 	3 se la password nuova è identica a quella vecchia,
	 * 		 	2 se la vecchia password è sbagliata,
	 * 			1 in caso di altri eventuali errori
	 * 			0 se la modifica va a buon fine
	 */
	public static int modificaPassword(User user,String passN){
		
		if(!logged.equals(user.getNickname())){
			return -1;
		}
			
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
		if(!pass.equals(DigestUtils.shaHex(passN)))	//le due password non combaciano
			return 2;
		sql="UPDATE memousers SET password=SHA('"+passN+"') WHERE nickname='"+verificaStringa(user.getNickname())+"'";
		int res=executeUpdate(sql);
		if(res!=0)
			return 0;
		else return 1;
	}

	public static boolean modificaUtente(User vecchio, User aggiornato){
		
		if(logged.equals(vecchio.getNickname())){
			logged=aggiornato.getNickname();
			String sql="UPDATE memousers SET nome='"+aggiornato.getNome()+"', cognome='"+aggiornato.getCognome()+"', ";
			sql+="genere=b'"+(aggiornato.isMaschio()?"1":"0")+"', lingua='";
			switch(aggiornato.getLingua()){
			case IT: sql+="it' "; break;
			case DE: sql+="de' "; break;
			case ES: sql+="es' "; break;
			default: sql+="en' ";
			}
			sql+="WHERE nickname='"+vecchio.getNickname()+"'";
			int x=executeUpdate(sql);
			return x==1;
		}
		return false;
	}
	/**
	 * Modifica un memo esistente sulla tabella, aggiornandone i valori
	 * @param nuovo, il memo con le modifiche che verranno passate alla query
	 * @param vecchio, il memo dentro la tabella che verrà modificato
	 * @return
	 */
	public static boolean modifyMemo(User user, String id, Memo nuovo){
		
		if(!user.getNickname().equals(logged))			return false;	//nessun utente connesso
		if(!contains(user, id,mn))
			return false;
		String sql="UPDATE "+mn+" SET";
		sql+=" descrizione='"+verificaStringa(nuovo.description())+"'";
		sql+=", prior='"+nuovo.priority()+"'";
		sql+=", end='"+Data.convertDateToString(nuovo.getEnd())+"'";
		sql+=", icon='"+verificaStringa(nuovo.getIcon())+"'";
		sql+=" WHERE id='"+id+"'";
		//System.out.println(sql);
		return executeUpdate(sql)!=0;
	}

	/**
	 * Muove un memo registrato in memnew dentro memold (evidentemente è stato completato)
	 * @param m
	 */
	public static void move(User user, Memo m,boolean completato){
		
		if(!user.getNickname().equals(logged))				return;		//nessun utente connesso								//memo trovato
			if(removeMemo(user, m,mn))						//memo rimosso da mn
				if(completato)							//memo eventualmente spuntato
					m.spunta();
			insertMemo(user, m ,mo);							//memo inserito in mo
	}
	
	public static MemoList processQuery(User user,String query,boolean scaduti){
		
		if(!user.getNickname().equals(logged)) return null;
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
				String id=rs.getString("id");
				Memo m=new Memo(d,pr,Data.convertStringToData(e),icon,id);
				if(c)
					m.spunta();
				sancarlo.add(m,true);
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Errori durante la creazione della MemoList");
		}
		return sancarlo;
	}

	/**
	 * Ritorna il rapporto tra memo completati e memo totali scaduti, utile per settare la progressBar
	 * @return
	 */
	public static int[] rapportoCompletati(User user){
		
		if(!user.getNickname().equals(logged)) return null;
		int[] risultato=new int[2];	//la prima posizione indica il numero di memo completati, la seconda indica il numero di memo totali scaduti;
		int comp=0,scad;
		scad=size(user,mo);
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
	 * Rimuove un memo dalla tabella(eliminandolo quindi per sempre)
	 * @param m
	 */
	public static boolean removeMemo(User user, Memo m){	//funziona
		
		if(!user.getNickname().equals(logged)) 		return false;	//nessun utente connesso
		if(removeMemo(user, m,mn))			return true;	//rimozione di un memo nuovo
		else if(removeMemo(user, m,mo))		return true;	//rimozione di un memo vecchio
		else return false;
	}

	/**
	 * Rimuove un utente dal database
	 * @param user
	 * @return
	 */
	public static boolean removeUser(String utont){
		
		String sqlnew="DELETE FROM "+mn+" WHERE user='"+utont+"'";
		String sqlold="DELETE FROM "+mo+" WHERE user='"+utont+"'";
		String sqluser="DELETE FROM memousers WHERE nickname='"+utont+"'";
		int result=executeUpdate(sqlnew);
		int result2=executeUpdate(sqlold);
		executeUpdate(sqluser);
		if(result==0 && result2==0)	//l'utente è stato rimosso
			return true;
		else return false;				//l'utente non è stato rimosso
	}

	/**
	 * Resetta tutto il database. Solo l'amministratore può usare questo metodo
	 */
	public static void reset(){
		
		if(logged.equals("admin")){
			System.out.println("Non hai i privilegi di amministratore");
			return;
		}
		String sql1="TRUNCATE TABLE "+mn;
		String sql2="TRUNCATE TABLE "+mo;
		executeUpdate(sql1);
		executeUpdate(sql2);
	}

	/**
	 * Ritorna il numero di righe della tabella
	 * @param soloAttivi, se messo a true conta anche il numero di righe della tabella mo
	 * @return
	 */
	public static int size(User user,boolean soloAttivi){
		
		if(!user.getNickname().equals(logged))					return 0;		//nessun utente connesso
		int x=size(user,mn);
		if(!soloAttivi)
			x+=size(user,mo);
		return x;
	}
	
	public static int sizeVecchi(User user){
		
		if(!user.getNickname().equals(logged))					return 0;
		int x=size(user,mo);
		return x;
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
	public static Object[] statistiche(User user){
		
		/*
		 * result[0]= (Integer)priorità alta
		 * result[1]= (Integer)priorità media
		 * result[2]= (Integer)priorità bassa
		 * result[3]= (Integer)memo in attesa
		 * result[4]= (Integer)memo completati
		 * result[5]= (Integer)memo archiviati
		 * result[6]= (Integer)memo attivi
		 * result[7]= (Integer)memo totali
		 */
		Object[] result=new Object[8];
		String pending="SELECT COUNT(*) AS wei FROM memodatanew WHERE end<curDate()";
		String prior1="SELECT COUNT(*) AS ehi FROM ";
		String prior2=" WHERE user='"+verificaStringa(user.getNickname())+"' AND prior='";
		String comp1="SELECT COUNT(*) AS wei FROM ";
		String comp2=" WHERE user='"+verificaStringa(user.getNickname())+"' AND completed='";
		try{
			conn=DriverManager.getConnection(DB_URL+DB, "root", DBpassword);
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
			if(!admin)
				pending=pending+" AND user='"+user+"'";
			ResultSet pend=stmt.executeQuery(pending);
			if(pend.next())
				result[3]=pend.getString("wei");
			if(admin)
				str=comp1+mo+" WHERE completed='1'";
			else str=comp1+mo+comp2+"1'";
			ResultSet comp=stmt.executeQuery(str);
			if(comp.next())
				result[4]=comp.getString("wei");						//completati
			comp.close();
			if(admin)
				str=comp1+mo+" WHERE completed='0'";
			else str=comp1+mo+comp2+"0'";
			ResultSet arch=stmt.executeQuery(str);
			if(arch.next())
				result[5]=arch.getString("wei");						//archiviati
			arch.close();
			str="SELECT COUNT(*) as hehe FROM "+mn;
			if(!admin)
				str=str+" WHERE user='"+verificaStringa(user.getNickname())+"'";
			ResultSet act=stmt.executeQuery(str);
			if(act.next())
				result[6]=act.getString("hehe");							//attivi		
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
		result[7]=size(user,false);
		return result;
	}
	
	/**
	 * Ritorna la tabella 'table' sottoforma di stringa
	 */
	public static String toString(boolean soloAttivi){
		
		if(logged.equals("none"))			return null;	//nessun utente connesso
		StringBuilder sb=new StringBuilder(1000);
		sb.append(toString(mn));
		if(!soloAttivi){
			System.out.println("anche quelli scaduti");
			sb.append("\n\tSCADUTI\n");
			sb.append(toString(mo));
		}
		return sb.toString();
	}

	public static HashSet<User> userList(){
		
		HashSet<User> utenti=new HashSet<User>();
		String sql="SELECT * FROM memousers";
		ResultSet rs=executeQuery(sql);
		try{
			while(rs.next()){
				String nick=rs.getString("nickname");
				String password=rs.getString("password");
				User nicko=new User(nick);
				nicko.setPassword(password);
				utenti.add(nicko);
			}
		}catch(SQLException sqle){
			sqle.printStackTrace();
			System.out.println("Eccezione durante la userList");
		}
		return utenti;
	}
	
	public static void main(String[] args){
		
		DBManager.prepareConnection("memodatabase");
		/*Memo m=new Memo("compleanno di Judy","normal",2014,2,28,22,00);
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
		User user=new User("fabrizio","Fabrizio","Gabriele",'m',"it");
		user.setPassword(DigestUtils.shaHex("uprising"));
		//System.out.println(user.getPassword());
		login(user.getNickname(),"uprising");
		//Memo patrick=new Memo("St Patrick's Day a Dublino",2015,3,17,0,0);
		//mdbm.insertMemo(patrick);
		//System.out.println(x)
		
		//mdbm.clearTable("memnew");
		//mdbm.deleteTable("memold");

		System.out.println("la size è "+DBManager.size(user,true));
		System.out.println(DBManager.toString(true));
		DBManager.logout(user);
		/*LinkedList<String> utenti=mdbm.userList();
		for(String s: utenti)
			System.out.println(s);
		*/
	}
}