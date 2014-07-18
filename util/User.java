package util;

public class User {
	
	private String nickname;
	private String nome;
	private String cognome;
	private boolean isMaschio;
	
	public User(String nickname,String nome,String cognome,char genere){
		this.nickname=nickname;
		this.nome=nome;
		this.cognome=cognome;
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

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	
	public boolean isMaschio(){
		
		return isMaschio;
	}
	
	public boolean equals(User s){
		
		if(this.nickname.equals(s.nickname))
			if(this.nome.equals(s.nome))
				if(this.cognome.equals(s.cognome))
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
