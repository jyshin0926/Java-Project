import javax.swing.*;

public class PlayerInfo {
	private String id;
	private int score, tnum, cornum, chattype;
	private double acc;
	private boolean auth;
	private String character;
	private String mypos;

	public PlayerInfo(){
	}
	public PlayerInfo (String id, int score, int tnum, 
		int cornum, int chattype, double acc, boolean auth,
		String character, String mypos) {

		this.id = id;
		this.score = score;
		this.tnum = tnum;
		this.cornum = cornum;
		this.chattype = chattype;
		this.acc = acc;
		this.auth = auth;
		this.character = character;
		this.mypos = mypos;
	}

	public void setId (String id) {
		this.id = id;
	}
	public void setScore (int score) {
		this.score = score;
	}
	public void setTnum (int tnum) {
		this.tnum = tnum;
	}
	public void setCornum (int cornum) {
		this.cornum = cornum;
	}
	public void setChattype (int chattype) {
		this.chattype = chattype;
	}
	public void setAcc (double acc) {
		this.acc = acc;
	}
	public void setAuth (boolean auth) {
		this.auth = auth;
	}
	public void setCharacter (String character) {
		this.character = character;
	}
	public void setMypos(String mypos){
		this.mypos = mypos;
	}
	public String getMypos(){
		return this.mypos;
	}
	public String getId(){
		return this.id;
	}
	public int getScore(){
		return this.score;
	}
	public int getTnum(){
		return this.tnum;
	}
	public int getCornum(){
		return this.cornum;
	}
	public int getChattype(){
		return this.chattype;
	}
	public double getAcc(){
		return this.acc;
	}
	public boolean getAuth(){
		return this.auth;
	}
	public String getCharacter(){
		return this.character;
	}

}
