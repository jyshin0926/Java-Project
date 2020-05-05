
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;
public class FindID extends JFrame implements ActionListener{
	private JTextField regname, regbth;
	private JButton find;
	private JLabel name, bth;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	public FindID(ObjectInputStream ois, ObjectOutputStream oos){
		this.ois = ois;
		this.oos = oos;
		
		setLayout(new FlowLayout());
		getContentPane().setBackground( Color.white);
		regname = new JTextField(15);
		regbth = new JTextField(15);
		name = new JLabel("          �̸�           ");
		bth = new JLabel("������� (ex: 930628)"); 
		find = new JButton("���̵� ã��"); 
		
		add(name);
		add(regname);
		add(bth);
		add(regbth);
		add(find);
		
		find.addActionListener(this);
		setBounds(700,200,230,210); //���ѻ�
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(find)){
			try {
				NetConn nc = new NetConn(ois, oos);
				nc.idFind(regname.getText(), regbth.getText());
			} 
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


}
