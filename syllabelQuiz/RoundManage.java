import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import java.net.Socket;
import java.awt.image.*;


public class RoundManage {

	private static ArrayList<String> correctList ;
	private static ArrayList<String> playerPointList ;
	private static Map<String, String> roundResult;
	private static Map<String, String[]> totalScoreList; //자리 : {아이디, 점수}
	

	public RoundManage(int totalround, int playernumber){ //참여 맴버수만큼 리스트 생성
		totalScoreList = new HashMap<String, String[]>();
		roundResult = new HashMap<String, String>(); //자리와 점수 
		/*
		roundResult.put("1", "0");
		roundResult.put("2", "0");
		roundResult.put("3", "0");
		roundResult.put("4", "0");
		*/
		correctList = new ArrayList<String>();
		playerPointList = new ArrayList<String>();
	}

	public void roundScorecal(String id, int pos, int order){
		
	}

	public Map<String, String[]> getTotalscorelist(){
		return totalScoreList;
	}
	public Map<String, String> getRoundresult(){
		return roundResult;
	}
	
	public ArrayList<String> getCorrectList(){
		return correctList;
	}

	public ArrayList<String> getPlayerPointList(){
		return playerPointList;
	}
	
}
