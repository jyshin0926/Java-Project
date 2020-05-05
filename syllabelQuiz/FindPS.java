
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class FindPS extends JFrame implements ActionListener{
	private JTextField regid, regname, regbirth;
	private JButton find;
	private JLabel id, name, birth;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	public FindPS(ObjectInputStream ois, ObjectOutputStream oos){
		this.ois = ois;
		this.oos = oos;
		
		setLayout(new FlowLayout());
		getContentPane().setBackground( Color.white);
		regid  = new JTextField(15);
		regname  = new JTextField(15); 
		regbirth  = new JTextField(15); 
		id = new JLabel("아이디"); 
		name = new JLabel("이름"); 
		birth = new JLabel("생년월일 (ex: 930628)"); 
		find = new JButton("비밀번호 찾기");
		add(id);
		add(regid);
		add(name); 
		add(regname); 
		add(birth);
		add(regbirth); 
		add(find);

		find.addActionListener(this);
		setBounds(700,200,200,270); 
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(find)){
			try {
				NetConn nc = new NetConn(ois, oos);
				nc.psFind(regid.getText());
			} 
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


}
