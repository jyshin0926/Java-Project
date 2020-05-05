import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class NetConn implements Serializable{
	private ObjectInputStream ois;
    private ObjectOutputStream oos;
	private Socket socket;

	public NetConn(ObjectInputStream ois, ObjectOutputStream oos){
		this.ois = ois;
		this.oos = oos;
	}

	public void idpsCheck(String id, String ps, LoginUI l) throws IOException {
		try{
			if(id.equals("")|| ps.equals("")) {
				JOptionPane.showMessageDialog(null, "��ĭ�� ��� ä���ּ���", "�� ���" , JOptionPane.WARNING_MESSAGE);
			}
			else {
				oos.writeObject("idcheck#"+id+"#"+ps);// ������ ���̵� ��й�ȣ ����
				
				String retmsg = (String) ois.readObject(); //���������� ���ϰ�
	
				String[] str = retmsg.split("#");
				if(str[0].equals("Success")) {
					System.out.println("Success");
					PlayerInfo pi = UserinfoToPlayerinfo(str);// �������� �� userinfo data�� �Ű������� �ѱ�
					WaitUI wu = new WaitUI(pi, ois, oos);//���â ����鼭 �ڱ� ���� �ѱ涧 playerinfo ��ü�� ���� �ѱ�
					if(pi.getAuth() == true)
						wu.setTableData("����",pi.getId()); //���̵� ������ ���̺� ���  - ���?
					else
						wu.setTableData(pi.getId());
					wu.setVisible(true); //���â ����	
					l.setVisible(false);
					
				}
				else if(retmsg.equals("Failed")) {
					System.out.println("Failed");
					JOptionPane.showMessageDialog(null, "��й�ȣ�� �ٽ� Ȯ���ϼ���", "�߸��� ��й�ȣ" , JOptionPane.WARNING_MESSAGE);
				}
				else if(retmsg.equals("No ID")) {
					System.out.println("No ID");
					JOptionPane.showMessageDialog(null, "���̵� �����ϴ�. ȸ������ �� �̿��ϼ���" ,"ID ����" , JOptionPane.WARNING_MESSAGE);
				}
				else{
					JOptionPane.showMessageDialog(null, "��ĭ�� ��� ä���ּ���", "�� ���" , JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
    }

	public void registerCheck(String name, String id, String ps, String bth) throws IOException{
		try {
			System.out.println("??1");
			oos.writeObject("regcheck#"+name+"#"+id+"#"+ps+"#"+bth);// ������ �̸� ���̵� ��й�ȣ ���� ����
			System.out.println("??");
			String retmsg = (String) ois.readObject(); //���������� ���ϰ�

			if(retmsg.equals("regSuccess")){// ���̵� �ߺ�  ������	
				System.out.println("���̵� ��� ����");
				JOptionPane.showMessageDialog(null, "ȸ�� ���� �Ϸ�!", "�����մϴ�" , JOptionPane.WARNING_MESSAGE);
			}
			else if(retmsg.equals("regFailed")) {// ���̵� �ߺ��Ǹ� 
				System.out.println("�ߺ��Ǵ� ���̵� ����");
				JOptionPane.showMessageDialog(null, "���̵� �ߺ��˴ϴ�!", "���̵� �ߺ�" , JOptionPane.WARNING_MESSAGE);
			}
			
		} 
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void idFind(String name, String bth) throws IOException{
		try {
			oos.writeObject("idfind#"+name+"#"+bth);// ������ �̸� ���� ����
			String retmsg = (String) ois.readObject(); //���������� ���ϰ�
			String tmp[] = retmsg.split( "#" );
			if(tmp[0].equals("findSuccess")){
				JOptionPane.showMessageDialog(null, "�ش� ������ ��ϵ� ���̵�� "+ tmp[1] +" �Դϴ�." , "����� ã�ҽ��ϴ�!", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(tmp[0].equals("findFailed")){
				JOptionPane.showMessageDialog(null, "�ش� ������ ��ϵ� ���̵� �����ϴ�." , "����� �����ϴ�", JOptionPane.WARNING_MESSAGE);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void psFind(String id) throws IOException{
		try {
			oos.writeObject("psfind#"+id);// ������ �̸� ���� ����
			String retmsg = (String) ois.readObject(); //���������� ���ϰ�
			String tmp[] = retmsg.split( "#" );
			if(tmp[0].equals("findSuccess")){
				JOptionPane.showMessageDialog(null, "�ش� ID �� ��й�ȣ�� "+ tmp[1] +" �Դϴ�." , "����� ã�ҽ��ϴ�", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(tmp[0].equals("findFailed")){
				JOptionPane.showMessageDialog(null, "�ش� ID�� ã�� �� �����ϴ�." , "����� �����ϴ�", JOptionPane.WARNING_MESSAGE);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public PlayerInfo UserinfoToPlayerinfo(String[] str){ //�α��� ������ ������ �÷��̾� ���� �����Ϳ� ��� �޼ҵ� 
		PlayerInfo pi = new PlayerInfo();
		pi.setId(str[1]);
		pi.setScore(Integer.parseInt(str[2]));
		pi.setTnum(Integer.parseInt(str[3]));
		pi.setCornum(Integer.parseInt(str[4]));
		pi.setChattype(Integer.parseInt(str[5]));
		pi.setAcc(Double.parseDouble(str[6]));
		
		if(str[7].equals("false"))
			pi.setAuth(false);
		else
			pi.setAuth(true);

		pi.setCharacter(str[8]);
		pi.setMypos(str[9]);

		return pi;
	}

}
