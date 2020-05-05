
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class UserInfo {
	private String name, password, birth;
	private String tpoint, cornum, tnum, auth;

	public UserInfo(String name, String password, String birth, String tpoint, String tnum, String cornum, String auth){
		this.name = name;
		this.password = password;
		this.tpoint = tpoint;
		this.cornum =  cornum;
		this.tnum = tnum;
		this.birth = birth;
		this.auth = auth;
	}
	public void setName(String name){
		this.name  = name;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public void setTpoint(String tpoint){
		this.tpoint = tpoint;
	}
	public void setCornum(String cornum){
		this.cornum = cornum;
	}
	public void senTnum(String tnum){
		this.tnum = tnum;
	}
	public void setBirth(String birth){
		this.birth = birth;
	}
	public void setAuth(String auth){
		this.auth = auth;
	}
	public String getName(){
		return this.name;
	}
	public String getPassword(){
		return this.password;
	}
	public String getTpoint(){
		return this.tpoint;
	}
	public String getCornum(){
		return this.cornum;
	}
	public String getTnum(){
		return this.tnum;
	}
	public String getBirth(){
		return this.birth;
	}
	public String getAuth(){
		return this.auth;
	}
	public double getCorRatio(){
		return (Integer.parseInt(this.cornum) / Double.parseDouble(this.tnum)) * 100; 
	}
}
