import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

class TeamSelectUI extends JFrame implements ActionListener {
	private JList liAteam, liBteam;
	private JButton btTmchange, btSelect;
	private DefaultListModel modelA, modelB;
	TitledBorder aBorder, bBorder;
	private String id;

	public TeamSelectUI (String myId) {
		this.id = myId;
		setTitle("�� ����");
		setLayout(null);
		modelA = new DefaultListModel();
		modelB = new DefaultListModel();

		//Ateam
		liAteam = new JList(modelA);
		liAteam.setBounds ( 20, 30, 130, 200 );
		add(liAteam);

		//TeamChange 
		btTmchange = new JButton("������");
		btTmchange.setBounds ( 168, 100, 60, 70 );
		add(btTmchange);

		//Bteam
		liBteam = new JList (modelB);
		liBteam.setBounds ( 245, 30, 130, 200 );
		add(liBteam);

		//SelectComplete
		btSelect = new JButton("���ÿϷ�");
		btSelect.setBounds ( 100, 270, 200, 50 );
		add(btSelect);
		
		//Border
		aBorder = new TitledBorder(new LineBorder(Color.blue), "A��");
		bBorder = new TitledBorder(new LineBorder(Color.red), "B��");
		liAteam.setBorder(aBorder);
		liBteam.setBorder(bBorder);
		// Window 
		setResizable (false);
		setBounds ( 300, 50, 400, 400 );
		setVisible (true);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	
	

	addWindowListener(
		new WindowAdapter(){
			public void windowClosing( WindowEvent e ) {
				System.exit(0);
			}
		}	
	);

	// ActionEvent ������
	btTmchange.addActionListener(this);
	btSelect.addActionListener(this);

} // ������
	
	public void actionPerformed ( ActionEvent e ) {
		if ( e.getSource().equals(btSelect)){
			setVisible(false); //you can't see me!
			dispose();  //destroy the JFrame object
		} else if(e.getSource().equals(btTmchange)){
			if(modelA.contains(id)){
				int index = modelA.indexOf(id);
				modelA.removeElementAt(index);
				modelB.addElement(id);
				
			}
			else{
				int index = modelB.indexOf(id);
				modelB.removeElementAt(index);
				modelA.addElement(id);
			}
		}	
	}

	public void init (){//â �� ������� �� �ҷ��ͼ� A���� �ֱ� 
		modelA.addElement(id);
		modelA.addElement("dd");
		System.out.println("�ε��Ҷ� ������� ���ҿ���");
	}

	public static void main (String [ ] args) {
		TeamSelectUI tu = new TeamSelectUI("��������������");
		tu.init();

	}
}


