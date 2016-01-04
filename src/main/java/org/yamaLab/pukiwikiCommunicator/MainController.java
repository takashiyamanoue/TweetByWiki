package org.yamaLab.pukiwikiCommunicator;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import org.yamaLab.TweetByWikiEx2.ClassWithJTable;
import org.yamaLab.TwitterConnector.TwitterApplication;
import org.yamaLab.TwitterConnector.TwitterController;
import org.yamaLab.TwitterConnector.Util;
import org.yamaLab.pukiwikiCommunicator.connector.PukiwikiJavaApplication;
import org.yamaLab.pukiwikiCommunicator.connector.SaveButtonDebugFrame;

import javax.swing.JComboBox;

/**
*
*/
public class MainController 
implements PukiwikiJavaApplication, TwitterApplication, Runnable
{
	private Thread me;
	public Properties setting;
//	private JTable commandTable;
	private int maxCommands;
	private CommandReceiver gui;
	TwitterController twitterController;
	private ClassWithJTable commandTableClass;

	public MainController(CommandReceiver g, ClassWithJTable t, Properties s){
		this.gui=g;
		setting=s;
		this.debugger=new SaveButtonDebugFrame(this,setting);
		this.debugger.setVisible(false);
		this.maxCommands=Integer.parseInt((String)setting.get("maxCommandsStr"));
		twitterController=new TwitterController(s,this);
		commandTableClass=t;
//		this.start();
	}
	
	public void start(){
		if(me==null){
			me=new Thread(this,"PukiwikiCommunicator");
			me.start();
		}
	}

	public void stop(){
		me=null;
	}

//	@Override
	public String getOutput() {
		// TODO Auto-generated method stub
		return this.result;
	}

//	@Override
	public void setInput(String x) {
		// TODO Auto-generated method stub
//		this.writeMessage("setInput("+x+")");
		this.writeMessage("reading commands from the wiki page");
		for(int i=0;i<maxCommands;i++){
			gui.command("wikiCommandTable setValueAt "+i+" 0", "");
			gui.command("wikiCommandTable setValueAt "+i+" 1", "");			
		}
		StringTokenizer st=new StringTokenizer(x,"\n");
		int no=0;
		while(st.hasMoreElements()){
			String l=st.nextToken();
			if(no>=maxCommands){
				this.writeMessage("too many commands.");
				return;
			}
			if(l.startsWith("command:")){
				String com=l.substring("command:".length());
				com=Util.skipSpace(com);
				gui.command("wikiCommandTable setValueAt "+no+" 0", ""+no);
				gui.command("wikiCommandTable setValueAt "+no+" 1", com);			
				
				this.writeMessage("setting "+com);
				no++;
			}
			else
			if(l.startsWith("#")){
				
			}
		}
	}
	private SaveButtonDebugFrame debugger;
//	@Override
	public void setSaveButtonDebugFrame(SaveButtonDebugFrame f) {
		// TODO Auto-generated method stub
		debugger=f;
	}
/*	*/
	static public void main(String[] args){
		new MainController(null,null,null).setVisible(true);
	}
	long lastCommandRequest;
	long lastReturnOutput;
	long lastExec;
//	@Override
	public void run() {
		lastCommandRequest=0;
		lastReturnOutput=0;
		lastExec=0;
		// TODO Auto-generated method stub
		while(me!=null){
			long time=System.currentTimeMillis();
			long readInterval=getReadRequestInterval();
			long execInterval=getExecRequestInterval();
			long returnInterval=getResultReturnInterval();
			if(time>lastCommandRequest+readInterval){
				this.writeMessage("connectionButton");
				String urlx=gui.command("getWikiUrl", "");
				this.connectButtonActionPerformed(urlx);
				lastCommandRequest=System.currentTimeMillis();
			}
			if(time>lastExec+execInterval){
				execCommands();
				lastExec=System.currentTimeMillis();
			}			
			if(time>lastReturnOutput+returnInterval){
				this.writeMessage("writeResult.");
//				this.writeResult();
				lastReturnOutput=System.currentTimeMillis();
			}
			try{
				Thread.sleep(100);
			}
			catch(InterruptedException e){				
			}
		}
		
	}
	
	public String wikiUrl;
	public void connectButtonActionPerformed(String url) {
		//TODO add your code for connectButton.actionPerformed
		if(url==null) return;
		if(url.equals("")) return;
		wikiUrl=url;
		(new Thread(){
			public void run(){
				    debugger.readFromPukiwikiPageAndSetData(wikiUrl);
				    setting.setProperty("managerUrl", wikiUrl);
		    }
		}).start();
	}
	private long getMilisec(String x){
		long rtn;
		StringTokenizer st=new StringTokenizer(x,"-");
		String numx=st.nextToken();
		String unit=st.nextToken();
		int dotPlace=numx.indexOf(".");
		long p1,p2;
		if(dotPlace<0){
			p1=(new Long(numx)).longValue();		
			p2=0;
		}
		else{
			String p1s=numx.substring(0,dotPlace);
			String p2s=numx.substring(dotPlace+1);
			p1=(new Long(p1s)).longValue();
			p2=(new Long((p2s+"000").substring(0,3))).longValue();
		}
		if(unit.equals("sec")){
			rtn=p1*1000+p2;
		}
		else
		if(unit.equals("min")){
			rtn=(p1*1000+p2)*60;
		}
		else
		if(unit.equals("h")){
			rtn=(p1*1000+p2)*60*60;
		}
		else{
			rtn=0;
		}
//		this.messageArea.append("getCommandRequestInterval="+rtn+"\n");
		return rtn;		
	}
	
	private long getExecRequestInterval(){
		long rtn,rx;
//		this.setting.setProperty("commandInterval", (String)(this.commandIntervalCombo.getSelectedItem()));
		String x=(String)(this.setting.getProperty("execInterval"));
		rtn=getMilisec(x);
	    return rtn;
	}
	
	private long getReadRequestInterval(){
		long rtn,rx;
//		this.setting.setProperty("commandInterval", (String)(this.commandIntervalCombo.getSelectedItem()));
		String x=(String)(this.setting.getProperty("readInterval"));
		rtn=getMilisec(x);
	    return rtn;		
	}
	private long getResultReturnInterval(){
		long rtn,rx;
//		this.setting.setProperty("returnInterval", (String)(this.returnIntervalCombo.getSelectedItem()));
		String x=(String)(this.setting.getProperty("returnInterval"));
		rtn=getMilisec(x);
	    return rtn;				
	}	
	
	String result="";
	Vector <String> resultVector;
	public void writeResult(){
		System.out.println("writeResult");
		String urlx=gui.command("getWikiUrl", "");
		this.connectButtonActionPerformed(urlx);		
		String x="";
		this.result=x;
		   this.debugger.saveText(x);
		   gui.command("wikiMessage", x);
	}
	
	public Properties getSetting(){
		return this.setting;
	}
			
//	@Override
	public void error(String x) {
		// TODO Auto-generated method stub
		if(x.startsWith("format-error")){
			this.writeMessage("pukiwiki-page:"+x+"\n");
			this.stop();
			return;
		}
	}
	public boolean parseCommand(String cmd, String v) {
		// TODO Auto-generated method stub
		if(cmd.startsWith("tweet ")){
			String l=cmd.substring("tweet ".length());
			String[] rest=new String[1];
			String[] param1=new String[1];
			String[] param2=new String[2];
			l=Util.skipSpace(l);
			if(!Util.parseStrConst(l, param1, rest)) return false;
			l=Util.skipSpace(rest[0]);
			if(Util.parseKeyWord(l, "when ", rest)) {
				l=Util.skipSpace(rest[0]);
				if(!Util.parseStrConst(l, param2, rest)) return false;
				tweetWhen(param1[0],param2[0]);
			}
			return true;
		}
		else
		if(cmd.startsWith("wikiDebuggerButton Clicked")){
			if(this.debugger.isVisible()){
				this.debugger.setVisible(false);
			}
			else{
				this.debugger.setVisible(true);;
			}
			return false;
		}
		else
		if(cmd.startsWith("onlineCommandRefresh")){
				this.setting.setProperty("onlineCommandRefresh", v);
				return false;
		}
		else
		if(cmd.startsWith("wikiStartWatching")){
			this.start();
			return false;
		}
		else
		if(cmd.startsWith("wikiConnectButton Clicked")){
			this.connectButtonActionPerformed(v);
			return false;
		}
		else
		if(cmd.startsWith("wikiEndWatching")){
			this.stop();
			return false;
		}
		else
		if(cmd.startsWith("twitterConnect")){
			twitterController.parseCommand("login", "");
			return false;
		}
		else
		if(cmd.startsWith("twitterTweet")){
			twitterController.parseCommand("tweet",v);
			return false;
		}
		else
		if(cmd.startsWith("guiMessage")){
			gui.command("wikiMessage",v);
			return false;
		}
		else
		if(cmd.startsWith("exit")){
			return false;
		}
		return true;
	}

	public void setVisible(boolean f) {
		// TODO Auto-generated method stub
		
	}
	public void writeMessage(String x){
	   gui.command("wikiMessage", x);
	}
	private void tweetWhen(String tw, String t){
		Calendar calendar=Calendar.getInstance();
		int cy=calendar.get(Calendar.YEAR);
		int cmon=calendar.get(Calendar.MONTH)+1;
		int cday=calendar.get(Calendar.DATE);
		int ch=calendar.get(Calendar.HOUR_OF_DAY);
		int cmin=calendar.get(Calendar.MINUTE);
		int csec=calendar.get(Calendar.SECOND);
		StringTokenizer st=new StringTokenizer(t,",");
		String gy=st.nextToken();
		String gmon=st.nextToken();
		String gday=st.nextToken();
		String gh=st.nextToken();
		String gmin=st.nextToken();
		String gsec=st.nextToken();
		if(!gy.equals("*")){ /* same year ? */
			int igy=(new Integer(gy)).intValue();
			if(igy!=cy) return;
		}
		if(!gmon.equals("*")){ /* same month ? */
			int igmon=(new Integer(gmon)).intValue();
			if(igmon!=cmon) return;
		}
		if(!gday.equals("*")){ /* same day ?*/
			int igday=(new Integer(gday)).intValue();
			if(igday!=cday) return;
		}
		if(!gh.equals("*")){ /* same hour ? */
			int igh=(new Integer(gh)).intValue();
			if(igh!=ch) return;
		}
		if(!gmin.equals("*")){ /* same minutes ? */
			int igmin=(new Integer(gmin)).intValue();
			if(igmin!=cmin) return;
		}
		if(!gsec.equals("*")){ /* same seconds ? */
			int igsec=(new Integer(gsec)).intValue();
			if(igsec!=csec) return;
		}
		twitterController.parseCommand("tweet", tw);
	}
	private void execCommands(){
		JTable commandTable=commandTableClass.getJTable("commandTable");
		if(commandTable==null) return;
		int tmax=commandTable.getRowCount();
		for(int i=0;i<tmax;i++){
			String command=(String)commandTable.getValueAt(i,1);
			boolean rtn=this.parseCommand(command,"");
			if(!rtn) return;
		}
	}
}
