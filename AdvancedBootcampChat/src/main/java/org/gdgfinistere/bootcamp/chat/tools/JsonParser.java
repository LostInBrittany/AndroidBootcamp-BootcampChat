package org.gdgfinistere.bootcamp.chat.tools;

import android.util.Log;

import org.gdgfinistere.bootcamp.chat.model.Tweet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class JsonParser {
	
	/**
	 * parseJsonFromSignin: get data from signin response
	 * @param message
	 * @return
	 */
	static public String parseJsonFromSignin(String message){
		try{
			JSONObject jObject = new JSONObject(message);
			return jObject.getString("token");
		}catch (Exception e){
			Log.e("org.gdgfinistere.bootcamp.chat", "parseJsonFromSignin exception ",e);
		}	
		return null;
	}
	
	/**
	 * parseJsonFromSendMsg : get data from send msg response
	 * @param message
	 * @return
	 */
	static public String parseJsonFromSendMsg(String message){
		try{
			JSONObject jObject = new JSONObject(message);
			return jObject.getString("timestamp");
		}catch (Exception e){
			Log.e("org.gdgfinistere.bootcamp.chat", "parseJsonFromSignin exception ",e);
		}	
		return "";
	}
	
	/**
	 * parseJsonFromSendMsg : get data from get msg response
	 * @param message
	 * @return
	 */
	static public List<Tweet> parseJsonFromGetMsgs(String message){
		try{
			Log.d("Bootcamp", "parseJsonFromGetMsgs "+ message);
			JSONArray jArray = new JSONArray(message);
			LinkedList<Tweet> lTweets = new LinkedList<Tweet>();
			Tweet t = null;
			// parse all json
			for(int i =0; i<jArray.length(); i++){
				t = new Tweet();
				t.setAuthor(jArray.getJSONObject(i).get("user").toString());
				t.setId(jArray.getJSONObject(i).get("id").toString());
				t.setTimestamp(jArray.getJSONObject(i).get("timestamp").toString());
				t.setMessage(jArray.getJSONObject(i).get("content").toString());
				lTweets.add(t);
			}
			return lTweets;
		} catch (Exception e){
			Log.d("org.gdgfinistere.bootcamp.chat", "parseJsonFromGetMsgs exception"+ message);
		}
		return null;
	}

    public static JSONObject isError(String message) {
        try {
            JSONArray jsonArray = new JSONArray(message);
            return null;
        } catch (Exception e) {
            //O.K., not an Array, so it could be an error
        }
        try{
            JSONObject jObject = new JSONObject(message);
            if ((jObject.has("status")) && (null != jObject.getString("status"))
                    && (!"".equals(jObject.getString("status")))) {
                return jObject;
            }
        }catch (Exception e){
            Log.e("JsonParser", " isError exception ",e);
        }
        return null;

    }

}
