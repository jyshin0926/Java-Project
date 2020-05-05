
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.border.*;

class RankingUI extends JFrame {
	JPanel panel = new JPanel();
	JPanel jpScore,jpAcc; //ÇÑ»ù
	JTabbedPane tab; //ÇÑ»ù
	JTable jtScore,jtAcc; //ÇÑ»ù
	DefaultTableModel model1,model2;
	Object data[][] = {};
	Object conName[] = {"Rank","ID","Score"};
	Object data2[][] = {};
	Object conName2[] = {"Rank","ID","Acc"};
	public RankingUI(){
		super("Ranking");	
		
		//tab ÇÑ»ù
		tab = new JTabbedPane (JTabbedPane. TOP);

		//panel,table ÇÑ»ù
		jpScore = new JPanel();
		jpAcc = new JPanel();

		tab.addTab("´©ÀûÆ÷ÀÎÆ®", jpScore); //ÇÑ»ù
		tab.addTab("´©ÀûÁ¤´ä·ü", jpAcc); //ÇÑ»ù

		//table
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.RIGHT );

		model1 = new DefaultTableModel(data, conName){
				public Class getColumnClass(int column)
				{
					return getValueAt(0, column).getClass();
				}
			};


		jtScore = new JTable (model1); //ÇÑ»ù
		jpScore.add(new JScrollPane(jtScore)); //ÇÑ»ù
 

		model2 = new DefaultTableModel(data2, conName2){
            public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
        };
		
		jtAcc = new JTable (model2); //ÇÑ»ù
		jpAcc.add(new JScrollPane (jtAcc)); //ÇÑ»ù

		jtScore.setDefaultRenderer(String.class, centerRenderer); 
		jtAcc.setDefaultRenderer(String.class, centerRenderer); 

		getContentPane().add(tab, BorderLayout.CENTER);
		setBounds(1000,300,500,530); //´ºÇÑ»ù
		setVisible(true);
		setResizable(false);
	
		}//constructor


	public void setRank(String msg){//±¸ºÐÀÚ+  ¾ÆÀÌµð, ´©Àû, acc

		//µ¥ÀÌÅÍ ¼¼ÆÃÇÏ´Â ÀÛ¾÷
		String[] pinfo = msg.split( "#" );
		int rank=1;
		int max= Integer.parseInt(pinfo[2]);
		int idx =2;
		String id =pinfo[1];
		System.out.println(msg);
		//showrank # dfd # 80 # 50.5 # insect # 120 # 33.3 #
		//´©Àû Á¡¼ö ·©Å·
		for(int j = 1; j <pinfo.length; j+=3) {
			for(int i=2; i<pinfo.length; i+=3) {
				if(max < Integer.parseInt(pinfo[i])) {
					max = Integer.parseInt(pinfo[i]);	
					id = pinfo[i-1];
					idx = i;
				}
			}
			model1.addRow(new Object[]{ rank, id, max});
			rank++;
			pinfo[idx] = "-2";
			max = -1;
		}

		rank = 1;
		double max1 = Double.parseDouble(pinfo[3]);
		idx = 3;
		//Á¤´ä·ü ·©Å·
		for(int j = 1; j <pinfo.length; j+=3) {
			for(int i=3; i<pinfo.length; i+=3) {
				if(max1 <  Double.parseDouble(pinfo[i])) {
					max1 =  Double.parseDouble(pinfo[i]);
					id = pinfo[i-2];
					idx = i;
				}
			}
			model2.addRow(new Object[]{rank, id, max1});
			rank++;
			pinfo[idx] = "-2.0";
			max1 = -1.0;
		}	
	}
	public static void main(String[] args) {
		new RankingUI();
	}
}
