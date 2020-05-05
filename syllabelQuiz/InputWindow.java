import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;


public class InputWindow extends JFrame implements ActionListener {
	private JTextField input;
	private JButton reg;
	private int roundnum =10;
	private int roundtime =10;
	private int flag = 0;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String id;
	public InputWindow(int flag, ObjectInputStream ois, ObjectOutputStream oos , String id){
		this.ois = ois;
		this.oos = oos;
		this.flag = flag;
		this.id = id;
		getContentPane().setBackground( Color.white);
		setLayout(new FlowLayout());
		input = new JTextField(15);
		reg = new JButton("등록");
		add(input);
		add(reg);

		input.addActionListener(this);
		reg.addActionListener(this);
		
		getRootPane().setDefaultButton(reg);
		setBounds(700,300,250,200);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(reg) && flag ==1) {
			
			try {
				roundtime = Integer.parseInt(input.getText());
				oos.writeObject("individualgame#" +id +"#"+Integer.toString(getRoundtime()));			
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "정수 값을 입력해 주세요" , "잘못된 값", JOptionPane.WARNING_MESSAGE);
			}
			dispose();
			
		}
		else if(e.getSource().equals(reg) && flag ==2){
			
			try {
				roundnum = Integer.parseInt(input.getText());
				oos.writeObject("teamgame#" + id+"#"+Integer.toString(getRoundnum()));
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "정수 값을 입력해 주세요" , "잘못된 값", JOptionPane.WARNING_MESSAGE);
			}
			dispose();
		}
	}
	public int getRoundtime(){
		return roundtime;
	}
	public int getRoundnum(){
		return roundnum;
	}
}
