package org.yamaLab.TweetByWikiEx2;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.yamaLab.TwitterConnector.Util;
import org.yamaLab.pukiwikiCommunicator.CommandReceiver;
import org.yamaLab.pukiwikiCommunicator.MainController;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
/* 
command: tweet "#test hello!" when "*,*,*,*,5,*"
 
 */
public class TweetByWikiEx2Gui extends JFrame implements CommandReceiver, ClassWithJTable
{
	private final JTabbedPane mainTabPane ;	
	App app;
	JTextArea messageArea;
	JTextArea tweetTextArea ;
	public Properties setting;
	String settingFileName="TweetByWikiEx2.properties";
	MainController mainController;
	
	private JLabel urlLabel;
	private JButton disConnectButton;
	private JButton clearCommandButton;
	private JScrollPane commandAreaPane;
	private JLabel resultLabel;
	private JComboBox readIntervalCombo;
	private JComboBox execIntervalCombo;
	private JComboBox returnIntervalCombo;
	private JLabel commandIntervalLabel;
	private JLabel execIntervalLabel;
	private JLabel returnIntervalLabel;
	private JLabel pukiwikiMessageLabel;
	private JTextArea resultArea;
	private JScrollPane resultPane;
	private JScrollPane messageAreaScrollPane;
	private JTextArea pukiwikiMessageArea;
	private JTextArea commandArea;
	private JLabel commandLabel;
	public JToggleButton wikiConnectButton;
	private JTextField wikiUrlTextField;
    private JRadioButton showDebuggerButton;
    public JCheckBox onlineCommandRefreshButton;
    public JToggleButton startWatchingButton;
    private JButton endWatchingButton;
	private JTable commandTable;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TweetByWikiEx2Gui frame = new TweetByWikiEx2Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * Create the frame.
	 */
	public void init(){
		this.loadProperties();
//		vtraffic = new VisualTrf[256][256];
		this.setProperties();	
		this.mainController=new MainController(this, this, setting);				
	}
	
	public TweetByWikiEx2Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 700);
		mainTabPane = new JTabbedPane();
		getContentPane().add(mainTabPane);
		mainTabPane.setBounds(3, 43, 800, 700);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveProperties();
			    System.exit(0);
			}
		});
		initTwitterGui();
		twitterAuthSettingGui();
		init();
	}
	
	public void initTwitterGui(){
		JPanel mainPanel= new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(null);
		mainTabPane.add("mainPanel",mainPanel);

		JButton btnConnectTwitter = new JButton("Connect Twitter");
		btnConnectTwitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Connect Twitter");
//				connectTwitter();
				mainController.parseCommand("twitterConnect", "");
			}
		});
		btnConnectTwitter.setBounds(6, 0, 165, 29);
		mainPanel.add(btnConnectTwitter);
		
		messageAreaScrollPane = new JScrollPane();
		messageAreaScrollPane.setBounds(110, 470, 550, 150);
		mainPanel.add(messageAreaScrollPane);
		
		messageArea = new JTextArea();
		messageAreaScrollPane.setViewportView(messageArea);		
		
		JLabel lblMessage = new JLabel("Message:");
		lblMessage.setBounds(6, 470, 101, 16);
		mainPanel.add(lblMessage);
		
		JScrollPane tweetScrollPane = new JScrollPane();
		tweetScrollPane.setBounds(180, 0, 225, 42);
		mainPanel.add(tweetScrollPane);
		
		JButton btnTweet = new JButton("Tweet");
		btnTweet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				System.out.println("Tweet");
				String x=tweetTextArea.getText();
//				int rtn=tweet(x);
//				if(rtn==1){
				mainController.parseCommand("twitterTweet", x);
					tweetTextArea.setText("");
//				}
			}
		});
		btnTweet.setBounds(432, 0, 90, 42);
		mainPanel.add(btnTweet);
		
		tweetTextArea = new JTextArea();
		tweetScrollPane.setViewportView(tweetTextArea);
		
		JLabel lblCommand = new JLabel("command:");
		lblCommand.setBounds(6, 50, 112, 16);
		mainPanel.add(lblCommand);
		
		JScrollPane commandScrollPane = new JScrollPane();
		commandScrollPane.setBounds(180, 50, 225, 42);
		mainPanel.add(commandScrollPane);
		
		JTextArea commandTextArea = new JTextArea();
		commandScrollPane.setViewportView(commandTextArea);
		
		JButton btnEnter = new JButton("enter");
		btnEnter.setBounds(432, 50, 90, 47);
		mainPanel.add(btnEnter);
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("enter");
			}
		});
		
//		tweetTextArea.setBounds(108, 219, -57, -32);
//		contentPane.add(tweetTextArea);
//  ---------------------------------------
//
		{
			urlLabel = new JLabel();
			mainPanel.add(urlLabel);
			urlLabel.setText("manager url:");
			urlLabel.setBounds(1, 95, 105, 24);
		}
		{
			wikiUrlTextField = new JTextField();
			mainPanel.add(wikiUrlTextField);
			wikiUrlTextField.setBounds(95, 95, 446, 30);
		}
		{
			wikiConnectButton = new JToggleButton();
			mainPanel.add(wikiConnectButton);
			wikiConnectButton.setText("connect");
			wikiConnectButton.setBounds(541, 95, 110, 30);
			wikiConnectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//							mainController.connectButtonActionPerformed(null);
					 if(wikiConnectButton.isSelected()){
						   if(onlineCommandRefreshButton.isSelected()){
//							            debugger.readFromPukiwikiPageAndSetData(urlTextField.getText());
						        String urlx=wikiUrlTextField.getText();
						        setting.setProperty("managerUrl", urlx);
						        saveProperties();
							    mainController.parseCommand("wikiConnectButton Clicked", urlx);
							    repaint();
//							            setting.setProperty("managerUrl", urlTextField.getText());
						   }
					 }
					 else{
					       if(!onlineCommandRefreshButton.isSelected()){
	    		  
					       } 
			        }						
					
				}
			});
		}
		{
			disConnectButton = new JButton();
			mainPanel.add(disConnectButton);
			disConnectButton.setText("Disconnect");
			disConnectButton.setBounds(651, 95, 120, 30);
			disConnectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					disConnectButtonActionPerformed(evt);
				}
			});
		}
		{
			commandLabel = new JLabel();
			mainPanel.add(commandLabel);
			commandLabel.setText("command:");
			commandLabel.setBounds(1, 162, 109, 33);
		}
		{
			commandAreaPane = new JScrollPane();
			mainPanel.add(commandAreaPane);
			commandAreaPane.setBounds(110, 163, 550, 200);
			{
				/*
				commandArea = new JTextArea();
				commandAreaPane.setViewportView(commandArea);
				*/
				String [] oneComLine=new String[]{"",""};
				String [][] comLines =new String[maxCommands][];
				for(int i=0;i<maxCommands;i++){
					comLines[i]=new String[]{"",""};
				}
				DefaultTableModel tableModel= new DefaultTableModel(
						comLines ,
						new String[] { "No","Command" });
				
				commandTable = new JTable();
				commandTable.setModel(tableModel);
				commandAreaPane.setViewportView(commandTable);
			}
		}
		{
			resultLabel = new JLabel();
		    mainPanel.add(resultLabel);
			resultLabel.setText("result:");
			resultLabel.setBounds(0, 380, 105, 29);
		}
		{
			resultPane = new JScrollPane();
			mainPanel.add(resultPane);
			resultPane.setBounds(111, 380, 550, 80);
			{
				resultArea = new JTextArea();
				resultPane.setViewportView(resultArea);
//				resultArea.setPreferredSize(new java.awt.Dimension(547, 79));
			}
		}
		{
			pukiwikiMessageLabel = new JLabel();
			mainPanel.add(pukiwikiMessageLabel);
			pukiwikiMessageLabel.setText("message:");
			pukiwikiMessageLabel.setBounds(2, 400, 101, 28);
		}
		{
			commandIntervalLabel = new JLabel();
			mainPanel.add(commandIntervalLabel);
			commandIntervalLabel.setText("read interval:");
			commandIntervalLabel.setBounds(0, 124, 110, 29);
		}
		{
			String[] interval={"10-sec","30-sec","1-min","5-min","10-min","30-min","1-h","12-h","24-h",""};
			readIntervalCombo = new JComboBox(interval);
			mainPanel.add(readIntervalCombo);
			readIntervalCombo.setBounds(100, 127, 100, 24);
			readIntervalCombo.setSelectedIndex(0);
			readIntervalCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					readIntervalComboActionPerformed(evt);
				}
			});
		}
		{
			execIntervalLabel = new JLabel();
			mainPanel.add(execIntervalLabel);
			execIntervalLabel.setText("exec interval:");
			execIntervalLabel.setBounds(210, 124, 100, 29);
		}
		
		{
			String[] interval={"0.1-sec","0.5-sec","1-sec","2-sec","5-sec","10-sec","30-sec","1-min","5-min","10-min","30-min","1-h","12-h","24-h",""};
			execIntervalCombo = new JComboBox(interval);
			mainPanel.add(execIntervalCombo);
			execIntervalCombo.setBounds(310, 127, 100, 24);
			execIntervalCombo.setSelectedIndex(0);
			execIntervalCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					execIntervalComboActionPerformed(evt);
				}
			});
		}
		{
			returnIntervalLabel = new JLabel();
			mainPanel.add(returnIntervalLabel);
			returnIntervalLabel.setText("return interval:");
			returnIntervalLabel.setBounds(410, 124, 100, 26);
		}
		{
			String[] interval={"10-sec","30-sec","1-min","5-min","10-min","30-min","1-h","12-h","24-h",""};
			returnIntervalCombo = new JComboBox(interval);
			mainPanel.add(returnIntervalCombo);
			returnIntervalCombo.setBounds(520, 126, 100, 25);
			returnIntervalCombo.setSelectedIndex(0);
			returnIntervalCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					returnIntervalComboActionPerformed(evt);
				}
			});
		}
		{
			showDebuggerButton = new JRadioButton();
			mainPanel.add(showDebuggerButton);
			showDebuggerButton.setText("show debugger");
			showDebuggerButton.setBounds(527, 80, 164, 18);
			showDebuggerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					debuggerButtonActionPerformed(evt);
					mainController.parseCommand("wikiDebuggerButton Clicked", "");
				}
			});
			
		}
		{
			onlineCommandRefreshButton = new JCheckBox();
			mainPanel.add(onlineCommandRefreshButton);
			onlineCommandRefreshButton.setText("online refresh");
			onlineCommandRefreshButton.setBounds(629, 129, 135, 25);
			onlineCommandRefreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					onlineCommandRefreshButtonActionPerformed(evt);
					mainController.parseCommand("onlineCommandRefresh", ""+(onlineCommandRefreshButton.isSelected()));
				}
			});			
		}
		{
			startWatchingButton = new JToggleButton();
			mainPanel.add(startWatchingButton);
			startWatchingButton.setText("Start");
			startWatchingButton.setBounds(662, 180, 80, 25);
			startWatchingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					startWatchingButtonActionPerformed(evt);
					mainController.parseCommand("wikiStartWatching", "");
				}
			});
		}
		{
			endWatchingButton = new JButton();
			mainPanel.add(endWatchingButton);
			endWatchingButton.setText("End");
			endWatchingButton.setBounds(662, 200, 80, 25);
			endWatchingButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
//					endWatchingButtonActionPerformed(evt);
					mainController.parseCommand("wikiEndWatching", "");
				}
			});
		}
		{
			clearCommandButton = new JButton();
			mainPanel.add(clearCommandButton);
			clearCommandButton.setText("Clear");
			clearCommandButton.setBounds(662, 220, 80, 25);
			clearCommandButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					clearCommandButtonActionPerformed(evt);
				}
			});
		}
		JButton savePropertiesButton = new JButton("SaveProperties");
		savePropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save Properties");
//				connectTwitter();
				reflectProperties();
				saveProperties();
			}
		});
		savePropertiesButton.setBounds(530, 0, 165, 29);
		mainPanel.add(savePropertiesButton);
		
		{
//		   mainPanel.setSize(600, 700);
//		   mainPanel.setPreferredSize(new java.awt.Dimension(788, 700));
		}
		this.setVisible(false);
//		this.setSize(804, 700);
		
	}
	
	JLabel consumerKeyLabel;
	JLabel consumerSecretLabel;
	JLabel accessTokenLabel;
	JLabel accessTokenSecretLabel;
	JTextField consumerKeyTextField;
	JTextField consumerSecretTextField;
	JTextField accessTokenTextField;
	JTextField accessTokenSecretTextField;
	
	public void twitterAuthSettingGui(){
		try{
		JPanel mainPanel= new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(null);
		
	    mainPanel.setLayout(null);
		if(this.mainTabPane!=null) {
			this.mainTabPane.add("TwitterAuth",mainPanel);
		}
		else{
			mainPanel.setLayout(null);
			mainPanel.add(this);
		}
		{
			consumerKeyLabel = new JLabel();
			mainPanel.add(consumerKeyLabel);
			consumerKeyLabel.setText("Comsumer Key:");
			consumerKeyLabel.setBounds(1, 30, 105, 24);
		}
		{
			consumerKeyTextField = new JTextField();
			mainPanel.add(consumerKeyTextField);
			consumerKeyTextField.setBounds(120, 30, 446, 30);
		}
		{
			consumerSecretLabel = new JLabel();
			mainPanel.add(consumerSecretLabel);
			consumerSecretLabel.setText("Comsumer Secret:");
			consumerSecretLabel.setBounds(1, 55, 120, 24);
		}
		{
			consumerSecretTextField = new JTextField();
			mainPanel.add(consumerSecretTextField);
			consumerSecretTextField.setBounds(120, 55, 446, 30);
		}
		{
			accessTokenLabel = new JLabel();
			mainPanel.add(accessTokenLabel);
			accessTokenLabel.setText("Access Token:");
			accessTokenLabel.setBounds(1, 80, 105, 24);
		}
		{
			accessTokenTextField = new JTextField();
			mainPanel.add(accessTokenTextField);
			accessTokenTextField.setBounds(120, 80, 446, 30);
		}
		{
			accessTokenSecretLabel = new JLabel();
			mainPanel.add(accessTokenSecretLabel);
			accessTokenSecretLabel.setText("AccessToken Secret:");
			accessTokenSecretLabel.setBounds(1, 105, 120, 24);
		}
		{
			accessTokenSecretTextField = new JTextField();
			mainPanel.add(accessTokenSecretTextField);
			accessTokenSecretTextField.setBounds(120, 105, 446, 30);
		}
		JButton savePropertiesButton = new JButton("SaveProperties");
		savePropertiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save Properties");
//				connectTwitter();
				reflectProperties();
				saveProperties();
			}
		});
		savePropertiesButton.setBounds(530, 0, 165, 29);
		mainPanel.add(savePropertiesButton);
		
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
//			this.setTitle("PukiwikiCommunicator");
		
	}
	

	private void readIntervalComboActionPerformed(ActionEvent evt) {
//		System.out.println("commandIntervalCombo.actionPerformed, event="+evt);
		//TODO add your code for commandIntervalCombo.actionPerformed
		this.setting.setProperty("readInterval", (String)(this.readIntervalCombo.getSelectedItem()));
	}
	private void execIntervalComboActionPerformed(ActionEvent evt) {
//		System.out.println("commandIntervalCombo.actionPerformed, event="+evt);
		//TODO add your code for commandIntervalCombo.actionPerformed
		this.setting.setProperty("execInterval", (String)(this.execIntervalCombo.getSelectedItem()));
	}
	
	private void returnIntervalComboActionPerformed(ActionEvent evt) {
//		System.out.println("returnIntervalCombo.actionPerformed, event="+evt);
		//TODO add your code for returnIntervalCombo.actionPerformed
		this.setting.setProperty("returnInterval", (String)(this.returnIntervalCombo.getSelectedItem()));
	}
	
	private void clearCommandButtonActionPerformed(ActionEvent evt) {
//		System.out.println("clearCommandButton.actionPerformed, event="+evt);
		//TODO add your code for clearCommandButton.actionPerformed
		for(int i=0;i<maxCommands;i++){
			this.commandTable.setValueAt("", i, 0);
			this.commandTable.setValueAt("", i, 1);
		}
	}
	
	private void disConnectButtonActionPerformed(ActionEvent evt) {
//		System.out.println("disConnectButton.actionPerformed, event="+evt);
		//TODO add your code for disConnectButton.actionPerformed
		this.wikiConnectButton.setSelected(false);
	}
	
	private int maxCommands=100;	
	    
    String TWITTER_CONSUMER_KEY    = "取得したコードを入力";
    String TWITTER_CONSUMER_SECRET = "取得したコードを入力";
     
    String TWITTER_ACCESS_TOKEN        = "取得したコードを入力";
    String TWITTER_ACCESS_TOKEN_SECRET = "取得したコードを入力";
//    Properties configuration = new Properties();       
    Twitter twitter;
    MainController pukiwikiCom;
	private Vector<String> putMessageQueue=new Vector();
	/**
	 * @wbp.nonvisual location=61,1
	 */
	public void putMessage(String x){
		putMessageQueue.add(x);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	String so=putMessageQueue.remove(0);
                messageArea.append(so+"¥n");
            }
    });

	}
	public void loadProperties(){
	       try {
	           setting = new Properties() ;
	           FileInputStream appS = new FileInputStream( settingFileName);
	           setting.load(appS);

	        } catch( Exception e){
//	           System.err.println(e);
		        setting = new Properties() ;
		        setProperties();
//	        	return;
	        } 
	}
	public void saveProperties(){
	       
	       try {
	           FileOutputStream saveS = new FileOutputStream(settingFileName);
	           if(setting==null){
	        	   setting=new Properties();
	           }
	           reflectProperties();
	           setting.store(saveS,"--- tweet-by-wiki settings ---");

	        } catch( Exception e){
	           System.err.println(e);
	        } 
	}
	
	public void setProperties(){
		if(this.setting==null)return;
		String w=this.setting.getProperty("managerUrl");
		if(w!=null)
		   this.wikiUrlTextField.setText(w);
		w=this.setting.getProperty("onlineCommandRefresh");
		if(w!=null){
			if(w.equals("true"))
		       this.onlineCommandRefreshButton.setSelected(true);
			else
				this.onlineCommandRefreshButton.setSelected(false);
		}
		w=this.setting.getProperty("readInterval");
		if(w!=null)
			this.readIntervalCombo.setSelectedItem(w);
		w=this.setting.getProperty("execInterval");
		if(w!=null)
			this.execIntervalCombo.setSelectedItem(w);
		w=this.setting.getProperty("returnInterval");
		if(w!=null)
			this.returnIntervalCombo.setSelectedItem(w);	
		w=this.setting.getProperty("maxCommandsStr");
		if(w==null){
			setting.put("maxCommandsStr",""+this.maxCommands);
		}
        w =this.setting.getProperty("oauth.consumerKey");
        if(w!=null){
        	this.consumerKeyTextField.setText(w);
        }
        w =this.setting.getProperty("oauth.consumerSecret");
        if(w!=null){
        	this.consumerSecretTextField.setText(w);
        }
        w =this.setting.getProperty("oauth.accessToken");
        if(w!=null){
        	this.accessTokenTextField.setText(w);
        }
        w =this.setting.getProperty("oauth.accessTokenSecret");
        if(w!=null){
        	this.accessTokenSecretTextField.setText(w);
        }		
	}
	public void reflectProperties(){
		if(this.setting==null)return;
		setting.put("managerUrl", this.wikiUrlTextField.getText());
		boolean selected=this.onlineCommandRefreshButton.isSelected();
		setting.put("onlineCommandRefresh", ""+selected);
		setting.put("readInterval", this.readIntervalCombo.getSelectedItem());
        setting.put("execInterval", this.execIntervalCombo.getSelectedItem());
		setting.put("returnInterval", this.returnIntervalCombo.getSelectedItem());
		setting.put("oauth.consumerKey", this.consumerKeyTextField.getText());
		setting.put("oauth.consumerSecret", this.consumerSecretTextField.getText());
		setting.put("oauth.accessToken", this.accessTokenTextField.getText());
		setting.put("oauth.accessTokenSecret", this.accessTokenSecretTextField.getText());
	}
	public String command(String x, String v) {
		// TODO Auto-generated method stub
		if(x.equals("wikiMessage")){
			this.writeMessage(v);
		}
		else
		if(x.equals("getWikiUrl")){
			String rtn=this.wikiUrlTextField.getText();
			return rtn;
		}
		else
		if(x.startsWith("wikiCommandTable setValueAt ")){
			String p0=x.substring("wikiCommandTable setValueAt ".length());
			String[] rest=new String[1];
			String[] sconst=new String[1];
			int[] iv2=new int[1];
			int[] iv3=new int[1];
			if(!Util.parseInt(p0,iv2,rest)) return "error";
			String p1=Util.skipSpace(rest[0]);
			if(!Util.parseInt(p1, iv3, rest)) return "error";
			int i=iv2[0];
			int j=iv3[0];
//			this.commandTable.setValueAt("", i, 0);
            commandTable.setValueAt(v, i, j);
            return "ok";
		}
		else{
		   return null;
		}
		return null;
	}
	public void writeMessage(String x){
		Date d=new Date();
		String w=this.messageArea.getText();
		if(w.length()>10000)
			w=w.substring(5000);
		w=w+d+" "+x+"\n";
		this.messageArea.setText(w);
		JScrollBar sb=messageAreaScrollPane.getVerticalScrollBar();
		sb.setValue(sb.getMaximum());
	}


	public JTable getJTable(String name) {
		// TODO Auto-generated method stub
		if(name.equals("commandTable")){
			return this.commandTable;
		}
		return null;
	}
	
}
