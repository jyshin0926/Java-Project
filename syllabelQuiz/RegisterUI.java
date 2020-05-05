
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class RegisterUI extends JFrame implements ActionListener{
	private JTextField regname, regid , regps, regbth;	
	private JButton done, reset;
	private JLabel name, id, ps, bth;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	public RegisterUI(ObjectInputStream ois, ObjectOutputStream oos) {
		this.ois = ois;
		this.oos = oos;
		
		setLayout(new FlowLayout());
		getContentPane().setBackground( Color.white);
		name = new JLabel("아이디     "); //한샘
		id = new JLabel("이름    "); //한샘
		ps = new JLabel("비밀번호"); //한샘
		bth = new JLabel("생일 (ex: 930628)   "); //한샘


		regname = new JTextField(15);
		regid = new JTextField(15);
		regps = new JTextField(15);
		regbth = new JTextField(15);

		done = new JButton("회원가입"); //한샘
		done.setBounds(160,375,60,50); //한샘

		reset = new JButton("초기화");
		reset.setBounds(270,375,60,50);

	
		add(name);
		add(regname);
		add(bth);
		add(regbth);
		add(id);
		add(regid);
		add(ps);
		add(regps);
		add(done);
		add(reset);

		getRootPane().setDefaultButton(done);
		reset.addActionListener(this);
		done.addActionListener(this);
		
		setBounds(1000,300,230,328); //뉴한샘

		
	}
	
	public void actionPerformed(ActionEvent e) { // 회원 등록
		if(e.getSource().equals(done)) {
			NetConn nc = new NetConn(ois, oos);
			if(regid.getText().equals("") || regps.getText().equals("")){
				System.out.println("양식을 다 채워주세요");
				JOptionPane.showMessageDialog(null, "양식을 다 채워주세요", "빈 양식" , JOptionPane.WARNING_MESSAGE);
			}
			else{
				try{
					//System.out.println(regname.getText() +regid.getText() + regps.getText() + regbth.getText());
					nc.registerCheck(regname.getText() ,regid.getText(), regps.getText(), regbth.getText());
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		else if(e.getSource().equals(reset)) {
			regid.setText("");
			regps.setText("");
			regname.setText("");
			regbth.setText("");
		}
	}

	// 아이디 ps 제한 메소드
	public int restrictInfo(){
		return 1;
	}
}
