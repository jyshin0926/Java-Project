
public class ProblemInfo {
	
	private String answer; // ����
	private String consonant; //����
	
	public ProblemInfo(String consonant, String answer){
		this.answer = answer;
		this.consonant = consonant;
	}

	public String getAnswer(){
		return answer;
	}
	public String getConsonant(){
		return consonant;
	}
	public void setAnswer(String answer){
		this.answer = answer;
	}
	public void setConsonant(String consonant){
		this.consonant = consonant;
	}
}
