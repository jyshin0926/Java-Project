import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.io.*;
import java.net.Socket;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.text.DecimalFormat;



class MultiClientThread extends Thread implements Serializable{
    private WaitUI wu;
	private ChatClient cc;
	private InGameUI gui;
	private GameChatClient gcc;
    public MultiClientThread( WaitUI wu, ChatClient cc, InGameUI gui, GameChatClient gcc){
        this.wu = wu; 
		this.cc = cc;
		this.gui = gui;
		this.gcc = gcc;
    }
    public synchronized void run(){		
		System.out.println("클라이언트 쓰레드 시작");
        String message = null;
        String[] receivedMsg = null;
        boolean isStop = false;
        while(!isStop){
            try{
                message = (String) gcc.getOis().readObject();  //왜 wu로 하면 안댐 ?
                receivedMsg = message.split( "#" );
				System.out.println("대기실 스레드가 받은 메세지 : "+message);
            }catch(Exception e){
                e.printStackTrace();
            }
	
			//대기방 
			if(receivedMsg[0].equals( "commonsense" )){
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n방장 "+ receivedMsg[1]+" 님이 상식 퀴즈를 선택하셨습니다.");
			}
			else if(receivedMsg[0].equals( "nonsense" )){
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n방장 "+ receivedMsg[1]+" 님이 넌센스 퀴즈를 선택하셨습니다.");
			}
			else if(receivedMsg[0].equals( "teamgame" )){
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n방장 "+ receivedMsg[1]+" 님이 라운드 수를 [ "+receivedMsg[2]+" ] 로 선택하셨습니다.");
			}
			else if(receivedMsg[0].equals( "individualgame" )){
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n방장 "+ receivedMsg[1]+" 님이 라운드 타임을 [ "+receivedMsg[2]+" ] 초로 선택하셨습니다.");
			}
			else if(receivedMsg[0].equals( "readytoplay" )){ //레디한번하면 버튼 비활성화 
				if(receivedMsg[1].equals(wu.getPlayerinfo().getId()))
					wu.setReady(); //브로드케스트 도서 그런듯 아이디 체크해야함 
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n"+ receivedMsg[1]+" 님이 레디 하셨습니다.");
			}
			else if(receivedMsg[0].equals( "startgame" )){ //게임시작 방장이 눌러도 실행되게 
				//플레이어정보,내 아이디, 내 케릭터 , 내 자리, 내 점수, 내가 푼 총 문제, 내가 맞춘 문제
				if(receivedMsg[1].equals("readynotenough")){
					JOptionPane.showMessageDialog(null, "모두 레디하지 않았습니다!" , "게임 시작 불가능", JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					try {
						gcc.getOos().writeObject("startplayerinfo#"+wu.getPlayerinfo().getId()+"#"+wu.getPlayerinfo().getCharacter()+"#"+wu.getPlayerinfo().getMypos()+"#"+wu.getPlayerinfo().getScore()+"#"+ wu.getPlayerinfo().getTnum()+"#"+ wu.getPlayerinfo().getCornum()); //내 자리랑 스코어 넘김
					}
					catch (Exception e) {
					}
					wu.getInfoText().setText(wu.getInfoText().getText() + "\n방장 "+ receivedMsg[1]+" 님이 게임을 시작하셨습니다.");
				}

			}	
			else if(receivedMsg[0].equals( "waitaminute" )){ //서버에 유저가 다 등록될때까지 잠시 대기 	
				try {
					Thread.sleep(1000);
					gcc.getOos().writeObject("waitisover#"+wu.getPlayerinfo().getId());
					
				} 
				catch (Exception e) {
				}
			}
			else if(receivedMsg[0].equals( "reallygamestart" )){ //게임 진짜진짜 시작
				wu.getMP().stop(wu.getBgm());
				gui.setPosition(message);
				gui.setVisible(true);  
			}
			else if(receivedMsg[0].equals( "memberlist" )){ //처음 접속할때 유저 정보 받아오는 곳
				for(int i =1; i< receivedMsg.length-1; i++){
					if(i==receivedMsg.length-2)
						wu.getDefaultModel().addRow(new Object[]{"방장", receivedMsg[i]});
					else
						wu.getDefaultModel().addRow(new Object[]{"", receivedMsg[i]});
				}
			}
			else if(receivedMsg[0].equals( "Success" )){ //다른 유저 로그인 했을때 알림 // 다른 상대 테이블에 등록하는작업도 해야함 
				if(!(receivedMsg[1].equals(wu.getMyinfo().getId()))){
					wu.getDefaultModel().addRow(new Object[]{"", receivedMsg[1]});
					cc.getJta().append(receivedMsg[1] +" 님이 접속했습니다. "+ System.getProperty("line.separator"));//채팅방에 알림
					cc.getJta().setCaretPosition(cc.getJta().getDocument().getLength());  
				}
			}
			//채팅
			//인게임 채팅 뿌리기
			else if(receivedMsg[0].equals( "ingame" )){
                gcc.getJta().append(receivedMsg[1] +" : "+receivedMsg[2]+ System.getProperty("line.separator"));
				gcc.getJta().setCaretPosition(gcc.getJta().getDocument().getLength());    
			}
			else if(receivedMsg[0].equals("thegamebegins" )){ //문제 뿌리기 // 문제, 초성, 현재라운드, 총라운드
				
				gui.getProblabel().setText(receivedMsg[1]+"\n                   "
						+ "                                                  "+receivedMsg[2]);//문제 세팅 - 타이머 쓰레드 안에서 해야할듯 

				gui.getTroundlabel().setText(receivedMsg[4]);
				gui.getCroundlabel().setText(receivedMsg[3]);
				gui.getTableModel().setRowCount(1);
				gui.getStartTimer(this,gui, gcc.getOos(), receivedMsg[1], Integer.parseInt(receivedMsg[5])); //마지막 인덱스는 각 문제별  제한시간 
					
			}
			else if(receivedMsg[0].equals("gameanswer" )){ //내 아이디가아니면 정답은 공백으로!
				//이미 해당아이디의 정답이 있으면 삭제 요청
				int delrow =0;
				for(int i =1; i< gui.getTableModel().getRowCount(); i++){
					if(gui.getAnstable().getValueAt(i, 1).equals(gui.getPlayerinfo().getId())){ //등록된 아이디가 있으면 삭제 
						//gui.getTableModel().removeRow(i);
						delrow = i;
						break;
					}
				}
				try {
					gcc.getOos().writeObject("deletethisrowfirst#"+Integer.toString(delrow)+"#"+receivedMsg[1]+"#"+receivedMsg[2]); //delrow가 0이면 없다는 뜻! 받은 아이디 정답도 다시 되돌려줘야함!1- 지워야할 로우 2- 정답 3- 아이디
				} catch (Exception e) {
				}
				
			}
			else if(receivedMsg[0].equals("gameanswerreg")){ 
				//서버에서 받은거 삭제 
				if(!receivedMsg[3].equals("0"))
					gui.getTableModel().removeRow(Integer.parseInt(receivedMsg[3]));

				//정렬먼저

				//정답 등록
				if(receivedMsg[2].equals(gui.getPlayerinfo().getId()))
					gui.getTableModel().addRow(new Object[]{ receivedMsg[1], receivedMsg[2]}); //내 정답 세팅 
				else {
					gui.getTableModel().addRow(new Object[]{ "???", receivedMsg[2]}); //다른사람 정답 세팅 
					wu.getMP().musicRepeat("sounds/SendAnswer.wav");
				}
				
				if(receivedMsg[2].equals(gui.getPlayerinfo().getId())) {
					System.out.println("내가 작성한 정답 : "+receivedMsg[1]);
					gui.setMyanswer(receivedMsg[1]);
				}
				
				//내 아이디가 있는 로우 검색 
				int myorderintable=0;
				for(int i =1; i< gui.getTableModel().getRowCount(); i++){
					if(gui.getAnstable().getValueAt(i, 1).equals(gui.getPlayerinfo().getId())){ //등록된 아이디가 있으면 삭제 
						myorderintable = i;
						break;
					}
				}
				gui.setMyorder(Integer.toString(myorderintable));
			}

			//타이머 끝나고 정답 받음 (자가 채점후 결과 리턴 )
			else if(receivedMsg[0].equals("checkanswer" )){
				try {
					gui.getProblabel().setText("\n\n   "
							+ "                                                               정답 : " + receivedMsg[1]);	
					wu.getPlayerinfo().setTnum((wu.getPlayerinfo().getTnum()+1)); //푼 문제 갯수 ++
					if(receivedMsg[1].equals(gui.getMyanswer())){
						System.out.println("정답!");
						wu.getPlayerinfo().setCornum((wu.getPlayerinfo().getCornum()+1)); //맞은 문제 갯수 ++
						gcc.getOos().writeObject("checkfromclient#correct#"+gui.getMyorder()+"#"+gui.getPlayerinfo().getId()+"#"+gui.getPlayerinfo().getMypos()); //0 - check , 1- correct, 2- 맞춘순서, 3- 아이디, 4 -자리
						gui.setMyanswer(""); //정답 초기화
						wu.getMP().musicRepeat("sounds/Succ.wav");
					}
					else{
						gcc.getOos().writeObject("checkfromclient#wrong#"+"0#"+gui.getPlayerinfo().getId()+"#"+gui.getPlayerinfo().getMypos()); //1 - wrong 2- 0줌 걍  3 - 아이디 4 - 자리
						System.out.println("오답..");
						gui.setMyanswer("");
						wu.getMP().musicRepeat("sounds/Fail.wav");
					}
					Thread.sleep(5000);
				}
				catch (Exception e) {
				}
			}
			else if(receivedMsg[0].equals("checkfinishedfromserver")){
				try {
					if(gui.getPlayerinfo().getMypos().equals("1"))
						gcc.getOos().writeObject("givemetheroundresult#");
				} catch (Exception e) {
				}
			}
			//서버에서 부여받은 정답 텍스트에 표기
			else if(receivedMsg[0].equals("pointfromserver")){
				System.out.println(message);
				gui.setEachScore(message);
			}
			//유저 종료 
			else if(receivedMsg[0].equals("userexited")){
				//종료한 유저의 아이디가 있는 로우 검색 
				int userorderintable=0;
				for(int i =1; i< wu.getDefaultModel().getRowCount(); i++){
					if(wu.getDefaultModel().getValueAt(i, 1).equals(receivedMsg[1])){ //등록된 아이디가 있으면 삭제 
						userorderintable = i;
						break;
					}
				}
				wu.getDefaultModel().removeRow(userorderintable); //삭제 
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n"+ receivedMsg[1] +"님이 종료하셨습니다.");
			}
			//총 점수 받아서 결과창에 띄우는 부분 
			else if(receivedMsg[0].equals("givemethetotalresult")){
				
				//내 점수 찾아서 누적 포인트에 저장 
				//아이디, 라운드점수, 총점수 순서로 들어옴
				String[] pinfo = message.split( "#" );
				for(int j = 1; j <pinfo.length; j+=3) {
					if(wu.getPlayerinfo().getId().equals(pinfo[j])) {
						wu.getPlayerinfo().setScore(Integer.parseInt(pinfo[j+2])); //누적 점수 세팅
						break;
					}
				}
				//서버로 아이디, 누적점수, 문제갯수, 푼문제갯수 전송 
				try {
					gcc.getOos().writeObject("updatemyinfo#"+wu.getPlayerinfo().getId()+"#"+wu.getPlayerinfo().getScore()+"#"+wu.getPlayerinfo().getTnum()+"#"+wu.getPlayerinfo().getCornum());
				} catch (IOException e) {
					e.printStackTrace();
				}
				ResultUI res = new ResultUI(gcc.getOis(), gcc.getOos(), wu.getPlayerinfo(), gui);
				res.setResultData(message);
				res.setVisible(true);
			}
			//게임 종료 후 대기실 복귀 
			else if(receivedMsg[0].equals("backtowaitroom")){
				//세팅 초기화
				gui.resetIngameui();
				gcc.resetChat();
				gui.setVisible(false);
				gui.dispose();	
				wu.resetReady();
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n방장 "+ receivedMsg[1] +" 님이 대기실 복귀를 선택하셨습니다.");
			}
			else if(receivedMsg[0].equals("updatemyinfo")){ // 1- 아이디, 2- 누적점수, 3- 푼문제, 4 - 맞은문제	 
				//내 대기실 정보 업데이트 
				wu.getStatlabel().setText(receivedMsg[2]+" 점");
				double acc;
				DecimalFormat df = new DecimalFormat("#.##");
				if(receivedMsg[3].contentEquals("0"))
					acc = 0.0;
				else{
					
					acc = Double.parseDouble(receivedMsg[4]) / Double.parseDouble(receivedMsg[3]) * 100;
				}
				wu.getAcclabel().setText(df.format(acc) + " %"+"( " + receivedMsg[4] + " / "+ receivedMsg[3]+" )");
			}
			else if(receivedMsg[0].equals("sendto")){     //sendto#sendid#myid#msg
				System.out.println("receivedMsg[0] : "+receivedMsg[0] );
				if(receivedMsg[1].equals(wu.getPlayerinfo().getId())){
	                cc.getJta().append(receivedMsg[2] +" 님으로부터의 귓속말 : "+receivedMsg[3]+ System.getProperty("line.separator"));
					cc.getJta().setCaretPosition(cc.getJta().getDocument().getLength());   
				}
				if(receivedMsg[2].equals(wu.getPlayerinfo().getId())){
	                cc.getJta().append(receivedMsg[1] +" 님에게 귓속말 : "+receivedMsg[3]+ System.getProperty("line.separator"));
					cc.getJta().setCaretPosition(cc.getJta().getDocument().getLength());   
				}

            }
			else if(receivedMsg[0].equals("ingamesendto")){  //sendto#sendid#myid#msg
				System.out.println("인게임 귓속말");
				System.out.println("receivedMsg[0] : "+receivedMsg[0] );
				if(receivedMsg[2].equals(wu.getPlayerinfo().getId())) {
					System.out.println("??");
	                gcc.getJta().append(receivedMsg[3] +" 님으로부터의 귓속말 : "+receivedMsg[4]+ System.getProperty("line.separator"));
					gcc.getJta().setCaretPosition(gcc.getJta().getDocument().getLength());   
				}
				if(receivedMsg[3].equals(wu.getPlayerinfo().getId())){
	                gcc.getJta().append(receivedMsg[2] +" 님에게 귓속말 : "+receivedMsg[4]+ System.getProperty("line.separator"));
					gcc.getJta().setCaretPosition(gcc.getJta().getDocument().getLength());   
				}
            }
			else if(receivedMsg[0].equals("showranking")){ // 아이디, 누적, acc
				RankingUI ru = new RankingUI();
				ru.setRank(message);
				ru.setVisible(true);
			}
			
			//대기실 채팅 뿌리기
			else{     
				System.out.println("receivedMsg[0] : "+receivedMsg[0] );
                cc.getJta().append(receivedMsg[0] +" : "+receivedMsg[1]+ System.getProperty("line.separator"));
				cc.getJta().setCaretPosition(cc.getJta().getDocument().getLength());    
            }
        }	
    }	
}


class WaitUI extends JFrame implements ActionListener {
	private JMenuBar mb;
	private JMenu minfo;
	private JTextArea infoText;
	JMenuItem mhelp;
    JMenuItem mrank;
	JPanel optPanel, pMyinfo, pMyId, pMyAcc, pMyStat;
	JToggleButton btCommonsense, btNonsense, btReady;
	JButton  btTeam, btIndi, btStart, btExit;
	ButtonGroup bgM, bgT, bgS;
	JLabel laChat, laWaitlist, laMyinfo, laMyChar, laMyId, laIdSlot, laMyAcc, laAccSlot, laMyStat, laStatSlot, emptylabel;
	JTable table;
	DefaultTableModel model;
	TitledBorder buttonBorder, waitBorder, infoBorder;
	GridBagLayout gb;
	GridBagConstraints gbc;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
	private int bSizex =40;
	private int bSizey =40;
	private String id;
	private static ArrayList<String> memlist = new ArrayList<String>();
	private static boolean ready=false;
	private static boolean gameinprocess = false;
	
	PlayerInfo myinfo;
	Object data [][] = {};
	Object conName[] = {"권한", "ID"};
	String fontName [] = {"Plain", "Bold", "Arial"}; //한샘
	private Font bPlain, bBold, bArial, bGothic; //한샘
	
	MusicPlayer mp = new MusicPlayer();
	Clip bgm, btn, enter;
	public WaitUI (PlayerInfo myinfo, ObjectInputStream ois,ObjectOutputStream oos) {
		setBackground(SystemColor.info);
		this.ois = ois;
		this.oos = oos;
		this.myinfo = myinfo;
		this.id = myinfo.getId(); //아이디 넣고 시작
		try {
			bgm = AudioSystem.getClip();
			btn= AudioSystem.getClip();
			enter = AudioSystem.getClip();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
		mp.music("sounds/back.wav", bgm); //배경음
		
		setTitle("메인창");

		// Layout
		gb = new GridBagLayout();
		getContentPane().setLayout (gb);
		gbc = new GridBagConstraints ();
		gbc.fill=GridBagConstraints.BOTH;
		
		// MenuBar
		mb = new JMenuBar ();
		minfo = new JMenu("정보");
		mhelp = new JMenuItem ("Help");
		mrank = new JMenuItem ("Ranking");
		mhelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               openFile("help");
            }
		});
		mrank.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				try {
					oos.writeObject("showranking");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

            }
		});

		minfo.add (mhelp);
		minfo.add (mrank);
		mb.add(minfo);
		setJMenuBar (mb);
		
		// Border
		buttonBorder = new TitledBorder(new LineBorder(Color.black), "옵션 선택");
		waitBorder = new TitledBorder(new LineBorder(Color.black), "참여자 리스트");
		infoBorder = new TitledBorder(new LineBorder(Color.black), "내 정보");

		//Setting the Font 한샘
		bPlain = new Font ("Sans", Font.PLAIN, 10);
		bBold = new Font ("Sans", Font.BOLD, 17);

		//Setting the Font Style 한샘
		bArial = new Font ("Arial", Font.PLAIN, 10);

		
		// ProblemType
		btCommonsense = new JToggleButton ("상식");
		btNonsense = new JToggleButton ("넌센스");
		btCommonsense.setBackground(new Color(0,34,102)); //한샘
		btCommonsense.setForeground(new Color (234,234,234)); //한샘
		btNonsense.setBackground(new Color(0,34,102)); //한샘
		btNonsense.setForeground(new Color (234,234,234)); //한샘


		bgM =new ButtonGroup();
		bgM.add(btCommonsense);
		bgM.add(btNonsense);
		btCommonsense.setFont(bBold); //한샘
		btNonsense.setFont(bBold); //한샘

		
		optPanel = new JPanel();
		optPanel.setLayout(new GridLayout(0,2,10,10));
		optPanel.setBorder(buttonBorder);

		optPanel.add(btCommonsense);
		optPanel.add(btNonsense);

		// GameType
		btTeam = new JButton ("문제 수 설정 (기본 : 4개)");
		btIndi = new JButton ("문제 타이머 설정 (기본 : 15초)");
		btTeam.setBackground(new Color(0,34,102)); //한샘
		btTeam.setForeground(new Color (234,234,234)); //한샘
		btIndi.setBackground(new Color(0,34,102)); //한샘
		btIndi.setForeground(new Color (234,234,234)); //한샘


		bgT = new ButtonGroup();
		bgT.add(btTeam);
		bgT.add(btIndi);
		btTeam.setFont(bBold); //한샘
		btIndi.setFont(bBold); //한샘

		optPanel.add(btTeam);
		optPanel.add(btIndi);
		
		// Ready or Start
		btReady = new JToggleButton ("READY"); 
		btStart = new JButton ("START"); 

		bgS = new ButtonGroup();
		bgS.add(btReady);
		bgS.add(btStart);
		btReady.setFont(bBold); //한샘
		btStart.setFont(bBold); //한샘
		btReady.setForeground(new Color (0,34,102)); //뉴한샘
		btStart.setForeground(Color.RED); //뉴한샘
		optPanel.add(btReady);
		optPanel.add(btStart);
		
		gbAdd(optPanel, 1,1,1,1,1.0,3.0);
		//방장은 ready불가능, 나머지는 ready만 가능
		if(myinfo.getAuth()==true) {//방장
			btReady.setEnabled(false);
		}
		else{//나머지
			btStart.setEnabled(false);
			btCommonsense.setEnabled(false);
			btNonsense.setEnabled(false);
			btTeam.setEnabled(false);
			btIndi.setEnabled(false);

		}
		// Info
		infoText = new JTextArea(2,2);
		gbAdd(new JScrollPane(infoText), 1,3,1,1,3.0,3.0);
		infoText.setEditable(false);

		// Chat
		laChat = new JLabel ("채팅창");

		// 겜방도 미리 만들어 놓음



		try {// 클라이언트 쓰레드랑 채팅방 실행 
			//setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("back.jpg")))));
			GameChatClient gcc = new GameChatClient( "0.0.0.0 - 아이피", id , ois, oos);
			InGameUI gui = new InGameUI(myinfo, ois, oos, gcc);
	
			ChatClient cc = new ChatClient( "0.0.0.0 - 아이피", id , ois, oos);
			gbAdd (cc,1,2,1,1,1.0,1.0);

			MultiClientThread ct = new MultiClientThread(this, cc, gui, gcc);
			Thread t = new Thread( ct );
			t.start();

			//gui.setVisible(true);
		} catch (Exception e ) {
			System.out.println("에러남");
		}



	
		// Create the list

		
		model = new DefaultTableModel(data, conName){
			public Class getColumnClass(int column){
					return getValueAt(0, column).getClass();
			}
			public boolean isCellEditable(int row, int column) {
					return false;
			}
			};

		table = new JTable (model);
		JScrollPane jp = new JScrollPane(table);
		jp.setBorder(waitBorder);
		jp.setPreferredSize(new Dimension(50,50));
		gbAdd(jp,2,1,1,1,1.0,1.0);
		

		pMyinfo = new JPanel ();
		pMyinfo.setLayout (new GridLayout (0,1));
		pMyId = new JPanel ();
		pMyId.setLayout(new GridLayout(0,2));
		pMyAcc = new JPanel ();
		pMyAcc.setLayout(new GridLayout(0,2));
		pMyStat = new JPanel ();
		pMyStat.setLayout(new GridLayout(0,2));

		laMyChar = new JLabel(getScaledImage(new ImageIcon(myinfo.getCharacter()), 50,50));
	
		Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
		laMyId = new JLabel("ID : ");
		laMyId.setFont(f);
		laIdSlot = new JLabel(myinfo.getId());
		laIdSlot.setFont(f);
		laMyStat = new JLabel ("누적점수 : ");
		laMyStat.setFont(f);
		laStatSlot = new JLabel(myinfo.getScore() +" 점");
		laStatSlot.setFont(f);
		laMyAcc = new JLabel ("정답률 : ");
		laMyAcc.setFont(f);
		if(myinfo.getTnum()==0 || myinfo.getCornum()==0)
			laAccSlot = new JLabel (0.0 + " %"+"( " + myinfo.getCornum()+ " / "+ myinfo.getTnum()+")");
		else {
			DecimalFormat df = new DecimalFormat("#.##");
			double acc = ((double)myinfo.getCornum()/ myinfo.getTnum() ) * 100;
			laAccSlot = new JLabel (df.format(acc)  + " %"+" ( " + myinfo.getCornum()+ " / "+ myinfo.getTnum()+" )");
		}
		laAccSlot.setFont(f);
		
		pMyId.add(laMyId);
		pMyId.add(laIdSlot);
		pMyAcc.add(laMyAcc);
		pMyAcc.add(laAccSlot);
		pMyStat.add(laMyStat);
		pMyStat.add(laStatSlot);
		pMyinfo.add(laMyChar);
		pMyinfo.add(pMyId);
		pMyinfo.add(pMyAcc);
		pMyinfo.add(pMyStat);
		pMyinfo.setPreferredSize(new Dimension(50,50));
		pMyinfo.setBorder(infoBorder);
		gbAdd (pMyinfo, 2,2,1,1, 1.0,1.0);
		
		// Exit
		btExit = new JButton ("EXIT");
		btExit.setForeground(Color.RED); //뉴한샘
		btExit.setFont(bBold); //뉴한샘
		gbAdd (btExit, 2,3,1,1,1.0,1.0);
		
		//listener
		btCommonsense.addActionListener(this);
		btNonsense.addActionListener(this);
		btTeam.addActionListener(this);
		btIndi.addActionListener(this);
		btReady.addActionListener(this);
		btStart.addActionListener(this);
		btExit.addActionListener(this);
		
		//setUndecorated(true);
		//getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		// Window
		setResizable (false);
		setBounds ( 300, 50, 900, 700 );
		setVisible (true);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

	} // 생성자

	public void actionPerformed(ActionEvent e) {
		mp.musicRepeat("sounds/BtnClick.wav");
		try {
			if(e.getSource().equals(btCommonsense)) { //상식버튼
				oos.writeObject("commonsense#" + id );
			}
			else if(e.getSource().equals(btNonsense)) { //넌센스버튼
				System.out.println("서버로 넌센스 전달");
				oos.writeObject("nonsense#" + id );
			}
			else if(e.getSource().equals(btTeam)) { //문제 수 설정
				InputWindow iw = new InputWindow(2, ois, oos, id);
				iw.setVisible(true);
			}
			else if(e.getSource().equals(btIndi)) { //문제 타이머 설정
				InputWindow iw = new InputWindow(1, ois, oos, id);
				iw.setVisible(true);
			
			}
			else if(e.getSource().equals(btReady)) { //레디버튼
				if(ready == false)
					oos.writeObject("readytoplay#" + id );
				else
					getInfoText().setText(getInfoText().getText() + "\n"+ "이미 레디 하셨습니다!");
			}
			else if(e.getSource().equals(btStart)) { //게임시작버튼 - 인원 안차면 겜 못하게 막아야댐 
			
				oos.writeObject("startgame#" + id );	
			}
			else if(e.getSource().equals(btExit)) {
				oos.writeObject("userwantstoexit#" + id );	
				System.exit(0);
			}
	
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void resetReady(){
		this.ready = false;
		this.btReady.setSelected(false);
	}
    public void setReady(){
		this.ready = true;
	}
	public void setTableData(String id){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		try {
			oos.writeObject("getMemberList");//현재 맴버 리스트 줘
		} catch (Exception e) {
		}
		
	}

	public void setTableData(String auth ,String id){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		try {
			oos.writeObject("getMemberList");//현재 맴버 리스트 줘
		} catch (Exception e) {
		}
	}
	
	// GridBagLayout Method
	private void gbAdd (JComponent c, int x, int y, int w, int h, double xw, double yw) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weightx = xw;
		gbc.weighty = yw;

		gbc.insets = new Insets (2,2,2,2);
		getContentPane().add(c,gbc);

	} // gbAdd

	public DefaultTableModel getDefaultModel(){
		return (DefaultTableModel) table.getModel();
	}
		
	public void openFile(String n) {
	    File f = new File(n+".jpg");
	    File newf = new File(f.getAbsolutePath());
	    Desktop dt = Desktop.getDesktop();
	    try {
			dt.open(newf);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public PlayerInfo getMyinfo(){
		return myinfo;
	}
	public JLabel getAcclabel() {
		return laAccSlot;
	}
	public JLabel getStatlabel() {
		return laStatSlot;
	}
	
	public Clip getBgm() {
		return bgm;
	}
	public ArrayList<String> getList(){
		return memlist;
	}
	public ObjectOutputStream getOos(){
		return oos;
	}
	public PlayerInfo getPlayerinfo(){
		return myinfo;
	}
	public MusicPlayer getMP() {
		return mp;
	}
	public ObjectInputStream getOis(){
        return ois;
    }
    public String getId(){
        return id;
    }
	public JTextArea getInfoText(){
        return infoText;
    }
	public boolean getGameprocess(){
		return gameinprocess;
	}
	public void setGameprocess(boolean t){
		this.gameinprocess = t;
	}
    public void exit(){
        System.exit( 0 );
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
	

	/*
	public static void main (String [ ] args) {
		WaitUI wui = new WaitUI ("엔코아");
		try {
			wui.init();
		} catch (Exception e) {
		}
	
	}
	*/
}
