
public class ProblemInfo {
	
	private String answer; // 정답
	private String consonant; //자음
	
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
