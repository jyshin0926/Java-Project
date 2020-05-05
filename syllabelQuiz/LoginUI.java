import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;


public class LoginUI extends JFrame implements ActionListener{
	private ImageIcon imlogo;
	private JTextField id;
	private JPasswordField ps;
	private JLabel lalogo, laid, lapasswd;
	private JButton login, register, findid, findps; 
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	public LoginUI(){
		getContentPane().setBackground( Color.white);
		imlogo = new ImageIcon ("./logo1.png"); //한샘
		lalogo = new JLabel(imlogo); //한샘
		laid = new JLabel ("아이디     "); //한샘
		lapasswd = new JLabel ("비밀번호 "); //한샘

		id = new JTextField(13);
		ps = new JPasswordField(13);
		ps.setEchoChar('*');
		login = new JButton("로그인");
		register = new JButton("회원 가입");
		findid = new JButton("아이디 찾기");
		findps = new JButton("비번 찾기");
		setLayout(new FlowLayout());
		add(lalogo); //한샘
		add(laid); //한샘i
		add(id);
		add(lapasswd); //한샘
		add(ps);
		add(login);
		add(register);
		add(findid);
		add(findps);

		login.addActionListener(this);
		register.addActionListener(this);
		findid.addActionListener(this);
		findps.addActionListener(this);


		getRootPane().setDefaultButton(login);
		setBounds(700,300,250,360); // 뉴한샘
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//네트워크 바로연결
		try{
			socket = new Socket( "localhost" , 3000 );			// ip
			System.out.println( "클라이언트 - 서버 연결" );
			oos = new ObjectOutputStream( socket.getOutputStream() );
			ois = new ObjectInputStream( socket.getInputStream() );
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(login)) {
			try{
				NetConn nc = new NetConn(ois, oos);
				String decrpytedps = new String(ps.getPassword()); //비밀번호 해독 
				nc.idpsCheck(id.getText(), decrpytedps, this); //아이디 비밀번호 netconn으로 넘김 . 넘겨서 처리함
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if(e.getSource().equals(register)) { //회원 가입
			RegisterUI rui = new RegisterUI(ois, oos);
			rui.setVisible(true);
		}
		else if(e.getSource().equals(findid)) { //아이디 찾기
			FindID fid = new FindID(ois, oos);
			fid.setVisible(true);
		}
		else if(e.getSource().equals(findps)) { //비밀번호 찾기
			FindPS fps = new FindPS(ois, oos);
			fps.setVisible(true);
		}
	}
	
	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); 
          }
          catch (Exception e) {
            e.printStackTrace();
          }      
		LoginUI l = new LoginUI();
	}
}
