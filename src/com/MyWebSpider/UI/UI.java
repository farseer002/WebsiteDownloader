package com.MyWebSpider.UI;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.print.attribute.standard.Finishings;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import ch.randelshofer.quaqua.util.Methods;

import com.MyWebSpider.Main.MyThreadPoolHelper;
import com.MyWebSpider.XSSScan.ReflectedXSSScan;
//import javax.swing.JFrame;

//import ch.randelshofer.quaqua.util.Methods;

public class UI extends JPanel implements ActionListener{
//		PropertyChangeListener {
	
	// http://127.0.0.1:8080/jsoupJavaDoc/ 3
	// http://127.0.0.1:8080/demoForJava/index.html 3

	private static JFrame jf;
	private TextField textFieldIn;
	private JEditorPane jepOut;
	private Choice choDep;
	private JLabel jlaForPic;
	private JButton jbtnOK,jbtnCancel;
	private JProgressBar jpb;
	private JDialog jd11, jd12, jd21;

	private JMenuBar jmb;
	private JMenu jm1;
	private JMenu jm2;
	private JMenuItem jm1i1;
	private JMenuItem jm1i2;
	private JMenuItem jm2i1;


	private JEditorPane jepOut12 ;
	
	// String textOrigin = "enter URL(<50):";
	String textOrigin = "http://127.0.0.1:8080/demoForJava/index.html";

	String browserPath = "D:\\firefox\\firefox.exe";
	PropertyChangeListener jpbListener;
	private static final int JFWIDTH = 750;
	private static final int JFHEIGHT = 600;
	private static final int JDWIDTH = 550;
	private static final int JDHEIGHT = 350;

	private static final String LABEL_URL = "URL";
	private static final String LABEL_Depth = "Depth";

	String url;
	int depth;


	private boolean okFlag =  true;
	
	MyThreadPoolHelper mtph = null;
	ReflectedXSSScan rxs = null;
	/**
	 * 
	 * 检查进度是否完成,并改变进度条的显示
	 *
	 */
	class TaskTest implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (!isFinish()) {
					// System.out.println("run not finish");
					jpb.setIndeterminate(true);
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch
						// block
						e.printStackTrace();
					}
				} else {
					// System.out.println("run finish");
					jpb.setValue(100);
					jpb.setString("100%");
					jpb.setIndeterminate(false);
					
				}
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		
		 if(!isFinish()){
			 System.out.println("action not finish");
			 jpb.setIndeterminate(true);
		 }else{
			 System.out.println("action finish");
			 jpb.setIndeterminate(false);
		 }
		 if (e.getSource() == jbtnOK) {
			 if(isFinish()){
				 url = textFieldIn.getText();
				 depth = choDep.getSelectedIndex() + 1;
				
				 System.out.println(url + " " + depth);
				 // 增加线程
				 try {
					 mtph.addDownLoad(url, depth);
				 } catch (Exception e1) {
					 e1.printStackTrace();
					 return;
				 }
	
				 // 最下面显示当前下载文件路径
				
				 String textAreaOutPrev = new String(jepOut.getText());
				 
				 String textAreaOutFull = new
				 String("<html><a href=\"file:///"+mtph.getDownLoadInfo()+
				 "\">"+mtph.getDownLoadInfo()+"</a><br/>"+textAreaOutPrev.substring(0,textAreaOutPrev.lastIndexOf("</html>"))+"</html>");
				 System.out.println("textAreaOutFull:"+textAreaOutFull);
				 jepOut.setText(textAreaOutFull);
				 
				 System.out.println("mtph:" + mtph.getDownLoadInfo());
				 //进度条
			 }
		 }
		 if(e.getSource() == jbtnCancel){
			 System.out.println("jbtnCancel");
			 mtph.cancel();
			 jepOut.setText("");
			 jpb.setIndeterminate(false);
			 okFlag = false;
			 jd11.setVisible(false);
		 }

	}
	/**
	 * 检查下载工作是否完成
	 * @return 工作是否完成
	 */
	private boolean isFinish() {
		if(mtph == null)
			return true;
		return mtph.isFinish();
	}
	/**
	 * 构造函数,初始化变量并注册监听,同时布局
	 */
	UI() {
		// init
		initConfig();
		initUI();
		//website download
		jm1i1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("jm1i1 action");
				jd11 = new JDialog(jf, "-*- download websites -*-",true);

				JScrollPane jsp = new JScrollPane(jepOut);
				
				jd11.setSize(JDWIDTH, JDHEIGHT);
				jd11.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				jd11.setLocation(jf.getLocation().x + 100,jf.getLocation().y + 100);
				jd11.setResizable(false);

//				jd11.setLayout(new FlowLayout(FlowLayout.LEFT,0, 0));
				jd11.setLayout(null);
				int componentHeight = JDHEIGHT/16;
				int componentWidth = JDWIDTH;
				
				JLabel jl1 = new JLabel(LABEL_URL);
				JLabel jl2 = new JLabel(LABEL_Depth);
				jl1.setFont(new Font("Microsoft YaHei UI",Font.BOLD,14));
				jl2.setFont(new Font("Microsoft YaHei UI",Font.BOLD,14));
				
				jl1.setBounds(0, 0,componentWidth/10,componentHeight);
				jd11.add(jl1);
				textFieldIn.setBounds(jl1.getWidth(),0,componentWidth-jl1.getWidth(),componentHeight);
				jd11.add(textFieldIn);
				
				jl2.setBounds(0,componentHeight + textFieldIn.getY(),componentWidth/10,componentHeight);
				jd11.add(jl2);
				choDep.setBounds(jl2.getWidth(),jl2.getY(),componentWidth-jl2.getWidth(),componentHeight);
				jd11.add(choDep);
				
				jpb.setBounds(0,componentHeight+choDep.getY()+componentHeight/4,componentWidth,componentHeight);
				jd11.add(jpb);
				
				
//				jepOut.setBounds(0,jpb.getY()+componentHeight , componentWidth, JDHEIGHT- jpb.getY()-componentHeight*7/2);
//				jd11.add(jepOut);
				
				jsp.setBounds(0,jpb.getY()+componentHeight , componentWidth, JDHEIGHT- jpb.getY()-componentHeight*7/2);
				jd11.add(jsp);
				
				jbtnOK.setBounds(componentWidth/6,JDHEIGHT- componentHeight*5/2,componentWidth/6,componentHeight*3/2);
				jd11.add(jbtnOK);
				
				jbtnCancel.setBounds(componentWidth*4/6,JDHEIGHT- componentHeight*5/2,componentWidth/6,componentHeight*3/2);
				jd11.add(jbtnCancel);
				
				if(mtph == null)
					mtph = new MyThreadPoolHelper();
				
				jd11.setVisible(true);
			}
		});
		//XSS Scan JDialog
		jm1i2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("jm1i2 action");
				jd12 = new JDialog(jf, "-*- XSS scan -*-",true);

				jd12.setSize(JDWIDTH, JDHEIGHT);
				jd12.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				jd12.setLocation(jf.getLocation().x + 100,jf.getLocation().y + 100);
				jd12.setResizable(false);

				jd12.setLayout(null);
				int componentHeight = JDHEIGHT/16;
				int componentWidth = JDWIDTH;
				
				JLabel jl1 = new JLabel(LABEL_URL);
				jl1.setFont(new Font("Microsoft YaHei UI",Font.PLAIN,14));
				final TextField tf12 = new TextField("");//enter URL
				JButton jbtnOK12 = new JButton("OK!");
				JButton jbtnCancel12 = new JButton("CANCEL!");
				jbtnOK12.setFont(new Font("Microsoft YaHei UI",Font.BOLD,14));
				jbtnCancel12.setFont(new Font("Microsoft YaHei UI",Font.BOLD,14));
			        jepOut12 = new JEditorPane();
				jepOut12.setEditable(false);
				
				final JTextArea textAreaOutput;
				textAreaOutput = new JTextArea("here is the result of XSS Scan\n");
				textAreaOutput.setFont(new Font("Microsoft YaHei UI",Font.PLAIN,18));
				textAreaOutput.setSelectedTextColor(Color.RED);
				textAreaOutput.setLineWrap(true);        //激活自动换行功能 
				textAreaOutput.setWrapStyleWord(true);            // 激活断行不断字功能
				textAreaOutput.setEditable(false);
				JScrollPane jsp = new JScrollPane(textAreaOutput);
				
				
				jl1.setBounds(0,0,componentWidth,componentHeight);
				jd12.add(jl1);
				jl1.setBounds(0, 0,componentWidth/12,componentHeight);
				jd12.add(jl1);
				tf12.setBounds(jl1.getWidth(),0,componentWidth-jl1.getWidth(),componentHeight);
				jd12.add(tf12);
				
				
//				jepOut12.setBounds(0,componentHeight+tf12.getY()+componentHeight/4,componentWidth,JDHEIGHT- tf12.getY()-componentHeight*7/2);
//				jd12.add(jepOut12);
				
//				textAreaOutput.setBounds(0,componentHeight+tf12.getY()+componentHeight/4,componentWidth,JDHEIGHT- tf12.getY()-componentHeight*7/2-componentHeight/4);
//				jd12.add(textAreaOutput);
				
				jsp.setBounds(0,componentHeight+tf12.getY()+componentHeight/4,componentWidth,JDHEIGHT- tf12.getY()-componentHeight*7/2-componentHeight/4);
				jd12.add(jsp);
				
				jbtnOK12.setBounds(componentWidth/6,JDHEIGHT- componentHeight*5/2,componentWidth/6,componentHeight*3/2);
				jd12.add(jbtnOK12);
				
				jbtnCancel12.setBounds(componentWidth*4/6,JDHEIGHT- componentHeight*5/2,componentWidth/6,componentHeight*3/2);
				jd12.add(jbtnCancel12);
				
				
				
				
			        rxs = new ReflectedXSSScan();
				jbtnOK12.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						System.out.println("jbtnOK12 action");
						String urlTest = tf12.getText();
						System.out.println("urlTest:"+urlTest);
						if(urlTest != null && !urlTest.trim().equals("") && urlTest.contains("?")){
							if(rxs.isFinish()){
								rxs.checkUrl(urlTest.trim());
								System.out.println("urlTest XSS point found :"+urlTest);
								
	//							jepOut12.setText(rxs.LogToString());
								textAreaOutput.setText(rxs.LogToString());
							}
						}else{
//							jepOut12.setText("");
							textAreaOutput.setText("");
						}
					}
				});
				
				jbtnCancel12.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						jd12.setVisible(false);
					}
				});
				
				jd12.setVisible(true);
			}
		});
		
		//help
		jm2i1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("jm2i1 action");
				jd21 = new JDialog(jf, "-*- Help -*-",true);

				jd21.setSize(JDWIDTH, JDHEIGHT);
				jd21.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				
				jd21.setLocation(jf.getLocation().x + 100,jf.getLocation().y + 100);
				jd21.setResizable(false);

				jd21.setLayout(null);
				int componentHeight = JDHEIGHT/16;
				int componentWidth = JDWIDTH;
				
				JButton jbtnOK21 = new JButton("OK!");
				jbtnOK21.setFont(new Font("Microsoft YaHei UI",Font.BOLD,14));

				JTextArea textAreaOutput;
				
				String textContent = "Website downloader\n"+
								   "Build v0.8.9\n" + 
								   "This product includes website download, xss scan,\n"+
								   "all of which are Copyright(c) Website downloader contributors and others\n";
								
				
				textAreaOutput = new JTextArea(textContent);
				textAreaOutput.setFont(new Font("Microsoft YaHei UI",Font.PLAIN,18));
				textAreaOutput.setSelectedTextColor(Color.BLUE);
				textAreaOutput.setLineWrap(true);        //激活自动换行功能 
				textAreaOutput.setWrapStyleWord(true);            // 激活断行不断字功能
				textAreaOutput.setEditable(false);
				JScrollPane jsp = new JScrollPane(textAreaOutput);
				
				
				jsp.setBounds(-2,0,componentWidth+2,JDHEIGHT-componentHeight*6/2);
				jd21.add(jsp);
				jbtnOK21.setBounds(componentWidth*4/5, JDHEIGHT-componentHeight*5/2, componentWidth/6, componentHeight*7/5);
				jd21.add(jbtnOK21);
				
				jbtnOK21.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						jd21.setVisible(false);
					}
				});
				
				jd21.setVisible(true);
			}
		});

		
		
		

		
		TaskTest tt = new TaskTest();
		Thread t = new Thread(tt);
		t.start();
	}
	
	/**
	 * 初始化UI
	 */
	public void initUI(){
		this.setLayout(null);
		
		jmb = new JMenuBar();
		jm1 = new JMenu();
		jm2 = new JMenu();
		jm1i1 = new JMenuItem();
		jm1i2 = new JMenuItem();
		jm2i1 = new JMenuItem();

		jmb = new JMenuBar();
		jm1 = new JMenu("File(F)");
		jm2 = new JMenu("Help(H)");

		// set the shortcut
		jm1.setMnemonic('F');
		jm2.setMnemonic('H');

		jm1i1 = new JMenuItem("New(N)");
		jm1i2 = new JMenuItem("XSS Scan(X)");
		jm2i1 = new JMenuItem("About(A)");
		jm1i1.setMnemonic('N');
		jm1i2.setMnemonic('X');
		jm2i1.setMnemonic('A');

		jm1.add(jm1i1);
		jm1.add(jm1i2);
		jm2.add(jm2i1);
		jmb.add(jm1);
		jmb.add(jm2);

		
		choDep = new Choice();
		textFieldIn = new TextField(textOrigin, 80);
		textFieldIn.setFont(new Font("Microsoft YaHei UI",Font.PLAIN,14));
		
		jepOut = new JEditorPane();
		jepOut.setContentType("text/html");
		jepOut.setEditable(false);
		jepOut.setFont(new Font("Microsoft YaHei UI",Font.PLAIN,14));
//		textFieldOut = new TextField(80);
//		textFieldOut.setEditable(false);
	
		jbtnOK = new JButton("OK!");
		jbtnCancel = new JButton("CANCEL!");
		jbtnOK.setFont(new Font("Microsoft YaHei UI",Font.BOLD,14));
		jbtnCancel.setFont(new Font("Microsoft YaHei UI",Font.BOLD,14));
		
		jpb = new JProgressBar();

		
		// add
		for (int i = 1; i < 6; ++i)
			choDep.add(String.valueOf(i));

		// add listener
		jbtnOK.addActionListener(this);
		jbtnCancel.addActionListener(this);
		
		textFieldIn.addFocusListener(new FocusListener(){

			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				if (textFieldIn.getText().equals("")) {
					textFieldIn.setText(textOrigin);
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				if (textFieldIn.getText().equals(textOrigin)) {
					textFieldIn.setText("");
				}
			}
		});

		jepOut.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent arg0) {
				// TODO Auto-generated method stub
				if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

					System.out.println("cmd /c \""
							+ browserPath
							+ "\"  \""
							+ arg0.getURL() + "\"");
					// exec
					try {
						String tempResultFilePath = " \""
								+ arg0.getURL()
								+ "\"";
						// Runtime.getRuntime().exec(
						// browserPath+"  "+tempResultFilePath);

						Runtime.getRuntime()
								.exec(browserPath
										+ " "
										+ tempResultFilePath);
					} catch (IOException e) {
						// block
						JOptionPane.showMessageDialog(null, "fail to open the browser,please check the config,txt");
						e.printStackTrace();
					}
				}
			}
		});


		jf.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
				
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
		
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {

			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			
			}
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				if (mtph != null) {
					mtph.destory();
					System.out.println("shutdown mtph");
				}
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				if (mtph != null) {
					mtph.destory();
					System.out.println("shutdown mtph");
				}
				System.exit(-1);
			}
			@Override
			public void windowActivated(WindowEvent arg0) {

			}
		});
		
		jmb.setBounds(0,0,JFWIDTH*11/2,JFHEIGHT/20);
		this.add(jmb);
		ImageIcon coverPic = new ImageIcon("MyImage/cover.png");
		coverPic.setImage(coverPic.getImage().getScaledInstance(JFWIDTH,JFHEIGHT*18/19, Image.SCALE_DEFAULT));
//		jpPic.add(new JLabel(coverPic));
		JLabel jlPic = new JLabel(coverPic);
		jlPic.setBounds(0,JFHEIGHT/20,JFWIDTH,JFHEIGHT*19/20);
		this.add(jlPic);
	}
	/**
	 * 初始化浏览器地址
	 */
	public void initConfig(){
		
		File file = new File("config.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		String tempString = null;
		try {
			while ((tempString = reader.readLine()) != null) {
				if(tempString.trim().equals(""))continue;
				if(tempString.trim().matches("^([C-Hc-h])[:\\\\](.*)(\\.exe)$")){
					browserPath = tempString.trim();
				}else{
					String alertString = "the browser path is not valid, please check the config.txt";
					System.out.println(alertString);
//					JOptionPane jop = new JOptionPane();
					JOptionPane.showMessageDialog(null, alertString);
					
				}
				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	public static void main(String args[]) {
		System.setProperty("Quaqua.tabLayoutPolicy", "wrap");

		if (!System.getProperty("os.name").toLowerCase()
				.startsWith("mac")) {// 不是max机器,设定JFrame,jDialog的样式
			try {
				Methods.invokeStatic(
						JFrame.class,
						"setDefaultLookAndFeelDecorated",
						Boolean.TYPE, Boolean.TRUE);
				Methods.invokeStatic(
						JDialog.class,
						"setDefaultLookAndFeelDecorated",
						Boolean.TYPE, Boolean.TRUE);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		try {
			// JFrame.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		
		
		jf = new JFrame("-*- Website Downloader -*-");
		jf.setSize(JFWIDTH, JFHEIGHT);
		jf.setContentPane(new UI());

		jf.setResizable(false);

		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}

}
