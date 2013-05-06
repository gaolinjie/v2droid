package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.loopj.android.http.PersistentCookieStore;



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

	private static String appUserAgent;
	
	static final String KEY_ID = "id";
	static final String KEY_TITLE = "title";
	static final String KEY_REPLIES = "replies";
	static final String KEY_USERNAME = "username";
	static final String KEY_AVATAR = "avatar";
	static final String KEY_NODE = "node";
	static final String KEY_TIME= "time";
	static final String KEY_REPLY = "reply";

	static final String KEY_HEADER_ID = "header_id";
	static final String KEY_HEADER = "header";
	static final String KEY_NAME = "name";
	static final String KEY_LINK = "link";
	
	static final String KEY_GRAY = "gray";
	
	private static Map<String, String> mCookies = new HashMap<String, String>();
	
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("V2EX.COM");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本
			ua.append("/"+android.os.Build.MODEL); //手机型号
			ua.append("/"+appContext.getAppId());//客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
	
	private static Map<String, String> getCookies(AppContext appContext) {
		if(mCookies.isEmpty() && appContext.getLogin()) {
			PersistentCookieStore  pcs = new PersistentCookieStore(appContext);
			List<Cookie> cookieList = pcs.getCookies();
			for (Cookie cookie : cookieList) {
				//cookieStore.addCookie(cookie);
				mCookies.put(cookie.getName(), cookie.getValue());
				System.out.println("getCookies=====>"
						+ cookie.getName());
			}
		}
		return mCookies;
	}
	
	public static void storeCookies(AppContext appContext) {
		PersistentCookieStore  pcs = new PersistentCookieStore(appContext);
		Map<String, String> cookies = getCookies(appContext);
		
		for (Entry<String, String> cookie : cookies.entrySet()) {
			Cookie c = new BasicClientCookie2(cookie.getKey(), cookie.getValue());
			pcs.addCookie(c);
			System.out.println("storeCookies=====>"
					+ cookie.getKey());

		}	
	}

	public static Document get(AppContext appContext, String url, String referrer) throws IOException {
		Map<String, String> cookies = getCookies(appContext);
		String userAgent = getUserAgent(appContext);
		
	    Connection connection = Jsoup.connect(url)
	    						.cookies(cookies)
	    						.referrer(referrer)
	    						.userAgent(userAgent);
	    
	    Response response = connection.execute();
	    cookies.putAll(response.cookies());
	    mCookies = cookies;
	    return response.parse();
	}
	
	public static Document post(AppContext appContext, String url, String referrer, List<NameValuePair> params) throws IOException {
		Map<String, String> cookies = getCookies(appContext);
		String userAgent = getUserAgent(appContext);
		
	    Connection connection = Jsoup.connect(url)
	    						.cookies(cookies)
	    						.referrer(referrer)
	    						.userAgent(userAgent)
	    						.method(Method.POST);
	    
	    for (NameValuePair param : params) {
	    	connection = connection.data(param.getName(), param.getValue());
	    }
	        
	    Response response = connection.execute();
	    cookies.putAll(response.cookies());
	    mCookies = cookies;
	    return response.parse();
	}
	
	public static boolean login (AppContext appContext, String username, String password) throws IOException {
		String once = getOnce(get(appContext, URLs.LOGIN_VALIDATE_HTTP, URLs.HOST));
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("next", "/"));
		params.add(new BasicNameValuePair("u", username));
		params.add(new BasicNameValuePair("p", password));
		params.add(new BasicNameValuePair("once", once));
		params.add(new BasicNameValuePair("next", "/"));
		
		post(appContext, URLs.LOGIN_VALIDATE_HTTP, URLs.LOGIN_VALIDATE_HTTP, params);
		
		Map<String, String> cookies = getCookies(appContext);
		
		if (cookies.containsKey("auth")) {
			return true;
		}

		/*
			for (Entry<String, String> cookie : cookies.entrySet()) {
				System.out.println("cookie.getKey()=====>"
						+ cookie.getKey());
				
				if (cookie.getKey() == "auth") {
					System.out.println("return true=====>"
							+ cookie.getKey());
					return true;
				}
			}
		*/
		
		return false;
	}
	
	public static String getOnce(Document doc) {
		String once = "";

		Element item = doc.select("input[name=once]").first();
		if (item != null) {
			once = item.attr("value");
			System.out.println("once======>" + item.attr("value"));
		}

		return once;
	}
	
	public static ArrayList<HashMap<String, String>> getTopics(AppContext appContext, Document doc,
			ArrayList<HashMap<String, String>> topics) {
			
			Elements items = doc.select("div[class=cell item]");

			if (!topics.isEmpty()) {
				topics.remove(topics.size() - 1);
			}

			for (Element item : items) {
				//System.out.println("item======>" + item.toString());
				Element titleElement = item.select("span[class=item_title]>a")
						.get(0);
				String href = titleElement.attr("href");
				String id = getMatcher("/t/([\\d]+)", href);
				String replies = getMatcher("#reply([\\d]+)", href);
				//System.out.println("replies======>" + replies);
				String title = titleElement.text();
				Element usernameElement = item.select("td>a").get(0);
				String href2 = usernameElement.attr("href");
				String username = getMatcher("/member/([0-9a-zA-Z]+)", href2);
				Element avatarElement = usernameElement.select("img").get(0);
				String avatar = avatarElement.attr("src");
				Element nodeElement = item.select("span[class=small fade]>a")
						.get(0);
				String node = nodeElement.text();
				//System.out.println(node);
				Element timeElement = item.select("span[class=small fade]").get(1);
				//System.out.println("t=>" + timeElement.text());
				String time = timeElement.text();
				//System.out.println("time=>" + time);
				/*
				Elements links = item.select("span[class=small fade]").select("a");
				String reply = "";
				if (links.size() == 3) {
					Element replyElement = links.get(2);
					reply = replyElement.text();
					System.out.println("replyElement======>" + replyElement.text());
				}*/
				

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key =>
				// value
				map.put(KEY_ID, id);
				map.put(KEY_TITLE, title);
				map.put(KEY_USERNAME, username);
				map.put(KEY_REPLIES, replies);
				map.put(KEY_AVATAR, avatar);
				map.put(KEY_NODE, node);
				map.put(KEY_TIME, time);
				/*
				//map.put(KEY_REPLY, reply);
				if (links.size() == 3) {
					map.put(KEY_REPLY, reply);
				}*/

				// adding HashList to ArrayList
				topics.add(map);
			}
		

		HashMap<String, String> mapMore = new HashMap<String, String>();

		mapMore.put(KEY_ID, MainActivity.MORE_TAG);
		mapMore.put(KEY_TITLE, MainActivity.MORE_TAG);
		mapMore.put(KEY_USERNAME, MainActivity.MORE_TAG);
		mapMore.put(KEY_REPLIES, MainActivity.MORE_TAG);
		mapMore.put(KEY_AVATAR, MainActivity.MORE_TAG);
		mapMore.put(KEY_NODE, MainActivity.MORE_TAG);

		// adding HashList to ArrayList
		topics.add(mapMore);

		return topics;
	}
	
	public static ArrayList<HashMap<String, String>> getFavorites(AppContext appContext, Document doc,
			ArrayList<HashMap<String, String>> topics) {
		
			Elements items = doc.select("div[class=cell item]");

			if (!topics.isEmpty()) {
				topics.remove(topics.size() - 1);
			}

			for (Element item : items) {
				//System.out.println("item======>" + item.toString());
				Element titleElement = item.select("span[class=item_title]>a")
						.get(0);
				String href = titleElement.attr("href");
				String id = getMatcher("/t/([\\d]+)", href);
				String replies = getMatcher("#reply([\\d]+)", href);
				System.out.println("replies======>" + replies);
				String title = titleElement.text();
				Element usernameElement = item.select("td>a").get(0);
				String href2 = usernameElement.attr("href");
				String username = getMatcher("/member/([0-9a-zA-Z]+)", href2);
				Element avatarElement = usernameElement.select("img").get(0);
				String avatar = avatarElement.attr("src");
				Element nodeElement = item.select("span[class=small fade]>a")
						.get(0);
				String node = nodeElement.text();
				System.out.println(node);
				Element timeElement = item.select("span[class=small fade]").get(0);
				System.out.println("t=>" + timeElement.text());
				String time = timeElement.text();
				System.out.println("time=>" + time);
				/*
				Elements links = item.select("span[class=small fade]").select("a");
				String reply = "";
				if (links.size() == 3) {
					Element replyElement = links.get(2);
					reply = replyElement.text();
					System.out.println("replyElement======>" + replyElement.text());
				}*/
				

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key =>
				// value
				map.put(KEY_ID, id);
				map.put(KEY_TITLE, title);
				map.put(KEY_USERNAME, username);
				map.put(KEY_REPLIES, replies);
				map.put(KEY_AVATAR, avatar);
				map.put(KEY_NODE, node);
				map.put(KEY_TIME, time);
				/*
				//map.put(KEY_REPLY, reply);
				if (links.size() == 3) {
					map.put(KEY_REPLY, reply);
				}*/

				// adding HashList to ArrayList
				topics.add(map);
			}
			
		HashMap<String, String> mapMore = new HashMap<String, String>();

		mapMore.put(KEY_ID, MainActivity.MORE_TAG);		
		mapMore.put(KEY_USERNAME, MainActivity.MORE_TAG);
		mapMore.put(KEY_REPLIES, MainActivity.MORE_TAG);
		mapMore.put(KEY_AVATAR, MainActivity.MORE_TAG);
		mapMore.put(KEY_NODE, MainActivity.MORE_TAG);
		
		if (items.isEmpty() && topics.isEmpty()) {
			mapMore.put(KEY_TITLE, "您目前尚未收藏任何主题");
		} else {
			mapMore.put(KEY_TITLE, MainActivity.MORE_TAG);
		}
		
		// adding HashList to ArrayList
		topics.add(mapMore);

		return topics;
	}
	
	public static ArrayList<HashMap<String, String>> getUserTopics(AppContext appContext, Document doc,
			ArrayList<HashMap<String, String>> topics, String avatar) {
			
			Elements items = doc.select("div[class=cell item]");

			if (!topics.isEmpty()) {
				topics.remove(topics.size() - 1);
			}

			for (Element item : items) {
				//System.out.println("item======>" + item.toString());
				Element titleElement = item.select("span[class=item_title]>a")
						.get(0);
				String href = titleElement.attr("href");
				String id = getMatcher("/t/([\\d]+)", href);
				String replies = getMatcher("#reply([\\d]+)", href);
				//System.out.println("replies======>" + replies);
				String title = titleElement.text();
				Element usernameElement = item.select("span[class=small fade]").select("a").get(1);
				String href2 = usernameElement.attr("href");
				String username = getMatcher("/member/([0-9a-zA-Z]+)", href2);
				
				Element nodeElement = item.select("span[class=small fade]>a")
						.get(0);
				String node = nodeElement.text();
				//System.out.println(node);
				Element timeElement = item.select("span[class=small fade]").get(0);
				//System.out.println("t=>" + timeElement.text());
				String time = timeElement.text();
				//System.out.println("time=>" + time);
				String[] s = time.split("\u00a0"); 
				time = "";
				for (int i = 4; i < s.length; i++) {  
					//System.out.println(s[i]);  
					time += s[i];
				}

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key =>
				// value
				map.put(KEY_ID, id);
				map.put(KEY_TITLE, title);
				map.put(KEY_USERNAME, username);
				map.put(KEY_REPLIES, replies);
				map.put(KEY_AVATAR, avatar);
				map.put(KEY_NODE, node);
				map.put(KEY_TIME, time);

				// adding HashList to ArrayList
				topics.add(map);
			}
			
		HashMap<String, String> mapMore = new HashMap<String, String>();

		mapMore.put(KEY_ID, MainActivity.MORE_TAG);		
		mapMore.put(KEY_USERNAME, MainActivity.MORE_TAG);
		mapMore.put(KEY_REPLIES, MainActivity.MORE_TAG);
		mapMore.put(KEY_AVATAR, MainActivity.MORE_TAG);
		mapMore.put(KEY_NODE, MainActivity.MORE_TAG);
		
		if (items.isEmpty() && topics.isEmpty()) {
			mapMore.put(KEY_TITLE, "您目前尚未收藏任何主题");
		} else {
			mapMore.put(KEY_TITLE, MainActivity.MORE_TAG);
		}
		
		// adding HashList to ArrayList
		topics.add(mapMore);

		return topics;
	}
	
	public static ArrayList<HashMap<String, String>> getUserReplies(AppContext appContext, Document doc,
			ArrayList<HashMap<String, String>> replies) {
		System.out.println("getUserReplies======>");
			Elements itemsDockArea = doc.select("div[class=dock_area]");
			Elements itemsInner = doc.select("div[class=reply_content]");
			System.out.println("itemsDockArea======>"+itemsDockArea.toString());
			System.out.println("itemsInner======>"+itemsInner.toString());

			if (!replies.isEmpty()) {
				replies.remove(replies.size() - 1);
			}

			int i = 0;
			for (Element item : itemsDockArea) {
				Element grayElement = item.select("span[class=gray]").get(0);
				String gray = grayElement.text();
				
				Element timeElement = item.select("span[class=fade]").get(0);
				String time = timeElement.text();
				
				Element replyElement = itemsInner.get(i);
				String reply = replyElement.toString();

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key =>
				// value
				map.put(KEY_GRAY, gray);
				map.put(KEY_TIME, time);
				map.put(KEY_REPLY, reply);
				
				System.out.println("gray======>" + gray);
				System.out.println("time======>" + time);

				// adding HashList to ArrayList
				replies.add(map);
				i++;
			}
			
		HashMap<String, String> mapMore = new HashMap<String, String>();

		mapMore.put(KEY_GRAY, MainActivity.MORE_TAG);		
		mapMore.put(KEY_TIME, MainActivity.MORE_TAG);
		//mapMore.put(KEY_REPLY, MainActivity.MORE_TAG);
		
		if (itemsDockArea.isEmpty() && replies.isEmpty()) {
			mapMore.put(KEY_REPLY, "目前尚未有回复");
		} else {
			mapMore.put(KEY_REPLY, MainActivity.MORE_TAG);
		}
		
		// adding HashList to ArrayList
		replies.add(mapMore);

		return replies;
	}
	
	public static String getUserReplies(AppContext appContext, Document doc) {
		String replies = null;
		if (doc!=null) {
			Elements items = doc.select("div#Wrapper");
			if (!items.isEmpty()) {
				replies = items.get(0).toString(); 
				//System.out.println("getUserAvatar=====>" + replies);
			}
		}
		
		return replies;
	}	
	
	public static String getUserAvatar(AppContext appContext, Document doc) {
		String avatar = null;
		if (doc!=null) {
			Elements items = doc.select("img[class=avatar]");
			if (!items.isEmpty()) {
				avatar = items.get(0).attr("src"); 
				System.out.println("getUserAvatar=====>" + avatar);
			}
		}
		
		return avatar;
	}
	
	public static String getMatcher(String regex, String source) {
		String result = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			result = matcher.group(1);// 只取第一组
		}
		return result;
	}
	
	public static String getMessageNum(AppContext appContext, Document doc) {
		String messageNum = null;
		if (doc!=null) {
			Elements items = doc.select("input[class=super special button]");
			if (!items.isEmpty()) {
				messageNum = items.get(0).attr("value").replaceAll("[^0-9]",""); 
				appContext.setMessageNum(messageNum);
				System.out.println("messageNum=====>" + messageNum);
			}
		}
		
		return messageNum;
	}	
	
	public static String getMessages(AppContext appContext, Document doc) {
		String messages = null;
		Elements items = doc.select("div[class=box]");
		if (!items.isEmpty()) {
			messages = items.get(0).toString(); 
		}
		
		return messages;
	}
	
	/*
	 * 
	 public static CookieStore getCookieStore(AppContext appContext) {
		return cookieStore;
	}
	
	public static HttpContext getCookieContext(AppContext appContext) {
		if (cookieContext == null) {
			cookieStore = new BasicCookieStore();
			if(AppConfig.getAppConfig(appContext).getLogin()) {
				PersistentCookieStore  pcs = new PersistentCookieStore(appContext);
				List<Cookie> cookieList = pcs.getCookies();
				for (Cookie cookie : cookieList) {
					cookieStore.addCookie(cookie);
				}
			}
				
			cookieContext = new BasicHttpContext();
			cookieContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		}
		System.out.println("getCookieContext=====>"
				+ cookieContext.getAttribute(ClientContext.COOKIE_STORE)
						.toString());
		return cookieContext;
	}
	
	
	
	private static HttpClient getHttpClient() {        
        HttpClient httpClient = new DefaultHttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        HttpClientParams.setCookiePolicy(httpClient.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);
		return httpClient;
	}	
	
	private static HttpGet getHttpGet(String url, String userAgent, String referer) {
		HttpGet httpGet = new HttpGet(url);
		//httpGet.setHeader("Host", URLs.HOST);
		httpGet.setHeader("Connection","Keep-Alive");
		//httpGet.setHeader("User-Agent", userAgent);
		if (referer!=null) {
			httpGet.setHeader("Referer", referer);
		}
		return httpGet;
	}
	
	private static HttpPost getHttpPost(String url, String userAgent, String referer) {
		HttpPost httpPost = new HttpPost(url);
		//httpPost.setHeader("Host", URLs.HOST);
		httpPost.setHeader("Connection","Keep-Alive");
		//httpPost.setHeader("User-Agent", userAgent);
		if (referer!=null) {
			httpPost.setHeader("Referer", referer);
		}
		return httpPost;
	}
	
	private static String http_get(AppContext appContext, String url, String referer) {	
		String userAgent = getUserAgent(appContext);
		HttpClient httpClient = getHttpClient();
		HttpGet httpGet = getHttpGet(url, userAgent, referer);
		
		//HttpContext localContext = getCookieContext(appContext);

		String responseBody = "";
		try {
			HttpResponse responce = httpClient.execute(httpGet, getCookieContext(appContext));
			int resStatu = responce.getStatusLine().getStatusCode();
			if (resStatu == HttpStatus.SC_OK) {
				HttpEntity entity = responce.getEntity();
				if (entity != null) {
					responseBody = EntityUtils.toString(entity);
				}
			}
		} catch (Exception e) {
			System.out.println("访问" + url + "出现异常!");
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		System.out.println("http_get-Cookie=====>"
				+ getCookieContext(appContext).getAttribute(ClientContext.COOKIE_STORE)
						.toString());

		return responseBody;
	}
	
	private static String http_post(AppContext appContext, String url, String referer, List<NameValuePair> params) {	
		String userAgent = getUserAgent(appContext);
		HttpClient httpClient = getHttpClient();
		HttpPost httpPost = getHttpPost(url, userAgent, referer);
		
		//HttpContext localContext = getCookieContext(appContext);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		String responseBody = "";
		try {
			HttpResponse responce = httpClient.execute(httpPost, getCookieContext(appContext));
			int resStatu = responce.getStatusLine().getStatusCode();
			if (resStatu == HttpStatus.SC_OK) {
				HttpEntity entity = responce.getEntity();
				if (entity != null) {
					responseBody = EntityUtils.toString(entity);
				}
			}
		} catch (Exception e) {
			System.out.println("访问" + url + "出现异常!");
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		System.out.println("http_post-Cookie=====>"
				+ getCookieContext(appContext).getAttribute(ClientContext.COOKIE_STORE)
						.toString());

		return responseBody;
	}
	
	
	public static boolean login (AppContext appContext, String username, String password) {
		
		System.out.println("pre_get-Cookie=====>"
				+ getCookieContext(appContext).getAttribute(ClientContext.COOKIE_STORE)
						.toString());
		String getHtml = http_get(appContext, URLs.LOGIN_VALIDATE_HTTP, null);
		
		String once = HtmlParser.getTopicOnce2(getHtml);
		System.out.println("once=====>" + once);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("next", "/"));
		params.add(new BasicNameValuePair("u", username));
		params.add(new BasicNameValuePair("p", password));
		params.add(new BasicNameValuePair("once", once));
		params.add(new BasicNameValuePair("next", "/"));
		
		System.out.println("pre_post-Cookie=====>"
				+ getCookieContext(appContext).getAttribute(ClientContext.COOKIE_STORE)
						.toString());
		http_post(appContext, URLs.LOGIN_VALIDATE_HTTP, URLs.LOGIN_VALIDATE_HTTP, params);
		
		CookieStore c = getCookieStore(appContext);
		if (c != null) {
			List<Cookie> cookieList = c.getCookies();
			//
			for (Cookie cookie : cookieList) {
				System.out.println("cookie.getName()=====>"
						+ cookie.getName());
				if (cookie.getName() == "auth") {
					System.out.println("return true=====>"
							+ cookie.getName());
					return true;
				}
			}//
			if (cookieList.size() == 4) {
				return true;
			}
		}
		
		return false;
	}
	
	public static ArrayList<HashMap<String, String>> getTopics(AppContext appContext, String url,
			ArrayList<HashMap<String, String>> topics) {

			String html = http_get(appContext, url, null);
			// String html = getHtmlByUrl(url);
			 Document doc = Jsoup.parse(html);
			//Document doc = Jsoup.connect(url).get();
			Elements items = doc.select("div[class=cell item]");

			if (!items.isEmpty() && !topics.isEmpty()) {
				topics.remove(topics.size() - 1);
			}

			for (Element item : items) {
				//System.out.println("item======>" + item.toString());
				Element titleElement = item.select("span[class=item_title]>a")
						.get(0);
				String href = titleElement.attr("href");
				String id = getMatcher("/t/([\\d]+)", href);
				String replies = getMatcher("#reply([\\d]+)", href);
				String title = titleElement.text();
				Element usernameElement = item.select("td>a").get(0);
				String href2 = usernameElement.attr("href");
				String username = getMatcher("/member/([0-9a-zA-Z]+)", href2);
				Element avatarElement = usernameElement.select("img").get(0);
				String avatar = avatarElement.attr("src");
				Element nodeElement = item.select("span[class=small fade]>a")
						.get(0);
				String node = nodeElement.text();
				System.out.println(node);

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key =>
				// value
				map.put(KEY_ID, id);
				map.put(KEY_TITLE, title);
				map.put(KEY_USERNAME, username);
				map.put(KEY_REPLIES, replies);
				map.put(KEY_AVATAR, avatar);
				map.put(KEY_NODE, node);

				// adding HashList to ArrayList
				topics.add(map);
			}
		

		HashMap<String, String> mapMore = new HashMap<String, String>();

		mapMore.put(KEY_ID, MainActivity.MORE_TAG);
		mapMore.put(KEY_TITLE, MainActivity.MORE_TAG);
		mapMore.put(KEY_USERNAME, MainActivity.MORE_TAG);
		mapMore.put(KEY_REPLIES, MainActivity.MORE_TAG);
		mapMore.put(KEY_AVATAR, MainActivity.MORE_TAG);
		mapMore.put(KEY_NODE, MainActivity.MORE_TAG);

		// adding HashList to ArrayList
		topics.add(mapMore);

		return topics;
	}
	
	private static String getMatcher(String regex, String source) {
		String result = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			result = matcher.group(1);// 只取第一组
		}
		return result;
	}
	*/
}


