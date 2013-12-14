package org.gdgfinistere.bootcamp.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.gdgfinistere.bootcamp.chat.R;
import org.gdgfinistere.bootcamp.chat.model.Tweet;

import java.util.LinkedList;
import java.util.List;

public class TweetListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Tweet> tweets;

	/**
	 * Ctor
	 * @param pContext : calling activity 
	 * @param pList : list to map
	 */
	public TweetListAdapter(Context pContext, List<Tweet> pList){
		inflater = LayoutInflater.from(pContext);
		if(pList == null)
			tweets = new LinkedList<Tweet>();
		else
			tweets = pList;
	}

	/**
	 * updating list when receiving new item from server
	 * @param list
	 */
	public void updateList(List<Tweet> list){
		if(list != null){
			tweets.clear();
			tweets.addAll(list);
		}
	}

	/**
	 * return count of list
	 */
	public int getCount() {
		if(tweets == null)
			return 0;
		return tweets.size();
	}

	/**
	 * get Item
	 */
	public Object getItem(int item) {
		return tweets.get(item);
	}

	public long getItemId(int item) {
		return item;
	}

	/**
	 * getView
	 * map view Item
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		try{
			// A ViewHolder keeps references to children views to avoid unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is no need
			// to reinflate it. We only inflate a new View when the convertView supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.tweet, null);

				// Creates a ViewHolder and store references to the two children views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.user = (TextView) convertView.findViewById(R.id.tweet_author);
				holder.message = (TextView) convertView.findViewById(R.id.tweet_message);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			holder.user.setText(tweets.get(position).getAuthor());
			holder.message.setText(tweets.get(position).getMessage());
		} catch (Exception e){

		}	

		return convertView;
	}

	static class ViewHolder {
		TextView user;
		TextView message;
	}

}
