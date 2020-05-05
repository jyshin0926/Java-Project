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
		imlogo = new ImageIcon ("./logo1.png"); //�ѻ�
		lalogo = new JLabel(imlogo); //�ѻ�
		laid = new JLabel ("���̵�     "); //�ѻ�
		lapasswd = new JLabel ("��й�ȣ "); //�ѻ�

		id = new JTextField(13);
		ps = new JPasswordField(13);
		ps.setEchoChar('*');
		login = new JButton("�α���");
		register = new JButton("ȸ�� ����");
		findid = new JButton("���̵� ã��");
		findps = new JButton("��� ã��");
		setLayout(new FlowLayout());
		add(lalogo); //�ѻ�
		add(laid); //�ѻ�i
		add(id);
		add(lapasswd); //�ѻ�
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
		setBounds(700,300,250,360); // ���ѻ�
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//��Ʈ��ũ �ٷο���
		try{
			socket = new Socket( "localhost" , 3000 );			// ip
			System.out.println( "Ŭ���̾�Ʈ - ���� ����" );
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
				String decrpytedps = new String(ps.getPassword()); //��й�ȣ �ص� 
				nc.idpsCheck(id.getText(), decrpytedps, this); //���̵� ��й�ȣ netconn���� �ѱ� . �Ѱܼ� ó����
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else if(e.getSource().equals(register)) { //ȸ�� ����
			RegisterUI rui = new RegisterUI(ois, oos);
			rui.setVisible(true);
		}
		else if(e.getSource().equals(findid)) { //���̵� ã��
			FindID fid = new FindID(ois, oos);
			fid.setVisible(true);
		}
		else if(e.getSource().equals(findps)) { //��й�ȣ ã��
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
