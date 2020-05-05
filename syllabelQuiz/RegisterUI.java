
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
		name = new JLabel("���̵�     "); //�ѻ�
		id = new JLabel("�̸�    "); //�ѻ�
		ps = new JLabel("��й�ȣ"); //�ѻ�
		bth = new JLabel("���� (ex: 930628)   "); //�ѻ�


		regname = new JTextField(15);
		regid = new JTextField(15);
		regps = new JTextField(15);
		regbth = new JTextField(15);

		done = new JButton("ȸ������"); //�ѻ�
		done.setBounds(160,375,60,50); //�ѻ�

		reset = new JButton("�ʱ�ȭ");
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
		
		setBounds(1000,300,230,328); //���ѻ�

		
	}
	
	public void actionPerformed(ActionEvent e) { // ȸ�� ���
		if(e.getSource().equals(done)) {
			NetConn nc = new NetConn(ois, oos);
			if(regid.getText().equals("") || regps.getText().equals("")){
				System.out.println("����� �� ä���ּ���");
				JOptionPane.showMessageDialog(null, "����� �� ä���ּ���", "�� ���" , JOptionPane.WARNING_MESSAGE);
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

	// ���̵� ps ���� �޼ҵ�
	public int restrictInfo(){
		return 1;
	}
}
