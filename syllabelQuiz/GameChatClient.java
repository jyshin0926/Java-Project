import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.event.*;


public class GameChatClient extends JPanel implements ActionListener {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private JPanel jframe;
    private JTextField jtf;
    private JTextArea jta;
    private JPanel jp1, jp2;
    private String ip;
    private String id;
	private Boolean chatStatus = false;
	MusicPlayer mp = new MusicPlayer();
	Clip send;
    public GameChatClient( String argIp, String argId, ObjectInputStream ois, ObjectOutputStream oos) {
    	setBackground(SystemColor.activeCaption);
		this.ois = ois;
		this.oos = oos;

		
        ip = argIp;
        id = argId;
        jtf = new JTextField( 30 );
        jta = new JTextArea( "", 10, 50 );
        jp1 = new JPanel();
        jp2 = new JPanel();
        jta.setBackground( SystemColor.text );
        jp1.setLayout( new BorderLayout() );
        jp2.setLayout( new BorderLayout() );
        jp1.add( jtf, BorderLayout.CENTER);
		
        add( jp1, BorderLayout.SOUTH );
        add( jp2, BorderLayout.NORTH );
        JScrollPane jsp = new JScrollPane( jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        add( jsp, BorderLayout.CENTER );

        jtf.addActionListener( this );


		//tab을 통한 정답, 채팅 화면 전환 
		jtf.setFocusTraversalKeysEnabled(false);
		jtf.addKeyListener(
			new KeyAdapter(){
				public void keyPressed(KeyEvent e){
					switch(e.getKeyCode()){
					case KeyEvent.VK_TAB:
						if(!chatStatus){
							jtf.setBackground(Color.blue);
							jtf.setForeground(Color.white);
							chatStatus = !chatStatus;
						}
						else{
							jtf.setBackground(Color.white);
							jtf.setForeground(Color.black);
							chatStatus = !chatStatus;
						}
						break;
					}
				}
			});

        jta.setEditable( false );
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int screenHeight = d.height;
        int screenWidth = d.width;
        
     
    }

    public void actionPerformed( ActionEvent e ) {
 
        Object obj = e.getSource();
        String msg = jtf.getText();
        String[] tmp;
        if ( obj == jtf ) {
            if ( msg == null || msg.length()==0 ) {
                JOptionPane.showMessageDialog( jframe, "글쓰세요", "경고", JOptionPane.WARNING_MESSAGE);
            } else {
                try {//문제랑 채팅부 전환
					if(chatStatus == false){
						if(msg.contains("sendto")) {
	                		tmp = msg.split("#");
	                		oos.writeObject("ingamesendto#"+tmp[0]+"#"+ tmp[1] + "#" + id +"#"+tmp[2]);
						}
	                	else
	                		oos.writeObject( "ingame#" +id + "#" + msg );
					}
					else{
						mp.musicRepeat("sounds/SendAnswer.wav");
						oos.writeObject( "gameanswer#"+id + "#" + msg); //게임 정답보내는 부분
					}
                } catch ( IOException ee ) {
                    ee.printStackTrace();
                }
                jtf.setText("");
            }
        } 
    }

    public void resetChat() {
    	jta.setText("");
    }
	public ObjectInputStream getOis(){
        return ois;
    }

	public ObjectOutputStream getOos(){
		return oos;
	}
    public JTextArea getJta(){
        return jta;
    }


}

