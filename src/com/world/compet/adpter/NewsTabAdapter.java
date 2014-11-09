package com.world.compet.adpter;

import java.util.ArrayList;
import java.util.List;
import com.world.compet.R;
import com.world.compet.activity.FocusFriendsActivity;
import com.world.compet.activity.TalkFriendActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsTabAdapter extends BaseAdapter{

	private Context mContext;
	private List<ItemElement> dataSource = new ArrayList<ItemElement>();
	private LayoutInflater inflater;
	
	public static final int TAB_FOCUS_FRIEND = 1;
	public static final int TAB_FOCUS_COMPETITION = TAB_FOCUS_FRIEND + 1;
	public static final int TAB_TALK_FRIEND = TAB_FOCUS_COMPETITION + 1;
	public static final int TAB_DAILY_BEST = TAB_TALK_FRIEND + 1;
	public static final int TAB_ALL_RANK = TAB_DAILY_BEST + 1;
	public static final int TAB_RELATED_UTILS = TAB_ALL_RANK + 1;
	
	public NewsTabAdapter(Context context) {
		super();
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		initDataSource();
	}
	
	private void initDataSource() {
		dataSource.clear();
		Resources resources = mContext.getResources();
		TypedArray logos = resources.obtainTypedArray(R.array.news_tab_logos);
		String[] titles = resources.getStringArray(R.array.news_tab_titles);
		for (int i = 0; i < titles.length; i++) {
			ItemElement itemElement = createItemElement(resources.getDrawable(R.drawable.tip_yellowstrip),logos.getResourceId(i, 0),
					titles[i], i + 1, i);
			dataSource.add(itemElement);
		}
	}
	
	public int getCount() {
		if (dataSource != null) {
			return dataSource.size();
		}
		
		return 0;
	}

	public Object getItem(int position) {		
		if (dataSource != null && dataSource.size() > position) {
			return dataSource.get(position);
		}
		
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ItemElement item = (ItemElement) getItem(position);
		final ViewHolder holder;
		if (item == null) {
			return null;
		}
		
		if (convertView == null || convertView.getTag() == null
				|| !(convertView.getTag() instanceof ViewHolder)) {
			convertView = inflater.inflate(R.layout.news_tab_item_layout,null);
			holder = new ViewHolder();
			holder.parentView = convertView.findViewById(R.id.tab);
			holder.bgImage = (RelativeLayout) convertView.findViewById(R.id.tab_content);
			holder.logo = (ImageView) convertView.findViewById(R.id.tab_logo);
			holder.title = (TextView) convertView.findViewById(R.id.tab_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		fillValue(holder, item, position);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				handleIntent(item);			
			}
		});
		
		return convertView;
	}
	
	private void handleIntent(ItemElement item) {
		if (item == null) {
			return ;
		}
		Intent intent = null;
		switch (item.tag) {
		case TAB_FOCUS_FRIEND:
			intent = new Intent(mContext,FocusFriendsActivity.class);
			mContext.startActivity(intent);
			break;
		case TAB_FOCUS_COMPETITION:
			
			break;
		case TAB_TALK_FRIEND:
			intent = new Intent(mContext,TalkFriendActivity.class);
			mContext.startActivity(intent);
			break;
		case TAB_DAILY_BEST:
			
			break;
		case TAB_ALL_RANK:
			
			break;
		case TAB_RELATED_UTILS:
			
			break;

		default:
			break;
		}
	}

	private void fillValue(ViewHolder holder, ItemElement item, int position) {
		if (holder == null || item == null) {
			return;
		}

		holder.bgImage.setBackgroundDrawable(item.bgImage);
		holder.logo.setBackgroundDrawable(mContext.getResources().getDrawable(item.logoId));
		holder.title.setText(item.title);

//		//根据不同位置来设置背景
//		if (position == 0) {   
//			holder.parentView.setBackgroundResource(R.drawable.card_upleft_selector);
//		}else if (position == 1) {
//			holder.parentView.setBackgroundResource(R.drawable.card_upmid_selector);
//		}else if (position == 2){
//			holder.parentView.setBackgroundResource(R.drawable.card_upright_selector);
//		}else if (position == getCount() -1){
//			holder.parentView.setBackgroundResource(R.drawable.card_downright_selector);
//		}else if (position == getCount() - 2){
//			holder.parentView.setBackgroundResource(R.drawable.card_downmid_selector);
//		}else if(position == getCount() - 3){
//			holder.parentView.setBackgroundResource(R.drawable.card_downleft_selector);
//		}else if (position % 3 == 0) {
//			holder.parentView.setBackgroundResource(R.drawable.card_midleft_selector);
//		}else if (position % 3 == 1) {
//			holder.parentView.setBackgroundResource(R.drawable.card_midmid_selector);
//		}else if (position % 3 == 2) {
//			holder.parentView.setBackgroundResource(R.drawable.card_midright_selector);
//		}

	}
	
	public class ViewHolder {
		View parentView;
		RelativeLayout bgImage;
		ImageView logo;
		TextView title;
	}
	
	/*
	 * ItemElement为动态tab各入口的数据结构
	 */
	public class ItemElement {

		public Drawable bgImage;//入口的背景图，也可以整个入口直接设置为一个背景图，而不需要设置logo和title
		public int logoId;//入口logo的资源id
		public String title;//入口的标题
		public int tag;//入口的标识
		public int position;//入口的位置：由左往右，由上往下顺序
//		public Intent intent = null;//入口跳转的activity intent

		public int topMargin = 0;// 上边距
		public int bottomMargin = 0;// 下边距
	}
	
	public ItemElement createItemElement(Drawable bgImage, int logoId, String title,
			int tag, int position) {
		
		ItemElement item = new ItemElement();
		item.bgImage = bgImage;
		item.logoId = logoId;
		item.title = title;
		item.tag = tag;
		item.position = position;
		return item;
	}

	public ItemElement getItemElement(int position) {
		if (dataSource != null && dataSource.size() > position) {
			return dataSource.get(position);
		}

		return null;
	}

}
