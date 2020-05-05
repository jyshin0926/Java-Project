import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Object;

// ä�� ������ 
class MultiServerThread implements Runnable,Serializable {
	private static final long serialVersionUID = 1L;
	private Socket socket;
    private MultiServer ms;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean chatStatus = true;
	private boolean m;
	private int o;

    public MultiServerThread( MultiServer ms,ObjectInputStream ois,ObjectOutputStream oos, boolean m , int order){
        this.ms = ms;
		this.ois = ois;
		this.oos = oos;
		this.m =m;
		this.o =order;
    }
    public synchronized void run(){
        boolean isStop = false;
        try{
            socket = ms.getSocket();
			System.out.println("���� ������ ���� ");
            String message = null;

            while( !isStop ){
                message = ( String )ois.readObject();
				System.out.println("�������� ���� �޽��� : "+message);
                String[] str = message.split( "#" );
                
				//�α��� �� 
				if(str[0].equals("idcheck")){ //���̵� üũ �϶�  //�Ѿ�� ��Ʈ�� �� :  0 - ������ , 1- ���̵�, 2- ��й�ȣ
					System.out.println("Requested Id Check");
					IdCheckSave ids = new IdCheckSave();
					if(ids.checkforId(str[1])) { //���̵� �ִ��� Ȯ��
						if(str[2].equals("")) {
							System.out.println("Failed");
							oos.writeObject("Failed");	
						}
						else if(ids.checkforMatch(str[1], str[2])) {//�α��� ����
							System.out.println("Success");
											
							UserInfo ui = (UserInfo)ids.getInfo().get(str[1]);
							ms.setMyuserinfo(ui);
							
							String id = str[1];
							String score = ui.getTpoint();
							String tnum = ui.getTnum();
							String cornum = ui.getCornum();
							String chattype = "0"; //�ʱⰪ ��üü���� 0����
							String acc = "0.0"; //�ϴ� 0.0����
							String auth = null;
							if(m == true)
								auth = "true"; //ó�� ���»���� true
							else
								auth = "false";
							
							//int index = new Random().nextInt(ms.getSeatposition().size());
							//String pos = ms.getSeatposition().get(index);
							//ms.getImagelist().remove(index);
							String pos = Integer.toString(o);
							int index = new Random().nextInt( ms.getImagelist().size());
							String character = ms.getImagelist().get(index); //���� ��ΰ��̴ϱ� �ϴ� null
							ms.getImagelist().remove(index);
							
							ms.getRoundresult().put(pos, "0"); //���߿� �������� [�ڸ�, ���崩������Ʈ] ��
							String[] idpoint = {str[1],ui.getTpoint()};
							ms.getTotalscorelist().put(pos, idpoint); //���߿� �������� [�ڸ�, {���̵�, �� ��������Ʈ}] ��
							
							broadCasting("Success#"+id+"#"+score+"#"+tnum+"#"+cornum+"#"+chattype+"#"+acc+"#"+auth+"#"+character+"#"+pos); //�α��� ������ ��ü���� �� �Ѹ� 
							//oos.writeObject("Success#"+id+"#"+score+"#"+tnum+"#"+cornum+"#"+chattype+"#"+acc+"#"+auth+"#"+character); //�����Ǹ� ���̵� ���� �ѱ� // Userinfo ������ ��ü�� ��Ʈ������ �Ѱܾ���
							ms.getMemberList().put(this, id);//���̵�� �� ������ �����Ҷ����� ��Ƶ�

						}
						else { // ��й�ȣ Ʋ��
							System.out.println("Failed");
							oos.writeObject("Failed");		
						}
					}
					else { // ���̵� ���� 
						System.out.println("No ID");
						oos.writeObject("No ID");
					}
				}
				else if(str[0].equals("regcheck")){ //ȸ�� ��� �϶� //�Ѿ�� ��Ʈ�� �� :  0 - ������ , 1- �̸�, 2- ���̵�, 3- ��й�ȣ, 4- ����
					IdCheckSave ids = new IdCheckSave();
					System.out.println("Requested Reg Check");
					if(!(ids.getInfo().containsKey(str[1]))){// ���̵� �ߺ�  ������
						UserInfo nui = new UserInfo(str[2], str[3], str[4], "0", "0", "0", "N"); //�ϴ� ������ �ʱⰪ�� 0 
						ids.getInfo().put(str[1], nui); //�ؽ����̺� ������Ʈ 
						ids.setInfo(str[1], nui); //�ؽ�Ʈ ���Ͽ� ������Ʈ 
						System.out.println(ids.getInfo().toString()); 
						System.out.println("���̵� ��� ����");
						oos.writeObject("regSuccess");	
					}
					else {// ���̵� �ߺ��Ǹ� 
						System.out.println("�ߺ��Ǵ� ���̵� ����");
						oos.writeObject("regFailed");	
					}
				}
				else if(str[0].equals("idfind")){ //���̵� ã�� �϶� //�Ѿ�� ��Ʈ�� �� :  0 - ������ , 1- �̸�, 2- ����
					IdCheckSave ids = new IdCheckSave();
					System.out.println("Requested Id Find");	
					Hashtable htb = ids.getInfo();
					Set<String> keys = htb.keySet();
					Iterator<String> itr = keys.iterator();
					String idmatch = null;
					int findornot = 0;
					while(itr.hasNext()){
						idmatch = itr.next();
						UserInfo ui = (UserInfo) htb.get(idmatch);
						if(ui.getName().equals(str[1]) && ui.getBirth().equals(str[2])){
							oos.writeObject("findSuccess#"+idmatch);
							findornot =1;
							break;
						}
					}
					if(findornot ==0){
						oos.writeObject("findFailed");
					}
				}
				else if(str[0].equals("psfind")){ //���̵� ã�� �϶� //�Ѿ�� ��Ʈ�� �� :  0 - ������ , 1- ���̵�
					IdCheckSave ids = new IdCheckSave();
					System.out.println("Requested Pwd Find");	
					Hashtable<String, UserInfo> htb = ids.getInfo();
					Set<String> keys = htb.keySet();
					Iterator<String> itr = keys.iterator();
					String idmatch = null;
					int findornot = 0;
					while(itr.hasNext()){
						idmatch = itr.next();
						if(idmatch.equals(str[1])){
							UserInfo ui = (UserInfo) htb.get(idmatch);
							oos.writeObject("findSuccess#"+ui.getPassword());
							findornot =1;
							break;
						}
					}
					if(findornot ==0){
						oos.writeObject("findFailed");
					}
				}

				//���޴���
				else if(str[0].equals("commonsense")){ // ���� �α��� ������ �˸� - ���̵�� ���̺��� 
					ms.setProbtypetoC();
					broadCasting("commonsense#"+str[1]);	
				}
				else if(str[0].equals("nonsense")){ // 	
					ms.setProbtypetoN();
					broadCasting("nonsense#"+str[1]);
				}
				else if(str[0].equals("teamgame")){ // ���� ��
					ms.setTotalroundnum(Integer.parseInt(str[2]));
					broadCasting("teamgame#"+str[1]+"#"+str[2]);
				}
				else if(str[0].equals("individualgame")){ // ���� Ÿ�̸� 	
					ms.setRoundtime(Integer.parseInt(str[2]));
					broadCasting("individualgame#"+str[1]+"#"+str[2]);
				}
				else if(str[0].equals("readytoplay")){ // 			
					ms.setReady();
					broadCasting("readytoplay#"+str[1]);
				}
				else if(str[0].equals("startgame")){ // ���̵� ������
					if(ms.getReadynum() == ms.getList().size()-1)
						broadCasting("startgame#"+str[1]);
					else
						send("startgame#readynotenough");
					//ȥ�� ���� ���ϰ� �ҷ��� �̰� �߰��ϸ� �� if(ms.getList().size() ==1) �϶� ȥ���Ҽ����� ������ �� 
				}
				
				//���� ������ �������Ʈ + ������� ����
				else if(str[0].equals("getMemberList")){ 				
					send("memberlist#"+ sendMemberList(ms.getMemberList())+ms.getList().size()); 
				}

				//�ΰ��� ä��
				else if(str[0].equals("ingame")){
					broadCasting( message ); // ��ü ����
				}
				//�ӼӸ� 
				else if(str[0].equals("sendto")){//sendto#sendid#myid#msg
					broadCasting(message); // �ϴ� �� ������ Ŭ���̾�Ʈ���� ���͸� 
					
				}
				else if(str[0].equals("ingamesendto")){
					broadCasting( message ); // ��ü ����
				}
				//�ΰ��� ���ۺκ� - ���� ��  - ���� �� ��� ������ ���° �������� �����ߴ� , ���� ���ѽð���
				else if(str[0].equals("thegamebegins")) {
					ProblemMap pm = new ProblemMap();
					String[] ans = pm.showProblem(ms.getProbtype()).split("\n");	
					broadCasting("thegamebegins#"+ans[0]+"#"+ans[1]+"#"+ms.getCurrentroundnum()+"#"+ms.getTotalroundnum()+"#"+ms.getRoundtime());  //����, �ʼ�, �������, �� ����, ���� ���ѽð�  
					ms.nextRound();
				}
				//���� ���۹޴� �κ�
				else if(str[0].equals("gameanswer")) {
					//broadCasting("gameanswer#"+str[2]+"#"+str[1]); // str1 ���̵�, str2 ���� //���� ���̺� ��Ͽ��̴� ��ε��ɽ�Ʈ�� ������
					send("gameanswer#"+str[2]+"#"+str[1]);
				}
				//Ÿ�̸� ������ ���� �����ִ� �κ�
				else if(str[0].equals("checkanswer")) {	
					ProblemMap pm = new ProblemMap();
					String ans = pm.sendAnswer(str[1], ms.getProbtype());
					//broadCasting("checkanswer#"+ans);
					send("checkanswer#"+ans); //�������¸� ������ �Ǵ°� �ƴ�?		
				}
				//�̹� �ش� ���̵�� ��ϵ� ���� ������ ������ �����ִ� �κ�
				else if(str[0].equals("deletethisrowfirst")) {
					broadCasting("gameanswerreg#"+str[2]+"#"+str[3]+"#"+str[1]); //1��°�� �����ϰ� 2,3�� ������� ��� ��
				}
				
				//Ŭ���̾�Ʈ�� ���� Ȯ�� 
				
				else if(str[0].equals("checkfromclient")) {	 //str[1] - wrong or correct // str[2] - ���� ��� ���� //str[3] - ���̵� //str[4] -�ڸ�
					if(str[1].equals("correct")){ //��������
						ms.getCorrectList().add(str[4]); //�ڸ�
						ms.getCorrectList().add(str[2]); //��ϼ���
						send("checkfinishedfromserver#" ); 
					}
					else{ //Ʋ���� ���� ���� //str[2] - 0 // str[3] - ���̵� //str[4] -�ڸ�
						ms.getCorrectList().add(str[4]); //�ڸ�
						ms.getCorrectList().add("999"); //�����缭 999 �� 
 						send("checkfinishedfromserver#");
					}		
				}
				// ���� �ο��� �Ѹ��� �κ�
				else if(str[0].equals("givemetheroundresult")){
					int iniScore = 40;
					int min = 1000;
					int idx = 100;
					String toSend="";
					//ArrayList<String> tmp = new ArrayList<String>( ms.getCorrectList());
					//List<Integer> newList = new ArrayList<>(oldList);
					for(int j = 0 ; j< ms.getCorrectList().size() / 2; j++){
						for(int i = 1; i< ms.getCorrectList().size() ; i+=2){
							if(Integer.parseInt(ms.getCorrectList().get(i)) < min){
								min =Integer.parseInt(ms.getCorrectList().get(i)); //��ϼ���
								idx = i;//��ϼ��� �ε���
							}
						}
						ms.getPlayerPointList().add(ms.getCorrectList().get(idx-1)); //�ڸ�
						toSend+= ms.getCorrectList().get(idx-1) +"#";
						if((ms.getCorrectList().get(idx)).equals("999")){
							ms.getPlayerPointList().add("0");
							toSend+= ("0")+"#";
						}
						else{
							ms.getPlayerPointList().add(Integer.toString(iniScore));
							toSend+= (Integer.toString(iniScore))+"#";
							iniScore-=10;
						}//����				
						System.out.println("before "+ms.getCorrectList().toString());
						ms.getCorrectList().set(idx, "1000");
						min =1000;
						System.out.println("After "+ms.getCorrectList().toString());
					}
					
					System.out.println(toSend);
					broadCasting("pointfromserver#"+toSend);
					ms.getCorrectList().clear();
					
					//ms.getPlayerPointList() = [�ڸ�, ����, �ڸ� , ����, �ڸ�, ���� ] ����(�ڸ��� 1,2,3,4 ���� �ƴҼ� ����)
					//���� ���� �����ϴ� �κ�
					for(int i = 0; i<ms.getPlayerPointList().size(); i+=2) { //����
						int newpoint = Integer.parseInt(ms.getPlayerPointList().get(i+1)); //3��° �ڸ��� ����  
						int oldpoint = Integer.parseInt(ms.getRoundresult().get(ms.getPlayerPointList().get(i))); //3��° �ڸ��� ���� ���� 
						ms.getRoundresult().replace(ms.getPlayerPointList().get(i), Integer.toString(newpoint + oldpoint)); // 3��° �ڸ��� ������ �������� ����
					}//���� ����Ʈ�� [�ڸ� : ��������] ���·� ���ε�
					ms.getPlayerPointList().clear();
					
				}
				//�� ���� ��� ������ 
				else if(str[0].equals("givemethetotalresult")) {	
					String res = "";
					System.out.println("���� ����     "+ms.getRoundresult().toString());
					System.out.println("���� �� ����     "+ms.getTotalscorelist().toString());
					for(int i=0; i< ms.getRoundresult().size(); i++) {
						//res += Integer.toString(i+1)+"#"; //�ڸ�
						int s = Integer.parseInt(ms.getRoundresult().get(Integer.toString(i+1)));
						int ts = Integer.parseInt((ms.getTotalscorelist().get(Integer.toString(i+1))[1]));
						res += ms.getTotalscorelist().get(Integer.toString(i+1))[0]+"#"; //���̵�
						res += s+"#"; //��������
						res += (s+ts)+"#"; //���� ������
					}
					send("givemethetotalresult#" + res);
				}	

				// �÷��̾���� �����ϸ� ���� �޾Ƽ� �����ϴ� �κ�
				else if(str[0].equals("startplayerinfo")) {	 
					//�÷��̾�����,�� ���̵�, �� ĳ����,  �� �ڸ�, �� ����, ���� Ǭ �� ����, ���� ���� ����
					String[] tmp = {str[2], str[3]}; 
					ms.getPlayerinfomap().put(str[1], tmp);
					send("waitaminute#"+str[1]);
				}
				else if(str[0].equals("waitisover")) {	 
					//���� �Ѹ� 
					String pinfo="";
					for (String key: ms.getPlayerinfomap().keySet()) {
						pinfo += key+"#"; //���̵�
						pinfo+= ms.getPlayerinfomap().get(key)[0] +"#"; //�� ĳ����
						pinfo+= ms.getPlayerinfomap().get(key)[1] +"#"; // �� �ڸ� 
						//System.out.println("value : " + map.get(key));
					}
					send("reallygamestart#"+pinfo);
				}				
				//���� ���� ��û 
				else if(str[0].equals("backtowaitroom")) {	 
					//���� ���ư��鼭 ���������� �����ؾߵ� �͵�
					ms.resetCurrentroundnum();
					ms.resetReadynum();
					ms.getCorrectList().clear();
					ms.getPlayerPointList().clear();
					for(int i =0; i<ms.getRoundresult().size(); i++) {
						ms.getRoundresult().replace(Integer.toString(i+1), "0"); //[�ڸ�, ���崩������Ʈ] �� - ��������Ʈ�� �ʱ�ȭ
					}				
					broadCasting("backtowaitroom#"+str[1]);
				}
				//�� ���� DB ������Ʈ 
				else if(str[0].equals("updatemyinfo")) { // 1- ���̵�, 2- ��������, 3- Ǭ����, 4 - ��������	 
					//���� ������Ʈ �ϰ� �ٽ� �����ֱ�(���ǿ��� �޾Ƽ� ����)
					IdCheckSave ids = new IdCheckSave();
					ids.updateInfo(message+"#"+ms.getMyuserinfo().getName()+"#"+ms.getMyuserinfo().getPassword()+"#"+ms.getMyuserinfo().getBirth()+"#"+ms.getMyuserinfo().getAuth());
					//������Ʈ ������ 0- ������ ,1-���̵�, 2- ��������, 3- Ǭ����, 4, ��������, 5- �̸�, 6-���, 7- ����, 8- ����
					send(message);
				}
				//��ŷ �޾ƿ��� �κ� 
				else if(str[0].equals("showranking")){ // ���̵�, ����, acc
					String tmp ="showranking#";
					double acc;
					IdCheckSave ids = new IdCheckSave();
					Hashtable<String, UserInfo> htb = ids.getInfo();
					Set<String> keys = htb.keySet();
					Iterator<String> itr = keys.iterator();
					while(itr.hasNext()){
						String userid = itr.next();
						tmp += userid+"#";
						UserInfo ui = (UserInfo) htb.get(userid);
						tmp += ui.getTpoint()+"#";
						if(ui.getTnum().equals("0"))
							acc =0.0;
						else
							acc = Double.parseDouble(ui.getCornum()) /  Double.parseDouble(ui.getTnum())* 100;
						tmp += acc+"#";
					}
					send(tmp);
					
				}
			    //��������
				else if(str[0].equals("userwantstoexit")) {	 
					isStop = true;
				}			
				//���� ä��
				else{
                    broadCasting( message );
					//send(message);
                }

            }
            System.out.println( ms.getMemberList().get(this) +" ("+socket.getInetAddress() + ") ���� ���������� �����ϼ̽��ϴ�" );
            //���� ��� ��ε� ĳ���� 
            ms.empySeatorder();//�ڸ� �ϳ�����
            broadCasting("userexited#"+ms.getMemberList().get(this));
            ms.getList().remove( this );
			ms.getMemberList().remove(this);
            chatStatus = false;
            System.out.println( "list size : " + ms.getList().size() );
			if(ms.getList().size() ==0)
				ms.setMaster();
        }catch(Exception e){
        	e.printStackTrace();
            System.out.println( ms.getMemberList().get(this) +" ("+socket.getInetAddress() + ") ���� ������ �����ϼ̽��ϴ�" );
            ms.empySeatorder();//�ڸ� �ϳ�����
            try {
				broadCasting("userexited#"+ms.getMemberList().get(this));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            ms.getList().remove( this );
			ms.getMemberList().remove(this);
            chatStatus = false;
            System.out.println( "list size : "+ms.getList().size() );
			if(ms.getList().size() ==0)
				ms.setMaster();
        }
    }
    public void broadCasting( String message ) throws IOException{
        for( MultiServerThread ct : ms.getList() ){
           ct.send( message );
        }
    }
    public void send( String message )throws IOException{
        oos.writeObject( message );        
    }
    public boolean getChatStatus() {
    	return chatStatus;
    }
	public String sendMemberList(Map<MultiServerThread, String > list){ //��� �� �̾���̴� �޼ҵ�
		String retString = "";
		System.out.println(list.values());
		for (String s : list.values()){
			
			retString += s + "#";
		}
		return retString;
	}
}


//���� 
public class MultiServer {
	private ArrayList< MultiServerThread > list;
	private Map<MultiServerThread, String> memberList;
	private ArrayList<String> imageList;
	private Socket socket;
	private ObjectInputStream ois;
    private ObjectOutputStream oos;
	private static boolean master = true;
	private ImageManage im;
	int[] imageindexlist; 
	private ArrayList<String> seatPosition;
	private static int readynum =0;
	private static int seatOrder = 1;
	private static int probtype = 0; //0- ���, 1- �ͼ���
	private static int totalroundnum = 4;
	private static int currentroundnum = 1;
	private static int roundtime =15;
	private Map <String, String[]> playerinfomap = new HashMap <String,String[]>(); //�÷��̾� ���̵�� �ڸ��� ��Ī�س��� ��
	RoundManage rm;
	private UserInfo myuserinfo;
	public MultiServer(int port) throws IOException, InterruptedException {
		list = new ArrayList < MultiServerThread >();
		memberList = new HashMap <MultiServerThread, String>();
		imageList = new ArrayList<String>();
		im = new ImageManage();
		imageList = im.getImageList();
		imageindexlist = im.setCharacter();
		seatPosition = new ArrayList<String>();
		seatPosition.add("1");
		seatPosition.add("2");
		seatPosition.add("3");
		seatPosition.add("4");
		rm = new RoundManage(totalroundnum , 4 );

		ServerSocket serverSocket = new ServerSocket( port );
		MultiServerThread mst = null; 

			
		while( true ){ 
			System.out.println("���� �����..." );
			socket = serverSocket.accept();
			ois = new ObjectInputStream( socket.getInputStream() );
            oos = new ObjectOutputStream( socket.getOutputStream() );
			System.out.println(socket.getInetAddress()+" : Ŭ���̾�Ʈ ���� ����!" );
			mst = new MultiServerThread(this, ois, oos, master, seatOrder);
			list.add( mst );
			if(master == true)
				master = false; //ù��° ����� ����
			seatOrder++; //�ڸ� ����
			Thread t = new Thread( mst );
			t.start();// ���������� ����  		

		}
	}
	public UserInfo getMyuserinfo() {
		return myuserinfo;
	}
	public void setMyuserinfo(UserInfo ui) {
		myuserinfo = ui;
	}
	public void resetReadynum() {
		readynum = 0;
	}
	public void resetCurrentroundnum() {
		currentroundnum = 1;
	}
	
	public ArrayList<String> getCorrectList(){
		return rm.getCorrectList();
	}
	public ArrayList<String> getPlayerPointList(){
		return rm.getPlayerPointList();
	}
	public Map<String, String> getRoundresult(){
		return rm.getRoundresult();
	}
	public Map<String, String[]> getTotalscorelist(){
		return rm.getTotalscorelist();
	}
	public void empySeatorder() {
		MultiServer.seatOrder--;
	}
	public void setMaster(){
		this.master = true;
	}
	public int getTotalroundnum(){
		return totalroundnum;
	}
	public int getCurrentroundnum(){
		return currentroundnum;
	}
	public void setTotalroundnum(int num){
		totalroundnum = num;
	}
	public void setRoundtime(int time){
		roundtime = time;
	}
	public void nextRound(){
		currentroundnum++;
	}
	public int getRoundtime(){
		return roundtime;
	}
	public int getReadynum(){
		return readynum;
	}
	public void setReady(){
		readynum ++;
	}
	public Map<String,String[]> getPlayerinfomap(){
		return playerinfomap;
	}
	public ArrayList< MultiServerThread > getList() {
		return list;
	}
	public Map<MultiServerThread, String > getMemberList() {
		return memberList;
	}
	public ArrayList< String > getSeatposition(){
		return seatPosition;
	}
	public Socket getSocket() {
		return socket;
	}
	public int getProbtype(){
		return probtype;
	}
	public void setProbtypetoN(){
		probtype = 1;
	}
	public void setProbtypetoC(){
		probtype =0;
	}
	public int[] getImageindexlist(){
		return imageindexlist;
	}
	public void setImageindexlist(int[] arr){
		this.imageindexlist = arr;
	}
	public ArrayList<String> getImagelist(){
		return imageList;
	}
	public static void main( String args[] ) throws IOException, InterruptedException {
		int port = 3000;
		new MultiServer(port);
	}
}

