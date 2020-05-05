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
		System.out.println("Ŭ���̾�Ʈ ������ ����");
        String message = null;
        String[] receivedMsg = null;
        boolean isStop = false;
        while(!isStop){
            try{
                message = (String) gcc.getOis().readObject();  //�� wu�� �ϸ� �ȴ� ?
                receivedMsg = message.split( "#" );
				System.out.println("���� �����尡 ���� �޼��� : "+message);
            }catch(Exception e){
                e.printStackTrace();
            }
	
			//���� 
			if(receivedMsg[0].equals( "commonsense" )){
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n���� "+ receivedMsg[1]+" ���� ��� ��� �����ϼ̽��ϴ�.");
			}
			else if(receivedMsg[0].equals( "nonsense" )){
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n���� "+ receivedMsg[1]+" ���� �ͼ��� ��� �����ϼ̽��ϴ�.");
			}
			else if(receivedMsg[0].equals( "teamgame" )){
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n���� "+ receivedMsg[1]+" ���� ���� ���� [ "+receivedMsg[2]+" ] �� �����ϼ̽��ϴ�.");
			}
			else if(receivedMsg[0].equals( "individualgame" )){
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n���� "+ receivedMsg[1]+" ���� ���� Ÿ���� [ "+receivedMsg[2]+" ] �ʷ� �����ϼ̽��ϴ�.");
			}
			else if(receivedMsg[0].equals( "readytoplay" )){ //�����ѹ��ϸ� ��ư ��Ȱ��ȭ 
				if(receivedMsg[1].equals(wu.getPlayerinfo().getId()))
					wu.setReady(); //��ε��ɽ�Ʈ ���� �׷��� ���̵� üũ�ؾ��� 
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n"+ receivedMsg[1]+" ���� ���� �ϼ̽��ϴ�.");
			}
			else if(receivedMsg[0].equals( "startgame" )){ //���ӽ��� ������ ������ ����ǰ� 
				//�÷��̾�����,�� ���̵�, �� �ɸ��� , �� �ڸ�, �� ����, ���� Ǭ �� ����, ���� ���� ����
				if(receivedMsg[1].equals("readynotenough")){
					JOptionPane.showMessageDialog(null, "��� �������� �ʾҽ��ϴ�!" , "���� ���� �Ұ���", JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					try {
						gcc.getOos().writeObject("startplayerinfo#"+wu.getPlayerinfo().getId()+"#"+wu.getPlayerinfo().getCharacter()+"#"+wu.getPlayerinfo().getMypos()+"#"+wu.getPlayerinfo().getScore()+"#"+ wu.getPlayerinfo().getTnum()+"#"+ wu.getPlayerinfo().getCornum()); //�� �ڸ��� ���ھ� �ѱ�
					}
					catch (Exception e) {
					}
					wu.getInfoText().setText(wu.getInfoText().getText() + "\n���� "+ receivedMsg[1]+" ���� ������ �����ϼ̽��ϴ�.");
				}

			}	
			else if(receivedMsg[0].equals( "waitaminute" )){ //������ ������ �� ��ϵɶ����� ��� ��� 	
				try {
					Thread.sleep(1000);
					gcc.getOos().writeObject("waitisover#"+wu.getPlayerinfo().getId());
					
				} 
				catch (Exception e) {
				}
			}
			else if(receivedMsg[0].equals( "reallygamestart" )){ //���� ��¥��¥ ����
				wu.getMP().stop(wu.getBgm());
				gui.setPosition(message);
				gui.setVisible(true);  
			}
			else if(receivedMsg[0].equals( "memberlist" )){ //ó�� �����Ҷ� ���� ���� �޾ƿ��� ��
				for(int i =1; i< receivedMsg.length-1; i++){
					if(i==receivedMsg.length-2)
						wu.getDefaultModel().addRow(new Object[]{"����", receivedMsg[i]});
					else
						wu.getDefaultModel().addRow(new Object[]{"", receivedMsg[i]});
				}
			}
			else if(receivedMsg[0].equals( "Success" )){ //�ٸ� ���� �α��� ������ �˸� // �ٸ� ��� ���̺� ����ϴ��۾��� �ؾ��� 
				if(!(receivedMsg[1].equals(wu.getMyinfo().getId()))){
					wu.getDefaultModel().addRow(new Object[]{"", receivedMsg[1]});
					cc.getJta().append(receivedMsg[1] +" ���� �����߽��ϴ�. "+ System.getProperty("line.separator"));//ä�ù濡 �˸�
					cc.getJta().setCaretPosition(cc.getJta().getDocument().getLength());  
				}
			}
			//ä��
			//�ΰ��� ä�� �Ѹ���
			else if(receivedMsg[0].equals( "ingame" )){
                gcc.getJta().append(receivedMsg[1] +" : "+receivedMsg[2]+ System.getProperty("line.separator"));
				gcc.getJta().setCaretPosition(gcc.getJta().getDocument().getLength());    
			}
			else if(receivedMsg[0].equals("thegamebegins" )){ //���� �Ѹ��� // ����, �ʼ�, �������, �Ѷ���
				
				gui.getProblabel().setText(receivedMsg[1]+"\n                   "
						+ "                                                  "+receivedMsg[2]);//���� ���� - Ÿ�̸� ������ �ȿ��� �ؾ��ҵ� 

				gui.getTroundlabel().setText(receivedMsg[4]);
				gui.getCroundlabel().setText(receivedMsg[3]);
				gui.getTableModel().setRowCount(1);
				gui.getStartTimer(this,gui, gcc.getOos(), receivedMsg[1], Integer.parseInt(receivedMsg[5])); //������ �ε����� �� ������  ���ѽð� 
					
			}
			else if(receivedMsg[0].equals("gameanswer" )){ //�� ���̵𰡾ƴϸ� ������ ��������!
				//�̹� �ش���̵��� ������ ������ ���� ��û
				int delrow =0;
				for(int i =1; i< gui.getTableModel().getRowCount(); i++){
					if(gui.getAnstable().getValueAt(i, 1).equals(gui.getPlayerinfo().getId())){ //��ϵ� ���̵� ������ ���� 
						//gui.getTableModel().removeRow(i);
						delrow = i;
						break;
					}
				}
				try {
					gcc.getOos().writeObject("deletethisrowfirst#"+Integer.toString(delrow)+"#"+receivedMsg[1]+"#"+receivedMsg[2]); //delrow�� 0�̸� ���ٴ� ��! ���� ���̵� ���䵵 �ٽ� �ǵ��������!1- �������� �ο� 2- ���� 3- ���̵�
				} catch (Exception e) {
				}
				
			}
			else if(receivedMsg[0].equals("gameanswerreg")){ 
				//�������� ������ ���� 
				if(!receivedMsg[3].equals("0"))
					gui.getTableModel().removeRow(Integer.parseInt(receivedMsg[3]));

				//���ĸ���

				//���� ���
				if(receivedMsg[2].equals(gui.getPlayerinfo().getId()))
					gui.getTableModel().addRow(new Object[]{ receivedMsg[1], receivedMsg[2]}); //�� ���� ���� 
				else {
					gui.getTableModel().addRow(new Object[]{ "???", receivedMsg[2]}); //�ٸ���� ���� ���� 
					wu.getMP().musicRepeat("sounds/SendAnswer.wav");
				}
				
				if(receivedMsg[2].equals(gui.getPlayerinfo().getId())) {
					System.out.println("���� �ۼ��� ���� : "+receivedMsg[1]);
					gui.setMyanswer(receivedMsg[1]);
				}
				
				//�� ���̵� �ִ� �ο� �˻� 
				int myorderintable=0;
				for(int i =1; i< gui.getTableModel().getRowCount(); i++){
					if(gui.getAnstable().getValueAt(i, 1).equals(gui.getPlayerinfo().getId())){ //��ϵ� ���̵� ������ ���� 
						myorderintable = i;
						break;
					}
				}
				gui.setMyorder(Integer.toString(myorderintable));
			}

			//Ÿ�̸� ������ ���� ���� (�ڰ� ä���� ��� ���� )
			else if(receivedMsg[0].equals("checkanswer" )){
				try {
					gui.getProblabel().setText("\n\n   "
							+ "                                                               ���� : " + receivedMsg[1]);	
					wu.getPlayerinfo().setTnum((wu.getPlayerinfo().getTnum()+1)); //Ǭ ���� ���� ++
					if(receivedMsg[1].equals(gui.getMyanswer())){
						System.out.println("����!");
						wu.getPlayerinfo().setCornum((wu.getPlayerinfo().getCornum()+1)); //���� ���� ���� ++
						gcc.getOos().writeObject("checkfromclient#correct#"+gui.getMyorder()+"#"+gui.getPlayerinfo().getId()+"#"+gui.getPlayerinfo().getMypos()); //0 - check , 1- correct, 2- �������, 3- ���̵�, 4 -�ڸ�
						gui.setMyanswer(""); //���� �ʱ�ȭ
						wu.getMP().musicRepeat("sounds/Succ.wav");
					}
					else{
						gcc.getOos().writeObject("checkfromclient#wrong#"+"0#"+gui.getPlayerinfo().getId()+"#"+gui.getPlayerinfo().getMypos()); //1 - wrong 2- 0�� ��  3 - ���̵� 4 - �ڸ�
						System.out.println("����..");
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
			//�������� �ο����� ���� �ؽ�Ʈ�� ǥ��
			else if(receivedMsg[0].equals("pointfromserver")){
				System.out.println(message);
				gui.setEachScore(message);
			}
			//���� ���� 
			else if(receivedMsg[0].equals("userexited")){
				//������ ������ ���̵� �ִ� �ο� �˻� 
				int userorderintable=0;
				for(int i =1; i< wu.getDefaultModel().getRowCount(); i++){
					if(wu.getDefaultModel().getValueAt(i, 1).equals(receivedMsg[1])){ //��ϵ� ���̵� ������ ���� 
						userorderintable = i;
						break;
					}
				}
				wu.getDefaultModel().removeRow(userorderintable); //���� 
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n"+ receivedMsg[1] +"���� �����ϼ̽��ϴ�.");
			}
			//�� ���� �޾Ƽ� ���â�� ���� �κ� 
			else if(receivedMsg[0].equals("givemethetotalresult")){
				
				//�� ���� ã�Ƽ� ���� ����Ʈ�� ���� 
				//���̵�, ��������, ������ ������ ����
				String[] pinfo = message.split( "#" );
				for(int j = 1; j <pinfo.length; j+=3) {
					if(wu.getPlayerinfo().getId().equals(pinfo[j])) {
						wu.getPlayerinfo().setScore(Integer.parseInt(pinfo[j+2])); //���� ���� ����
						break;
					}
				}
				//������ ���̵�, ��������, ��������, Ǭ�������� ���� 
				try {
					gcc.getOos().writeObject("updatemyinfo#"+wu.getPlayerinfo().getId()+"#"+wu.getPlayerinfo().getScore()+"#"+wu.getPlayerinfo().getTnum()+"#"+wu.getPlayerinfo().getCornum());
				} catch (IOException e) {
					e.printStackTrace();
				}
				ResultUI res = new ResultUI(gcc.getOis(), gcc.getOos(), wu.getPlayerinfo(), gui);
				res.setResultData(message);
				res.setVisible(true);
			}
			//���� ���� �� ���� ���� 
			else if(receivedMsg[0].equals("backtowaitroom")){
				//���� �ʱ�ȭ
				gui.resetIngameui();
				gcc.resetChat();
				gui.setVisible(false);
				gui.dispose();	
				wu.resetReady();
				wu.getInfoText().setText(wu.getInfoText().getText() + "\n���� "+ receivedMsg[1] +" ���� ���� ���͸� �����ϼ̽��ϴ�.");
			}
			else if(receivedMsg[0].equals("updatemyinfo")){ // 1- ���̵�, 2- ��������, 3- Ǭ����, 4 - ��������	 
				//�� ���� ���� ������Ʈ 
				wu.getStatlabel().setText(receivedMsg[2]+" ��");
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
	                cc.getJta().append(receivedMsg[2] +" �����κ����� �ӼӸ� : "+receivedMsg[3]+ System.getProperty("line.separator"));
					cc.getJta().setCaretPosition(cc.getJta().getDocument().getLength());   
				}
				if(receivedMsg[2].equals(wu.getPlayerinfo().getId())){
	                cc.getJta().append(receivedMsg[1] +" �Կ��� �ӼӸ� : "+receivedMsg[3]+ System.getProperty("line.separator"));
					cc.getJta().setCaretPosition(cc.getJta().getDocument().getLength());   
				}

            }
			else if(receivedMsg[0].equals("ingamesendto")){  //sendto#sendid#myid#msg
				System.out.println("�ΰ��� �ӼӸ�");
				System.out.println("receivedMsg[0] : "+receivedMsg[0] );
				if(receivedMsg[2].equals(wu.getPlayerinfo().getId())) {
					System.out.println("??");
	                gcc.getJta().append(receivedMsg[3] +" �����κ����� �ӼӸ� : "+receivedMsg[4]+ System.getProperty("line.separator"));
					gcc.getJta().setCaretPosition(gcc.getJta().getDocument().getLength());   
				}
				if(receivedMsg[3].equals(wu.getPlayerinfo().getId())){
	                gcc.getJta().append(receivedMsg[2] +" �Կ��� �ӼӸ� : "+receivedMsg[4]+ System.getProperty("line.separator"));
					gcc.getJta().setCaretPosition(gcc.getJta().getDocument().getLength());   
				}
            }
			else if(receivedMsg[0].equals("showranking")){ // ���̵�, ����, acc
				RankingUI ru = new RankingUI();
				ru.setRank(message);
				ru.setVisible(true);
			}
			
			//���� ä�� �Ѹ���
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
	Object conName[] = {"����", "ID"};
	String fontName [] = {"Plain", "Bold", "Arial"}; //�ѻ�
	private Font bPlain, bBold, bArial, bGothic; //�ѻ�
	
	MusicPlayer mp = new MusicPlayer();
	Clip bgm, btn, enter;
	public WaitUI (PlayerInfo myinfo, ObjectInputStream ois,ObjectOutputStream oos) {
		setBackground(SystemColor.info);
		this.ois = ois;
		this.oos = oos;
		this.myinfo = myinfo;
		this.id = myinfo.getId(); //���̵� �ְ� ����
		try {
			bgm = AudioSystem.getClip();
			btn= AudioSystem.getClip();
			enter = AudioSystem.getClip();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
		mp.music("sounds/back.wav", bgm); //�����
		
		setTitle("����â");

		// Layout
		gb = new GridBagLayout();
		getContentPane().setLayout (gb);
		gbc = new GridBagConstraints ();
		gbc.fill=GridBagConstraints.BOTH;
		
		// MenuBar
		mb = new JMenuBar ();
		minfo = new JMenu("����");
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
		buttonBorder = new TitledBorder(new LineBorder(Color.black), "�ɼ� ����");
		waitBorder = new TitledBorder(new LineBorder(Color.black), "������ ����Ʈ");
		infoBorder = new TitledBorder(new LineBorder(Color.black), "�� ����");

		//Setting the Font �ѻ�
		bPlain = new Font ("Sans", Font.PLAIN, 10);
		bBold = new Font ("Sans", Font.BOLD, 17);

		//Setting the Font Style �ѻ�
		bArial = new Font ("Arial", Font.PLAIN, 10);

		
		// ProblemType
		btCommonsense = new JToggleButton ("���");
		btNonsense = new JToggleButton ("�ͼ���");
		btCommonsense.setBackground(new Color(0,34,102)); //�ѻ�
		btCommonsense.setForeground(new Color (234,234,234)); //�ѻ�
		btNonsense.setBackground(new Color(0,34,102)); //�ѻ�
		btNonsense.setForeground(new Color (234,234,234)); //�ѻ�


		bgM =new ButtonGroup();
		bgM.add(btCommonsense);
		bgM.add(btNonsense);
		btCommonsense.setFont(bBold); //�ѻ�
		btNonsense.setFont(bBold); //�ѻ�

		
		optPanel = new JPanel();
		optPanel.setLayout(new GridLayout(0,2,10,10));
		optPanel.setBorder(buttonBorder);

		optPanel.add(btCommonsense);
		optPanel.add(btNonsense);

		// GameType
		btTeam = new JButton ("���� �� ���� (�⺻ : 4��)");
		btIndi = new JButton ("���� Ÿ�̸� ���� (�⺻ : 15��)");
		btTeam.setBackground(new Color(0,34,102)); //�ѻ�
		btTeam.setForeground(new Color (234,234,234)); //�ѻ�
		btIndi.setBackground(new Color(0,34,102)); //�ѻ�
		btIndi.setForeground(new Color (234,234,234)); //�ѻ�


		bgT = new ButtonGroup();
		bgT.add(btTeam);
		bgT.add(btIndi);
		btTeam.setFont(bBold); //�ѻ�
		btIndi.setFont(bBold); //�ѻ�

		optPanel.add(btTeam);
		optPanel.add(btIndi);
		
		// Ready or Start
		btReady = new JToggleButton ("READY"); 
		btStart = new JButton ("START"); 

		bgS = new ButtonGroup();
		bgS.add(btReady);
		bgS.add(btStart);
		btReady.setFont(bBold); //�ѻ�
		btStart.setFont(bBold); //�ѻ�
		btReady.setForeground(new Color (0,34,102)); //���ѻ�
		btStart.setForeground(Color.RED); //���ѻ�
		optPanel.add(btReady);
		optPanel.add(btStart);
		
		gbAdd(optPanel, 1,1,1,1,1.0,3.0);
		//������ ready�Ұ���, �������� ready�� ����
		if(myinfo.getAuth()==true) {//����
			btReady.setEnabled(false);
		}
		else{//������
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
		laChat = new JLabel ("ä��â");

		// �׹浵 �̸� ����� ����



		try {// Ŭ���̾�Ʈ ������� ä�ù� ���� 
			//setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("back.jpg")))));
			GameChatClient gcc = new GameChatClient( "0.0.0.0 - ������", id , ois, oos);
			InGameUI gui = new InGameUI(myinfo, ois, oos, gcc);
	
			ChatClient cc = new ChatClient( "0.0.0.0 - ������", id , ois, oos);
			gbAdd (cc,1,2,1,1,1.0,1.0);

			MultiClientThread ct = new MultiClientThread(this, cc, gui, gcc);
			Thread t = new Thread( ct );
			t.start();

			//gui.setVisible(true);
		} catch (Exception e ) {
			System.out.println("������");
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
		laMyStat = new JLabel ("�������� : ");
		laMyStat.setFont(f);
		laStatSlot = new JLabel(myinfo.getScore() +" ��");
		laStatSlot.setFont(f);
		laMyAcc = new JLabel ("����� : ");
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
		btExit.setForeground(Color.RED); //���ѻ�
		btExit.setFont(bBold); //���ѻ�
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

	} // ������

	public void actionPerformed(ActionEvent e) {
		mp.musicRepeat("sounds/BtnClick.wav");
		try {
			if(e.getSource().equals(btCommonsense)) { //��Ĺ�ư
				oos.writeObject("commonsense#" + id );
			}
			else if(e.getSource().equals(btNonsense)) { //�ͼ�����ư
				System.out.println("������ �ͼ��� ����");
				oos.writeObject("nonsense#" + id );
			}
			else if(e.getSource().equals(btTeam)) { //���� �� ����
				InputWindow iw = new InputWindow(2, ois, oos, id);
				iw.setVisible(true);
			}
			else if(e.getSource().equals(btIndi)) { //���� Ÿ�̸� ����
				InputWindow iw = new InputWindow(1, ois, oos, id);
				iw.setVisible(true);
			
			}
			else if(e.getSource().equals(btReady)) { //�����ư
				if(ready == false)
					oos.writeObject("readytoplay#" + id );
				else
					getInfoText().setText(getInfoText().getText() + "\n"+ "�̹� ���� �ϼ̽��ϴ�!");
			}
			else if(e.getSource().equals(btStart)) { //���ӽ��۹�ư - �ο� ������ �� ���ϰ� ���ƾߴ� 
			
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
			oos.writeObject("getMemberList");//���� �ɹ� ����Ʈ ��
		} catch (Exception e) {
		}
		
	}

	public void setTableData(String auth ,String id){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		try {
			oos.writeObject("getMemberList");//���� �ɹ� ����Ʈ ��
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
	
	public ImageIcon getScaledImage(ImageIcon srcImg, int row, int col){ // �̹��� ������¡
	
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
		WaitUI wui = new WaitUI ("���ھ�");
		try {
			wui.init();
		} catch (Exception e) {
		}
	
	}
	*/
}
