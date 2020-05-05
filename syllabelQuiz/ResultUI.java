
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import javax.swing.table.*;
import javax.swing.border.*;

public class ResultUI extends JFrame implements ActionListener{

	private JTable table;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private JPanel buttonPanel;
	private JButton restart, back, exit;
	private JToggleButton ready;
	DefaultTableModel model;
	Object colName[] = {"���", "���̵�", " ȹ������", "�� ���� ����"};
	Object[][] resultData = {};
	PlayerInfo pi;
	InGameUI gui;
	public ResultUI(ObjectInputStream ois,ObjectOutputStream oos, PlayerInfo pi, InGameUI gui){
		this.ois = ois;
		this.oos = oos;
		this.pi = pi;
		this.gui = gui;
		setTitle("��� â");
		setLayout(new GridLayout(0,1));
		restart = new JButton("�����");
		ready = new JToggleButton("READY");
		back = new JButton("���Ƿ� ����");
		exit = new JButton("���� ����");	
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,2));
		buttonPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(40, 40, 40, 40), new EtchedBorder()));

		
		model = new DefaultTableModel(resultData, colName){
            public Class getColumnClass(int column){
                return getValueAt(0, column).getClass();
            }
			public boolean isCellEditable(int i, int c) {
                return false;
            }
        };
        
		table = new JTable(model);
		add(new JScrollPane(table));
		restart.setPreferredSize(new Dimension(100, 100));
		ready.setPreferredSize(new Dimension(100, 100));
		back.setPreferredSize(new Dimension(100, 100));
		exit.setPreferredSize(new Dimension(100, 100));
	

		buttonPanel.add(back);
		buttonPanel.add(exit);
	
		add(buttonPanel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.RIGHT ); 
		table.setDefaultRenderer(String.class, centerRenderer); 
		restart.addActionListener(this);
		ready.addActionListener(this);
		back.addActionListener(this);
		exit.addActionListener(this);
	
		setSize(400,400);
		setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource().equals(back)) { //���� ���� ��ư 
				oos.writeObject("backtowaitroom#"+ pi.getId());
				//���� ���鼭 �����ؾߵ� �͵� 
				model.setRowCount(0); //���â ����
				this.dispose();
			}
			else if(e.getSource().equals(exit)) { //�����ư
				oos.writeObject("userwantstoexit#" + pi.getId() );	
				model.setRowCount(0);
				System.exit(0);
			}
		}
		catch(Exception ex) {
			
		}
		
	}
	

	public void setResultData(String res){ //�ڸ�, ���̵�, ��������, ������ ������ ����
		//��� ������ �����ϴ� �۾�
		String[] pinfo = res.split( "#" );
		System.out.println(res);
		int rank=1;
		int max= Integer.parseInt(pinfo[2]);
		int score= Integer.parseInt(pinfo[3]);
		int idx =2;
		String id =pinfo[1];
		//givemethetotalresult # dfd # 80 # 120 # insect # 0 # 0 #
		for(int j = 1; j <pinfo.length; j+=3) {
			for(int i=2; i<pinfo.length; i+=3) {
				if(max < Integer.parseInt(pinfo[i])) {
					max = Integer.parseInt(pinfo[i]);
					score = Integer.parseInt(pinfo[i+1]);
					id = pinfo[i-1];
					idx = i;
				}
			}
			model.addRow(new Object[]{ rank, id, max, score});
			rank++;
			pinfo[idx] = "-2";
			max = -1;
		}
	}
	public static void main(String[] args) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		PlayerInfo pi = null;
		//new ResultUI(ois, oos, pi);
	}



	
}
