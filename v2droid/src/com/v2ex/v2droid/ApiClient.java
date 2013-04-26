package com.v2ex.v2droid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



/**
 * API客户端接口：用于访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 1;

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}
	
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本
			ua.append("/"+android.os.Build.MODEL); //手机型号
			ua.append("/"+appContext.getAppId());//客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
	
	public static String getCookie(AppContext appContext) {
		if(appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}
	
	
	private static HttpClient getHttpClient() {        
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}	
	
	private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		//httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url, String cookie, String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection","Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		//httpPost.setRequestHeader("User-Agent", userAgent);
		
		return httpPost;
	}
	
	/**
	 * get请求URL
	 * @param url
	 * @throws AppException 
	 */
	private static String http_get(AppContext appContext, String url) throws AppException {	
		//System.out.println("get_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		System.out.println("http_get cookie==> "+cookie);
		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);			
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				
				Cookie[] cookies = httpClient.getState().getCookies();
	            String tmpcookies = "";
	            for (Cookie ck : cookies) {
	                tmpcookies += ck.toString()+";";
	            }
	            //保存cookie   
        		if(appContext != null && tmpcookies != "" && (cookie==null||cookie=="")){
        			appContext.setProperty("cookie", tmpcookies);
        			appCookie = tmpcookies;
        			System.out.println("appCookie==> "+appCookie);
        		}
        		
				responseBody = httpGet.getResponseBodyAsString();
				//System.out.println("XMLDATA=====>"+responseBody);
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		
		//responseBody = responseBody.replaceAll("\\p{Cntrl}", "");

		return responseBody;
	}
	
	/**
	 * 公用post方法
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static String _post(AppContext appContext, String url, Part[] parts) throws AppException {
		System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		PostMethod httpPost = null;

		
		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				System.out.println("getHttpClient==> ");
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);	
				httpPost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
				httpPost.setRequestHeader("Content-Length", "application/x-www-form-urlencoded");
		        httpPost.setRequestEntity(new MultipartRequestEntity(parts,httpPost.getParams()));		        
		        int statusCode = httpClient.executeMethod(httpPost);
		        System.out.println("statusCode==> "+statusCode);	 
		        Header[]  heade = httpPost.getRequestHeaders();
		        if ((null != heade) && (0 != heade.length)) {
					for (Header h : heade) {
						System.out.println("heade==> "+h.getValue());
					}
					
					//return urlAddress;
				}
		        
		        if(statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_MOVED_TEMPORARILY ) 
		        {
		        	System.out.println("cookies=====>");
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //保存cookie   
	        		if(appContext != null && tmpcookies != "" && (cookie==null||cookie=="")){
	        			appContext.setProperty("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        			System.out.println("appCookie==> "+appCookie);
	        		}
	        		
	        		if (HttpStatus.SC_MOVED_TEMPORARILY == statusCode) {
						Header[]  headers = httpPost.getResponseHeaders("Location");
						if ((null != headers) && (0 != headers.length)) {
							String urlAddress = headers[headers.length - 1].getValue();
							System.out.println("Location==> "+urlAddress);
							//return urlAddress;
						}
						
						//String testhtml = http_get(appContext, "http://www.v2ex.com/t/66388");
						//System.out.println("testhtml====================================> "+testhtml);
					}
	        		
	        		
		        } else {
		        	throw AppException.http(statusCode);
		        }
		     	responseBody = httpPost.getResponseBodyAsString();
		        System.out.println("XMLDATA=====>"+responseBody);
		     	break;	     	
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
        
        responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        
        InputStream is = new ByteArrayInputStream(responseBody.getBytes());

        return is.toString();
	}
	
	/**
	 * post请求URL
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException 
	 * @throws IOException 
	 * @throws  
	 */
	private static String http_post(AppContext appContext, String url, Part[] parts) throws AppException, IOException {
        return _post(appContext, url, parts);  
	}
	
	/**
	 * 登录， 自动处理cookie
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 *//*
	public static String login(AppContext appContext, String username, String pwd) throws AppException {
		String html = http_get(appContext, "http://www.v2ex.com/signin");
		String once = getHtmlOnce(html);
		
		Part[] parts = new Part[5];
		parts[0] = new StringPart("next", "/", UTF_8);
		parts[1] = new StringPart("u", username, UTF_8);
		parts[2] = new StringPart("p", pwd, UTF_8);
		parts[3] = new StringPart("once", once, UTF_8);
		parts[4] = new StringPart("next", "/", UTF_8);
		
		for (int i=0; i<parts.length; i++) {
			System.out.println("postkey====>"+parts[i].toString());
		}
				
		String loginurl = URLs.LOGIN_VALIDATE_HTTP;
		
		try{
			return _post(appContext, loginurl, parts);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}*/
	
	public static String replyTopic(AppContext appContext, String topicID, String content, String once) throws AppException {
		
		Part[] parts = new Part[2];
		parts[0] = new StringPart("content", content, UTF_8);
		parts[1] = new StringPart("once", once, UTF_8);
		System.out.println("onceonceonceonceonceonceonce======>"+once);
		
		try{
			return http_post(appContext, URLs.REPLY_TOPIC + topicID, parts);		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	public static String getTopicOnce(AppContext appContext, String url) {
		System.out.println("getTopicOnce======>");
		String once="";
		try {
		String html = http_get(appContext, url);
		
		System.out.println("html======>" + html);
		Document doc = Jsoup.parse(html);
		
		Element item = doc.select("input[name=once]").first();
		if (item!=null) {
			once = item.attr("value");
			System.out.println("once======>" + item.attr("value"));
		}
		} catch(AppException e) {
			
		}
		return once;
	}
	
	
	public static String getHtmlOnce(String html) {
		System.out.println("getHtmlOnce======>");
		String once=null;
		
		System.out.println("html======>" + html);
		Document doc = Jsoup.parse(html);
		
		Element item = doc.select("input[name=once]").first();
		if (item!=null) {
			once = item.attr("value");
			System.out.println("once======>" + item.attr("value"));
		}

		return once;
	}
	
	public static boolean sendPostRequest(String path, Map<String, String> params, String enc) throws Exception{
		// title=dsfdsf&timelength=23&method=save
		StringBuilder sb = new StringBuilder();
		if(params!=null && !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				sb.append(entry.getKey()).append('=')
					.append(URLEncoder.encode(entry.getValue(), enc)).append('&'); 
			}
			sb.deleteCharAt(sb.length()-1);
		}
		//得到实体的二进制数据，以便计算长度
		byte[] entitydata = sb.toString().getBytes();
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(5 * 1000);
		conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
		//Content-Type: application/x-www-form-urlencoded
		//Content-Length: 38
		//下面的两个属性是必须的
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(entitydata.length)); //传递数据的长据
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entitydata);
		//把内存中的数据刷新输送给对方
		outStream.flush();
		outStream.close();
		//获取服务端的响应，200代表成功
		if(conn.getResponseCode()==200){
			return true;
		}
		return false;
	}
	
	public static String login(AppContext appContext, String username, String pwd) throws AppException, Exception {
		

			String html = http_get(appContext, "http://www.v2ex.com/signin");
			String once = getHtmlOnce(html);
			StringBuilder sb = new StringBuilder();
			sb.append("next=/&u=").append(username).append("&p=").append(pwd).append("&once=").append(once).append("&next=/");
			System.out.println("send form data======>" + sb.toString());
			System.out.println("send form data======>" + sb.length());
			
			byte[] entitydata = sb.toString().getBytes();
			String path ="http://www.v2ex.com/signin";
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5 * 1000);
			conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
			//Content-Type: application/x-www-form-urlencoded
			//Content-Length: 38
			//下面的两个属性是必须的
			String cookie = getCookie(appContext);
			conn.setDoOutput(true);//发送POST请求必须设置允许输出
		    conn.setUseCaches(false);//不使用Cache
		    conn.setRequestMethod("POST");         
		    conn.setRequestProperty("Connection", "Keep-Alive");//维持长连接
		    conn.setRequestProperty("Charset", "UTF-8");

		    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		    
			conn.setRequestProperty("Cookie", cookie);

			conn.setRequestProperty("Content-Length", String.valueOf(entitydata.length+4)); //传递数据的长据
			OutputStream outStream = conn.getOutputStream();
			outStream.write(entitydata);
			//把内存中的数据刷新输送给对方
			outStream.flush();
			outStream.close();
			//获取服务端的响应，200代表成功
			System.out.println("conn.getResponseCode()====>" + conn.getResponseCode());
			if(conn.getResponseCode()==302){
				return "true";
			}
			return "";
	
	}
	
	public static void loginTest(AppContext appContext) throws AppException, Exception {
		

		String html = http_get(appContext, "http://www.v2ex.com/signin");
		String once = getHtmlOnce(html);
		System.out.println("loginText once======>" + once);
		
		String cookie = getCookie(appContext);
		System.out.println("loginText cookie======>" + cookie);
		
		StringBuilder sb = new StringBuilder();
		sb.append("next").append('=').append(URLEncoder.encode("/", "utf-8")).append('&');
		sb.append("u").append('=').append(URLEncoder.encode("burnex", "utf-8")).append('&'); 
		sb.append("p").append('=').append(URLEncoder.encode("003491", "utf-8")).append('&'); 
		sb.append("once").append('=').append(URLEncoder.encode(once, "utf-8")).append('&'); 
		sb.append("next").append('=').append(URLEncoder.encode("/", "utf-8")); 
		
		System.out.println("loginText send form data======>" + sb.toString());
		System.out.println("loginText send form data======>" + sb.length());
		
		byte[] entitydata = sb.toString().getBytes();
		
		String path ="http://www.v2ex.com/signin";
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(5 * 1000);
		conn.setDoOutput(true);//如果通过post提交数据，必须设置允许对外输出数据
		//Content-Type: application/x-www-form-urlencoded
		//Content-Length: 38
		//下面的两个属性是必须的

		conn.setDoOutput(true);//发送POST请求必须设置允许输出
	    conn.setUseCaches(false);//不使用Cache
	    conn.setRequestMethod("POST");         
	    conn.setRequestProperty("Connection", "Keep-Alive");//维持长连接
	    conn.setRequestProperty("Charset", "UTF-8");
	    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");	    
		//conn.setRequestProperty("Cookie", cookie);

		conn.setRequestProperty("Content-Length", String.valueOf(entitydata.length)); //传递数据的长据
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entitydata);
		//把内存中的数据刷新输送给对方
		outStream.flush();
		outStream.close();
		//获取服务端的响应，200代表成功
		//System.out.println("conn.getResponseCode()====>" + conn.getResponseCode());
		int s = conn.getResponseCode();
		System.out.println("conn.getResponseCode()====>" + Integer.toString(s));
		if(conn.getResponseCode()==302){
			System.out.println("loginText 302======>");

		}


}
	
}


