import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Object;

// 채팅 쓰레드 
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
			System.out.println("서버 스레드 시작 ");
            String message = null;

            while( !isStop ){
                message = ( String )ois.readObject();
				System.out.println("서버에서 받은 메시지 : "+message);
                String[] str = message.split( "#" );
                
				//로그인 부 
				if(str[0].equals("idcheck")){ //아이디 체크 일때  //넘어온 스트링 값 :  0 - 구분자 , 1- 아이디, 2- 비밀번호
					System.out.println("Requested Id Check");
					IdCheckSave ids = new IdCheckSave();
					if(ids.checkforId(str[1])) { //아이디 있는지 확인
						if(str[2].equals("")) {
							System.out.println("Failed");
							oos.writeObject("Failed");	
						}
						else if(ids.checkforMatch(str[1], str[2])) {//로그인 성공
							System.out.println("Success");
											
							UserInfo ui = (UserInfo)ids.getInfo().get(str[1]);
							ms.setMyuserinfo(ui);
							
							String id = str[1];
							String score = ui.getTpoint();
							String tnum = ui.getTnum();
							String cornum = ui.getCornum();
							String chattype = "0"; //초기값 전체체팅인 0으로
							String acc = "0.0"; //일단 0.0으로
							String auth = null;
							if(m == true)
								auth = "true"; //처음 들어온사람만 true
							else
								auth = "false";
							
							//int index = new Random().nextInt(ms.getSeatposition().size());
							//String pos = ms.getSeatposition().get(index);
							//ms.getImagelist().remove(index);
							String pos = Integer.toString(o);
							int index = new Random().nextInt( ms.getImagelist().size());
							String character = ms.getImagelist().get(index); //파일 경로값이니까 일단 null
							ms.getImagelist().remove(index);
							
							ms.getRoundresult().put(pos, "0"); //나중에 쓰기위한 [자리, 라운드누적포인트] 맵
							String[] idpoint = {str[1],ui.getTpoint()};
							ms.getTotalscorelist().put(pos, idpoint); //나중에 쓰기위한 [자리, {아이디, 총 누적포인트}] 맵
							
							broadCasting("Success#"+id+"#"+score+"#"+tnum+"#"+cornum+"#"+chattype+"#"+acc+"#"+auth+"#"+character+"#"+pos); //로그인 했을때 전체에게 다 뿌림 
							//oos.writeObject("Success#"+id+"#"+score+"#"+tnum+"#"+cornum+"#"+chattype+"#"+acc+"#"+auth+"#"+character); //성공되면 아이디도 같이 넘김 // Userinfo 데이터 전체를 스트링으로 넘겨야함
							ms.getMemberList().put(this, id);//아이디랑 현 쓰레드 접속할때마다 모아둠

						}
						else { // 비밀번호 틀림
							System.out.println("Failed");
							oos.writeObject("Failed");		
						}
					}
					else { // 아이디 없음 
						System.out.println("No ID");
						oos.writeObject("No ID");
					}
				}
				else if(str[0].equals("regcheck")){ //회원 등록 일때 //넘어온 스트링 값 :  0 - 구분자 , 1- 이름, 2- 아이디, 3- 비밀번호, 4- 생일
					IdCheckSave ids = new IdCheckSave();
					System.out.println("Requested Reg Check");
					if(!(ids.getInfo().containsKey(str[1]))){// 아이디 중복  없으면
						UserInfo nui = new UserInfo(str[2], str[3], str[4], "0", "0", "0", "N"); //일단 나머지 초기값은 0 
						ids.getInfo().put(str[1], nui); //해쉬테이블 업데이트 
						ids.setInfo(str[1], nui); //텍스트 파일에 업데이트 
						System.out.println(ids.getInfo().toString()); 
						System.out.println("아이디 등록 성공");
						oos.writeObject("regSuccess");	
					}
					else {// 아이디 중복되면 
						System.out.println("중복되는 아이디가 있음");
						oos.writeObject("regFailed");	
					}
				}
				else if(str[0].equals("idfind")){ //아이디 찾기 일때 //넘어온 스트링 값 :  0 - 구분자 , 1- 이름, 2- 생일
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
				else if(str[0].equals("psfind")){ //아이디 찾기 일때 //넘어온 스트링 값 :  0 - 구분자 , 1- 아이디
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

				//대기메뉴부
				else if(str[0].equals("commonsense")){ // 유저 로그인 했음을 알림 - 아이디랑 같이보냄 
					ms.setProbtypetoC();
					broadCasting("commonsense#"+str[1]);	
				}
				else if(str[0].equals("nonsense")){ // 	
					ms.setProbtypetoN();
					broadCasting("nonsense#"+str[1]);
				}
				else if(str[0].equals("teamgame")){ // 문제 수
					ms.setTotalroundnum(Integer.parseInt(str[2]));
					broadCasting("teamgame#"+str[1]+"#"+str[2]);
				}
				else if(str[0].equals("individualgame")){ // 문제 타이머 	
					ms.setRoundtime(Integer.parseInt(str[2]));
					broadCasting("individualgame#"+str[1]+"#"+str[2]);
				}
				else if(str[0].equals("readytoplay")){ // 			
					ms.setReady();
					broadCasting("readytoplay#"+str[1]);
				}
				else if(str[0].equals("startgame")){ // 아이디를 돌려줌
					if(ms.getReadynum() == ms.getList().size()-1)
						broadCasting("startgame#"+str[1]);
					else
						send("startgame#readynotenough");
					//혼자 게임 못하게 할려면 이거 추가하면 됨 if(ms.getList().size() ==1) 일때 혼자할수없다 보내면 됨 
				}
				
				//현재 접소한 멤버리스트 + 몇명인지 보냄
				else if(str[0].equals("getMemberList")){ 				
					send("memberlist#"+ sendMemberList(ms.getMemberList())+ms.getList().size()); 
				}

				//인게임 채팅
				else if(str[0].equals("ingame")){
					broadCasting( message ); // 전체 전송
				}
				//귓속말 
				else if(str[0].equals("sendto")){//sendto#sendid#myid#msg
					broadCasting(message); // 일단 다 보내고 클라이언트에서 필터링 
					
				}
				else if(str[0].equals("ingamesendto")){
					broadCasting( message ); // 전체 전송
				}
				//인게임 시작부분 - 문제 냄  - 낼때 총 몇개의 문제중 몇번째 문제인지 보내야댐 , 라운드 제한시간도
				else if(str[0].equals("thegamebegins")) {
					ProblemMap pm = new ProblemMap();
					String[] ans = pm.showProblem(ms.getProbtype()).split("\n");	
					broadCasting("thegamebegins#"+ans[0]+"#"+ans[1]+"#"+ms.getCurrentroundnum()+"#"+ms.getTotalroundnum()+"#"+ms.getRoundtime());  //문제, 초성, 현재라운드, 총 라운드, 라운드 제한시간  
					ms.nextRound();
				}
				//정답 전송받는 부분
				else if(str[0].equals("gameanswer")) {
					//broadCasting("gameanswer#"+str[2]+"#"+str[1]); // str1 아이디, str2 정답 //여긴 테이블 등록용이니 브로드케스트가 맞을듯
					send("gameanswer#"+str[2]+"#"+str[1]);
				}
				//타이머 끝나고 정답 보내주는 부분
				else if(str[0].equals("checkanswer")) {	
					ProblemMap pm = new ProblemMap();
					String ans = pm.sendAnswer(str[1], ms.getProbtype());
					//broadCasting("checkanswer#"+ans);
					send("checkanswer#"+ans); //유저한태만 보내야 되는거 아님?		
				}
				//이미 해당 아이디로 등록된 답이 있을때 지우라고 보내주는 부분
				else if(str[0].equals("deletethisrowfirst")) {
					broadCasting("gameanswerreg#"+str[2]+"#"+str[3]+"#"+str[1]); //1번째는 삭제하고 2,3번 등록해줘 라는 뜻
				}
				
				//클라이언트의 정답 확인 
				
				else if(str[0].equals("checkfromclient")) {	 //str[1] - wrong or correct // str[2] - 정답 등록 순서 //str[3] - 아이디 //str[4] -자리
					if(str[1].equals("correct")){ //차등점수
						ms.getCorrectList().add(str[4]); //자리
						ms.getCorrectList().add(str[2]); //등록순서
						send("checkfinishedfromserver#" ); 
					}
					else{ //틀려서 점수 없음 //str[2] - 0 // str[3] - 아이디 //str[4] -자리
						ms.getCorrectList().add(str[4]); //자리
						ms.getCorrectList().add("999"); //못맞춰서 999 줌 
 						send("checkfinishedfromserver#");
					}		
				}
				// 점수 부여후 뿌리는 부분
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
								min =Integer.parseInt(ms.getCorrectList().get(i)); //등록순서
								idx = i;//등록순서 인덱스
							}
						}
						ms.getPlayerPointList().add(ms.getCorrectList().get(idx-1)); //자리
						toSend+= ms.getCorrectList().get(idx-1) +"#";
						if((ms.getCorrectList().get(idx)).equals("999")){
							ms.getPlayerPointList().add("0");
							toSend+= ("0")+"#";
						}
						else{
							ms.getPlayerPointList().add(Integer.toString(iniScore));
							toSend+= (Integer.toString(iniScore))+"#";
							iniScore-=10;
						}//점수				
						System.out.println("before "+ms.getCorrectList().toString());
						ms.getCorrectList().set(idx, "1000");
						min =1000;
						System.out.println("After "+ms.getCorrectList().toString());
					}
					
					System.out.println(toSend);
					broadCasting("pointfromserver#"+toSend);
					ms.getCorrectList().clear();
					
					//ms.getPlayerPointList() = [자리, 점수, 자리 , 점수, 자리, 점수 ] 구조(자리는 1,2,3,4 순이 아닐수 있음)
					//라운드 누적 저장하는 부분
					for(int i = 0; i<ms.getPlayerPointList().size(); i+=2) { //예시
						int newpoint = Integer.parseInt(ms.getPlayerPointList().get(i+1)); //3번째 자리의 점수  
						int oldpoint = Integer.parseInt(ms.getRoundresult().get(ms.getPlayerPointList().get(i))); //3번째 자리의 누적 점수 
						ms.getRoundresult().replace(ms.getPlayerPointList().get(i), Integer.toString(newpoint + oldpoint)); // 3번째 자리의 점수에 총점수를 맵핑
					}//라운드 리절트에 [자리 : 누적점수] 형태로 맵핑됨
					ms.getPlayerPointList().clear();
					
				}
				//총 라운드 결과 돌려줌 
				else if(str[0].equals("givemethetotalresult")) {	
					String res = "";
					System.out.println("누적 점수     "+ms.getRoundresult().toString());
					System.out.println("누적 총 점수     "+ms.getTotalscorelist().toString());
					for(int i=0; i< ms.getRoundresult().size(); i++) {
						//res += Integer.toString(i+1)+"#"; //자리
						int s = Integer.parseInt(ms.getRoundresult().get(Integer.toString(i+1)));
						int ts = Integer.parseInt((ms.getTotalscorelist().get(Integer.toString(i+1))[1]));
						res += ms.getTotalscorelist().get(Integer.toString(i+1))[0]+"#"; //아이디
						res += s+"#"; //누적점수
						res += (s+ts)+"#"; //누적 총점수
					}
					send("givemethetotalresult#" + res);
				}	

				// 플레이어들이 레디하면 정보 받아서 저장하는 부분
				else if(str[0].equals("startplayerinfo")) {	 
					//플레이어정보,내 아이디, 내 캐릭터,  내 자리, 내 점수, 내가 푼 총 문제, 내가 맞춘 문제
					String[] tmp = {str[2], str[3]}; 
					ms.getPlayerinfomap().put(str[1], tmp);
					send("waitaminute#"+str[1]);
				}
				else if(str[0].equals("waitisover")) {	 
					//정보 뿌름 
					String pinfo="";
					for (String key: ms.getPlayerinfomap().keySet()) {
						pinfo += key+"#"; //아이디
						pinfo+= ms.getPlayerinfomap().get(key)[0] +"#"; //내 캐릭터
						pinfo+= ms.getPlayerinfomap().get(key)[1] +"#"; // 내 자리 
						//System.out.println("value : " + map.get(key));
					}
					send("reallygamestart#"+pinfo);
				}				
				//대기실 복귀 요청 
				else if(str[0].equals("backtowaitroom")) {	 
					//대기실 돌아가면서 서버측에서 리셋해야될 것들
					ms.resetCurrentroundnum();
					ms.resetReadynum();
					ms.getCorrectList().clear();
					ms.getPlayerPointList().clear();
					for(int i =0; i<ms.getRoundresult().size(); i++) {
						ms.getRoundresult().replace(Integer.toString(i+1), "0"); //[자리, 라운드누적포인트] 맵 - 누적포인트만 초기화
					}				
					broadCasting("backtowaitroom#"+str[1]);
				}
				//내 정보 DB 업데이트 
				else if(str[0].equals("updatemyinfo")) { // 1- 아이디, 2- 누적점수, 3- 푼문제, 4 - 맞은문제	 
					//정보 업데이트 하고 다시 돌려주기(대기실에서 받아서 세팅)
					IdCheckSave ids = new IdCheckSave();
					ids.updateInfo(message+"#"+ms.getMyuserinfo().getName()+"#"+ms.getMyuserinfo().getPassword()+"#"+ms.getMyuserinfo().getBirth()+"#"+ms.getMyuserinfo().getAuth());
					//업데이트 보낼때 0- 구분자 ,1-아이디, 2- 누적점수, 3- 푼문제, 4, 맞은문제, 5- 이름, 6-비번, 7- 생일, 8- 권한
					send(message);
				}
				//랭킹 받아오는 부분 
				else if(str[0].equals("showranking")){ // 아이디, 누적, acc
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
			    //종료조건
				else if(str[0].equals("userwantstoexit")) {	 
					isStop = true;
				}			
				//대기실 채팅
				else{
                    broadCasting( message );
					//send(message);
                }

            }
            System.out.println( ms.getMemberList().get(this) +" ("+socket.getInetAddress() + ") 님이 정상적으로 종료하셨습니다" );
            //종료 사실 브로드 캐스팅 
            ms.empySeatorder();//자리 하나감소
            broadCasting("userexited#"+ms.getMemberList().get(this));
            ms.getList().remove( this );
			ms.getMemberList().remove(this);
            chatStatus = false;
            System.out.println( "list size : " + ms.getList().size() );
			if(ms.getList().size() ==0)
				ms.setMaster();
        }catch(Exception e){
        	e.printStackTrace();
            System.out.println( ms.getMemberList().get(this) +" ("+socket.getInetAddress() + ") 님이 강제로 종료하셨습니다" );
            ms.empySeatorder();//자리 하나감소
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
	public String sendMemberList(Map<MultiServerThread, String > list){ //멤버 맵 이어붙이는 메소드
		String retString = "";
		System.out.println(list.values());
		for (String s : list.values()){
			
			retString += s + "#";
		}
		return retString;
	}
}


//메인 
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
	private static int probtype = 0; //0- 상식, 1- 넌센스
	private static int totalroundnum = 4;
	private static int currentroundnum = 1;
	private static int roundtime =15;
	private Map <String, String[]> playerinfomap = new HashMap <String,String[]>(); //플레이어 아이디와 자리를 매칭해놓은 맵
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
			System.out.println("서버 대기중..." );
			socket = serverSocket.accept();
			ois = new ObjectInputStream( socket.getInputStream() );
            oos = new ObjectOutputStream( socket.getOutputStream() );
			System.out.println(socket.getInetAddress()+" : 클라이언트 연결 성공!" );
			mst = new MultiServerThread(this, ois, oos, master, seatOrder);
			list.add( mst );
			if(master == true)
				master = false; //첫번째 사람만 방장
			seatOrder++; //자리 증가
			Thread t = new Thread( mst );
			t.start();// 서버스레드 시작  		

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

