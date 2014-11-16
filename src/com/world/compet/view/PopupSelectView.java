package com.world.compet.view;

import java.util.ArrayList;
import java.util.List;

import com.world.compet.R;
import com.world.compet.adpter.TextAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class PopupSelectView extends RelativeLayout implements ViewBaseAction{

	private ListView mListView;
	private String[] mItems = new String[]{};//显示字段
	private List<String> mItemIds = new ArrayList<String>();//隐藏id
	private int mBgResourceId;//不同下拉菜单的背景资源
	private OnSelectListener mOnSelectListener;
	private TextAdapter adapter;
	private String mDistance;
	private String mTitle;
	private String showText;
	private Context mContext;

	public String getShowText() {
		return showText;
	}

	public PopupSelectView(Context context, int resId, String[] datas) {
		super(context);
		initDataSource(datas);
		mBgResourceId = resId;
		init(context);
	}

	public PopupSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void initDataSource(String[] strings) {
		mItems = strings;
		for (int i = 0; i < mItems.length; i++) {
			mItemIds.add(String.valueOf(i));
		}
	}		

	private void init(Context context) {
		mContext = context;
		showText = mTitle;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_distance, this, true);
		setBackgroundDrawable(getResources().getDrawable(mBgResourceId));
		mListView = (ListView) findViewById(R.id.listView);
		adapter = new TextAdapter(context, mItems, R.drawable.choose_item_right, R.drawable.choose_eara_item_selector);
		adapter.setTextSize(17);
		if (mDistance != null) {
			for (int i = 0; i < mItemIds.size(); i++) {
				if (mItemIds.get(i).equals(mDistance)) {
					adapter.setSelectedPositionNoNotify(i);
					showText = mItems[i];
					break;
				}
			}
		}
		mListView.setAdapter(adapter);
		adapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

			public void onItemClick(View view, int position) {

				if (mOnSelectListener != null) {
					showText = mItems[position];
					mOnSelectListener.getValue(mItemIds.get(position), mItems[position]);
				}
			}
		});
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(String distance, String showText);
	}

	public void hide() {
		
	}

	public void show() {
		
	}

}
