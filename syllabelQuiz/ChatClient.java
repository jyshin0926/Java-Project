
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;


public class ChatClient extends JPanel implements ActionListener {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private JPanel jframe;
    private JTextField jtf;
    private JTextArea jta;
    private JLabel jlb1;
    private JPanel jp1, jp2;
    private String ip;
    private String id;
	private Boolean chatStatus = false;
	private JButton chatclear;

    public ChatClient( String argIp, String argId, ObjectInputStream ois, ObjectOutputStream oos) {
    	setBackground(SystemColor.info);
		this.ois = ois;
		this.oos = oos;

        ip = argIp;
        id = argId;
        jtf = new JTextField( 30 );
        jta = new JTextArea( "", 10, 50 );
        jlb1 = new JLabel( "내 아이디 : [[ " + id + "]]" );
        jp1 = new JPanel();
        jp2 = new JPanel();
        jlb1.setBackground( Color.yellow );
        jta.setBackground( SystemColor.textHighlightText );
        jp1.setLayout( new BorderLayout() );
        jp2.setLayout( new BorderLayout() );
        jp1.add( jtf, BorderLayout.CENTER);
        jp2.add( jlb1, BorderLayout.CENTER );
		chatclear = new JButton("클리어");
        add( jp1, BorderLayout.SOUTH );
		add( chatclear,BorderLayout.NORTH);
        add( jp2, BorderLayout.NORTH );
        JScrollPane jsp = new JScrollPane( jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        add( jsp, BorderLayout.CENTER );

        jtf.addActionListener( this );
		chatclear.addActionListener( this );

		//tab을 통한 정답, 채팅 화면 전환 
		jtf.setFocusTraversalKeysEnabled(false);
		jtf.addKeyListener(
			new KeyAdapter(){
				public void keyPressed(KeyEvent e){
					switch(e.getKeyCode()){
					case KeyEvent.VK_TAB:
						if(!chatStatus){
							jtf.setBackground(Color.blue);
							chatStatus = !chatStatus;
						}
						else{
							jtf.setBackground(Color.white);
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

		if(e.getSource().equals(chatclear)){
			jta.setText("");
			jtf.setText("");
		}
        if ( obj == jtf ) {
            if ( msg == null || msg.length()==0 ) {
                JOptionPane.showMessageDialog( jframe, "글을쓰세요", "경고", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                	if(msg.contains("sendto")) {
                		tmp = msg.split("#");
                		oos.writeObject(tmp[0]+"#"+ tmp[1] + "#" + id +"#"+tmp[2] );
                	}
                	else
                		oos.writeObject( id + "#" + msg );
                } catch ( IOException ee ) {
                    ee.printStackTrace();
                }
                jtf.setText("");
            }
        }

    }

	public ObjectInputStream getOis(){
        return ois;
    }
    public JTextArea getJta(){
        return jta;
    }


}

