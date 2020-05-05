import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;


public class ImageManage extends JFrame {

	public static ArrayList<String> charImgArray = new ArrayList<String>();
	public static List newList = new List();
	public ImageManage(){
		
		charImgArray.add("character_images/abe.png");
		charImgArray.add("character_images/abe_face.png");
		charImgArray.add("character_images/zebra.png");
		charImgArray.add("character_images/moon.png");
		charImgArray.add("character_images/moon_face.png");
		charImgArray.add("character_images/crab.png");
		charImgArray.add("character_images/cow.png");
		charImgArray.add("character_images/ghost.png");
		charImgArray.add("character_images/cat.png");
		charImgArray.add("character_images/trump.png");
		charImgArray.add("character_images/trump_face.png");
		charImgArray.add("character_images/obama.png");
		charImgArray.add("character_images/obama_face.png");
		charImgArray.add("character_images/putin.png");
		charImgArray.add("character_images/putin_face.png");
		charImgArray.add("character_images/kim.png");
		charImgArray.add("character_images/kim_face.png");
		charImgArray.add("character_images/AmericanH.png"); //´ºÇÑ»ù
		charImgArray.add("character_images/HulkH.png"); //´ºÇÑ»ù
		charImgArray.add("character_images/IronManH.png"); //´ºÇÑ»ù
		charImgArray.add("character_images/SpiderManH.png"); //´ºÇÑ»ù

	
	} //constructor


	public int[] setCharacter(){
		Random rand = new Random();
		int randomIndex = rand.nextInt(charImgArray.size());			
		int m[] = new int[4];
		for( int i=0; i<m.length; i++){
			m[i] = rand.nextInt(charImgArray.size());
			for(int j=0; j<i; j++){					
				if(m[i] == m[j]) i--;				
				}										
			}

		return m;
	}
	
	public ArrayList<String> getImageList(){
		return charImgArray;
	}
	public static void main(String[] args) {
		ImageManage im = new ImageManage();
	}
}

// Ä³¸¯ÅÍ 5°³ Áß 3°³ ·£´ýÀ¸·Î Ã¢¿¡ Ãâ·Â

