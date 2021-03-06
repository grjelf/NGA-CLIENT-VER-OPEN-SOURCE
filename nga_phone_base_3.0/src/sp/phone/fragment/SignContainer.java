package sp.phone.fragment;

import gov.anzong.androidnga.activity.MainActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.R;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.adapter.AppendableSignAdapter;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.adapter.SignPageAdapter;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.SignData;
import sp.phone.bean.TopicListInfo;
import sp.phone.fragment.TopiclistContainer.ListRefreshListener;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonSignLoadTask;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SignContainer extends Fragment implements
		OnSignPageLoadFinishedListener {
	final String TAG = SignContainer.class.getSimpleName();
	static final int MESSAGE_SENT = 1;
	int fid;
	int authorid;
	int searchpost;
	int favor;
	String key;
	String table;
	String author;
	boolean isrefresh=false;
	PullToRefreshAttacher attacher = null;
	private ListView listView;
	SignPageAdapter adapter;
	boolean canDismiss = true;
	int category = 0;
	private SignData result;
	View headview;
	LayoutInflater inflatera;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			category = savedInstanceState.getInt("category", 0);
		}
		this.inflatera=inflater;
		try {
			PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
			attacher = attacherOnwer.getAttacher();

		} catch (ClassCastException e) {
			Log.e(TAG,
					"father activity should implement PullToRefreshAttacherOnwer");
		}
		
		listView = new ListView(getActivity());
		listView.setDivider(null);
		adapter = new SignPageAdapter(this.getActivity());
		headview =  inflater.inflate(R.layout.signresult, null);
		headview.setVisibility(View.GONE);
//		refreshheadviewdata(headview);
		listView.addHeaderView(headview, null, false);
		listView.setAdapter(adapter);

		if (attacher != null)
			attacher.addRefreshableView(listView, new ListRefreshListener());
		return listView;
	}

	public void refreshheadviewdata(View headview){
			ThemeManager cfg = ThemeManager.getInstance();
			int colorId = R.color.shit1;
			boolean isnight=false;
			if (cfg.getMode() == ThemeManager.MODE_NIGHT) {
				colorId = R.color.night_bg_color;
				isnight=true;
			}
			headview.setBackgroundResource(colorId);
			TextView nickName = (TextView) headview
					.findViewById(R.id.nickName);
			TextView signtime = (TextView) headview
					.findViewById(R.id.signtime);
			TextView signdate = (TextView) headview
					.findViewById(R.id.signdate);
			TextView signstate = (TextView) headview
					.findViewById(R.id.signstate);
			TextView lasttext = (TextView) headview
					.findViewById(R.id.lasttext);
			TextView signdatedata = (TextView) headview
					.findViewById(R.id.signdatedata);
			TextView statetext = (TextView) headview
					.findViewById(R.id.statetext);
			ImageView avatarImage = (ImageView) headview
					.findViewById(R.id.avatarImage);
			View lineviewforshow = (View) headview
					.findViewById(R.id.lineviewforshow);
			TextView successnum = (TextView) headview
					.findViewById(R.id.successnum);
			TextView availablenum = (TextView) headview
					.findViewById(R.id.availablenum);
			String userName;
			String signtimes;
			String signdates;
			String signstates = "δ֪";
			String availablenums;
			String successnums;
			
			if (StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {
				userName = "δ֪";
			} else {
				userName = PhoneConfiguration.getInstance().userName;
			}
			String userId = "-9999";
			if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().uid)) {
				userId =PhoneConfiguration.getInstance().uid;
			}
			if(result!=null){
				if (StringUtil.isEmpty(result.get__SignResult())) {
					signstates = "δ֪";
				} else {
					signstates = result.get__SignResult();
				}

				if (result.get__is_json_error()) {
					signtimes = "δ֪";
					signdates = "δ֪";
					availablenums = "δ֪";
					successnums = "δ֪";
				}else {
					if (StringUtil.isEmpty(result.get__Last_time())) {
						signtimes = "δ֪";
					} else {
						signtimes = result.get__Last_time();
					}
					signdates = String.valueOf(result.get__Continued()) + "/"
							+ String.valueOf(result.get__Sum());
					if (result.get__Availablerows() == 0) {
						availablenums = "δ֪";
					} else {
						availablenums = String
								.valueOf(result.get__Availablerows())+"��";
					}
					if (result.get__Successrows() == 0) {
						successnums = "δ֪";
					} else {
						successnums = String
								.valueOf(result.get__Successrows())+"��";
					}
					if(result.get__Totalrows()>0){
						lineviewforshow.setVisibility(View.VISIBLE);
					}
				}
				
			}else{
				signtimes = "δ֪";
				signdates = "δ֪";
				availablenums = "δ֪";
				successnums = "δ֪";
			}
			nickName.setText(userName);
			signtime.setText(signtimes);
			signdate.setText(signdates);
			signstate.setText(signstates);
			availablenum.setText(availablenums);
			successnum.setText(successnums);
			ImageUtil.recycleImageView(avatarImage);
			handleUserAvatat(avatarImage,userId);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		canDismiss = true;
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.refresh();
		super.onViewCreated(view, savedInstanceState);
	}// ��ȡ����

	private void refresh_saying() {
		DefaultHeaderTransformer transformer = null;

		if (attacher != null) {
			uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
			headerTransformer = attacher.getHeaderTransformer();
			if (headerTransformer != null
					&& headerTransformer instanceof DefaultHeaderTransformer)
				transformer = (DefaultHeaderTransformer) headerTransformer;
		}

		if (transformer == null)
			ActivityUtil.getInstance().noticeSaying(this.getActivity());
		else
			transformer.setRefreshingText(ActivityUtil.getSaying());
		if (attacher != null)
			attacher.setRefreshing(true);
	}// ��Ч

	void refresh() {
		JsonSignLoadTask task = new JsonSignLoadTask(getActivity(), this);
		// ActivityUtil.getInstance().noticeSaying(this.getActivity());
		refresh_saying();
		task.execute("SIGN");
		isrefresh=true;
	}// ��ȡJSON��

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuId;
		if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// �����б���UIFLAGΪ1����1+2����1+4����1+2+4
				menuId = R.menu.signpage_menu_left;
			} else {
				menuId = R.menu.signpage_menu;
			}
		} else {
			menuId = R.menu.signpage_menu;
		}
		inflater.inflate(menuId, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.signpage_menuitem_refresh:
			refresh();
			break;
		case R.id.signpage_menuitem_back:
			getActivity().finish();
		default:
			break;
		}
		return true;
	}

	@Override
	public void jsonfinishLoad(SignData result) {
		if (attacher != null)
			attacher.setRefreshComplete();

		if (result == null)
			return;
		this.result=result;
		adapter.clear();
		adapter.jsonfinishLoad(result);
		if(isrefresh==true){
			headview.setVisibility(View.VISIBLE);
			refreshheadviewdata(headview);
			isrefresh=false;
		}
		listView.setAdapter(adapter);
		if (canDismiss)
			ActivityUtil.getInstance().dismiss();
	}

	
	
	public void handleUserAvatat(ImageView avatarIV,String userId) {
		Bitmap defaultAvatar = null, bitmap = null;
		if (PhoneConfiguration.getInstance().nikeWidth < 3) {
			return;
		}
		if (defaultAvatar == null
				|| defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
			Resources res = inflatera.getContext().getResources();
			InputStream is = res.openRawResource(R.drawable.default_avatar);
			InputStream is2 = res.openRawResource(R.drawable.default_avatar);
			defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
		}
		Object tagObj = avatarIV.getTag();
		if (tagObj instanceof AvatarTag) {
			AvatarTag origTag = (AvatarTag) tagObj;
			if (origTag.isDefault == false) {
				ImageUtil.recycleImageView(avatarIV);
				// Log.d(TAG, "recycle avatar:" + origTag.lou);
			} else {
				// Log.d(TAG, "default avatar, skip recycle");
			}
		}
		AvatarTag tag = new AvatarTag(Integer.parseInt(userId), true);
		avatarIV.setImageBitmap(defaultAvatar);
		avatarIV.setTag(tag);
		String avatarPath = HttpUtil.PATH_AVATAR + "/" + userId;
		String[] extension = { ".jpg", ".png", ".gif", ".jpeg", ".bmp" };
		for (int i = 0; i < 5; i++) {
			File f = new File(avatarPath+extension[i]);
			if (f.exists()) {
				
				bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath+extension[i]);
				if (bitmap == null) {
					f.delete();
				}
				long date = f.lastModified();
				if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
					f.delete();
				}
				break;
			}
		}
		if (bitmap!=null) {
			avatarIV.setImageBitmap(bitmap);
			tag.isDefault = false;
		} else {
			avatarIV.setImageBitmap(defaultAvatar);
			tag.isDefault = true;
		}

	}
	public void onCategoryChanged(int position) {
		if (position != category) {
			category = position;
			refresh();
		}
	}
	
	class ListRefreshListener implements
			PullToRefreshAttacher.OnRefreshListener {

		@Override
		public void onRefreshStarted(View view) {
			refresh();
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("category", category);
		canDismiss = false;
		super.onSaveInstanceState(outState);
	}
}
