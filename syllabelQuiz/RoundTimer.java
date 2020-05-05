import java.util.*;
import java.io.*;
import java.awt.Color;

public class RoundTimer extends Thread{
	public int roundTlimit;
	public int count = 0;
	private boolean running; // stop 플래그
	private String answer ="";
	MusicPlayer mp = new MusicPlayer();
	public RoundTimer(MultiClientThread mc, InGameUI gui, ObjectOutputStream oos, String prob, int roundTlimit){
		this.roundTlimit = roundTlimit;
		count =0;
		running = false;
		Timer m_timer = new Timer();
		gui.getStartbutton().setOpaque(false); //못누르게 투명처리
		gui.getStartbutton().setContentAreaFilled(false);
		gui.getStartbutton().setBorderPainted(false);
		gui.getStartbutton().setText("");
		gui.getTimerlabel().setForeground(Color.black);
		TimerTask m_task = new TimerTask(){
			public void run(){
				while(!running){
					if(count<roundTlimit){
						System.out.println(+ roundTlimit - count);
						gui.getTimerlabel().setText(Integer.toString(roundTlimit - count));
						gui.background.repaint();
						count++;
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//시간 경고
						if(roundTlimit - count ==4) {
							mp.musicRepeat("sounds/LimitTime.wav");
							gui.getTimerlabel().setForeground(Color.red);
						}
					}
					else{
						//타이머 끝
						//채점 하라고 알려줘야함
						try {
							oos.writeObject("checkanswer#"+prob);
							Thread.sleep(5000);
						} 
						catch (Exception e) {
						}
						
						if((Integer.parseInt(gui.getCroundlabel().getText())<Integer.parseInt(gui.getTroundlabel().getText())) && gui.getPlayerinfo().getMypos().equals("1")){
							try {
								oos.writeObject("thegamebegins");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//gui.getStartbutton().doClick();
						}
						if((Integer.parseInt(gui.getCroundlabel().getText()) == Integer.parseInt(gui.getTroundlabel().getText()))){
							try {
								Thread.sleep(7000);
								oos.writeObject("givemethetotalresult#");
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
						m_timer.cancel();
						setStop();
					} 
				}
				setStop();
			}
		};
		m_timer.schedule(m_task, 100, 1000);
	}
	public void setAnswer(String ans){
		this.answer = ans;
	}
	public void setStop(){
		this.running = true;
	}
	public void setRun(){
		this.running = false;
	}
}


class RoundAnswer extends Thread {
	private volatile String an;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	public RoundAnswer (){
		
	}
	public synchronized void run(){
		try {
			while(true){
				an = br.readLine();
			}
		} catch (IOException e) {
		}
	}
	public String getAnswer(){
		return an;
	}
}

class Timertest {
	public static int t = 0;
	public static void main(String[] args) throws IOException, InterruptedException {
		RoundAnswer ra = new RoundAnswer();
		ra.start();
		/*
		while(true){
			ProblemMap pm = new ProblemMap();
			String[] ans = pm.showProblem(0).split("\n");	
			System.out.println(ans[0]+"\n"+ans[1]);
			RoundTimer tt = new RoundTimer();
			tt.start();

			while(true){
				if(pm.checkAnswer(ans[0], ra.getAnswer(), 0)){
					System.out.println("정답!");
					tt.setStop();
					break;
				}
				else{
					//System.out.println("땡!");
				}
				if(t==1){
					tt.setStop();
					t =0;
					break;
				}
			}
		}
		*/
	}
}
