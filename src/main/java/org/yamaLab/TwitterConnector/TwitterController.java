package org.yamaLab.TwitterConnector;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
//import org.yamalab.android.twitter2neomatrixex1.twitterconnector.Tweet;
import java.util.StringTokenizer;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterController  {
	static final String TAG = "TwitterController ";
		
	public String CONSUMER_KEY = ""; // dev.twitter.com ������
	public String CONSUMER_SECRET = ""; // dev.twitter.com ������
	public String CALLBACK_URL = "http://www.yama-lab.org" ; // "myapp://oauth";
	public String ACCESS_TOKEN="";
	public String ACCESS_TOKEN_SECRET="";
	private String nechatterStatus;
	private TwitterApplication service;
//	private Hashtable<String, String> context;
	private Properties context;
	public RequestToken requestToken = null;
	public Twitter twitter = null;
	private Tweet mTweet = null;
	private TwitterLoginController mTwitterLoginController=null;
//	public OAuthAuthorization twitterOauth;
	private boolean accessingWeb=false;
    /** Called when the activity is first created. */
    
    public TwitterController(Properties ct, TwitterApplication svs) {
    	context=ct;
        service=svs;
		System.out.println(TAG+"TwitterController");
//        mTwitterController=activity.mTwitterController;
        //������������������������������������������������������������
//	    SharedPreferences pref = context.getSharedPreferences("Twitter_setting", context.MODE_PRIVATE);
	    //���������������������K���v������������������������������������������������������
		nechatterStatus  = (String)context.get("status");
        mTweet = new Tweet(this);
        CONSUMER_KEY =context.getProperty("oauth.consumerKey");
        CONSUMER_SECRET =context.getProperty("oauth.consumerSecret");
        ACCESS_TOKEN =context.getProperty("oauth.accessToken");
        ACCESS_TOKEN_SECRET =context.getProperty("oauth.accessTokenSecret");
        CALLBACK_URL =context.getProperty("oauth.callbackUrl");
        if(CALLBACK_URL==null){
        	CALLBACK_URL="http://www.yama-lab.org/";
        	ct.put("oauth.callbackUrl", CALLBACK_URL);
        }
		mTwitterLoginController = new TwitterLoginController(this);
    }
	
    final private boolean isConnected(String nechatterStatus){
		if(nechatterStatus != null && nechatterStatus.equals("available")){
			return true;
		}else{
			return false;
		}
	}
    
    public void disconnectTwitter(){		
    	context.put("status", null);
        
        //finish();
	}
	public boolean parseCommand(String x, String v){
		String subcmd=Util.skipSpace(x);
		String [] rest=new String[1];
		String [] match=new String[1];
	   	if(this.service==null) return false;
	   	if(this.mTweet==null) return false;
//		System.out.println(TAG+"parseCommand("+x+","+v+")");
	   	service.parseCommand("guiMessage",x+" "+v);
		int [] intv = new int[1];
		String tweetMessage="";
		if(v==null) return false;
		if(Util.parseKeyWord(subcmd,"tweet",rest)){
		   	String subsub=Util.skipSpace(rest[0]);
//		    SharedPreferences pref = context.getSharedPreferences("Twitter_setting", context.MODE_PRIVATE);
			Status status=null;
			Calendar calendar=Calendar.getInstance();
			int cy=calendar.get(Calendar.YEAR);
			int cmon=calendar.get(Calendar.MONTH)+1;
			int cday=calendar.get(Calendar.DATE);
			int ch=calendar.get(Calendar.HOUR_OF_DAY);
			int cmin=calendar.get(Calendar.MINUTE);
			int csec=calendar.get(Calendar.SECOND);
			int dp=v.indexOf("<day>");
			if(dp>0){
                v=v.replace("<day>", ""+cday);				
			}
			int dh=v.indexOf("<hour>");
			if(dh>0){
                v=v.replace("<hour>", ""+ch);				
			}
			int dm=v.indexOf("<min>");
			if(dm>0){
                v=v.replace("<min>", ""+cmin);				
			}
			
			if(twitter==null){
//				putMessage("error... twitter is not connected.");
				service.parseCommand("guiMessage", "error... twitter is not connected.");
				return true;
			}
			try{
			   status=twitter.updateStatus(v);
			}
			catch(Exception e){
//				putMessage("error when tweet, "+e.getMessage());
				service.parseCommand("guiMessage", "error when tweet, "+e.getMessage());				
				return true;
			}
		   	return true;
		}
		else
		if(Util.parseKeyWord(subcmd,"getNewTweet",rest)){
			nechatterStatus  = (String)context.get("status");
			if(accessingWeb) return true;
			accessingWeb=true;
		   	if(isConnected(nechatterStatus)){
//		   		if(tweetMessage==null) return false;
		   	    mTweet.getHashTweet();
		   	}
		   	else{
		   		/* */
//					new connectTwitterTask().execute("");
		   		/* */
		   	}
		   	return true;
		}
		else
		if(Util.parseKeyWord(subcmd,"set ",rest)){
		   	String subsub=Util.skipSpace(rest[0]);
		   	if(subsub.equals("uploadHashTag")){
		   		mTweet.setUploadHashTag(v);
		   		service.parseCommand("activity twitter set uploadHashTag", v);
		   		return true;
		   	}
		   	else
		   	if(subsub.equals("downloadHashTag")){
		   		service.parseCommand("activity twitter set downloadHashTag",v);
		   		mTweet.setDownloadHashTag(v);
		   		return true;
		   	}
		   	else
		   	if(subsub.equals("OAuth")){
		   		StringTokenizer st=new StringTokenizer(v);
		   		String oAuthToken=st.nextToken();
		   		String oAuthVerifier=st.nextToken();
		   		mTwitterLoginController.startOAuthTask(oAuthToken,oAuthVerifier);

		   		return true;
		   	}
		   	else
		   	if(subsub.equals("accessingweb")){
		   		if(v.equals("true")){
		   			
		   		}
		   		else
		   		if(v.equals("false")){
		   			this.setAccessingWeb(false);		   			
		   		}

		   		return true;
		   	}
		   	
		   	return false;
		}
		else
		if(subcmd.equals("login")){
			loginTwitter();
		}
		return false;
	}
	private void loginTwitter(){
        try{
        // アクセストークンの設定
        AccessToken token = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
         
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        twitter.setOAuthAccessToken(token);
        User user = twitter.verifyCredentials();
        nechatterStatus="available";
        // 表示してみる。
        List<Status> list_status = twitter.getHomeTimeline();
//        System.out.println("自分の名前：" + user.getScreenName());
        service.parseCommand("guiMessage","自分の名前：" + user.getScreenName());
//        System.out.println("概要　　　：" + user.getDescription());
        service.parseCommand("guiMessage","概要　　　：" + user.getDescription());
//        for (Status status : list_status) {
//            System.out.println("ツイート：" + status.getText());
//        	service.parseCommand("guiMessage","ツイート：" + status.getText());        
//          }
        }
        catch(Exception e){
        	System.out.println("error:"+e.toString());
        	nechatterStatus=null;
        }
        context.setProperty("status", nechatterStatus);
		
	}
	public void setAccessingWeb(boolean x){
		accessingWeb=x;
	}
	public TwitterApplication getService(){
		return service;
	}
	public Properties getContext(){
		return context;
	}

}
