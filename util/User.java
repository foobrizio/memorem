package util;

import graphic.MemoremGUI.Lang;;
public class User {
	
	private String nickname;
	private String password;
	private String nome;
	private String cognome;
	private boolean isMaschio;
	private Lang language;
	
	public User(String nickname,String nome,String cognome, char genere, String language){
		this.nickname=nickname;
		this.nome=nome;
		this.cognome=cognome;
		if(language.equals("it"))
			this.language=Lang.IT;
		else if(language.equals("en"))
			this.language=Lang.EN;
		else if(language.equals("es"))
			this.language=Lang.ES;
		else if(language.equals("de"))
			this.language=Lang.DE;
		genere=Character.toLowerCase(genere);
		if(genere=='m')
			isMaschio=true;
		if(genere=='f')
			isMaschio=false;
	}
	public User(String nickname,String nome,String cognome){
		
		this.setNickname(nickname);
		this.setNome(nome);
		this.setCognome(cognome);
		isMaschio=true;
	}
	
	public User(String nickname){
		
		this.setNickname(nickname);
		this.nome="---";
		this.cognome="---";
		isMaschio=true;
	}

	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public Lang getLingua(){
		return language;
	}
	public void setLingua(String language){
		if(language.equals("it"))
			this.language=Lang.IT;
		else if(language.equals("en"))
			this.language=Lang.EN;
		else if(language.equals("es"))
			this.language=Lang.ES;
		else if(language.equals("de"))
			this.language=Lang.DE;
	}
	public void setLingua(Lang language){
		this.language=language;
	}
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPassword(){
		
		return password;
	}
	
	public void setPassword(String password){
		
		this.password=password;
	}
	/**
	 * Ritorna true se l'utente è di sesso maschile
	 * @return
	 */
	public boolean isMaschio(){
		
		return isMaschio;
	}
	
	/**
	 * Stabilisce che l'utente è maschio
	 */
	public void setMale(){
		
		isMaschio=true;
	}
	/**
	 * Stabilisce che l'utente è femmina
	 */
	public void setFemale(){
		
		isMaschio=false;
	}
	
	/**
	 * Ritorna true se l'utente è l'amministratore
	 * @return
	 */
	public boolean isAdmin(){
		
		return nickname.equals("admin");
	}
	
	/**
	 * Ritorna true se l'utente è un ospite
	 * @return
	 */
	public boolean isGuest(){
		
		return nickname.equals("guest");
	}
	
	public boolean equals(User s){
		
		if(this.nickname.equals(s.nickname))
					return true;
		return false;
	}
	
	public String toString(){
		
		if(this.nome.equals("---") && this.cognome.equals("---"))
				return this.nickname;
		if(this.nome.equals("---")){
			if(isMaschio)
				return "sig. "+this.cognome;
			else
				return "miss "+this.cognome;
		}
		else return this.nome;
	}
	

}
