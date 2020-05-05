
import java.io.*;
import java.util.*;

public class IdCheckSave implements Serializable {
	String infoFilePath = "UserInfo.txt";   //"\\\\192.168.103.79\\ExpertJava\\Images\\UserInfo.txt"; - ������
	public static Hashtable idpsTable = new Hashtable<>();
	
	public IdCheckSave() {
        try{
            //���� ��ü ����
            File file = new File(infoFilePath);
            //�Է� ��Ʈ�� ����
            FileReader filereader = new FileReader(file);
            //�Է� ���� ����
            BufferedReader bufReader = new BufferedReader(filereader);
       
            String lineFetched = null;
			String[] infoArray;

            while(true){
            	lineFetched = bufReader.readLine();
        		if(lineFetched == null)
            		break;
            	else{//������ ���� - ID : �̸�, ��й�ȣ, ������Ʈ, �ѹ���, ��������, ����
					infoArray = lineFetched.split("\t");
					UserInfo ui = new UserInfo(infoArray[1], infoArray[2], infoArray[3], infoArray[4], infoArray[5], infoArray[6] , infoArray[7]);
					idpsTable.put(infoArray[0], ui);
				}
            }
   
			filereader.close();
            bufReader.close();
          
        }catch (FileNotFoundException e) {
            // TODO: handle exception
        }catch(IOException e){
            System.out.println(e);
        }

	}
	
	public boolean checkforId(String id) {
		if(idpsTable.containsKey(id)) {
			return true;
		}
		else
			return false;
	}
	
	public boolean checkforMatch(String id, String ps) {
		UserInfo tmpinfo = (UserInfo) idpsTable.get(id);
	
		if(tmpinfo.getPassword().equals(ps)) {
			return true;
		}
		else
			return false;
	}
	
	public Hashtable getInfo() {
		return idpsTable;
	}
	
	public void setInfo(String id, UserInfo ui){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(infoFilePath, true));
			
			out.write(id+"\t"); 
			out.write(ui.getName()+"\t");
			out.write(ui.getPassword()+"\t");
			out.write(ui.getBirth()+"\t");
			out.write(ui.getTpoint()+"\t");
			out.write(ui.getTnum()+"\t");
			out.write(ui.getCornum()+"\t");
			out.write(ui.getAuth()+"\t");
			out.newLine();
			out.flush();
			out.close();

		} catch (IOException e) {
			System.err.println(e); // ������ �ִٸ� �޽��� ���
			System.exit(1);
		}
	}
	public void updateInfo(String info) {
		String tmpfile = "UserInfo_tmp.txt";
		String[] pinfo = info.split( "#" ); // 0- ������ ,1-���̵�, 2- ��������, 3- Ǭ����, 4, ��������, 5- �̸�, 6-���, 7- ����, 8- ����
		
		try {
	
		
			BufferedReader in = new BufferedReader(new FileReader(infoFilePath));
			BufferedWriter out = new BufferedWriter(new FileWriter(tmpfile));
			synchronized (out) {
				String[] infoArray;
				String line;
				String newline = pinfo[1]+"\t"+pinfo[5]+"\t"+pinfo[6]+"\t"+pinfo[7]+"\t"+pinfo[2]+"\t"+pinfo[3]+"\t"+pinfo[4]+"\t"+pinfo[8]+"\t";
				while ((line = in.readLine()) != null) {
					infoArray = line.split("\t");
					if (infoArray[0].equals(pinfo[1])) {
						line = line.replace(line, newline);
						out.write(line);	
						out.newLine();
						out.flush();	               
					}
					else {
						out.write(line);
						out.newLine();
						out.flush();
					}
			
				}
				in.close();
				out.close();
				// Once everything is complete, delete old file..
				File oldFile = new File(infoFilePath);
				oldFile.delete();

				// And rename tmp file's name to old file name
				File newFile = new File(tmpfile);
				newFile.renameTo(oldFile);
			}
		}
		catch(Exception e) {
			
		}

			
		
	}
}
