import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.math.*;



public class JyCalculator3 extends JFrame implements ActionListener{
//	private JFrame frame;
//	private ActionListener btnClickListener;	//
	
	/////////�ʵ�/////////////////
	private BigDecimal resultValue;	// �����
	private BigDecimal memoryValue;	// �Էµ� �� ����
	private Boolean isNewValue;		// ���� �� ���Է�
	private static final String divideErrorText = "0���� ���� �� �����ϴ�"; //

	////////���̺�///////////
	
	////////////�ʱ�ȭ/////////
	JPanel jpButton = new JPanel();
	JPanel jpResult = new JPanel();
//	jpButton.setBackground(Color.GRAY);
//	jpButton.setLayout(new GridLayout(5,4,3,3));
//	JButton[] jbButton = new JButton[numArr.length];
//	JLabel jlbResult = new JLabel("",JLabel.RIGHT);
//	JLabel jlbDefault = new JLabel("0",JLabel.RIGHT);
	JLabel jlbRecord,jlbResult;
//	jlbResult.setFont(new Font("����",Font.BOLD,30));	
	JButton [] jbButton = null;	
	

//	String [] digitArr = {"7","8","9","4","5","6","1","2","3","0","00","."};	//
//	String [] reArr = {"CE","C","��","="};//
//	String [] delArr = {"��","��","-","+"}; //

	String [] padArr = {//"%,"sqrt","1/x","x^2",
						"CE", "C","��","��",
						"7", "8", "9", "��",
						"4", "5", "6", "-",
						"1", "2", "3", "+",
						"��", "0", ".", "=" };	
	String result =  "";		// ���
	String result1 = "";		// ��� ǥ��(Record)
	String result2 = "";		// ����� ǥ��(Result)

	String number0 ="";
	String number1 ="";
	String number2 ="";
//	String [] number = {"","",""};	// result, result1, result2
	
	

//	jpResult.add(jlbResult);
//	jpResult.add(jlbDefault);
//	add(jpResult,"North");
	

	public JyCalculator3(String frameText){
		super(frameText);

	getContentPane().setLayout(new BorderLayout());
	jpResult = new JPanel(new GridLayout(4,1));
	jpResult.setBackground(Color.BLACK);
	

	jlbRecord = new JLabel("",JLabel.RIGHT);
	jlbResult = new JLabel("0",JLabel.RIGHT);
	jlbResult.setFont(new Font("SANS_SERIF",Font.BOLD,30));
	jlbResult.setForeground(Color.WHITE);
	jlbRecord.setForeground(Color.WHITE);
	jpResult.add(jlbRecord);
	jpResult.add(jlbResult);
	
	
//	jpDigitButton = new JPanel(new GridLayout(3,4,2,2));	
//	jpOpButton = new JPanel(new GridLayout(1,4,2,2));
//	jpDelButton = new JPanel(new GridLayout(3,1,2,2));

	jpButton = new JPanel(new GridLayout(5,4,2,2));
	jpButton.setBackground(Color.BLACK);
	
	jbButton = new JButton[padArr.length];	
	for(int i=0;i<padArr.length;i++){
		jbButton[i] = new JButton(padArr[i]);
		jbButton[i].setBackground(Color.GRAY);
		jbButton[i].setForeground(Color.WHITE);
		jbButton[i].setFont(new Font("Arial",Font.BOLD,30));
		jpButton.add(jbButton[i]);
		jbButton[i].addActionListener(this);
	}	//for

	
	getContentPane().add("North",jpResult);
	getContentPane().add("Center",jpButton);
	getContentPane().setBackground(Color.BLACK);
	setBounds(1000,300,500,600);
	setVisible(true);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}	//constructor
	


///////////////////Listener///////////////////////
		


////////////////////ButtonEvent/////////////////////////
	//ActionListener padButtonListener = new ActionListener(){} 	

	//this.pad = new ActionListener();
	
/*	addWindowListener(
		new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);

			}
		}	
	);		*/

	
//	public void actionPerformed(ActionEvent e){
//		String padButton = e.getActionCommand();	//	String Component		
	
//	} // actionPerformed


////////////////////Calculate////////////////////////////
	public String Cal(){
			String num = "";
			BigDecimal big1 = new BigDecimal(number0);
			BigDecimal big2 = new BigDecimal(number2);
				
		//for(int i=0; i<2;i++){}

				if(number1.equals("+")){		// +
					if(number0.indexOf(".")!=-1||number2.indexOf(".")!=-1){
							num = big1.add(big2,MathContext.DECIMAL32)+"";
					}else{
							if("".equals(number2)){
									num = result2;
							}else{
									if(!"".equals(result)){
												number0 = result;
									}
									num = (Long.parseLong(number0)+Long.parseLong(number2))+"";
									result =num;
									number0 =result;
							}
					
					}			
		
		}//+ 
			else if(number1.equals("-")){		// +
					if(number0.indexOf(".")!=-1||number2.indexOf(".")!=-1){
						num = big1.subtract(big2, MathContext.DECIMAL32) +"";					
					}else{
							if("".equals(number2)){
									num = result2;
							}else{
									if(!"".equals(result)){
												number0 = result;
									}
									num = (Long.parseLong(number0)-Long.parseLong(number2))+"";
									result =num;
									number0 =result;
							}
					
					}			
		
		}//-
			else if(number1.equals("��")){		// +
					if(number0.indexOf(".")!=-1||number2.indexOf(".")!=-1){
							num = big1.multiply(big2,MathContext.DECIMAL32)+"";
					}else{
							if("".equals(number2)){
									num = result2;
							}else{
									if(!"".equals(result)){
												number0 = result;
									}
									num = (Long.parseLong(number0)*Long.parseLong(number2))+"";
									result =num;
									number0 =result;
							}
					
					}			
		
		}//*
			 

			return num;
	
	} // Cal method		*/

		

////////////////ButtonEvent///////////////////////////////
	public void actionPerformed(ActionEvent e){
			String padButton = e.getActionCommand();

			if(padButton.equals("1")||padButton.equals("2")||padButton.equals("3")||	// 1~9
			padButton.equals("4")||padButton.equals("5")||padButton.equals("6")||
			padButton.equals("7")||padButton.equals("8")||padButton.equals("9")){
					if(result2.equals("0")){
						result2 = "";
					}	// digitif - if
					result2 += padButton;		// 1~9�����Է�
					jlbResult.setText(result2);

			}else if(padButton.equals("��")){						//backspc
					int len = jlbResult.getText().length();
					if(len==1){
							result2 ="";
							jlbResult.setText("0");	//����1�� backspc event
					} else {
							if(!"".equals(result2)){
										result2 = result2.substring(0,len-1);	// ���� ���� �� backspc event
										//result2 = result2.substring(0,len-2);
										jlbResult.setText(result2);	
							}
					
					}				
			
			}else if(padButton.equals(".")){						// .
					if("".equals(result2)){
							result2 = "0" + padButton;				// �Է�â ���� ���� �� 0���� �⺻����
					}else{
							if(result2.lastIndexOf(".")==-1){
										result2 += padButton;		// �Է�â ���� ���� �� & �Ҽ��� ���� ��
							}
					}
					jlbResult.setText(result2);

			}else if(padButton.equals("=")){		// =
					if(!"".equals(result2)){
							number2 = result2;					
					}
					if("".equals(result1)){
							if(!number1.equals("")){
									
									result = Cal();
									if(divideErrorText.equals(result)){
												result=""; result1=""; result2 ="";
												number0 =""; number1="";number2="" ;
									}else{
									number0 = result;
									jlbResult.setText(result);

									}				
							}
							jlbRecord.setText(result1);

				} else{
						if("".equals(number2)){
								number2 = result2;						
						}
						result = number0;

						if(divideErrorText.equals(result)){
								jlbResult.setText(result);
								result="";result1="";result2="";
								number0 =""; number1="";number2="" ;
						
						}else{
								result = Cal();
								if(!"0���� ���� �� �����ϴ�.".equals(result)){
										result1 = "";
										number0 = result;
										jlbResult.setText(result);
										jlbRecord.setText(result1);
										result2 = "";
								}else{
										jlbResult.setText(divideErrorText);
										jlbResult.setText("");
										result="";result1="";result2="";
										number0 =""; number1="";number2="" ;						
								
								}//!divErrTxt else						
												
							}//divErrTxt
							
				}




			}else if(padButton.equals("CE")){		// CE /�����â clear
					result = "";
					result2 = "";
					jlbResult.setText("0");
					number0 = "0";
			}else if(padButton.equals("C")){		// C /AC������� ���� /
					result = "";
					result2 = "";
					result1 = "";			
					jlbResult.setText("0");
					number0 = "";
					number1 = "";
					number2 = "";					// ���� ����

			}else if(padButton.equals("0")){		// 0 �Է�.........
					if(!("".equals(result2))){		//	�����â �� ���� ���
								if(!"0".equals(result2)){			
											
							result2 += padButton;			// 0�ƴ� digit -�ڿ� 0 �߰����
							jlbResult.setText(result2);
								}
								
					}else{									//default �Ǵ� ó�� �Է��� 0�� ��� �״��0 
								result2 = "0";
									
					}
			


		/*	}else if(padButton.equals("��")){
						if("".equals(result2)){
									result2 ="0";		//default�� �״��, 
						}else{
							if(result2.charAt(0)!="-"){
									result2 = "-"+ result2;
							}else{
									result2 = "" + result2;
							}

							
							}
							
						
						}*/
			
			


/*+/-*/		} else if(padButton.equals("+")||padButton.equals("-")||padButton.equals("��")||padButton.equals("��")){			// ��Ģ����opbtn
					if("".equals(result1)){				// record �� �������
							if("".equals(result2)){			///// �Է°��� �������
									if("".equals(result)){			// ����� �������
										//	number[0] = "0";	// 									
											result1 ="0" + padButton; // 0 ������ �;��� 
											number0 = "0";	// result=0

									} else{						//  ������� ���
											result1 = result + padButton;	//record�� =����� +�Է°�
									} // 0����else
						
							}else{												////�Է°��������
									if(!"".equals(result)){						//�Է°�=�����
												result1 = result + padButton;	// �����â = ���+ ��ȣ
									} // �Է°�=�����if
								//	number[0] = result2;
									result1 = result2 + padButton;			// record�� = ��°�+�Է°�
									number0 = result2;					// result = ��°�
							}
							number1 = padButton;
					}else{											// record�� �������
							if("".equals(result2)){					//����°�������
									result1 =result1.substring(0,result1.length()-1)+padButton;	//record�� = record
									number1 = padButton;				// record����
							
							}else{
									//number[0] = result;
									//number[2] = result2;
									result1 += result2 + padButton;		// record�� = ��°�+��ȣ
									number2 = result2;		//		// ��°��� �״��
									result = Cal();						
									number1 = padButton;				// record����
									jlbResult.setText(result);
									number0 = result;					
							}
					
					
					} //��Ģelseif - if~else else	
					if(number1.equals("��")&&(number2.equals("0"))){		// 0���� ���� ��� / Record/&&Result0 
						
					}else{
						result2 = "";
						jlbResult.setText(result1);
						
					}



			}


			
	} //actionPerformed


	public static void main(String[] args){
		new JyCalculator3("�뷡��������");
		}	

	}	//	mainclass







// �����ؾ� �� �� - frame, buttonevent, keyevent, main, calculate
// frame - gridlayout
// button - op,digit,del
//

// ���� ���� recordâ �����ϴµ� ���������� �� ��
// ��ȣ��ȯ ����x
// ������ ���� -
// Long ���� -