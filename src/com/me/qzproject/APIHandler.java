package com.me.qzproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class APIHandler {

	public static String ip = "128.189.70.67";
	
	private static final String URL = "http://" + ip + ":80/laravel/public/index.php/api";
	
	public static String error;
	
	public static String user_id = "1";
	
	public static String signature;//efc6bab6ab4de757c5e205ee9a45ecd0d2dab860623f7100cde234f6e55cf963db5f2ff2fc6b12d42adfa76727b4d43951341ec8af244ed25c96d0ad56214cc4
	
	public static String key;
	
	public static String email = "anov1992@yandex.ru";	
	
	public static String password;	
	
	public static String name = "Andrey Novitskiy";

	public static User user = new User(user_id, name, email, false);
	
	public static Map<String, ArrayList<Map<String, String>>> getThemes(){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		
		JSONObject res = getResponseForGet("question/getthemes", headers);
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		if(res != null){
			try{
				JSONArray ts = res.getJSONArray("themes");
				for(int i = 0; i < ts.length(); i++){
					JSONObject t = (JSONObject) ts.get(i);
					Iterator it = t.keys();
					Map<String, String> theme = new HashMap<String, String>();					
					while(it.hasNext()){
						String k = (String) it.next();
						theme.put(k, t.getString(k));
					}
					result.add(theme);
				}
				Map<String, ArrayList<Map<String, String>>> themes = new HashMap<String, ArrayList<Map<String, String>>>();
				for(int i = 0; i < result.size(); i++){
					Map<String, String> th = result.get(i);
					String parent = th.get("parent");
					String id = th.get("id");
					if(parent.equals("0")){
						if(themes.containsKey(id)){
							themes.get(id).add(th);
						}else{
							ArrayList<Map<String, String>> ar = new ArrayList<Map<String, String>>();
							ar.add(th);
							themes.put(id, ar);
						}
					}else{
						if(themes.containsKey(parent)){
							themes.get(parent).add(th);							
						}else{
							ArrayList<Map<String, String>> ar = new ArrayList<Map<String, String>>();
							ar.add(th);
							themes.put(parent, ar);
						}
					}
				}
				
				return themes;
			}catch(Exception e){
				error = e.toString();
				return null;
			}
			
		}
		
		return null;
	}
	
	public static Map<String, Map<String, String>> getQuestionsByIds(String theme_id, List<String> ids){

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		
		Map<String, String> args = new HashMap<String, String>();
		for(int i = 0; i < ids.size(); i++){
			args.put("qid_" + i, ids.get(i) + "");			
		}
		JSONObject res = getResponseForPost("question/getquestionsbyids/" + theme_id, args, headers);	
		Map<String, Map<String, String>> qs = new HashMap<String, Map<String, String>>();
		if(res != null){
			try {
				JSONObject questions = res.getJSONObject("questions");
				for(String id : ids){
					JSONObject question = questions.getJSONObject(id);
					Map<String, String> q = new HashMap<String, String>();
					Iterator it = question.keys();
					while(it.hasNext()){
						String k = (String) it.next();
						q.put(k, question.getString(k));
					}
					qs.put(id, q);
				}
				
				return qs;
				
			}catch(Exception e){
				error = e.toString();
				return null;
			}
		}
		
		return null;
	}
	
	public static boolean register(){
		Map<String, String> args = new HashMap<String, String>();
		args.put("email", email);
		args.put("password", password);
		args.put("fullname", name);
		JSONObject res = getResponseForPost("user/register", args, null);
		return !(res == null);
	}
	
	public static Map<String, String> login(){
		Map<String, String> args = new HashMap<String, String>();
		args.put("email", email);
		args.put("password", password);
		
		JSONObject res = getResponseForPost("user/login", args, null);
		
		if(res == null){
			return null;
		}	
		
		Map<String, String> result = new HashMap<String, String>();
		try {
			result.put("key", res.getString("key"));
			result.put("id", res.getString("id"));
		} catch (Exception e) {
			error = e.toString();
			return null;
		}
		return result;
	} 
	
	public static boolean logout(){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/logout", headers);
		return !(res == null);
	}
	
	public static ArrayList<Map<String, String>> getSinglePlayer(int theme_id){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("question/single/" + theme_id, headers);
		
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
		if(res != null){
			try {
				JSONArray array = res.getJSONArray("questions");
				for(int i = 0; i < array.length(); i++){
					JSONObject obj = array.getJSONObject(i);
					Map<String, String> temp = new HashMap<String, String>();
					Iterator it = obj.keys();
					while(it.hasNext()){
						String k = (String) it.next();
						String v = obj.getString(k);
						temp.put(k, v);
					}
					result.add(temp);
				}
			} catch (Exception e) {
				error = e.toString();
				return null;
			}
			return result;
		}else{
			error = "1";
			return null;
		}
	}
	
	public static Map<String, Map<String, String>> getFakeMP(){
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		result.put("user", null);
		String test = "0#2000_1#3000_2#4500_3#6000";
		for(int i = 0; i < 10; i++){
			Map<String, String> m = new HashMap<String, String>();
			int rand = new Random().nextInt(4);
			m.put("ans_seq", test);
			m.put("question", "q" + i);
			m.put("ans1", "a");
			m.put("ans2", "b");
			m.put("ans3", "c");
			m.put("ans4", "d");
			m.put("right_ans", rand + "");
			result.put("question_" + i, m);			
		}
		
		return result;
	}
	
	public static Map<String, Map<String, String>> getMultiPlayer(int theme_id){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("question/" + theme_id, headers);
		if(res == null){
			return null;
		}
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		try {
			JSONObject user = res.getJSONObject("user");
			Map<String, String> u = new HashMap<String, String>();
			Iterator i1 = user.keys();
			while(i1.hasNext()){
				String k = (String) i1.next();
				u.put(k, user.getString(k));
			}
			result.put("user", u);
			JSONArray questions = res.getJSONArray("questions");
			for(int i = 0; i < questions.length(); i++){
				
				JSONObject obj = questions.getJSONObject(i);
				JSONObject question = (JSONObject) obj.get("0");
				
				String ansSeq = obj.getString("ans_seq");
				Map<String, String> m = new HashMap<String, String>();
				m.put("ans_seq", ansSeq);
				Iterator it = question.keys();
				while(it.hasNext()){
					String k = (String) it.next();
					m.put(k, question.getString(k));
				}
				result.put("question_" + i, m);
			}

			return result;
		} catch (Exception e) {
			error = e.toString();
			return null;
		}
	}
	
	
	public static boolean saveGame(Map<String, String> results, int theme_id, int score){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		Iterator i = results.entrySet().iterator();
		Map<String, String> params = new HashMap<String, String>();
		int j = 0;
		while(i.hasNext()){
			Map.Entry<String, String> entry = (Map.Entry<String, String>)i.next();
			params.put("question_id_" + j, entry.getKey());
			params.put("ans_seq_" + j, entry.getValue());
			j++;
		}
		
		params.put("score", score + "");
		
		JSONObject res = getResponseForPost("question/" + theme_id, params, headers);
		
		return !(res == null);
	}
	
	public static JSONObject getResponseForGet(String method, Map<String, String> headers){
		
		HttpClient httpclient = new DefaultHttpClient();
		String getData = URL + "/" + method;
				
		HttpGet httpget = new HttpGet(getData); 			

		if(headers != null){
			Iterator iter = headers.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
				httpget.addHeader(entry.getKey(), entry.getValue());
			}
		}
				
		System.out.println(httpget);
		
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
			
		} catch (Exception e) {	
			error = "No connection 2 " + e.toString();
			return null;			
		}		
		
		String requestResult = null;
		try {
			requestResult = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {	
			error = "3" + e.toString();
			return null;
		}	
		
		if(requestResult != null) {      
			JSONObject jsonResult = null;			
			try {
				jsonResult = new JSONObject(requestResult);
				if(!jsonResult.getBoolean("success")){
										
					error = jsonResult.getJSONObject("message").getString("text");
					return null;
					
				}else{
					error = null;				
					return new JSONObject(jsonResult.getString("message"));					
				}
			} catch (Exception e) {				
				error = "4" + e.toString();
				return null;		
			}
		}			
		return null;	
	}
	
	public static JSONObject getResponseForPost(String method, Map<String, String> arguments, Map<String, String> headers){
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		if(arguments == null){    
			arguments = new HashMap<String, String>();	        
		}
		
		Iterator i = arguments.entrySet().iterator();
		
		while(i.hasNext()){
			Map.Entry<String, String> entry = (Map.Entry<String, String>)i.next();
						
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
				
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(URL + "/" + method); 
		
		if(headers != null){
			Iterator iter = headers.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
				httppost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params));
		} catch (Exception e) {	
			error = "No connection 1 " + e.toString();
			return null;
		}
				
		HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
			
		} catch (Exception e) {	
			error = "No connection 2 " + e.toString();
			return null;			
		}		
		
		String requestResult = null;
		try {
			requestResult = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {	
			error = "3" + e.toString();
			return null;
		}	
		
		if(requestResult != null) {      
			JSONObject jsonResult = null;			
			try {
				jsonResult = new JSONObject(requestResult);
				if(!jsonResult.getBoolean("success")){
										
					error = jsonResult.getJSONObject("message").getString("text");
					return null;
					
				}else{
					error = null;				
					return new JSONObject(jsonResult.getString("message"));					
				}
			} catch (Exception e) {				
				error = "4" + e.toString();
				return null;		
			}
		}			
		return null;
	}
	
	public static ArrayList<Map<String, String>> getFriends(){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/getfriends", headers);
		if(res == null){
			return null;
		}
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
		try{
			JSONArray ar = res.getJSONArray("friends");
			for(int i = 0; i < ar.length(); i++){
				JSONObject user = ar.getJSONArray(i).getJSONObject(0);
				Map<String, String> u = new HashMap<String, String>();
				Iterator it = user.keys();
				while(it.hasNext()){
					String k = (String) it.next();
					String v = user.getString(k);
					u.put(k, v);
				}
				result.add(u);
			}			
			return result;
			
		}catch(Exception e){
			error = e.toString();
			return null;
		}	
	}
	
	public static boolean makeFriendRequest(String id){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/sendrequest/" + id, headers);
		
		return !(res == null);
	}
	
	public static boolean confirmFriendRequest(String id){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/confirmrequest/" + id, headers);
		
		return !(res == null);
	}
	
	public static Map<String, ArrayList<Map<String, String>>> getFriendRequests(){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/checkrequests", headers);
		if(res == null){
			return null;
		}
		
		Map<String, ArrayList<Map<String, String>>> result = new HashMap<String, ArrayList<Map<String, String>>>();
		try {
			JSONArray mine = res.getJSONArray("forme");
			ArrayList<Map<String, String>> ar1 = new ArrayList<Map<String, String>>();
			for(int i = 0; i < mine.length(); i++){
				JSONObject obj = mine.getJSONArray(i).getJSONObject(0);
				Map<String, String> u = new HashMap<String, String>();
				Iterator it = obj.keys();
				while(it.hasNext()){
					String k = (String) it.next();
					String v = obj.getString(k);
					u.put(k, v);
				}
				ar1.add(u);
			}
			result.put("mine", ar1);
			
			JSONArray forme = res.getJSONArray("forme");
			ArrayList<Map<String, String>> ar2 = new ArrayList<Map<String, String>>();
			for(int i = 0; i < forme.length(); i++){
				JSONObject obj = forme.getJSONArray(i).getJSONObject(0);
				Map<String, String> u = new HashMap<String, String>();
				Iterator it = obj.keys();
				while(it.hasNext()){
					String k = (String) it.next();
					String v = obj.getString(k);
					u.put(k, v);
				}
				ar2.add(u);
			}
			result.put("forme", ar2);
			
			return result;
			
		} catch (Exception e) {
			error = e.toString();
			return null;
		}
	}
	
	public static boolean cancelFriendRequest(String id){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/cancelrequest/" + id, headers);
		
		return !(res == null);
	}
	
	public static boolean declineFriendRequest(String id){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/declinerequest/" + id, headers);
		
		return !(res == null);
	}
	
	public static Map<String, String> getUserById(String id){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/getuserbyid/" + id, headers);		
		
		if(res == null){
			return null;
		}
		
		try{
			Map<String, String> map = new HashMap<String, String>();
			JSONObject user = res.getJSONArray("user").getJSONObject(0);
			Iterator it = user.keys();
			while(it.hasNext()){
				String k = (String) it.next();
				String v = user.getString(k);
				map.put(k, v);
			}
			
			return map;
		}catch(Exception e){
			error = e.toString();
			return null;
		}
	}

	
	public static ArrayList<Map<String, String>> findUser(String name){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		headers.put("name", name);
		JSONObject res = getResponseForGet("user/finduser", headers);
		
		if(res == null){
			return null;
		}
		
		try{
			ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();			
			
			JSONArray ar = res.getJSONArray("users");
			
			for(int i = 0; i < ar.length(); i++){
				JSONObject obj = ar.getJSONObject(i);
				Map<String, String> u = new HashMap<String, String>();
				Iterator it = obj.keys();
				while(it.hasNext()){
					String k = (String) it.next();
					String v = obj.getString(k);
					u.put(k, v);
				}
				result.add(u);
			}
			
			return result;
			
		}catch(Exception e){
			error = e.toString();
			return null;			
		}
		
	}
	
	public static boolean unfriend(String id){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		JSONObject res = getResponseForGet("user/unfriend/" + id, headers);		
		
		return !(res == null);
	}
	
	public static boolean uploadImage(String base64EncodedBitmap){
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("HTTP_USERID", user_id);
		headers.put("HTTP_SIGNATURE", signature);
		
		Map<String, String> args = new HashMap<String, String>();
		args.put("image", base64EncodedBitmap);
		JSONObject res = getResponseForPost("user/uploadimage", args, headers);	
	
		return !(res == null);
	}
	
	public static String getHash(String input, String key){

		Mac mac;
        String _sign = "";
        try {
            byte[] bytesKey = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(bytesKey, "HmacSHA512" );
            mac = Mac.getInstance( "HmacSHA512" );
            mac.init( secretKey );
            final byte[] macData = mac.doFinal(input.getBytes());
            byte[] hex = new Hex().encode(macData);
            _sign = new String( hex, "ISO-8859-1" );
        } catch(Exception e){
        	//System.out.println("Hashing exception" + e.toString());
        	//Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        	error = e.toString();
        	return null;
        }
        return _sign;       
	}	
}
