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
				JOptionPane.showMessageDialog(null, "빈칸을 모두 채워주세요", "빈 양식" , JOptionPane.WARNING_MESSAGE);
			}
			else {
				oos.writeObject("idcheck#"+id+"#"+ps);// 서버에 아이디 비밀번호 보냄
				
				String retmsg = (String) ois.readObject(); //서버에서의 리턴값
	
				String[] str = retmsg.split("#");
				if(str[0].equals("Success")) {
					System.out.println("Success");
					PlayerInfo pi = UserinfoToPlayerinfo(str);// 서버에서 온 userinfo data를 매개변수로 넘김
					WaitUI wu = new WaitUI(pi, ois, oos);//대기창 만들면서 자기 정보 넘길때 playerinfo 객체로 만들어서 넘김
					if(pi.getAuth() == true)
						wu.setTableData("방장",pi.getId()); //아이디만 던져서 테이블에 등록  - 사용?
					else
						wu.setTableData(pi.getId());
					wu.setVisible(true); //대기창 진입	
					l.setVisible(false);
					
				}
				else if(retmsg.equals("Failed")) {
					System.out.println("Failed");
					JOptionPane.showMessageDialog(null, "비밀번호를 다시 확인하세요", "잘못된 비밀번호" , JOptionPane.WARNING_MESSAGE);
				}
				else if(retmsg.equals("No ID")) {
					System.out.println("No ID");
					JOptionPane.showMessageDialog(null, "아이디가 없습니다. 회원가입 후 이용하세요" ,"ID 누락" , JOptionPane.WARNING_MESSAGE);
				}
				else{
					JOptionPane.showMessageDialog(null, "빈칸을 모두 채워주세요", "빈 양식" , JOptionPane.WARNING_MESSAGE);
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
			oos.writeObject("regcheck#"+name+"#"+id+"#"+ps+"#"+bth);// 서버에 이름 아이디 비밀번호 생일 보냄
			System.out.println("??");
			String retmsg = (String) ois.readObject(); //서버에서의 리턴값

			if(retmsg.equals("regSuccess")){// 아이디 중복  없으면	
				System.out.println("아이디 등록 성공");
				JOptionPane.showMessageDialog(null, "회원 가입 완료!", "축하합니다" , JOptionPane.WARNING_MESSAGE);
			}
			else if(retmsg.equals("regFailed")) {// 아이디 중복되면 
				System.out.println("중복되는 아이디가 있음");
				JOptionPane.showMessageDialog(null, "아이디가 중복됩니다!", "아이디 중복" , JOptionPane.WARNING_MESSAGE);
			}
			
		} 
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void idFind(String name, String bth) throws IOException{
		try {
			oos.writeObject("idfind#"+name+"#"+bth);// 서버에 이름 생일 보냄
			String retmsg = (String) ois.readObject(); //서버에서의 리턴값
			String tmp[] = retmsg.split( "#" );
			if(tmp[0].equals("findSuccess")){
				JOptionPane.showMessageDialog(null, "해당 정보로 등록된 아이디는 "+ tmp[1] +" 입니다." , "결과를 찾았습니다!", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(tmp[0].equals("findFailed")){
				JOptionPane.showMessageDialog(null, "해당 정보로 등록된 아이디가 없습니다." , "결과가 없습니다", JOptionPane.WARNING_MESSAGE);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void psFind(String id) throws IOException{
		try {
			oos.writeObject("psfind#"+id);// 서버에 이름 생일 보냄
			String retmsg = (String) ois.readObject(); //서버에서의 리턴값
			String tmp[] = retmsg.split( "#" );
			if(tmp[0].equals("findSuccess")){
				JOptionPane.showMessageDialog(null, "해당 ID 의 비밀번호는 "+ tmp[1] +" 입니다." , "결과를 찾았습니다", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(tmp[0].equals("findFailed")){
				JOptionPane.showMessageDialog(null, "해당 ID를 찾을 수 없습니다." , "결과가 없습니다", JOptionPane.WARNING_MESSAGE);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public PlayerInfo UserinfoToPlayerinfo(String[] str){ //로그인 유저의 정보를 플레이어 인포 데이터에 담는 메소드 
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
