package com.MyWebSpider.UI;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executor;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.MyWebSpider.Main.MyThreadPoolHelper;

import ch.randelshofer.quaqua.util.Methods;
//import javax.swing.JFrame;

//import ch.randelshofer.quaqua.util.Methods;

public class OldUI extends JPanel implements ActionListener,PropertyChangeListener  {

	//http://127.0.0.1:8080/jsoupJavaDoc/ 3
	//http://127.0.0.1:8080/demoForJava/index.html 3

	private static JFrame jf;
	private TextField textFieldIn, textFieldOut;
	private JEditorPane jepOut;
	private Choice choDep;
	private JLabel jlaForPic;
	private JButton jbtnOK;
	private JProgressBar jpb;
	// String textOrigin = "enter URL(<50):";
	String textOrigin = "http://127.0.0.1:8080/demoForJava/index.html";
	String picOriginPath = "MyImage/acmusume/";
	String browserPath = "D:\\firefox\\firefox.exe";
	PropertyChangeListener jpbListener ;

	
	private static final String LABEL_URL = "URL";
	private static final String LABEL_Depth = "Depth";


	String url;
	int depth;

	private boolean firstFlag = true;
	MyThreadPoolHelper mtph;
	class TaskTest implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				if(!isFinish()){
//					System.out.println("run not finish");
					jpb.setIndeterminate(true);
					jpb.setValue(100);  
			                jpb.setString("100%");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
//					System.out.println("run finish");
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
		// TODO Auto-generated method stub
		if(!isFinish()){
			System.out.println("action not finish");
			jpb.setIndeterminate(true);
		}else{
			System.out.println("action finish");
			jpb.setIndeterminate(false);
		}
		if (e.getSource() == jbtnOK) {
			// 新建线程池
			if (firstFlag) {
				mtph = new MyThreadPoolHelper();
				firstFlag = false;
			}
			url = textFieldIn.getText();
			depth = choDep.getSelectedIndex() + 1;

			System.out.println(url + " " + depth);
			// 增加线程
			try {
				mtph.addDownLoad(url, depth);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			// 出图像
			Random r = new Random();
			int n = Math.abs(r.nextInt() % 54) + 1;
			String tempPath;
			if (n < 10)
				tempPath = new String("0" + String.valueOf(n));
			else
				tempPath = new String(String.valueOf(n));
			tempPath = tempPath + ".png";
			
			jlaForPic.setIcon(new ImageIcon(picOriginPath + tempPath));
			// 最下面显示当前下载文件路径
			
//			String textFieldOutPrev = new String(textFieldOut.getText());
//			
//			textFieldOut.setText(mtph.getDownLoadInfo()+"\n" + textFieldOutPrev);
			
			String textAreaOutPrev = new String(jepOut.getText());
			String textAreaOutFull = new String("<html><a href=\"file:///"+mtph.getDownLoadInfo()+
					"\">"+mtph.getDownLoadInfo()+"</a><br/>"+textAreaOutPrev.substring(0,textAreaOutPrev.lastIndexOf("</html>"))+"</html>");
			System.out.println("textAreaOutFull:"+textAreaOutFull);
			jepOut.setText(textAreaOutFull);
			
			
			
			System.out.println("mtph:" + mtph.getDownLoadInfo());
			//进度条
			
				
			
		}
		//按下jframe的关闭按钮时,关闭线程池pool.shutdown()
	}

	private String getBoldHTML(String s) {
		return "<html><b>" + s + "</b></html>";
	}

	private JComponent layoutProgress(){
		JComponent result = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridheight = GridBagConstraints.RELATIVE;
		

		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 5, 5);
		result.add(jpb, gbc);

	
		return result;
	}
	
	private JComponent layoutOutput(){
		JComponent result = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridheight = GridBagConstraints.RELATIVE;
		

		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 5, 5);
		

		result.add(jepOut);
		
		
	
		return result;
	}
	
	/**
	 * 包含图片和按钮的部分
	 * @return
	 */
	private JComponent layoutControl() {
		JComponent result = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 5, 5);
			
		result.add(jlaForPic,gbc);
		++gbc.gridx;
		result.add(jbtnOK,gbc);
		++gbc.gridx;
//		result.add(jpb,gbc);
//		++gbc.gridy;
//		gbc.gridx = 0;
//		result.add(jepOut);
		
		return result;
	}
	/**
	 * 包含URL和depth的部分
	 * @return
	 */
	private JComponent layoutFields() {
		JComponent result = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 5, 5);
		result.add(new JLabel(getBoldHTML(LABEL_URL)), gbc);
		gbc.gridy++;
		result.add(new JLabel(getBoldHTML(LABEL_Depth)), gbc);

		gbc.gridx++;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		result.add(textFieldIn, gbc);
		gbc.gridy++;
		result.add(choDep, gbc);

		return result;

	}
	private boolean isFinish(){
		if(firstFlag)return false;
		return mtph.isFinish();
	}
	
	OldUI() {
		// init
//		super(new BorderLayout(6, 6));
		super(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		
//		jlaURL = new JLabel("URL");
//		jlaDepth = new JLabel("Depth");
		choDep = new Choice();
		textFieldIn = new TextField(textOrigin, 80);
		
		jepOut = new JEditorPane();
		jepOut.setContentType("text/html");
		jepOut.setEditable(false);
		textFieldOut = new TextField(80);
		textFieldOut.setEditable(false);
		
		ImageIcon icon = new ImageIcon(picOriginPath + "02.png");
		jlaForPic = new JLabel(icon);
		jbtnOK = new JButton("OK!");

		jbtnOK.setSize(icon.getIconWidth() / 2, icon.getIconHeight() / 2);
		
		
		jpb = new JProgressBar();
		/*jpbListener = new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				// TODO Auto-generated method stub
				if(isFinish()){
				            jpb.setIndeterminate(false);   
				            jpb.setValue(100);  
				            jpb.setString("100%");
				            System.out.println("PropertyChange isFinish");
				}else{
					jpb.setIndeterminate(true);
					System.out.println("PropertyChange not finish");
				}
			}
		};
		jpb.addPropertyChangeListener(jpbListener);
		*/
		
		// add
		for (int i = 1; i < 6; ++i)
			choDep.add(String.valueOf(i));

		// add listener
		jbtnOK.addActionListener(this);
		textFieldIn.addFocusListener(new FocusListener() {

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
				             
				                 
					 System.out.println("cmd /c \"" + browserPath+"\"  \""+arg0.getURL()+"\"");
					 //exec
					 try {
						String tempResultFilePath = " \""+arg0.getURL()+"\"";
//						Runtime.getRuntime().exec( browserPath+"  "+tempResultFilePath);
						
						Runtime.getRuntime().exec(browserPath+" " + tempResultFilePath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }       
			}
		});
		textFieldOut.addActionListener(this);
		
//		add(layoutFields(),BorderLayout.NORTH);
//		add(layoutControl(), BorderLayout.CENTER);
		
		add(layoutFields(),gbc);
		++gbc.gridy;
		add(layoutControl(),gbc);
		++gbc.gridy;
		add(layoutProgress(),gbc);
		++gbc.gridy;
		add(layoutOutput(),gbc);
		
//		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		
		jf.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				if(mtph != null){
					mtph.destory();
					System.out.println("shutdown mtph");
				}
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				if(mtph != null){
					mtph.destory();
					System.out.println("shutdown mtph");
				}
				System.exit(-1);
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		TaskTest tt = new TaskTest();
		Thread t = new Thread(tt);
		t.start();
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		if(!isFinish()){
			System.out.println("in stateChanged not finish");
			jpb.setIndeterminate(true);
		}else{
			System.out.println("in stateChanged finish");
			jpb.setIndeterminate(false);
		}	
	}
	
	
	public static void main(String args[]) {
		System.setProperty("Quaqua.tabLayoutPolicy", "wrap");

		if (!System.getProperty("os.name").toLowerCase().startsWith("mac")) {//不是max机器,设定JFrame,jDialog的样式
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
			UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
		} catch (Exception e) {
//			e.printStackTrace();
		}

		jf = new JFrame("-*- 整站下载器 -*-");
		jf.setSize(700, 600);
		jf.setContentPane(new OldUI());
		
		
		
		jf.setResizable(true);
		
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}


	
}
