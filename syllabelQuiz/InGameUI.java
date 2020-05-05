import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import java.net.Socket;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;


class BgmThread extends Thread {
	boolean isStop = false;
	String file;
	MusicPlayer mp;
	public BgmThread( String file, MusicPlayer mp) {
		this.file =file;
		this.mp = mp;
	}
	public void run() {
		while(!isStop) {
			mp.musicRepeat(file);
			try {
				Thread.sleep(90000); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	public void stopMusic() {
		isStop = true;
	}
}

class LoseEffectThread extends Thread {
	JLabel l;
	public LoseEffectThread(JLabel l){
		this.l = l;
	}
	public void run(){	
		l.setVisible(true);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
		}

		l.setVisible(false);
	}
}	

class WinEffectThread extends Thread {
	JLabel l;
	public WinEffectThread(JLabel l){
		this.l = l;
	}
	public void run(){	
		l.setVisible(true);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
		}

		l.setVisible(false);
	}
}	

class InGameUI extends JFrame implements ActionListener{
	JFrame frame = new JFrame();
	JPanel jpProb = new JPanel();
	JPanel jpChat = new JPanel();
	JPanel jpTimer = new JPanel();
	JPanel jpCount = new JPanel();	
	JPanel jpAnswer = new JPanel();
	JPanel jpTab = new JPanel();
	JPanel jpBox = new JPanel();
	JPanel panel = new JPanel();
	JPanel jpAns1 = new JPanel();
	JPanel jpAns2 = new JPanel();
	JPanel jpAns3 = new JPanel();
	JPanel jpAns4 = new JPanel();
	JPanel box1 = new JPanel();
	JPanel box2 = new JPanel();
	JPanel box3 = new JPanel();
	JPanel box4 = new JPanel();
	JPanel speechBubble = new JPanel();
	JPanel jpStart = new JPanel();
	JButton startb;
	private ObjectInputStream ois;
    private ObjectOutputStream oos;
	PlayerInfo pi;
	RoundTimer rt;
	JTable tableAns = new JTable();
	DefaultTableModel modelAns;
	private String myAnswer =""; //내가 제출한 정답 기억
	private String myOrder;  //정답 몇번재로 제출했는지 기억

	

	Object coName[]={"answer","ID"};
	Object data[][] = {{"제출한 답","아이디"}};

	JTextField idBox,chatBox,scoreBox,timerBox,ansBox,tf1,tf2,tf3,tf4,tf5,tf6,tf7,tf8;
	JTextArea probTxt, jlbProb;
	JLabel jlbChat,jlbCount,jlbBlankC,jlbTotal,jlbBlankT,jlbSlash1,jlbSlash2,jlbTimer,jlbAnswer,jlbTab,
		jlbBox1,jlbBox2,jlbBox3,jlbBox4,jlbAns1,jlbAns2,jlbAns3,jlbAns4,jlbspeechBubble,jlbspeechTab1,
		jlbspeechTab2,jlbspeechTab3, et1, et2, et3, et4, background, win1, win2, win3, win4, lose1, lose2, lose3, lose4;
	Font f = new Font ( Font.SANS_SERIF, Font.BOLD, 35 );
	Font f2 = new Font ( Font.SANS_SERIF, Font.ITALIC, 25 );
	Font f3 = new Font ( Font.SANS_SERIF, Font.BOLD, 25 );
	MusicPlayer mp = new MusicPlayer();
	Clip bgm, startbtn;
	BgmThread bt;
	private boolean bgmisOff= false;
	WinEffect we;
	public InGameUI(PlayerInfo pi, ObjectInputStream ois, ObjectOutputStream oos, GameChatClient gcc){
		
		super("HangOut");
		this.pi = pi;
		this.ois = ois;
		this.oos = oos;
		try {
			bgm = AudioSystem.getClip();
			startbtn = AudioSystem.getClip();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
		panel.setLayout(null);

		//문제 패널
		jlbProb = new JTextArea("문제",200,50);
		jlbProb.setFont(f3);
		jlbProb.setBorder(new EmptyBorder(0,0,0,0 ));
		jpProb = new JPanel();
		jpProb.setBorder(new EmptyBorder(0,0,0,0 ));	///테두리 수정
		jpProb.setBackground(Color.WHITE);
		jpProb.add(jlbProb);
		add(jpProb);
		jpProb.setBounds(200,20,1200,200);
		jlbProb.setLineWrap(true);
        jlbProb.setWrapStyleWord(true);
        jlbProb.setOpaque(false);
		jlbProb.setEditable(false);

		
		//timer 패널
		jpTimer.setBounds(30,50,150,160);	
		jpTimer.setBorder(new TitledBorder(new LineBorder(Color.gray,5),""));   ///테두리 수정
		ImageIcon timer = getScaledImage(new ImageIcon("images/timer_tp.png"), 150,120);
		jlbTimer = new JLabel(timer,JLabel.CENTER);
		jlbTimer.setOpaque(false);
		jlbTimer.setFont(new Font("보통",Font.BOLD,40));
		jlbTimer.setHorizontalTextPosition(JLabel.CENTER);
		jpTimer.setBackground(new Color(178,235,244));
		jpTimer.add(jlbTimer);
		add(jpTimer);

		// count패널
		jpCount.setBounds(1420,50,150,160);
		jpCount.setLayout(new GridLayout(2,2));   
		  
		jpCount.setBorder(new TitledBorder(new LineBorder(Color.gray,5),""));   ///테두리 수정
		jlbCount = new JLabel("",JLabel.CENTER);      
		jlbCount.setFont(new Font("보통",Font.BOLD,60));
		jlbTotal = new JLabel("",JLabel.CENTER);
		jlbTotal.setFont(new Font("보통",Font.BOLD,60));   
		ImageIcon slash = getScaledImage(new ImageIcon("images/slash_tp.png"), 120,140);
		jlbSlash1 = new JLabel(slash);
		jlbSlash2 = new JLabel(slash);

        //jpCount.setBackground(Color.white);   
		jpCount.setBackground(new Color(178,235,244));
		jpCount.add(jlbCount);
		jpCount.add(jlbSlash1);
		jpCount.add(jlbSlash2);
		jpCount.add(jlbTotal);
		
		
		add(jpCount);	
			
		//answer table	
		

		DefaultTableModel modelAns = new DefaultTableModel(data,coName);
		tableAns = new JTable(modelAns);	
		tableAns.setBounds(450,230,700,170);
		tableAns.setTableHeader(null);			
		//add(new JScrollPane(tableAns));
		add(tableAns);


		try {// 채팅방 실행 
			gcc.setBounds(500,720,600,220);
			add(gcc);
		} catch (Exception e ) {
		}
		
		//탭패널
		jpTab.setBounds(40,800,210,140);
		jpTab.setBackground(new java.awt.Color(255,0,0,0));   
		ImageIcon tapPic = getScaledImage(new ImageIcon("images/tab_key_tp.png"), 170,140);
		jlbTab = new JLabel(tapPic,JLabel.CENTER);//
		jpTab.setBackground(Color.BLACK);
		jpTab.add(jlbTab);
		add(jpTab);

		//말풍선
		speechBubble.setBounds(230,740,250,170); //panel
		speechBubble.setBackground(new java.awt.Color(255,0,0,0));
		ImageIcon speechPic = getScaledImage(new ImageIcon("images/balloon_tp.png"), 200,160);
		jlbspeechBubble = new JLabel(speechPic,JLabel.CENTER); //label
		speechBubble.add(jlbspeechBubble);
		add(speechBubble);

		//이펙트 
		win1 = new JLabel(new ImageIcon("effect_images/win_effect.gif"));
		win1.setBounds(250,400,200,150);
		win1.setBackground(new java.awt.Color(255,0,0,0));		
		win1.setVisible(false);
		add(win1);

		win2 = new JLabel(new ImageIcon("effect_images/win_effect.gif"));
		win2.setBounds(550,400,200,150);
		win2.setBackground(new java.awt.Color(255,0,0,0));		
		win2.setVisible(false);
		add(win2);

		win3 = new JLabel(new ImageIcon("effect_images/win_effect.gif"));
		win3.setBounds(850,400,200,150);
		win3.setBackground(new java.awt.Color(255,0,0,0));		
		win3.setVisible(false);
		add(win3);

		win4 = new JLabel(new ImageIcon("effect_images/win_effect.gif"));
		win4.setBounds(1050,400,200,150);
		win4.setBackground(new java.awt.Color(255,0,0,0));		
		win4.setVisible(false);
		add(win4);

		lose1 = new JLabel(new ImageIcon("effect_images/lose_effect.gif"));
		lose1.setBounds(250,400,200,150);
		lose1.setBackground(new java.awt.Color(255,0,0,0));		
		lose1.setVisible(false);
		add(lose1);

		lose2 = new JLabel(new ImageIcon("effect_images/lose_effect.gif"));
		lose2.setBounds(550,400,200,150);
		lose2.setBackground(new java.awt.Color(255,0,0,0));		
		lose2.setVisible(false);
		add(lose2);

		lose3 = new JLabel(new ImageIcon("effect_images/lose_effect.gif"));
		lose3.setBounds(850,400,200,150);
		lose3.setBackground(new java.awt.Color(255,0,0,0));		
		lose3.setVisible(false);
		add(lose3);

		lose4 = new JLabel(new ImageIcon("effect_images/lose_effect.gif"));
		lose4.setBounds(1050,400,200,150);
		lose4.setBackground(new java.awt.Color(255,0,0,0));		
		lose4.setVisible(false);
		add(lose4);
	
	


		//박스
		box1 = new JPanel();
		
		box1.setBounds(200,520,290,270);
		box1.setBackground(new java.awt.Color(255,0,0,0));
		ImageIcon normal1 = getScaledImage(new ImageIcon("images/ufo2_tp.png"), 290,170);
		jlbBox1 = new JLabel(normal1,JLabel.CENTER);
		et1 = new JLabel("         ", SwingConstants.CENTER);
		et1.setMinimumSize(new Dimension(120, 120));
		et1.setPreferredSize(new Dimension(120, 120));
		et1.setMaximumSize(new Dimension(120, 120));
		jlbBox1.setLayout ( new GridLayout(0,1));
		tf1 = new JTextField("ID : ");
		tf2 = new JTextField("Score : ");
		tf1.setForeground (new Color(29,139,21));      
		tf2.setForeground ( new Color(178,235,244));
		tf1.setHorizontalAlignment(JTextField.CENTER);
		tf2.setHorizontalAlignment(JTextField.CENTER);
		tf1.setBorder(BorderFactory.createEmptyBorder());
		tf2.setBorder(BorderFactory.createEmptyBorder());
		tf1.setFont ( f ) ;
		tf2.setFont ( f ) ;
		tf1.setEditable(false);
		tf2.setEditable(false);
		tf1.setOpaque(false);
		tf2.setOpaque(false);
		tf1.setBackground(new java.awt.Color(255,0,0,0));	
		tf2.setBackground(new java.awt.Color(255,0,0,0));	
		jlbBox1.add(et1,"Center");
		jlbBox1.add(tf2,"South");
		jlbBox1.add(tf1,"South");
		box1.add(jlbBox1);
		add(box1);

		box2 = new JPanel();
		box2.setBounds(500,520,290,270);
		box2.setBackground(new java.awt.Color(255,0,0,0));
		ImageIcon normal2 = getScaledImage(new ImageIcon("images/ufo2_tp.png"), 290,170);
		jlbBox2 = new JLabel(normal2,JLabel.CENTER);
		et2 = new JLabel("                ",SwingConstants.CENTER);
		jlbBox2.setLayout ( new GridLayout(0,1));
		tf3 = new JTextField("ID : ");
		tf4 = new JTextField("Score : ");
		tf3.setForeground (new Color(29,139,21));      
		tf4.setForeground ( new Color(178,235,244));
		tf3.setHorizontalAlignment(JTextField.CENTER);
		tf4.setHorizontalAlignment(JTextField.CENTER);
		tf3.setBorder(BorderFactory.createEmptyBorder());
		tf4.setBorder(BorderFactory.createEmptyBorder());
		tf3.setFont ( f ) ;
		tf4.setFont ( f ) ;
		tf3.setEditable(false);
		tf4.setEditable(false);
		tf3.setOpaque(false);
		tf4.setOpaque(false);
		tf3.setBackground(new java.awt.Color(255,0,0,0));	
		tf4.setBackground(new java.awt.Color(255,0,0,0));	
		jlbBox2.add(et2,"Center");
		jlbBox2.add(tf4,"South");
		jlbBox2.add(tf3,"South");
		box2.add(jlbBox2);
		add(box2);

		box3 = new JPanel();
		box3.setBounds(800,520,290,270);
		box3.setBackground(new java.awt.Color(255,0,0,0));
		ImageIcon normal3 = getScaledImage(new ImageIcon("images/ufo2_tp.png"), 290,170);
		jlbBox3 = new JLabel(normal3,JLabel.CENTER);
		et3 = new JLabel("                ",SwingConstants.CENTER);
		jlbBox3.setLayout ( new GridLayout(0,1));
		tf5 = new JTextField("ID : ");
		tf6 = new JTextField("Score : ");
		tf5.setForeground (new Color(29,139,21));      
		tf6.setForeground ( new Color(178,235,244));
		tf5.setHorizontalAlignment(JTextField.CENTER);
		tf6.setHorizontalAlignment(JTextField.CENTER);
		tf5.setBorder(BorderFactory.createEmptyBorder());
		tf6.setBorder(BorderFactory.createEmptyBorder());
		tf5.setFont ( f ) ;
		tf6.setFont ( f ) ;
		tf5.setEditable(false);
		tf6.setEditable(false);
		tf5.setOpaque(false);
		tf6.setOpaque(false);
		tf5.setBackground(new java.awt.Color(255,0,0,0));	
		tf6.setBackground(new java.awt.Color(255,0,0,0));	
		jlbBox3.add(et3,"Center");
		jlbBox3.add(tf6,"South");
		jlbBox3.add(tf5,"South");
		box3.add(jlbBox3);
		add(box3);
		


		box4 = new JPanel();
		box4.setBounds(1100,520,290,270);
		box4.setBackground(new java.awt.Color(255,0,0,0));	
		ImageIcon normal4 = getScaledImage(new ImageIcon("images/ufo2_tp.png"), 290,170);
		jlbBox4 = new JLabel(normal4,JLabel.CENTER);
		et4 = new JLabel("                ",SwingConstants.CENTER);
		jlbBox4.setLayout ( new GridLayout(0,1));
		tf7 = new JTextField("ID : ");
		tf8 = new JTextField("Score : ");
		tf7.setForeground (new Color(29,139,21));      
		tf8.setForeground ( new Color(178,235,244));
		tf7.setHorizontalAlignment(JTextField.CENTER);
		tf8.setHorizontalAlignment(JTextField.CENTER);
		tf7.setBorder(BorderFactory.createEmptyBorder());
		tf8.setBorder(BorderFactory.createEmptyBorder());
		tf7.setFont ( f ) ;
		tf8.setFont ( f ) ;
		tf7.setEditable(false);
		tf8.setEditable(false);
		tf7.setOpaque(false);
		tf8.setOpaque(false);
		tf7.setBackground(new java.awt.Color(255,0,0,0));	
		tf8.setBackground(new java.awt.Color(255,0,0,0));	
		jlbBox4.add(et4,"Center");
		jlbBox4.add(tf8,"South");
		jlbBox4.add(tf7,"South");
		box4.add(jlbBox4);
		add(box4);
	
		jpStart = new JPanel();
		jpStart.setBounds(1150,750,400,200);
		jpStart.setBackground(new Color(255,0,0,0));
		jpStart.setLayout(new GridLayout(0,1));
		ImageIcon startimg =new ImageIcon("images/start_tp.png");
		startb = new JButton("",startimg);
		//startb.setBackground(new Color(0,0,84));
		startb.setBackground(new Color(255,0,0,0));
		startb.setBorder(new LineBorder(new Color(255,0,0,0)));
		jpStart.add(startb);
		add(jpStart);
		startb.addActionListener(this);
		////// 버튼수정 ////////////

		
		bt = new BgmThread("sounds/GamePlay.wav", mp);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		add(panel);
		background = new JLabel(new ImageIcon("images/background.png"));		// 배경 수정
		this.getContentPane().setBackground(Color.WHITE); // 배경 수정
		add(background);	// 배경 수정
		setBounds(200,20,1600,1000);
		setResizable(false);
		

	}//Constructor
	
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource().equals(startb)) {
				mp.music("sounds/GameStart.wav", startbtn);	
				startb.setVisible(false);
				if(bgmisOff == false) {
					bt.start();//무한 재생 
					bgmisOff = true;
				}
				oos.writeObject("thegamebegins");
				background.repaint();
			}
		}
		catch (Exception ex) {
			
		}
		
	}
	public void resetIngameui() {
		tf2.setText("0");
		tf4.setText("0");
		tf6.setText("0");
		tf8.setText("0");
		startb.setVisible(true);
		background.repaint();
		jlbCount.setText("1");
		jlbProb.setText("문제");
	}
	public JTable getAnstable(){
		return tableAns;
	}
	public PlayerInfo getPlayerinfo(){
		return pi;
	}
	public JTextArea getProblabel(){
		return jlbProb;
	}
	public DefaultTableModel getTableModel (){
		return (DefaultTableModel) tableAns.getModel();
	}
	public JLabel getTimerlabel(){
		return jlbTimer;
	}
	public JLabel getTroundlabel(){
		return jlbTotal;
	}
	public JLabel getCroundlabel(){
		return jlbCount;
	}
	public void getStartTimer(MultiClientThread mc, InGameUI gui, ObjectOutputStream oos, String prob, int roundtime){
		rt = new RoundTimer(mc, gui, oos, prob, roundtime);
	}
	public void setMyanswer(String ans){
		this.myAnswer = ans;
	}
	public String getMyanswer(){
		return myAnswer;
	}
	public void setMyorder(String ord){
		this.myOrder = ord;
	}
	public String getMyorder(){
		return this.myOrder;
	}
	public JTextField getScorelabel(){
		return tf2; //아 이건 라벨에 따라 적용해야대는데
	}
	public JButton getStartbutton(){
		return startb;
	}

	public void setPosition(String pinfoString){ //자리 배치
		System.out.println(pinfoString);
		String[] pinfo = pinfoString.split( "#" ); //아이디, 캐릭터, 자리 순 


		for(int i=1; i< pinfo.length ; i+=3){
			if(pinfo[i+2].equals("1")){
				et1.setIcon(this.getScaledImage(new ImageIcon(pinfo[i+1]), 60, 60));
				if(pi.getId().equals(pinfo[i]))
					tf1.setForeground(Color.GREEN);
				tf1.setText("ID : " + pinfo[i]);
				tf2.setText("0");
			}
			else if(pinfo[i+2].equals("2")){
				et2.setIcon(this.getScaledImage(new ImageIcon(pinfo[i+1]), 60, 60));
				if(pi.getId().equals(pinfo[i]))
					tf3.setForeground(Color.GREEN);
				tf3.setText("ID : " + pinfo[i]);
				tf4.setText("0");
			}
			else if(pinfo[i+2].equals("3")){
				et3.setIcon(this.getScaledImage(new ImageIcon(pinfo[i+1]), 60, 60));
				if(pi.getId().equals(pinfo[i]))
					tf5.setForeground(Color.GREEN);
				tf5.setText("ID : " + pinfo[i]);
				tf6.setText("0");
			}
			else if(pinfo[i+2].equals("4")){
				et4.setIcon(this.getScaledImage(new ImageIcon(pinfo[i+1]), 60, 60));
				if(pi.getId().equals(pinfo[i]))
					tf7.setForeground(Color.GREEN);
				tf7.setText("ID : " + pinfo[i]);
				tf8.setText("0");
			}
			background.repaint();
		}
	}
	public void setEachScore(String st) { //구분자, 자리, 점수, 자리, 점수, 자리, 점수 
		String[] pinfo = st.split( "#" );
		for(int i= 1; i<pinfo.length; i+=2) {
			if(pinfo[i].equals("1")){ //1번 좌석	
				LoseEffectThread let = new LoseEffectThread(lose1);
				WinEffectThread wet = new WinEffectThread(win1);
				tf2.setText(Integer.toString(Integer.parseInt(tf2.getText())+Integer.parseInt(pinfo[i+1])));
				if(pinfo[i+1].equals("0")){
					System.out.println(pinfo[i+1] + " 점 오답");
					let.start();
				}
				else{
					System.out.println(pinfo[i+1] + " 점 정답");
					wet.start();
				}
				background.repaint();
			}
			else if(pinfo[i].equals("2")){
				LoseEffectThread let = new LoseEffectThread(lose2);
				WinEffectThread wet = new WinEffectThread(win2);
				tf4.setText(Integer.toString(Integer.parseInt(tf4.getText())+Integer.parseInt(pinfo[i+1])));	
				if(Integer.parseInt(pinfo[i+1])==0)
					let.start();
				else
					wet.start();
				background.repaint();
			}
			else if(pinfo[i].equals("3")){
				LoseEffectThread let = new LoseEffectThread(lose3);
				WinEffectThread wet = new WinEffectThread(win3);
				tf6.setText(Integer.toString(Integer.parseInt(tf6.getText())+Integer.parseInt(pinfo[i+1])));	
				if(Integer.parseInt(pinfo[i+1])==0)
					let.start();
				else
					wet.start();				
				background.repaint();
			}
			else if(pinfo[i].equals("4")){
				LoseEffectThread let = new LoseEffectThread(lose4);
				WinEffectThread wet = new WinEffectThread(lose4);
				tf8.setText(Integer.toString(Integer.parseInt(tf8.getText())+Integer.parseInt(pinfo[i+1])));
				if(Integer.parseInt(pinfo[i+1])==0)
					let.start();
				else
					wet.start();
				background.repaint();
			}		
		}	
		background.repaint();
	}

	public ImageIcon getScaledImage(ImageIcon srcImg, int row, int col){ // 이미지 리사이징
	
		BufferedImage resizedImg = new BufferedImage(row, col, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();
		Image originImg = srcImg.getImage();
		
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(originImg, 0, 0, row, col, null);
		g2.dispose();
		ImageIcon icon = new ImageIcon(resizedImg);
		return icon;
	}

	public static void main(String[] args) {
		
		//ObjectInputStream ois = new ObjectInputStream();
		//ObjectOutputStream oos = new ObjectOutputStream();
		//PlayerInfo pi = new PlayerInfo();
		//new InGameUI(pi, ois, oos);
		
	}
}
