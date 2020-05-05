
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MainPanel extends JFrame{
	//GridBagConstraints c[] = new GridBagConstraints();
	private JPanel gamePanel, optionPanel;
	private DefaultListModel<String> model;
	private JList<String> pList;
	private JButton teammode;
	
	public MainPanel(){
		setLayout(new FlowLayout());
		model = new DefaultListModel<>();
		pList = new JList(model);
		add(pList);
		
		
		setBounds(200,300,1000,1000);
		//setVisible(true);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public void addMember(String m ){
		model.addElement(m);
	}
	public static void main(String[] args){
		new MainPanel();
	}
}

