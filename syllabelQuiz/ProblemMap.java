
import java.io.*;
import java.util.*;

public class ProblemMap {
	private String nsensePath = "nsense.txt";
	private String csensePath = "csense.txt";
	public static Map<String, ProblemInfo> nsenseMap = new HashMap<>();
	public static Map<String, ProblemInfo> csenseMap = new HashMap<>();
	
	public ProblemMap(){
		try{
            //���� ��ü ����
            File nfile = new File(nsensePath);
			File cfile = new File(csensePath);
            //�Է� ��Ʈ�� ����
            FileReader nfilereader = new FileReader(nfile);
			FileReader cfilereader = new FileReader(cfile);
            //�Է� ���� ����
            BufferedReader nbufReader = new BufferedReader(nfilereader);
			BufferedReader cbufReader = new BufferedReader(cfilereader);
       
            String nlineFetched = null;
			String[] ninfoArray;
			String clineFetched = null;
			String[] cinfoArray;

            while(true){
            	nlineFetched = nbufReader.readLine();
        		if(nlineFetched == null)
            		break;
            	else{//������ ���� - ���� : (�ʼ�, ����)
					System.out.println(nlineFetched);
					ninfoArray = nlineFetched.split("\t");
					ProblemInfo npi = new ProblemInfo(ninfoArray[1], ninfoArray[2]);
					nsenseMap.put(ninfoArray[0], npi);
				}
            }

			while(true){
            	clineFetched = cbufReader.readLine();
        		if(clineFetched == null)
            		break;
            	else{//������ ���� - ���� : (�ʼ�, ����)
					cinfoArray = clineFetched.split("\t");
					ProblemInfo cpi = new ProblemInfo(cinfoArray[1], cinfoArray[2]);
					csenseMap.put(cinfoArray[0], cpi);
				}
            }


			nfilereader.close();
            nbufReader.close();
			cfilereader.close();
            cbufReader.close();
          
        }catch (FileNotFoundException e) {
            // TODO: handle exception
        }catch(IOException e){
            System.out.println(e);
        }
	}

	public String showProblem(int mode){ // 0 - ���, 1- �ͼ���
		Random r = new Random();
		if(mode==0){
			List<String> keysAsArray = new ArrayList<String>(csenseMap.keySet());
			String randomProb = keysAsArray.get(r.nextInt(keysAsArray.size()));
			String consonant = (csenseMap.get(randomProb)).getConsonant();
			
			return randomProb+"\n"+consonant;
		}
		else if(mode==1){
			List<String> keysAsArray = new ArrayList<String>(nsenseMap.keySet());
			String randomProb = keysAsArray.get(r.nextInt(keysAsArray.size()));
			String consonant = (nsenseMap.get(randomProb)).getConsonant();
			
			return randomProb+"\n"+consonant;

		}
		else{
			System.out.println("�߸��� ���");
			return null;
		}
	}
	
	public String sendAnswer(String problem, int mode){
		if(mode ==0){
			return (csenseMap.get(problem)).getAnswer();
		}
		else{
			return (nsenseMap.get(problem)).getAnswer();
		}
	}

	public boolean checkAnswer(String problem, String submit, int mode){
		//�� �´��� Ȯ��
		if(mode == 0){//���
			if ((csenseMap.get(problem)).getAnswer().equals(submit)){
				return true;
			}
			else{
				return false;
			}
		}
		else if(mode ==1){
			if ((nsenseMap.get(problem)).getAnswer().equals(submit)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public static void main(String[] args) {
		ProblemMap pm = new ProblemMap();
		System.out.println(pm.showProblem(0));

		System.out.println();
	}
}
